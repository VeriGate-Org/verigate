/*
 * VeriGate (c) 2025. All rights reserved.
 */

package verigate.webbff.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import verigate.webbff.ai.dto.CaseSummaryResponse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Service for generating AI-powered case summaries with suggested dispositions.
 */
@Service
public class CaseSummaryService {

    private static final Logger logger = LoggerFactory.getLogger(CaseSummaryService.class);

    private final BedrockChatService chatService;
    private final ObjectMapper objectMapper;
    private final String systemPrompt;

    public CaseSummaryService(BedrockChatService chatService, ObjectMapper objectMapper) {
        this.chatService = chatService;
        this.objectMapper = objectMapper;
        this.systemPrompt = loadPrompt("prompts/case-summary.txt");
    }

    /**
     * Generates an AI summary for a case including suggested disposition.
     *
     * @param caseId the case ID
     * @param caseDataJson JSON string with case details (verifications, risk scores, signals)
     * @return structured summary response
     */
    public CaseSummaryResponse summarize(String caseId, String caseDataJson) {
        try {
            String userPrompt = "Analyze case " + caseId + " with the following data:\n"
                    + caseDataJson;

            String response = chatService.chat(systemPrompt, userPrompt);
            return parseResponse(response);

        } catch (Exception e) {
            logger.error("Failed to generate case summary for {}: {}", caseId, e.getMessage(), e);
            return new CaseSummaryResponse(
                    "Unable to generate AI summary at this time.",
                    "ESCALATE",
                    "AI analysis unavailable",
                    List.of()
            );
        }
    }

    private CaseSummaryResponse parseResponse(String content) {
        try {
            String json = content.trim();
            if (json.startsWith("```")) {
                json = json.replaceAll("```json\\s*", "").replaceAll("```\\s*$", "").trim();
            }

            JsonNode root = objectMapper.readTree(json);

            String summary = root.path("summary").asText("No summary available");
            String disposition = root.path("suggestedDisposition").asText("ESCALATE");
            String reasoning = root.path("reasoning").asText("");
            List<String> signals = objectMapper.convertValue(
                    root.path("keySignals"),
                    new TypeReference<List<String>>() {});

            return new CaseSummaryResponse(summary, disposition, reasoning,
                    signals != null ? signals : List.of());

        } catch (Exception e) {
            logger.warn("Failed to parse case summary response: {}", e.getMessage());
            return new CaseSummaryResponse(content, "ESCALATE", "", List.of());
        }
    }

    private String loadPrompt(String path) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                logger.warn("Prompt template not found: {}", path);
                return "You are a verification case analyst.";
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.warn("Failed to load prompt: {}", path);
            return "You are a verification case analyst.";
        }
    }
}
