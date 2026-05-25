/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.services;

import java.math.BigDecimal;
import java.util.Optional;
import verigate.billing.domain.models.Refund;

/**
 * Service for processing payment refunds.
 */
public interface RefundService {

    Refund requestFullRefund(String paymentId, String reason);

    Refund requestPartialRefund(String paymentId, BigDecimal amount, String reason);

    Refund processRefund(String refundId);

    Optional<Refund> getRefund(String refundId);
}
