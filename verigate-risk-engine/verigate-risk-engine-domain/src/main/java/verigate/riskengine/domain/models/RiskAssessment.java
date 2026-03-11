/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.domain.models;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import verigate.riskengine.domain.enums.RiskDecision;

/**
 * The computed risk assessment result for a verification workflow.
 *
 * @param assessmentId      unique identifier for this assessment
 * @param verificationId    the parent workflow verification ID
 * @param partnerId         the partner who initiated the verification
 * @param compositeScore    the aggregated risk score (0-100)
 * @param riskTier          the tier name this score maps to
 * @param decision          the final risk decision
 * @param decisionReason    human-readable explanation for the decision
 * @param individualScores  the normalized scores from each adapter
 * @param overrideApplied   whether an override rule was triggered
 * @param overrideRuleId    the ID of the override rule that fired (null if none)
 * @param assessedAt        when the assessment was computed
 */
public record RiskAssessment(
    UUID assessmentId,
    UUID verificationId,
    String partnerId,
    int compositeScore,
    String riskTier,
    RiskDecision decision,
    String decisionReason,
    List<AdapterScore> individualScores,
    boolean overrideApplied,
    String overrideRuleId,
    Instant assessedAt
) {

    public RiskAssessment {
        if (assessmentId == null) {
            throw new IllegalArgumentException("assessmentId must not be null");
        }
        if (verificationId == null) {
            throw new IllegalArgumentException("verificationId must not be null");
        }
        if (partnerId == null || partnerId.isBlank()) {
            throw new IllegalArgumentException("partnerId must not be null or blank");
        }
        if (compositeScore < 0 || compositeScore > 100) {
            throw new IllegalArgumentException("compositeScore must be between 0 and 100");
        }
        if (riskTier == null || riskTier.isBlank()) {
            throw new IllegalArgumentException("riskTier must not be null or blank");
        }
        if (decision == null) {
            throw new IllegalArgumentException("decision must not be null");
        }
        if (individualScores == null) {
            individualScores = List.of();
        }
        if (assessedAt == null) {
            throw new IllegalArgumentException("assessedAt must not be null");
        }
    }
}
