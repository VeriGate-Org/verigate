/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.application.handlers;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.riskengine.domain.enums.WorkflowStatus;
import verigate.riskengine.domain.models.VerificationWorkflow;
import verigate.riskengine.domain.services.WorkflowRepository;

/**
 * Handles workflow timeout: scans for PENDING workflows older than the
 * configured timeout and triggers aggregation with available scores.
 */
public class WorkflowTimeoutHandler {

    private static final Logger LOG = LoggerFactory.getLogger(WorkflowTimeoutHandler.class);
    private static final Duration DEFAULT_TIMEOUT = Duration.ofMinutes(10);
    private static final int SCAN_LIMIT = 50;

    private final WorkflowRepository workflowRepository;
    private final VerificationEventHandler eventHandler;
    private final Duration timeout;

    public WorkflowTimeoutHandler(WorkflowRepository workflowRepository,
                                  VerificationEventHandler eventHandler) {
        this(workflowRepository, eventHandler, DEFAULT_TIMEOUT);
    }

    public WorkflowTimeoutHandler(WorkflowRepository workflowRepository,
                                  VerificationEventHandler eventHandler,
                                  Duration timeout) {
        this.workflowRepository = workflowRepository;
        this.eventHandler = eventHandler;
        this.timeout = timeout;
    }

    /**
     * Scans for timed-out workflows and triggers aggregation.
     */
    public void handleTimeouts() {
        Instant cutoff = Instant.now().minus(timeout);
        List<VerificationWorkflow> timedOutWorkflows =
            workflowRepository.findPendingBefore(cutoff, SCAN_LIMIT);

        if (timedOutWorkflows.isEmpty()) {
            LOG.debug("No timed-out workflows found");
            return;
        }

        LOG.info("Found {} timed-out workflows", timedOutWorkflows.size());

        for (VerificationWorkflow workflow : timedOutWorkflows) {
            try {
                LOG.warn("Workflow {} timed out: {}/{} checks completed, triggering partial aggregation",
                    workflow.workflowId(),
                    workflow.completedChecks().size(),
                    workflow.expectedChecks().size());

                // Mark as TIMED_OUT before aggregation
                var timedOutWorkflow = new VerificationWorkflow(
                    workflow.workflowId(),
                    workflow.partnerId(),
                    workflow.policyId(),
                    workflow.expectedChecks(),
                    workflow.completedChecks(),
                    WorkflowStatus.TIMED_OUT,
                    workflow.createdAt(),
                    Instant.now()
                );
                workflowRepository.save(timedOutWorkflow);

                // Trigger aggregation with available scores
                eventHandler.triggerAggregation(timedOutWorkflow);

            } catch (Exception e) {
                LOG.error("Failed to process timed-out workflow {}", workflow.workflowId(), e);
            }
        }
    }
}
