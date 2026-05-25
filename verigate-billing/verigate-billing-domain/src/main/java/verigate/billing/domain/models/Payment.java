/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.models;

import java.math.BigDecimal;
import java.time.Instant;
import verigate.billing.domain.enums.PaymentMethod;
import verigate.billing.domain.enums.PaymentStatus;

/**
 * Represents a payment transaction against an invoice.
 *
 * @param paymentId          unique payment identifier
 * @param invoiceId          the invoice being paid
 * @param partnerId          the paying partner
 * @param amount             payment amount
 * @param currency           currency code
 * @param status             current payment status
 * @param paymentMethod      method of payment
 * @param payFastPaymentId   PayFast's payment identifier
 * @param payFastReference   PayFast merchant reference
 * @param merchantId         PayFast merchant ID
 * @param initiatedAt        when payment was initiated
 * @param completedAt        when payment completed (null if pending)
 * @param failureReason      reason for failure (null if not failed)
 * @param itnPayload         raw ITN notification payload for audit
 */
public record Payment(
    String paymentId,
    String invoiceId,
    String partnerId,
    BigDecimal amount,
    String currency,
    PaymentStatus status,
    PaymentMethod paymentMethod,
    String payFastPaymentId,
    String payFastReference,
    String merchantId,
    Instant initiatedAt,
    Instant completedAt,
    String failureReason,
    String itnPayload
) {
    public Payment {
        if (paymentId == null || paymentId.isBlank()) {
            throw new IllegalArgumentException("paymentId must not be null or blank");
        }
        if (invoiceId == null || invoiceId.isBlank()) {
            throw new IllegalArgumentException("invoiceId must not be null or blank");
        }
        if (partnerId == null || partnerId.isBlank()) {
            throw new IllegalArgumentException("partnerId must not be null or blank");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("currency must not be null or blank");
        }
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        if (initiatedAt == null) {
            throw new IllegalArgumentException("initiatedAt must not be null");
        }
    }
}
