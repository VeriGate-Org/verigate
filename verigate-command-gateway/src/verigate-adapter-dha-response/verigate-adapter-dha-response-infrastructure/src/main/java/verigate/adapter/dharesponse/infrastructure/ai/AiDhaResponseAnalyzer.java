package verigate.adapter.dharesponse.infrastructure.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;
import verigate.adapter.dharesponse.domain.models.DhaPermitVerificationOutcome;
import verigate.adapter.dharesponse.domain.models.DhaResponseVerificationResult;
import verigate.adapter.dharesponse.domain.services.DhaResponseParserService;

/** Analyzes DHA response emails using AI via Amazon Bedrock. */
public class AiDhaResponseAnalyzer implements DhaResponseParserService {

  private static final Logger logger =
      LoggerFactory.getLogger(AiDhaResponseAnalyzer.class);

  private final BedrockRuntimeClient bedrockClient;
  private final String modelId;
  private final ObjectMapper objectMapper;
  private final String promptTemplate;

  /** Creates a new AiDhaResponseAnalyzer. */
  public AiDhaResponseAnalyzer(
      BedrockRuntimeClient bedrockClient, String modelId,
      ObjectMapper objectMapper) {
    this.bedrockClient = bedrockClient;
    this.modelId = modelId;
    this.objectMapper = objectMapper;
    this.promptTemplate = loadPromptTemplate();
  }

  @Override
  public DhaResponseVerificationResult analyzeResponse(
      String emailBody, String permitNumber,
      String permitType, String nationality) {
    String prompt = promptTemplate
        .replace("{{EMAIL_BODY}}", emailBody)
        .replace("{{PERMIT_NUMBER}}", permitNumber)
        .replace("{{PERMIT_TYPE}}", permitType)
        .replace("{{NATIONALITY}}", nationality);

    try {
      String requestBody = objectMapper.writeValueAsString(Map.of(
          "anthropic_version", "bedrock-2023-05-31",
          "max_tokens", 2048,
          "temperature", 0.1,
          "messages", new Object[]{
              Map.of("role", "user", "content", prompt)
          }
      ));

      InvokeModelResponse response = bedrockClient.invokeModel(
          InvokeModelRequest.builder()
              .modelId(modelId)
              .contentType("application/json")
              .accept("application/json")
              .body(SdkBytes.fromUtf8String(requestBody))
              .build());

      String responseBody = response.body().asUtf8String();
      JsonNode responseJson = objectMapper.readTree(responseBody);

      // Extract the text content from Claude's response
      String contentText =
          responseJson.path("content").get(0).path("text").asText();

      // Parse the JSON from the response
      String jsonStr = extractJsonFromResponse(contentText);
      JsonNode resultJson = objectMapper.readTree(jsonStr);

      return mapToResult(resultJson);
    } catch (Exception e) {
      logger.error("AI analysis failed, returning INCONCLUSIVE", e);
      return new DhaResponseVerificationResult(
          DhaPermitVerificationOutcome.INCONCLUSIVE,
          permitNumber, permitType, null, nationality,
          false, false, null, null,
          "AI analysis failed: " + e.getMessage(),
          0.0, Map.of(), Instant.now());
    }
  }

  private DhaResponseVerificationResult mapToResult(JsonNode json) {
    DhaPermitVerificationOutcome outcome;
    try {
      outcome = DhaPermitVerificationOutcome.valueOf(
          json.path("outcome").asText("INCONCLUSIVE").toUpperCase());
    } catch (IllegalArgumentException e) {
      outcome = DhaPermitVerificationOutcome.INCONCLUSIVE;
    }

    Map<String, String> rawFields = new HashMap<>();
    json.fields().forEachRemaining(entry ->
        rawFields.put(entry.getKey(), entry.getValue().asText()));

    return new DhaResponseVerificationResult(
        outcome,
        null, null,
        json.path("holderName").isNull()
            ? null : json.path("holderName").asText(),
        json.path("nationality").isNull()
            ? null : json.path("nationality").asText(),
        json.path("isAuthentic").asBoolean(false),
        json.path("isCurrentlyValid").asBoolean(false),
        json.path("expiryDate").isNull()
            ? null : json.path("expiryDate").asText(),
        json.path("employerMatch").isNull()
            ? null : json.path("employerMatch").asText(),
        json.path("additionalNotes").asText(""),
        json.path("confidenceScore").asDouble(0.0),
        rawFields,
        Instant.now());
  }

  private String extractJsonFromResponse(String text) {
    // Strip markdown code fences if present
    String stripped = text.strip();
    if (stripped.startsWith("```json")) {
      stripped = stripped.substring(7);
    } else if (stripped.startsWith("```")) {
      stripped = stripped.substring(3);
    }
    if (stripped.endsWith("```")) {
      stripped = stripped.substring(0, stripped.length() - 3);
    }
    return stripped.strip();
  }

  private String loadPromptTemplate() {
    try (InputStream is = getClass().getResourceAsStream(
        "/prompts/dha-response-analysis.txt")) {
      if (is == null) {
        throw new IllegalStateException(
            "Prompt template not found: "
                + "/prompts/dha-response-analysis.txt");
      }
      return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new IllegalStateException(
          "Failed to load prompt template", e);
    }
  }
}
