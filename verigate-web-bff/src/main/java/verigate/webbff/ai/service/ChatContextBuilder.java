/*
 * VeriGate (c) 2025. All rights reserved.
 */

package verigate.webbff.ai.service;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Builds context-aware system prompts for the AI chat copilot.
 */
@Component
public class ChatContextBuilder {

    private final String baseSystemPrompt;

    public ChatContextBuilder() {
        this.baseSystemPrompt = loadPrompt("prompts/chat-copilot-system.txt");
    }

    /**
     * Builds a system prompt enriched with the user's current page context.
     *
     * @param pageContext the current page route and relevant IDs
     * @return the complete system prompt
     */
    public String buildSystemPrompt(String pageContext) {
        StringBuilder prompt = new StringBuilder(baseSystemPrompt);

        if (pageContext != null && !pageContext.isEmpty()) {
            prompt.append("\n\n## Current Context\n");
            prompt.append("The user is currently viewing: ").append(pageContext);
            prompt.append("\nTailor your responses to be relevant to what they are looking at.");
        }

        return prompt.toString();
    }

    private String loadPrompt(String path) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                return getDefaultSystemPrompt();
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return getDefaultSystemPrompt();
        }
    }

    private String getDefaultSystemPrompt() {
        return """
            You are VeriGate AI, an intelligent assistant for the VeriGate verification platform.
            Help partners understand verification results, risk scores, and compliance requirements.
            Be concise, accurate, and helpful. Always provide actionable guidance.
            """;
    }
}
