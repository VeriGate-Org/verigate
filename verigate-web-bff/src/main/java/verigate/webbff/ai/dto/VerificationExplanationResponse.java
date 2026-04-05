/*
 * VeriGate (c) 2025. All rights reserved.
 */

package verigate.webbff.ai.dto;

public record VerificationExplanationResponse(
    String explanation,
    String whatWasChecked,
    String whatItMeans,
    String recommendedAction
) {
}
