/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.enums;

/**
 * Types of financial events that generate ledger entries.
 */
public enum FinancialEventType {
    INVOICE_ISSUED,
    PAYMENT_RECEIVED,
    CREDIT_APPLIED,
    REFUND_PROCESSED,
    INVOICE_VOIDED
}
