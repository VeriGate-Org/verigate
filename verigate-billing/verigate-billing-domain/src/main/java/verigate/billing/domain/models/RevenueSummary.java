/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.models;

import java.math.BigDecimal;
import java.time.YearMonth;

/**
 * Aggregated revenue summary for a partner and verification type in a period.
 */
public record RevenueSummary(
    YearMonth period,
    String partnerId,
    String verificationType,
    BigDecimal grossRevenue,
    BigDecimal vatAmount,
    BigDecimal netRevenue,
    BigDecimal creditsApplied,
    BigDecimal refundsIssued,
    long verificationCount
) {
    public RevenueSummary {
        if (period == null) {
            throw new IllegalArgumentException("period must not be null");
        }
        if (partnerId == null || partnerId.isBlank()) {
            throw new IllegalArgumentException("partnerId must not be null or blank");
        }
        if (grossRevenue == null || grossRevenue.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("grossRevenue must not be null or negative");
        }
        if (vatAmount == null || vatAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("vatAmount must not be null or negative");
        }
        if (netRevenue == null) {
            throw new IllegalArgumentException("netRevenue must not be null");
        }
    }
}
