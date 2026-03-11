/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.domain.services;

import java.util.List;
import java.util.UUID;
import verigate.riskengine.domain.models.AdapterScore;
import verigate.riskengine.domain.models.RiskAssessment;
import verigate.riskengine.domain.models.RiskScoringConfig;

/**
 * Aggregates individual adapter scores into a composite risk assessment.
 */
public interface RiskAggregator {

    /**
     * Computes a composite risk assessment from individual adapter scores.
     *
     * @param verificationId the parent verification/workflow ID
     * @param partnerId      the partner who initiated the verification
     * @param scores         the normalized individual adapter scores
     * @param config         the partner's scoring configuration
     * @return the computed risk assessment
     */
    RiskAssessment assess(UUID verificationId, String partnerId,
                          List<AdapterScore> scores, RiskScoringConfig config);
}
