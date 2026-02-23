/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * Represents a verification completed event consumed from the Kinesis stream.
 * This event is published by the verification adapters upon completion of any
 * verification and is consumed by the billing service for usage tracking.
 *
 * @param eventId            unique identifier for this event
 * @param verificationId     the verification identifier
 * @param partnerId          the partner who initiated the verification
 * @param verificationType   the type of verification performed
 * @param outcome            the verification outcome (SUCCESS, FAILURE, SYSTEM_ERROR)
 * @param eventTimestamp     when the verification completed
 * @param provider           the verification provider used (e.g., CIPC, WorldCheck)
 * @param correlationId      the correlation identifier for tracing
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record VerificationCompletedEvent(
    @JsonProperty("eventId") String eventId,
    @JsonProperty("verificationId") String verificationId,
    @JsonProperty("partnerId") String partnerId,
    @JsonProperty("verificationType") String verificationType,
    @JsonProperty("outcome") String outcome,
    @JsonProperty("eventTimestamp") LocalDateTime eventTimestamp,
    @JsonProperty("provider") String provider,
    @JsonProperty("correlationId") String correlationId
) {

    /**
     * Validates that critical fields required for billing are present.
     */
    public VerificationCompletedEvent {
        if (verificationId == null || verificationId.isBlank()) {
            throw new IllegalArgumentException("verificationId must not be null or blank");
        }
        if (partnerId == null || partnerId.isBlank()) {
            throw new IllegalArgumentException("partnerId must not be null or blank");
        }
        if (verificationType == null || verificationType.isBlank()) {
            throw new IllegalArgumentException("verificationType must not be null or blank");
        }
        if (outcome == null || outcome.isBlank()) {
            throw new IllegalArgumentException("outcome must not be null or blank");
        }
    }
}
