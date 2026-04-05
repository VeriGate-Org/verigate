/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.infrastructure.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.ai.common.domain.models.AiResponse;
import verigate.ai.common.infrastructure.bedrock.BedrockVisionService;
import verigate.ai.common.infrastructure.prompts.PromptTemplates;

/**
 * AI-powered document analyzer using Claude Vision via AWS Bedrock. Extracts text fields, validates
 * document authenticity, and cross-validates against expected values.
 */
public class AiDocumentAnalyzer {

  private static final Logger logger = LoggerFactory.getLogger(AiDocumentAnalyzer.class);

  private static final String PROMPT_TEMPLATE = "prompts/document-analysis.txt";

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
      String prompt = PromptTemplates.load(PROMPT_TEMPLATE, Map.of(
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

  private DocumentAnalysisResult parseResult(String content) {
    try {
      String json = content.trim();
      if (json.startsWith("```")) {
        json = json.replaceAll("```json\\s*", "").replaceAll("```\\s*$", "").trim();
      }

      JsonNode root = objectMapper.readTree(json);

      Map<String, String> extractedFields = objectMapper.convertValue(
          root.path("extractedFields"),
          new TypeReference<Map<String, String>>() {});

      Map<String, String> crossValidation = objectMapper.convertValue(
          root.path("crossValidation"),
          new TypeReference<Map<String, String>>() {});

      double authenticityScore = root.path("authenticityScore").asDouble(0.0);

      List<String> anomalies = objectMapper.convertValue(
          root.path("anomalies"),
          new TypeReference<List<String>>() {});

      return new DocumentAnalysisResult(
          extractedFields != null ? extractedFields : Map.of(),
          crossValidation != null ? crossValidation : Map.of(),
          authenticityScore,
          anomalies != null ? anomalies : List.of()
      );

    } catch (Exception e) {
      logger.warn("Failed to parse AI document analysis: {}", e.getMessage());
      return DocumentAnalysisResult.error("Failed to parse analysis: " + e.getMessage());
    }
  }

  /**
   * Result of AI document analysis.
   */
  public record DocumentAnalysisResult(
      Map<String, String> extractedFields,
      Map<String, String> crossValidation,
      double authenticityScore,
      List<String> anomalies
  ) {

    public static DocumentAnalysisResult error(String message) {
      return new DocumentAnalysisResult(
          Map.of(), Map.of(), 0.0, List.of(message));
    }

    public boolean hasAnomalies() {
      return anomalies != null && !anomalies.isEmpty();
    }
  }
}
