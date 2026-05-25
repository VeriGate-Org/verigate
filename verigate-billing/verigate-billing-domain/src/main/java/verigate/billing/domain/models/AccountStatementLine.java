/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.models;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * A single line in an account statement.
 */
public record AccountStatementLine(
    Instant date,
    String description,
    String referenceId,
    BigDecimal debit,
    BigDecimal credit,
    BigDecimal runningBalance
) {
    public AccountStatementLine {
        if (date == null) {
            throw new IllegalArgumentException("date must not be null");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description must not be null or blank");
        }
    }
}
