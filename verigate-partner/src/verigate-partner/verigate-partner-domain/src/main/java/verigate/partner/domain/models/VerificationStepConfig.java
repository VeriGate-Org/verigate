/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package verigate.partner.domain.models;

/**
 * Configuration for a single verification step within a flow.
 */
public record VerificationStepConfig(
    String stepType,
    int order,
    double successThreshold,
    boolean required,
    int maxRetries,
    long retryDelayMs,
    long timeoutMs
) {
    public VerificationStepConfig {
        if (stepType == null || stepType.isBlank()) {
            throw new IllegalArgumentException("stepType must not be null or blank");
        }
        if (order < 0) {
            throw new IllegalArgumentException("order must be non-negative");
        }
        if (successThreshold < 0 || successThreshold > 1.0) {
            throw new IllegalArgumentException("successThreshold must be between 0 and 1");
        }
    }

    public static VerificationStepConfig withDefaults(String stepType, int order) {
        return new VerificationStepConfig(stepType, order, 0.7, true, 3, 1000L, 30000L);
    }
}
