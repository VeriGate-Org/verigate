/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.models;

import java.time.LocalDateTime;

/**
 * Represents a single usage record for a verification event.
 * Each verification completion results in one usage record that tracks
 * the partner, verification type, and outcome for billing purposes.
 *
 * @param usageId            unique identifier for this usage record
 * @param partnerId          the partner who initiated the verification
 * @param verificationType   the type of verification performed
 * @param verificationId     the original verification identifier (used for deduplication)
 * @param outcome            the outcome of the verification (SUCCESS, FAILURE, SYSTEM_ERROR)
 * @param eventTimestamp     when the verification event occurred
 */
public record UsageRecord(
    String usageId,
    String partnerId,
    String verificationType,
    String verificationId,
    String outcome,
    LocalDateTime eventTimestamp
) {

    /**
     * Validates that required fields are present.
     */
    public UsageRecord {
        if (usageId == null || usageId.isBlank()) {
            throw new IllegalArgumentException("usageId must not be null or blank");
        }
        if (partnerId == null || partnerId.isBlank()) {
            throw new IllegalArgumentException("partnerId must not be null or blank");
        }
        if (verificationType == null || verificationType.isBlank()) {
            throw new IllegalArgumentException("verificationType must not be null or blank");
        }
        if (verificationId == null || verificationId.isBlank()) {
            throw new IllegalArgumentException("verificationId must not be null or blank");
        }
        if (outcome == null || outcome.isBlank()) {
            throw new IllegalArgumentException("outcome must not be null or blank");
        }
        if (eventTimestamp == null) {
            throw new IllegalArgumentException("eventTimestamp must not be null");
        }
    }
}
