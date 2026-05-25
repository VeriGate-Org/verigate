/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.models;

import java.math.BigDecimal;
import java.time.Instant;
import verigate.billing.domain.enums.RefundStatus;

/**
 * A refund issued against a payment.
 */
public record Refund(
    String refundId,
    String partnerId,
    String paymentId,
    String invoiceId,
    BigDecimal amount,
    RefundStatus status,
    String providerReference,
    String creditNoteId,
    String reason,
    Instant requestedAt,
    Instant completedAt
) {
    public Refund {
        if (refundId == null || refundId.isBlank()) {
            throw new IllegalArgumentException("refundId must not be null or blank");
        }
        if (partnerId == null || partnerId.isBlank()) {
            throw new IllegalArgumentException("partnerId must not be null or blank");
        }
        if (paymentId == null || paymentId.isBlank()) {
            throw new IllegalArgumentException("paymentId must not be null or blank");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        if (requestedAt == null) {
            throw new IllegalArgumentException("requestedAt must not be null");
        }
    }
}
