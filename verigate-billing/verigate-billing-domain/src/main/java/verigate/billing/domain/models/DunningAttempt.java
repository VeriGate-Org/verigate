/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.models;

import java.time.Instant;

/**
 * A single retry attempt within a dunning schedule.
 */
public record DunningAttempt(
    int attemptNumber,
    Instant attemptDate,
    boolean successful,
    String failureReason,
    String providerReference
) {
    public DunningAttempt {
        if (attemptNumber < 1) {
            throw new IllegalArgumentException("attemptNumber must be at least 1");
        }
        if (attemptDate == null) {
            throw new IllegalArgumentException("attemptDate must not be null");
        }
    }
}
