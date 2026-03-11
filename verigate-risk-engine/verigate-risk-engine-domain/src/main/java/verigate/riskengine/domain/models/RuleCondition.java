/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.domain.models;

import verigate.riskengine.domain.enums.ComparisonOperator;
import verigate.riskengine.domain.enums.VerificationType;

/**
 * Condition for an override rule, evaluated against adapter scores.
 *
 * @param checkType the verification type this condition applies to
 * @param signalKey the key in the adapter score's signals map to evaluate
 * @param operator  the comparison operator
 * @param value     the value to compare against
 */
public record RuleCondition(
    VerificationType checkType,
    String signalKey,
    ComparisonOperator operator,
    String value
) {

    public RuleCondition {
        if (checkType == null) {
            throw new IllegalArgumentException("checkType must not be null");
        }
        if (signalKey == null || signalKey.isBlank()) {
            throw new IllegalArgumentException("signalKey must not be null or blank");
        }
        if (operator == null) {
            throw new IllegalArgumentException("operator must not be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("value must not be null");
        }
    }

    /**
     * Evaluates this condition against a signal value from an adapter score.
     *
     * @param actualValue the actual signal value to compare
     * @return true if the condition is satisfied
     */
    public boolean evaluate(String actualValue) {
        if (actualValue == null) {
            return false;
        }

        return switch (operator) {
            case CONTAINS -> actualValue.contains(value);
            case EQ -> actualValue.equals(value);
            default -> evaluateNumeric(actualValue);
        };
    }

    private boolean evaluateNumeric(String actualValue) {
        try {
            double actual = Double.parseDouble(actualValue);
            double target = Double.parseDouble(value);
            return switch (operator) {
                case GT -> actual > target;
                case LT -> actual < target;
                case GTE -> actual >= target;
                case LTE -> actual <= target;
                default -> false;
            };
        } catch (NumberFormatException e) {
            return actualValue.compareTo(value) == switch (operator) {
                case GT -> 1;
                case LT -> -1;
                default -> 0;
            };
        }
    }
}
