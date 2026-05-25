/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.enums;

/**
 * Status of a dunning (payment retry) schedule.
 */
public enum DunningStatus {
    ACTIVE,
    COMPLETED,
    EXHAUSTED,
    CANCELLED
}
