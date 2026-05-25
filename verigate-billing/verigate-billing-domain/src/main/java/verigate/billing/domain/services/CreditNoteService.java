/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.services;

import java.math.BigDecimal;
import java.util.List;
import verigate.billing.domain.enums.CreditNoteType;
import verigate.billing.domain.models.CreditNote;

/**
 * Service for issuing and applying credit notes.
 */
public interface CreditNoteService {

    CreditNote issueCredit(String partnerId, BigDecimal amount, CreditNoteType type,
                           String referenceInvoiceId, String reason, String issuedBy);

    BigDecimal getCreditBalance(String partnerId);

    CreditNote applyCredit(String creditNoteId, String invoiceId, BigDecimal amount);

    List<CreditNote> getAvailableCredits(String partnerId);

    List<CreditNote> getCreditHistory(String partnerId);
}
