/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.ai.common.domain.models;

/**
 * Represents a request to an AI model.
 *
 * @param prompt the user prompt
 * @param systemPrompt the system prompt providing context and instructions
 * @param maxTokens maximum number of tokens to generate
 * @param temperature controls randomness (0.0 = deterministic, 1.0 = creative)
 */
public record AiRequest(
    String prompt,
    String systemPrompt,
    int maxTokens,
    double temperature
) {

  public AiRequest(String prompt, String systemPrompt) {
    this(prompt, systemPrompt,
        AiConstants.DEFAULT_MAX_TOKENS, AiConstants.DEFAULT_TEMPERATURE);
  }
}
