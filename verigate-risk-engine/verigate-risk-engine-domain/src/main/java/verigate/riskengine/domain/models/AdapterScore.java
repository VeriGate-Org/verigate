/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.domain.models;

import java.time.Instant;
import java.util.Map;
import verigate.riskengine.domain.enums.VerificationOutcome;
import verigate.riskengine.domain.enums.VerificationType;

/**
 * Score from a single adapter check, normalized to 0-100.
 *
 * @param verificationType the type of verification performed
 * @param outcome          the raw outcome from the adapter
 * @param confidenceScore  normalized score in range 0-100
 * @param signals          key risk signals extracted from auxiliaryData
 * @param completedAt      when the adapter check completed
 */
public record AdapterScore(
    VerificationType verificationType,
    VerificationOutcome outcome,
    int confidenceScore,
    Map<String, String> signals,
    Instant completedAt
) {

    public AdapterScore {
        if (verificationType == null) {
            throw new IllegalArgumentException("verificationType must not be null");
        }
        if (outcome == null) {
            throw new IllegalArgumentException("outcome must not be null");
        }
        if (confidenceScore < 0 || confidenceScore > 100) {
            throw new IllegalArgumentException("confidenceScore must be between 0 and 100");
        }
        if (signals == null) {
            signals = Map.of();
        }
        if (completedAt == null) {
            throw new IllegalArgumentException("completedAt must not be null");
        }
    }
}
