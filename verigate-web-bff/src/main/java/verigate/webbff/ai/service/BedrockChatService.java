/*
 * VeriGate (c) 2025. All rights reserved.
 */

package verigate.webbff.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.util.function.Consumer;

/**
 * Spring service wrapping BedrockRuntimeClient for AI chat interactions.
 */
@Service
public class BedrockChatService {

    private static final Logger logger = LoggerFactory.getLogger(BedrockChatService.class);
    private static final String ANTHROPIC_VERSION = "bedrock-2023-05-31";

    private final BedrockRuntimeClient bedrockClient;
    private final ObjectMapper objectMapper;
    private final String modelId;

    public BedrockChatService(
            BedrockRuntimeClient bedrockClient,
            ObjectMapper objectMapper,
            @Value("${verigate.ai.model-id:us.anthropic.claude-sonnet-4-5-20250929-v1:0}") String modelId) {
        this.bedrockClient = bedrockClient;
        this.objectMapper = objectMapper;
        this.modelId = modelId;
    }

    /**
     * Synchronous chat invocation.
     */
    public String chat(String systemPrompt, String userMessage) {
        try {
            String requestBody = buildRequestBody(systemPrompt, userMessage, 4096, 0.3);

            InvokeModelResponse response = bedrockClient.invokeModel(
                    InvokeModelRequest.builder()
                            .modelId(modelId)
                            .contentType("application/json")
                            .accept("application/json")
                            .body(SdkBytes.fromUtf8String(requestBody))
                            .build());

            JsonNode root = objectMapper.readTree(response.body().asByteArray());
            JsonNode contentArray = root.get("content");
            if (contentArray != null && contentArray.isArray() && !contentArray.isEmpty()) {
                return contentArray.get(0).path("text").asText("");
            }

            return "";
        } catch (Exception e) {
            logger.error("Bedrock chat invocation failed: {}", e.getMessage(), e);
            throw new RuntimeException("AI chat failed", e);
        }
    }

    /**
     * Streaming chat invocation. Calls the consumer for each word/phrase to simulate streaming.
     * Uses the sync client and splits the full response into chunks for SSE delivery.
     */
    public void chatStream(String systemPrompt, String userMessage, Consumer<String> onChunk) {
        try {
            String fullResponse = chat(systemPrompt, userMessage);

            // Split response into word-level chunks for streaming effect
            String[] words = fullResponse.split("(?<=\\s)");
            for (String word : words) {
                if (!word.isEmpty()) {
                    onChunk.accept(word);
                }
            }
        } catch (Exception e) {
            logger.error("Bedrock streaming failed: {}", e.getMessage(), e);
            throw new RuntimeException("AI streaming chat failed", e);
        }
    }

    private String buildRequestBody(
            String systemPrompt, String userMessage, int maxTokens, double temperature) {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("anthropic_version", ANTHROPIC_VERSION);
            root.put("max_tokens", maxTokens);
            root.put("temperature", temperature);

            if (systemPrompt != null && !systemPrompt.isEmpty()) {
                root.put("system", systemPrompt);
            }

            ArrayNode messages = root.putArray("messages");
            ObjectNode msg = messages.addObject();
            msg.put("role", "user");
            ArrayNode content = msg.putArray("content");
            ObjectNode textBlock = content.addObject();
            textBlock.put("type", "text");
            textBlock.put("text", userMessage);

            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            throw new RuntimeException("Failed to build request body", e);
        }
    }
}
