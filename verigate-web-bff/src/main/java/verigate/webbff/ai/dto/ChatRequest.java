/*
 * VeriGate (c) 2025. All rights reserved.
 */

package verigate.webbff.ai.dto;

public record ChatRequest(
    String message,
    String conversationId,
    String pageContext
) {
}
