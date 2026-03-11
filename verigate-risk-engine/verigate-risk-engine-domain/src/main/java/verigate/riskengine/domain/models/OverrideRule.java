/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.domain.models;

import verigate.riskengine.domain.enums.RiskDecision;

/**
 * An override rule that can force a risk decision regardless of composite score.
 * Rules are evaluated in priority order (lowest number = highest priority).
 *
 * @param id             unique identifier for this rule
 * @param name           human-readable name
 * @param condition      the condition that triggers this override
 * @param forcedDecision the decision to force when the condition is met
 * @param priority       evaluation priority (lower = evaluated first)
 */
public record OverrideRule(
    String id,
    String name,
    RuleCondition condition,
    RiskDecision forcedDecision,
    int priority
) {

    public OverrideRule {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be null or blank");
        }
        if (condition == null) {
            throw new IllegalArgumentException("condition must not be null");
        }
        if (forcedDecision == null) {
            throw new IllegalArgumentException("forcedDecision must not be null");
        }
    }
}
