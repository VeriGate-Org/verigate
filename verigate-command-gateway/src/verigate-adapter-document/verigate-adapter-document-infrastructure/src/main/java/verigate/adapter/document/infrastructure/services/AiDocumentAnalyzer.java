/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.infrastructure.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.ai.common.domain.models.AiResponse;
import verigate.ai.common.infrastructure.bedrock.BedrockVisionService;
import verigate.ai.common.infrastructure.prompts.PromptTemplates;

/**
 * AI-powered document analyzer using Claude Vision via AWS Bedrock. Extracts text fields with
 * per-field confidence scoring, validates document authenticity, detects tampering, and
 * cross-validates against expected values.
 */
public class AiDocumentAnalyzer {

  private static final Logger logger = LoggerFactory.getLogger(AiDocumentAnalyzer.class);

  private static final String PROMPT_TEMPLATE_DEFAULT = "prompts/document-analysis.txt";
  private static final Map<String, String> DOCUMENT_TYPE_PROMPTS = Map.of(
      "IDENTITY_DOCUMENT", "prompts/document-analysis-identity.txt",
      "PASSPORT", "prompts/document-analysis-identity.txt",
      "DRIVERS_LICENSE", "prompts/document-analysis-identity.txt",
      "B_BBEE_CERTIFICATE", "prompts/document-analysis-bbee.txt",
      "TAX_CERTIFICATE", "prompts/document-analysis-tax.txt",
      "FINANCIAL_STATEMENT", "prompts/document-analysis-financial.txt",
      "UTILITY_BILL", "prompts/document-analysis-utility.txt",
      "CIPC_REGISTRATION", "prompts/document-analysis-cipc.txt"
  );

  private final BedrockVisionService visionService;
  private final ObjectMapper objectMapper;

  public AiDocumentAnalyzer(BedrockVisionService visionService) {
    this.visionService = visionService;
    this.objectMapper = new ObjectMapper();
  }

  /**
   * Analyzes a document image using AI vision capabilities.
   *
   * @param imageBytes the raw image bytes
   * @param mediaType the image media type (e.g., "image/jpeg")
   * @param expectedName the expected name on the document
   * @param expectedIdNumber the expected ID number on the document
   * @param documentType the type of document being verified
   * @return the document analysis result
   */
  public DocumentAnalysisResult analyze(
      byte[] imageBytes, String mediaType,
      String expectedName, String expectedIdNumber, String documentType) {

    try {
      String promptTemplate = resolvePromptTemplate(documentType);
      String prompt = PromptTemplates.load(promptTemplate, Map.of(
          "expectedName", expectedName != null ? expectedName : "",
          "expectedIdNumber", expectedIdNumber != null ? expectedIdNumber : "",
          "documentType", documentType != null ? documentType : "UNKNOWN"
      ));

      AiResponse response = visionService.analyzeDocument(imageBytes, mediaType, prompt);
      return parseResult(response.content());

    } catch (Exception e) {
      logger.error("AI document analysis failed: {}", e.getMessage(), e);
      return DocumentAnalysisResult.error("AI analysis failed: " + e.getMessage());
    }
  }

  private String resolvePromptTemplate(String documentType) {
    if (documentType == null) {
      return PROMPT_TEMPLATE_DEFAULT;
    }
    String template = DOCUMENT_TYPE_PROMPTS.get(documentType.toUpperCase());
    if (template != null) {
      try {
        PromptTemplates.loadRaw(template);
        return template;
      } catch (RuntimeException e) {
        logger.warn("Type-specific prompt not found for {}, using default", documentType);
      }
    }
    return PROMPT_TEMPLATE_DEFAULT;
  }

  private DocumentAnalysisResult parseResult(String content) {
    try {
      String json = content.trim();
      if (json.startsWith("```")) {
        json = json.replaceAll("```json\\s*", "").replaceAll("```\\s*$", "").trim();
      }

      JsonNode root = objectMapper.readTree(json);

      Map<String, ExtractedField> extractedFields =
          parseExtractedFields(root.path("extractedFields"));

      Map<String, String> crossValidation = objectMapper.convertValue(
          root.path("crossValidation"),
          new TypeReference<Map<String, String>>() {});

      double authenticityScore = root.path("authenticityScore").asDouble(0.0);

      List<String> anomalies = objectMapper.convertValue(
          root.path("anomalies"),
          new TypeReference<List<String>>() {});

      TamperingIndicators tamperingIndicators =
          parseTamperingIndicators(root.path("tamperingIndicators"));

      double overallConfidence = computeOverallConfidence(extractedFields);

      return new DocumentAnalysisResult(
          extractedFields != null ? extractedFields : Map.of(),
          crossValidation != null ? crossValidation : Map.of(),
          authenticityScore,
          anomalies != null ? anomalies : List.of(),
          tamperingIndicators,
          overallConfidence,
          List.of()
      );

    } catch (Exception e) {
      logger.warn("Failed to parse AI document analysis: {}", e.getMessage());
      return DocumentAnalysisResult.error("Failed to parse analysis: " + e.getMessage());
    }
  }

  private Map<String, ExtractedField> parseExtractedFields(JsonNode fieldsNode) {
    if (fieldsNode == null || fieldsNode.isMissingNode()) {
      return Map.of();
    }

    Map<String, ExtractedField> result = new LinkedHashMap<>();
    var fieldNames = fieldsNode.fieldNames();
    while (fieldNames.hasNext()) {
      String fieldName = fieldNames.next();
      JsonNode fieldNode = fieldsNode.get(fieldName);

      if (fieldNode.isObject() && fieldNode.has("value")) {
        // New format: { "value": "...", "confidence": 0.0-1.0 }
        String value = fieldNode.path("value").isNull() ? null : fieldNode.path("value").asText();
        double confidence = fieldNode.path("confidence").asDouble(0.0);
        result.put(fieldName, new ExtractedField(value, confidence));
      } else {
        // Legacy format: plain string value
        String value = fieldNode.isNull() ? null : fieldNode.asText();
        result.put(fieldName, new ExtractedField(value, 0.0));
      }
    }
    return result;
  }

  private TamperingIndicators parseTamperingIndicators(JsonNode node) {
    if (node == null || node.isMissingNode()) {
      return null;
    }

    List<String> flags = node.has("flags")
        ? objectMapper.convertValue(node.path("flags"), new TypeReference<List<String>>() {})
        : List.of();

    return new TamperingIndicators(
        node.path("fontConsistency").asInt(0),
        node.path("layoutAlignment").asInt(0),
        node.path("imageQuality").asInt(0),
        node.path("securityFeatures").asInt(0),
        node.path("metadataConsistency").asInt(0),
        node.path("overallTamperingScore").asInt(0),
        flags != null ? flags : List.of()
    );
  }

  private double computeOverallConfidence(Map<String, ExtractedField> fields) {
    if (fields == null || fields.isEmpty()) {
      return 0.0;
    }

    double sum = 0.0;
    int count = 0;
    for (ExtractedField field : fields.values()) {
      if (field.value() != null && !field.value().equals("null")) {
        sum += field.confidence();
        count++;
      }
    }
    return count > 0 ? sum / count : 0.0;
  }

  /**
   * Per-field extracted value with confidence score.
   */
  public record ExtractedField(String value, double confidence) {}

  /**
   * Tampering detection indicators from document analysis.
   */
  public record TamperingIndicators(
      int fontConsistency,
      int layoutAlignment,
      int imageQuality,
      int securityFeatures,
      int metadataConsistency,
      int overallTamperingScore,
      List<String> flags
  ) {}

  /**
   * Validation check result (e.g., Luhn check, format validation).
   */
  public record ValidationCheck(String name, String status, String detail) {}

  /**
   * Result of AI document analysis with per-field confidence scoring, tampering indicators,
   * and post-extraction validation checks.
   */
  public record DocumentAnalysisResult(
      Map<String, ExtractedField> extractedFields,
      Map<String, String> crossValidation,
      double authenticityScore,
      List<String> anomalies,
      TamperingIndicators tamperingIndicators,
      double overallConfidence,
      List<ValidationCheck> validationChecks
  ) {

    public static DocumentAnalysisResult error(String message) {
      return new DocumentAnalysisResult(
          Map.of(), Map.of(), 0.0, List.of(message), null, 0.0, List.of());
    }

    public boolean hasAnomalies() {
      return anomalies != null && !anomalies.isEmpty();
    }

    /**
     * Returns a copy with validation checks added.
     */
    public DocumentAnalysisResult withValidationChecks(List<ValidationCheck> checks) {
      return new DocumentAnalysisResult(
          extractedFields, crossValidation, authenticityScore,
          anomalies, tamperingIndicators, overallConfidence, checks);
    }

    /**
     * Returns extractedFields as a flat map of field name to value string (backwards compatible).
     */
    public Map<String, String> extractedFieldsFlat() {
      Map<String, String> flat = new LinkedHashMap<>();
      if (extractedFields != null) {
        extractedFields.forEach((key, field) -> flat.put(key, field.value()));
      }
      return flat;
    }
  }
}
