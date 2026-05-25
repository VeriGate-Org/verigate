/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.models;

import java.time.Instant;
import java.time.LocalDate;
import verigate.billing.domain.enums.TrialStatus;

/**
 * Represents a partner's trial period.
 */
public record Trial(
    String trialId,
    String partnerId,
    String planId,
    LocalDate startDate,
    LocalDate endDate,
    TrialStatus status,
    Instant convertedAt,
    Instant cancelledAt
) {
    public Trial {
        if (trialId == null || trialId.isBlank()) {
            throw new IllegalArgumentException("trialId must not be null or blank");
        }
        if (partnerId == null || partnerId.isBlank()) {
            throw new IllegalArgumentException("partnerId must not be null or blank");
        }
        if (planId == null || planId.isBlank()) {
            throw new IllegalArgumentException("planId must not be null or blank");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("startDate must not be null");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("endDate must not be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
    }
}
