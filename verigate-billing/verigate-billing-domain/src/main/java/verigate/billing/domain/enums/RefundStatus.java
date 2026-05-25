/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.enums;

/**
 * Status of a refund request.
 */
public enum RefundStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED
}
