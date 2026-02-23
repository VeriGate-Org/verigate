/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.models;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Represents a billing plan associated with a partner. Each partner has a plan
 * that defines per-verification-type pricing and a monthly minimum charge.
 *
 * @param planId                      unique identifier for the billing plan
 * @param partnerId                   the partner this plan belongs to
 * @param pricePerVerificationType    map of verification type to unit price
 * @param monthlyMinimum              the minimum monthly charge for the partner
 * @param active                      whether this plan is currently active
 */
public record BillingPlan(
    String planId,
    String partnerId,
    Map<String, BigDecimal> pricePerVerificationType,
    BigDecimal monthlyMinimum,
    boolean active
) {

    /**
     * Validates that required fields are present.
     */
    public BillingPlan {
        if (planId == null || planId.isBlank()) {
            throw new IllegalArgumentException("planId must not be null or blank");
        }
        if (partnerId == null || partnerId.isBlank()) {
            throw new IllegalArgumentException("partnerId must not be null or blank");
        }
        if (pricePerVerificationType == null) {
            throw new IllegalArgumentException("pricePerVerificationType must not be null");
        }
        if (monthlyMinimum == null) {
            throw new IllegalArgumentException("monthlyMinimum must not be null");
        }
        if (monthlyMinimum.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("monthlyMinimum must not be negative");
        }
        // Defensive copy to ensure immutability
        pricePerVerificationType = Map.copyOf(pricePerVerificationType);
    }

    /**
     * Returns the unit price for a given verification type.
     * If no specific price is configured, returns {@link BigDecimal#ZERO}.
     *
     * @param verificationType the verification type to look up
     * @return the unit price for the verification type
     */
    public BigDecimal getPriceForType(String verificationType) {
        return pricePerVerificationType.getOrDefault(verificationType, BigDecimal.ZERO);
    }
}
