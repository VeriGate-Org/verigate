/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.domain.events;

import java.time.Instant;
import java.util.UUID;
import verigate.riskengine.domain.enums.RiskDecision;

/**
 * Event published when a risk assessment has been completed for a workflow.
 *
 * @param eventId          unique event identifier
 * @param assessmentId     the assessment that was computed
 * @param verificationId   the parent workflow/verification ID
 * @param partnerId        the partner ID
 * @param compositeScore   the computed composite score
 * @param riskTier         the tier the score maps to
 * @param decision         the final decision
 * @param overrideApplied  whether an override was triggered
 * @param assessedAt       when the assessment completed
 */
public record RiskAssessmentCompletedEvent(
    UUID eventId,
    UUID assessmentId,
    UUID verificationId,
    String partnerId,
    int compositeScore,
    String riskTier,
    RiskDecision decision,
    boolean overrideApplied,
    Instant assessedAt
) {

    public static final String DETAIL_TYPE = "RiskAssessmentCompleted";
}
