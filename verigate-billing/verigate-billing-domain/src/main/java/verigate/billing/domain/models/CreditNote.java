/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.models;

import java.math.BigDecimal;
import java.time.Instant;
import verigate.billing.domain.enums.CreditNoteStatus;
import verigate.billing.domain.enums.CreditNoteType;

/**
 * A credit note issued to a partner, reducing amount owed.
 */
public record CreditNote(
    String creditNoteId,
    String partnerId,
    String creditNoteNumber,
    BigDecimal amount,
    BigDecimal appliedAmount,
    BigDecimal remainingAmount,
    CreditNoteType type,
    CreditNoteStatus status,
    String referenceInvoiceId,
    String appliedToInvoiceId,
    String reason,
    String issuedBy,
    Instant issuedAt,
    Instant appliedAt
) {
    public CreditNote {
        if (creditNoteId == null || creditNoteId.isBlank()) {
            throw new IllegalArgumentException("creditNoteId must not be null or blank");
        }
        if (partnerId == null || partnerId.isBlank()) {
            throw new IllegalArgumentException("partnerId must not be null or blank");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        if (issuedAt == null) {
            throw new IllegalArgumentException("issuedAt must not be null");
        }
    }
}
