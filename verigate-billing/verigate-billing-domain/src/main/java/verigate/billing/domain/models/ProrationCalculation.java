/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.models;

import java.math.BigDecimal;

/**
 * Result of a proration calculation when changing plans mid-cycle.
 */
public record ProrationCalculation(
    int totalDaysInPeriod,
    int remainingDays,
    BigDecimal dailyRateOldPlan,
    BigDecimal dailyRateNewPlan,
    BigDecimal creditAmount,
    BigDecimal chargeAmount,
    BigDecimal netAdjustment
) {
    public ProrationCalculation {
        if (totalDaysInPeriod <= 0) {
            throw new IllegalArgumentException("totalDaysInPeriod must be positive");
        }
        if (remainingDays < 0) {
            throw new IllegalArgumentException("remainingDays must not be negative");
        }
        if (dailyRateOldPlan == null || dailyRateOldPlan.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("dailyRateOldPlan must not be null or negative");
        }
        if (dailyRateNewPlan == null || dailyRateNewPlan.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("dailyRateNewPlan must not be null or negative");
        }
        if (creditAmount == null || creditAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("creditAmount must not be null or negative");
        }
        if (chargeAmount == null || chargeAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("chargeAmount must not be null or negative");
        }
        if (netAdjustment == null) {
            throw new IllegalArgumentException("netAdjustment must not be null");
        }
    }
}
