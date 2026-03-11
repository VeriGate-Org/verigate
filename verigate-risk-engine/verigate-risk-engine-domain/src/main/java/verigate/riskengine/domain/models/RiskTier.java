/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.domain.models;

import verigate.riskengine.domain.enums.RiskDecision;

/**
 * Partner-defined risk tier mapping a score range to a decision.
 *
 * @param name       human-readable tier name (e.g., "LOW_RISK", "MEDIUM_RISK", "HIGH_RISK")
 * @param lowerBound inclusive lower bound of the score range
 * @param upperBound exclusive upper bound of the score range
 * @param decision   the risk decision for scores falling in this tier
 */
public record RiskTier(
    String name,
    int lowerBound,
    int upperBound,
    RiskDecision decision
) {

    public RiskTier {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be null or blank");
        }
        if (lowerBound < 0 || lowerBound > 100) {
            throw new IllegalArgumentException("lowerBound must be between 0 and 100");
        }
        if (upperBound < 0 || upperBound > 101) {
            throw new IllegalArgumentException("upperBound must be between 0 and 101");
        }
        if (lowerBound >= upperBound) {
            throw new IllegalArgumentException("lowerBound must be less than upperBound");
        }
        if (decision == null) {
            throw new IllegalArgumentException("decision must not be null");
        }
    }

    /**
     * Returns true if the given score falls within this tier's range [lowerBound, upperBound).
     */
    public boolean contains(int score) {
        return score >= lowerBound && score < upperBound;
    }
}
