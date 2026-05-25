/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.models;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import verigate.billing.domain.enums.DunningStatus;

/**
 * Manages automatic payment retry attempts for a failed invoice payment.
 */
public record DunningSchedule(
    String dunningId,
    String partnerId,
    String invoiceId,
    DunningStatus status,
    int retryCount,
    int maxRetries,
    LocalDate nextRetryDate,
    List<DunningAttempt> attempts,
    Instant createdAt
) {
    public DunningSchedule {
        if (dunningId == null || dunningId.isBlank()) {
            throw new IllegalArgumentException("dunningId must not be null or blank");
        }
        if (partnerId == null || partnerId.isBlank()) {
            throw new IllegalArgumentException("partnerId must not be null or blank");
        }
        if (invoiceId == null || invoiceId.isBlank()) {
            throw new IllegalArgumentException("invoiceId must not be null or blank");
        }
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        if (maxRetries < 1) {
            throw new IllegalArgumentException("maxRetries must be at least 1");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("createdAt must not be null");
        }
        attempts = attempts != null ? List.copyOf(attempts) : List.of();
    }
}
