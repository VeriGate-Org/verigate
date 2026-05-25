/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.models;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import verigate.billing.domain.enums.FinancialEventType;
import verigate.billing.domain.enums.LedgerAccountType;
import verigate.billing.domain.enums.LedgerEntryType;

/**
 * A single entry in the double-entry accounting ledger.
 */
public record LedgerEntry(
    String ledgerEntryId,
    String partnerId,
    LedgerAccountType account,
    LedgerEntryType entryType,
    BigDecimal amount,
    FinancialEventType financialEventType,
    String referenceId,
    String referenceType,
    String description,
    YearMonth period,
    Instant createdAt
) {
    public LedgerEntry {
        if (ledgerEntryId == null || ledgerEntryId.isBlank()) {
            throw new IllegalArgumentException("ledgerEntryId must not be null or blank");
        }
        if (partnerId == null || partnerId.isBlank()) {
            throw new IllegalArgumentException("partnerId must not be null or blank");
        }
        if (account == null) {
            throw new IllegalArgumentException("account must not be null");
        }
        if (entryType == null) {
            throw new IllegalArgumentException("entryType must not be null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        if (financialEventType == null) {
            throw new IllegalArgumentException("financialEventType must not be null");
        }
        if (referenceId == null || referenceId.isBlank()) {
            throw new IllegalArgumentException("referenceId must not be null or blank");
        }
        if (period == null) {
            throw new IllegalArgumentException("period must not be null");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("createdAt must not be null");
        }
    }
}
