/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.models;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.util.List;
import verigate.billing.domain.enums.ReconciliationStatus;

/**
 * Monthly reconciliation report comparing invoiced amounts against payments.
 */
public record ReconciliationReport(
    String reconciliationId,
    YearMonth period,
    BigDecimal totalInvoiced,
    BigDecimal totalPaymentsReceived,
    BigDecimal totalCreditsIssued,
    BigDecimal totalRefunds,
    BigDecimal expectedBalance,
    BigDecimal actualBalance,
    BigDecimal discrepancy,
    ReconciliationStatus status,
    List<ReconciliationDiscrepancy> discrepancies,
    Instant generatedAt
) {
    public ReconciliationReport {
        if (reconciliationId == null || reconciliationId.isBlank()) {
            throw new IllegalArgumentException("reconciliationId must not be null or blank");
        }
        if (period == null) {
            throw new IllegalArgumentException("period must not be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        if (generatedAt == null) {
            throw new IllegalArgumentException("generatedAt must not be null");
        }
        discrepancies = discrepancies != null ? List.copyOf(discrepancies) : List.of();
    }
}
