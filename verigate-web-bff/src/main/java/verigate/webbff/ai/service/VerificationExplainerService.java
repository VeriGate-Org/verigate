/*
 * VeriGate (c) 2025. All rights reserved.
 */

package verigate.webbff.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import verigate.webbff.ai.dto.VerificationExplanationResponse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Service for generating AI-powered explanations of verification results.
 */
@Service
public class VerificationExplainerService {

    private static final Logger logger = LoggerFactory.getLogger(VerificationExplainerService.class);

    private final BedrockChatService chatService;
    private final ObjectMapper objectMapper;
    private final String systemPrompt;

    public VerificationExplainerService(BedrockChatService chatService, ObjectMapper objectMapper) {
        this.chatService = chatService;
        this.objectMapper = objectMapper;
        this.systemPrompt = loadPrompt("prompts/verification-explainer.txt");
    }

    /**
     * Generates a plain-English explanation of verification results.
     *
     * @param commandId the verification command ID
     * @param verificationDataJson JSON string with verification result data
     * @return structured explanation response
     */
    public VerificationExplanationResponse explain(String commandId, String verificationDataJson) {
        try {
            String userPrompt = "Explain verification " + commandId + ":\n" + verificationDataJson;

            String response = chatService.chat(systemPrompt, userPrompt);
            return parseResponse(response);

        } catch (Exception e) {
            logger.error("Failed to explain verification {}: {}", commandId, e.getMessage(), e);
            return new VerificationExplanationResponse(
                    "Unable to generate explanation at this time.",
                    "Unknown",
                    "AI analysis unavailable",
                    "Please review the verification details manually."
            );
        }
    }

    private VerificationExplanationResponse parseResponse(String content) {
        try {
            String json = content.trim();
            if (json.startsWith("```")) {
                json = json.replaceAll("```json\\s*", "").replaceAll("```\\s*$", "").trim();
            }

            JsonNode root = objectMapper.readTree(json);

            return new VerificationExplanationResponse(
                    root.path("explanation").asText(""),
                    root.path("whatWasChecked").asText(""),
                    root.path("whatItMeans").asText(""),
                    root.path("recommendedAction").asText("")
            );

        } catch (Exception e) {
            logger.warn("Failed to parse explanation response: {}", e.getMessage());
            return new VerificationExplanationResponse(content, "", "", "");
        }
    }

    private String loadPrompt(String path) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                return "You are a verification result analyst.";
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "You are a verification result analyst.";
        }
    }
}
