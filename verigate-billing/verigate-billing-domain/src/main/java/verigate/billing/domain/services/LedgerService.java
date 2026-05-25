/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.services;

import java.math.BigDecimal;
import verigate.billing.domain.models.Invoice;
import verigate.billing.domain.models.Payment;

/**
 * Double-entry accounting ledger service.
 */
public interface LedgerService {

    void recordInvoiceIssued(Invoice invoice);

    void recordPaymentReceived(Payment payment, Invoice invoice);

    void recordCreditApplied(String partnerId, String creditNoteId, String invoiceId,
                             BigDecimal amount, java.time.YearMonth period);

    void recordRefundProcessed(String partnerId, String refundId, BigDecimal amount,
                               java.time.YearMonth period);

    void recordInvoiceVoided(Invoice invoice);

    BigDecimal getAccountBalance(String partnerId);
}
