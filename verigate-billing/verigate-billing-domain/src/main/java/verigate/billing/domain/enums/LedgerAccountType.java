/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.enums;

/**
 * Chart of accounts for double-entry ledger.
 */
public enum LedgerAccountType {
    ACCOUNTS_RECEIVABLE,
    REVENUE,
    VAT_LIABILITY,
    CASH,
    CREDIT_NOTES,
    DEFERRED_REVENUE
}
