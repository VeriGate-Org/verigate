/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.domain.models;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import verigate.riskengine.domain.enums.VerificationType;
import verigate.riskengine.domain.enums.WorkflowStatus;

/**
 * Tracks a multi-check verification workflow, recording which checks
 * are expected and which have completed.
 *
 * @param workflowId       the workflow ID (same as the parent verificationId)
 * @param partnerId        the partner who initiated the workflow
 * @param policyId         the policy that defined the checks
 * @param expectedChecks   map of verification type to the commandId dispatched
 * @param completedChecks  map of verification type to the normalized adapter score
 * @param status           current workflow status
 * @param createdAt        when the workflow was created
 * @param completedAt      when the workflow completed (null if still pending)
 */
public record VerificationWorkflow(
    UUID workflowId,
    String partnerId,
    String policyId,
    Map<VerificationType, UUID> expectedChecks,
    Map<VerificationType, AdapterScore> completedChecks,
    WorkflowStatus status,
    Instant createdAt,
    Instant completedAt
) {

    public VerificationWorkflow {
        if (workflowId == null) {
            throw new IllegalArgumentException("workflowId must not be null");
        }
        if (partnerId == null || partnerId.isBlank()) {
            throw new IllegalArgumentException("partnerId must not be null or blank");
        }
        if (policyId == null || policyId.isBlank()) {
            throw new IllegalArgumentException("policyId must not be null or blank");
        }
        if (expectedChecks == null || expectedChecks.isEmpty()) {
            throw new IllegalArgumentException("expectedChecks must not be null or empty");
        }
        if (completedChecks == null) {
            completedChecks = Map.of();
        }
        if (status == null) {
            status = WorkflowStatus.PENDING;
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("createdAt must not be null");
        }
    }

    /**
     * Returns true when all expected checks have completed.
     */
    public boolean isComplete() {
        return completedChecks.size() >= expectedChecks.size();
    }
}
