/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.models;

import java.time.Instant;
import java.time.LocalDate;
import verigate.billing.domain.enums.CancellationType;
import verigate.billing.domain.enums.SubscriptionStatus;

/**
 * Represents a partner's active subscription to a billing plan.
 */
public record Subscription(
    String subscriptionId,
    String partnerId,
    String planId,
    SubscriptionStatus status,
    LocalDate currentPeriodStart,
    LocalDate currentPeriodEnd,
    Instant cancelledAt,
    CancellationType cancellationType,
    String cancelReason,
    Instant createdAt,
    Instant updatedAt
) {
    public Subscription {
        if (subscriptionId == null || subscriptionId.isBlank()) {
            throw new IllegalArgumentException("subscriptionId must not be null or blank");
        }
        if (partnerId == null || partnerId.isBlank()) {
            throw new IllegalArgumentException("partnerId must not be null or blank");
        }
        if (planId == null || planId.isBlank()) {
            throw new IllegalArgumentException("planId must not be null or blank");
        }
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        if (currentPeriodStart == null) {
            throw new IllegalArgumentException("currentPeriodStart must not be null");
        }
        if (currentPeriodEnd == null) {
            throw new IllegalArgumentException("currentPeriodEnd must not be null");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("createdAt must not be null");
        }
    }
}
