/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.domain.services;

import java.util.Map;
import verigate.riskengine.domain.enums.VerificationOutcome;
import verigate.riskengine.domain.enums.VerificationType;
import verigate.riskengine.domain.models.AdapterScore;

/**
 * Normalizes raw adapter results into a 0-100 confidence score.
 */
public interface ScoreNormalizer {

    /**
     * Normalizes the raw adapter result into an {@link AdapterScore}.
     *
     * @param type          the type of verification performed
     * @param outcome       the raw outcome from the adapter
     * @param auxiliaryData the raw auxiliary data from the adapter response
     * @return a normalized adapter score
     */
    AdapterScore normalize(VerificationType type, VerificationOutcome outcome,
                           Map<String, String> auxiliaryData);
}
