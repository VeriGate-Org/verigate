/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.models;

import java.math.BigDecimal;

/**
 * A single discrepancy found during reconciliation for a partner.
 */
public record ReconciliationDiscrepancy(
    String partnerId,
    BigDecimal expectedAmount,
    BigDecimal actualAmount,
    BigDecimal difference,
    String description
) {
    public ReconciliationDiscrepancy {
        if (partnerId == null || partnerId.isBlank()) {
            throw new IllegalArgumentException("partnerId must not be null or blank");
        }
        if (expectedAmount == null) {
            throw new IllegalArgumentException("expectedAmount must not be null");
        }
        if (actualAmount == null) {
            throw new IllegalArgumentException("actualAmount must not be null");
        }
    }
}
