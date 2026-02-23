/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.enums;

/**
 * Represents the billing aggregation period.
 * Usage records are aggregated into summaries at these intervals.
 */
public enum BillingPeriod {

    /**
     * Daily aggregation period. Used for intermediate summaries
     * and real-time usage dashboards.
     */
    DAILY,

    /**
     * Monthly aggregation period. Used for billing invoice generation
     * and cost calculations against billing plans.
     */
    MONTHLY
}
