/*
 * VeriGate (c) 2025. All rights reserved.
 */

package verigate.webbff.ai.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import verigate.webbff.ai.dto.CaseSummaryResponse;
import verigate.webbff.ai.dto.ChatRequest;
import verigate.webbff.ai.dto.VerificationExplanationResponse;
import verigate.webbff.ai.service.BedrockChatService;
import verigate.webbff.ai.service.CaseSummaryService;
import verigate.webbff.ai.service.ChatContextBuilder;
import verigate.webbff.ai.service.VerificationExplainerService;

import java.util.Map;
import java.util.UUID;

/**
 * REST controller for AI-powered features in the partner portal.
 */
@RestController
@RequestMapping("/api/partner/ai")
public class AiController {

    private static final Logger logger = LoggerFactory.getLogger(AiController.class);

    private final CaseSummaryService caseSummaryService;
    private final VerificationExplainerService verificationExplainerService;
    private final BedrockChatService chatService;
    private final ChatContextBuilder chatContextBuilder;

    public AiController(
            CaseSummaryService caseSummaryService,
            VerificationExplainerService verificationExplainerService,
            BedrockChatService chatService,
            ChatContextBuilder chatContextBuilder) {
        this.caseSummaryService = caseSummaryService;
        this.verificationExplainerService = verificationExplainerService;
        this.chatService = chatService;
        this.chatContextBuilder = chatContextBuilder;
    }

    /**
     * Generate an AI summary of a case with suggested disposition.
     */
    @PostMapping("/cases/{caseId}/summary")
    public ResponseEntity<CaseSummaryResponse> summarizeCase(
            @PathVariable String caseId,
            @RequestBody(required = false) Map<String, Object> caseData) {
        logger.info("AI case summary requested for caseId={}", caseId);

        String caseDataJson = caseData != null ? caseData.toString() : "{}";
        CaseSummaryResponse response = caseSummaryService.summarize(caseId, caseDataJson);

        return ResponseEntity.ok(response);
    }

    /**
     * Generate a plain-English explanation of a verification result.
     */
    @PostMapping("/verifications/{commandId}/explain")
    public ResponseEntity<VerificationExplanationResponse> explainVerification(
            @PathVariable String commandId,
            @RequestBody(required = false) Map<String, Object> verificationData) {
        logger.info("AI verification explanation requested for commandId={}", commandId);

        String dataJson = verificationData != null ? verificationData.toString() : "{}";
        VerificationExplanationResponse response =
                verificationExplainerService.explain(commandId, dataJson);

        return ResponseEntity.ok(response);
    }

    /**
     * Chat copilot endpoint with SSE streaming.
     */
    @PostMapping("/chat")
    public SseEmitter chat(@RequestBody ChatRequest request) {
        String conversationId = request.conversationId() != null
                ? request.conversationId()
                : UUID.randomUUID().toString();

        logger.info("AI chat request: conversationId={}, pageContext={}",
                conversationId, request.pageContext());

        SseEmitter emitter = new SseEmitter(60000L);

        Thread.startVirtualThread(() -> {
            try {
                String systemPrompt = chatContextBuilder.buildSystemPrompt(request.pageContext());

                chatService.chatStream(systemPrompt, request.message(), chunk -> {
                    try {
                        emitter.send(SseEmitter.event()
                                .data(Map.of(
                                        "delta", chunk,
                                        "conversationId", conversationId
                                ), MediaType.APPLICATION_JSON));
                    } catch (Exception e) {
                        logger.warn("Failed to send SSE chunk: {}", e.getMessage());
                    }
                });

                emitter.send(SseEmitter.event()
                        .name("done")
                        .data(Map.of("conversationId", conversationId),
                                MediaType.APPLICATION_JSON));
                emitter.complete();

            } catch (Exception e) {
                logger.error("Chat streaming failed: {}", e.getMessage(), e);
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    /**
     * Explain a monitoring alert.
     */
    @PostMapping("/monitoring/alerts/{alertId}/explain")
    public ResponseEntity<Map<String, String>> explainAlert(
            @PathVariable String alertId,
            @RequestBody(required = false) Map<String, Object> alertData) {
        logger.info("AI alert explanation requested for alertId={}", alertId);

        try {
            String systemPrompt = loadAlertExplainerPrompt();
            String userPrompt = "Explain alert " + alertId + ":\n"
                    + (alertData != null ? alertData.toString() : "{}");

            String explanation = chatService.chat(systemPrompt, userPrompt);

            return ResponseEntity.ok(Map.of(
                    "alertId", alertId,
                    "explanation", explanation
            ));
        } catch (Exception e) {
            logger.error("Failed to explain alert {}: {}", alertId, e.getMessage());
            return ResponseEntity.ok(Map.of(
                    "alertId", alertId,
                    "explanation", "Unable to generate explanation at this time."
            ));
        }
    }

    private String loadAlertExplainerPrompt() {
        try (var is = getClass().getClassLoader().getResourceAsStream("prompts/alert-explainer.txt")) {
            if (is != null) {
                return new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            logger.warn("Failed to load alert explainer prompt");
        }
        return "You are a monitoring alert analyst. Explain what this alert means and what action to take.";
    }
}
