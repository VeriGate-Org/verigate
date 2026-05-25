/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.enums;

/**
 * Result status of a reconciliation report.
 */
public enum ReconciliationStatus {
    BALANCED,
    DISCREPANCY,
    PENDING_REVIEW
}
