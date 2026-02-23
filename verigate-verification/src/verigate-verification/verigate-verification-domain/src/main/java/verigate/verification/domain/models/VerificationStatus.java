/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.domain.models;

/**
 * Enumeration of verification statuses.
 */
public enum VerificationStatus {
    INITIATED,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    CANCELLED,
    RETRY_PENDING
}