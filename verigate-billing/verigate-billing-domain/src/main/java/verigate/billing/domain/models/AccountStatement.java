/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * A partner's account statement for a date range.
 */
public record AccountStatement(
    String partnerId,
    LocalDate fromDate,
    LocalDate toDate,
    BigDecimal openingBalance,
    BigDecimal closingBalance,
    List<AccountStatementLine> lines
) {
    public AccountStatement {
        if (partnerId == null || partnerId.isBlank()) {
            throw new IllegalArgumentException("partnerId must not be null or blank");
        }
        if (fromDate == null) {
            throw new IllegalArgumentException("fromDate must not be null");
        }
        if (toDate == null) {
            throw new IllegalArgumentException("toDate must not be null");
        }
        if (openingBalance == null) {
            throw new IllegalArgumentException("openingBalance must not be null");
        }
        if (closingBalance == null) {
            throw new IllegalArgumentException("closingBalance must not be null");
        }
        lines = lines != null ? List.copyOf(lines) : List.of();
    }
}
