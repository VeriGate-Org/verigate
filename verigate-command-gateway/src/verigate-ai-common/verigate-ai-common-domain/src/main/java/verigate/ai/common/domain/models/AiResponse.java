/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.ai.common.domain.models;

/**
 * Represents a response from an AI model invocation.
 *
 * @param content the generated text content
 * @param stopReason the reason the model stopped generating (e.g., "end_turn", "max_tokens")
 * @param inputTokens number of input tokens consumed
 * @param outputTokens number of output tokens generated
 */
public record AiResponse(
    String content,
    String stopReason,
    int inputTokens,
    int outputTokens
) {
}
