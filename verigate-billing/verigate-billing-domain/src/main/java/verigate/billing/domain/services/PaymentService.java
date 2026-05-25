/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import verigate.billing.domain.models.Payment;

/**
 * Service for payment processing and PayFast integration.
 */
public interface PaymentService {

    Payment initiatePayment(String invoiceId);

    Payment processItnNotification(Map<String, String> itnData);

    Optional<Payment> getPayment(String paymentId);

    List<Payment> getPaymentsForInvoice(String invoiceId);

    String generatePaymentUrl(String invoiceId);
}
