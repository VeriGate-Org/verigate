/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.domain.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import verigate.riskengine.domain.models.VerificationWorkflow;

/**
 * Repository for multi-check verification workflow tracking.
 */
public interface WorkflowRepository {

    void save(VerificationWorkflow workflow);

    Optional<VerificationWorkflow> findByWorkflowId(UUID workflowId);

    List<VerificationWorkflow> findPendingBefore(java.time.Instant cutoff, int limit);
}
