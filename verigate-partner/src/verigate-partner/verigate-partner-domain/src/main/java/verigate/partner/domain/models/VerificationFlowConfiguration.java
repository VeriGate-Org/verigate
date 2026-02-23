/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package verigate.partner.domain.models;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Configuration defining the verification flow for a partner.
 * Maps verification types to ordered steps with thresholds and retry policies.
 */
public class VerificationFlowConfiguration {

    private final Map<String, List<VerificationStepConfig>> verificationFlows;
    private final int defaultMaxRetries;
    private final long defaultRetryDelayMs;

    public VerificationFlowConfiguration(Map<String, List<VerificationStepConfig>> verificationFlows,
                                         int defaultMaxRetries, long defaultRetryDelayMs) {
        this.verificationFlows = verificationFlows != null ? Map.copyOf(verificationFlows) : Map.of();
        this.defaultMaxRetries = defaultMaxRetries;
        this.defaultRetryDelayMs = defaultRetryDelayMs;
    }

    public static VerificationFlowConfiguration defaultConfiguration() {
        return new VerificationFlowConfiguration(Map.of(), 3, 1000L);
    }

    public List<VerificationStepConfig> getStepsForVerificationType(String verificationType) {
        return verificationFlows.getOrDefault(verificationType, Collections.emptyList());
    }

    public Map<String, List<VerificationStepConfig>> getVerificationFlows() {
        return verificationFlows;
    }

    public int getDefaultMaxRetries() {
        return defaultMaxRetries;
    }

    public long getDefaultRetryDelayMs() {
        return defaultRetryDelayMs;
    }
}
