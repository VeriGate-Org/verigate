/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.domain.models;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import verigate.riskengine.domain.enums.AggregationStrategy;
import verigate.riskengine.domain.enums.RiskDecision;
import verigate.riskengine.domain.enums.VerificationType;

/**
 * Complete scoring configuration for a partner, including weights,
 * aggregation strategy, risk tiers, and override rules.
 *
 * @param partnerId      the partner this config belongs to
 * @param weights        weight per verification type (0.0 - 1.0)
 * @param strategy       how to aggregate individual scores
 * @param tiers          risk tiers mapping score ranges to decisions
 * @param overrideRules  rules that can override the tier-based decision
 * @param version        config version for optimistic concurrency
 * @param updatedAt      when this config was last updated
 */
public record RiskScoringConfig(
    String partnerId,
    Map<VerificationType, Double> weights,
    AggregationStrategy strategy,
    List<RiskTier> tiers,
    List<OverrideRule> overrideRules,
    String version,
    Instant updatedAt
) {

    public RiskScoringConfig {
        if (partnerId == null || partnerId.isBlank()) {
            throw new IllegalArgumentException("partnerId must not be null or blank");
        }
        if (weights == null) {
            weights = Map.of();
        }
        if (strategy == null) {
            strategy = AggregationStrategy.WEIGHTED_AVERAGE;
        }
        if (tiers == null || tiers.isEmpty()) {
            throw new IllegalArgumentException("tiers must not be null or empty");
        }
        if (overrideRules == null) {
            overrideRules = List.of();
        }
    }

    /**
     * Returns the system default scoring config used when a partner
     * has no custom configuration.
     */
    public static RiskScoringConfig systemDefault(String partnerId) {
        return new RiskScoringConfig(
            partnerId,
            Map.of(), // empty = all weights equal at 1.0
            AggregationStrategy.WEIGHTED_AVERAGE,
            List.of(
                new RiskTier("HIGH_RISK", 0, 50, RiskDecision.REJECT),
                new RiskTier("MEDIUM_RISK", 50, 80, RiskDecision.MANUAL_REVIEW),
                new RiskTier("LOW_RISK", 80, 101, RiskDecision.APPROVE)
            ),
            List.of(), // no override rules by default
            "1",
            Instant.now()
        );
    }
}
