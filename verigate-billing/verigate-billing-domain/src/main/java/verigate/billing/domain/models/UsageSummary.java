/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.models;

import java.math.BigDecimal;
import java.time.YearMonth;

/**
 * Represents an aggregated usage summary for a specific partner, verification type,
 * and billing period. Summaries are computed by the usage aggregation process and
 * used for invoice generation.
 *
 * @param partnerId          the partner identifier
 * @param verificationType   the type of verification
 * @param period             the billing period (year and month)
 * @param successCount       number of successful verifications in the period
 * @param failureCount       number of failed verifications in the period
 * @param totalCount         total number of verifications in the period
 * @param totalCost          computed cost based on the partner's billing plan
 */
public record UsageSummary(
    String partnerId,
    String verificationType,
    YearMonth period,
    long successCount,
    long failureCount,
    long totalCount,
    BigDecimal totalCost
) {

    /**
     * Validates that required fields are present and counts are non-negative.
     */
    public UsageSummary {
        if (partnerId == null || partnerId.isBlank()) {
            throw new IllegalArgumentException("partnerId must not be null or blank");
        }
        if (verificationType == null || verificationType.isBlank()) {
            throw new IllegalArgumentException("verificationType must not be null or blank");
        }
        if (period == null) {
            throw new IllegalArgumentException("period must not be null");
        }
        if (successCount < 0) {
            throw new IllegalArgumentException("successCount must not be negative");
        }
        if (failureCount < 0) {
            throw new IllegalArgumentException("failureCount must not be negative");
        }
        if (totalCount < 0) {
            throw new IllegalArgumentException("totalCount must not be negative");
        }
        if (totalCost == null) {
            throw new IllegalArgumentException("totalCost must not be null");
        }
        if (totalCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("totalCost must not be negative");
        }
    }
}
