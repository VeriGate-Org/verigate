/*
 * VeriGate (c) 2025. All rights reserved.
 */

package verigate.ai.risksignal.domain.models;

import java.time.Instant;
import java.util.List;

/**
 * AI-generated risk enhancement for a verification workflow.
 */
public record AiRiskEnhancement(
    String workflowId,
    int confidenceAdjustment,
    String reasoning,
    List<String> correlations,
    List<String> anomalies,
    Instant analyzedAt,
    long ttl
) {
}
