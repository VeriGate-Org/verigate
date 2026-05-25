/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.services;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import verigate.billing.domain.enums.InvoiceStatus;
import verigate.billing.domain.models.Invoice;

/**
 * Service for invoice lifecycle management.
 */
public interface InvoiceService {

    Invoice generateInvoice(String partnerId, YearMonth period);

    Invoice issueInvoice(String invoiceId);

    Invoice updateStatus(String invoiceId, InvoiceStatus status);

    Optional<Invoice> getInvoice(String invoiceId);

    List<Invoice> getInvoicesForPartner(String partnerId);

    String generatePdf(String invoiceId);
}
