/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.ai.common.infrastructure.bedrock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;
import verigate.ai.common.domain.models.AiConstants;
import verigate.ai.common.domain.models.AiRequest;
import verigate.ai.common.domain.models.AiResponse;
import verigate.ai.common.domain.services.AiService;

/**
 * Implementation of {@link AiService} using AWS Bedrock with the Anthropic Messages API.
 * Includes retry logic with exponential backoff and token usage logging.
 */
public class BedrockAiService implements AiService {

  private static final Logger logger = LoggerFactory.getLogger(BedrockAiService.class);

  private static final int MAX_RETRIES = 3;
  private static final long BASE_DELAY_MS = 1000;

  private final BedrockRuntimeClient bedrockClient;
  private final ObjectMapper objectMapper;
  private final String modelId;

  public BedrockAiService(BedrockRuntimeClient bedrockClient, String modelId) {
    this.bedrockClient = bedrockClient;
    this.objectMapper = new ObjectMapper();
    this.modelId = modelId != null ? modelId : AiConstants.DEFAULT_MODEL_ID;
  }

  public BedrockAiService(BedrockRuntimeClient bedrockClient) {
    this(bedrockClient, AiConstants.DEFAULT_MODEL_ID);
  }

  @Override
  public AiResponse invoke(AiRequest request) {
    String requestBody = buildRequestBody(request);

    for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
      try {
        InvokeModelResponse response = bedrockClient.invokeModel(
            InvokeModelRequest.builder()
                .modelId(modelId)
                .contentType("application/json")
                .accept("application/json")
                .body(SdkBytes.fromUtf8String(requestBody))
                .build());

        AiResponse aiResponse = parseResponse(response);

        logger.info("Bedrock invocation succeeded: model={}, inputTokens={}, outputTokens={}",
            modelId, aiResponse.inputTokens(), aiResponse.outputTokens());

        return aiResponse;

      } catch (Exception e) {
        if (attempt == MAX_RETRIES) {
          logger.error("Bedrock invocation failed after {} attempts: {}",
              MAX_RETRIES, e.getMessage(), e);
          throw new RuntimeException("AI invocation failed after " + MAX_RETRIES + " attempts", e);
        }

        long delay = BASE_DELAY_MS * (long) Math.pow(2, attempt - 1);
        logger.warn("Bedrock invocation attempt {} failed, retrying in {}ms: {}",
            attempt, delay, e.getMessage());

        try {
          Thread.sleep(delay);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          throw new RuntimeException("Interrupted during retry backoff", ie);
        }
      }
    }

    throw new RuntimeException("AI invocation failed unexpectedly");
  }

  private String buildRequestBody(AiRequest request) {
    try {
      ObjectNode root = objectMapper.createObjectNode();
      root.put("anthropic_version", AiConstants.ANTHROPIC_VERSION);
      root.put("max_tokens", request.maxTokens());
      root.put("temperature", request.temperature());

      if (request.systemPrompt() != null && !request.systemPrompt().isEmpty()) {
        root.put("system", request.systemPrompt());
      }

      ArrayNode messages = root.putArray("messages");
      ObjectNode userMessage = messages.addObject();
      userMessage.put("role", "user");
      ArrayNode content = userMessage.putArray("content");
      ObjectNode textBlock = content.addObject();
      textBlock.put("type", "text");
      textBlock.put("text", request.prompt());

      return objectMapper.writeValueAsString(root);
    } catch (Exception e) {
      throw new RuntimeException("Failed to build Bedrock request body", e);
    }
  }

  private AiResponse parseResponse(InvokeModelResponse response) {
    try {
      JsonNode root = objectMapper.readTree(response.body().asByteArray());

      String content = "";
      JsonNode contentArray = root.get("content");
      if (contentArray != null && contentArray.isArray() && contentArray.size() > 0) {
        content = contentArray.get(0).path("text").asText("");
      }

      String stopReason = root.path("stop_reason").asText("unknown");

      int inputTokens = 0;
      int outputTokens = 0;
      JsonNode usage = root.get("usage");
      if (usage != null) {
        inputTokens = usage.path("input_tokens").asInt(0);
        outputTokens = usage.path("output_tokens").asInt(0);
      }

      return new AiResponse(content, stopReason, inputTokens, outputTokens);
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse Bedrock response", e);
    }
  }
}
