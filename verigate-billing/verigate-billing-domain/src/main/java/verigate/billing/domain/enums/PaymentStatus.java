/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.enums;

/**
 * Represents the status of a payment transaction.
 */
public enum PaymentStatus {
    PENDING,
    PROCESSING,
    COMPLETE,
    FAILED,
    CANCELLED,
    REFUNDED
}
