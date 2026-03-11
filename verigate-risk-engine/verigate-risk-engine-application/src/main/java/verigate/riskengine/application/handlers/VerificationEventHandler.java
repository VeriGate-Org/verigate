/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.application.handlers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.riskengine.domain.enums.VerificationOutcome;
import verigate.riskengine.domain.enums.VerificationType;
import verigate.riskengine.domain.enums.WorkflowStatus;
import verigate.riskengine.domain.models.AdapterScore;
import verigate.riskengine.domain.models.RiskScoringConfig;
import verigate.riskengine.domain.models.VerificationWorkflow;
import verigate.riskengine.domain.services.RiskAggregator;
import verigate.riskengine.domain.services.RiskAssessmentRepository;
import verigate.riskengine.domain.services.RiskScoringConfigRepository;
import verigate.riskengine.domain.services.ScoreNormalizer;
import verigate.riskengine.domain.services.WorkflowRepository;

/**
 * Handles incoming verification completed events from Kinesis.
 * When a workflow has all checks completed, triggers risk aggregation.
 */
public class VerificationEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(VerificationEventHandler.class);

    private final WorkflowRepository workflowRepository;
    private final RiskScoringConfigRepository configRepository;
    private final RiskAssessmentRepository assessmentRepository;
    private final ScoreNormalizer scoreNormalizer;
    private final RiskAggregator riskAggregator;
    private final EventPublisher eventPublisher;

    public VerificationEventHandler(
            WorkflowRepository workflowRepository,
            RiskScoringConfigRepository configRepository,
            RiskAssessmentRepository assessmentRepository,
            ScoreNormalizer scoreNormalizer,
            RiskAggregator riskAggregator,
            EventPublisher eventPublisher) {
        this.workflowRepository = workflowRepository;
        this.configRepository = configRepository;
        this.assessmentRepository = assessmentRepository;
        this.scoreNormalizer = scoreNormalizer;
        this.riskAggregator = riskAggregator;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Processes a verification completed event. Looks up the workflow,
     * normalizes the score, updates completed checks, and triggers
     * aggregation if all checks are done.
     */
    public void handle(VerificationCompletedPayload payload) {
        LOG.info("Processing verification event: commandId={}, type={}, outcome={}",
            payload.commandId(), payload.verificationType(), payload.outcome());

        // Look up the workflow this command belongs to
        var workflowOpt = workflowRepository.findByWorkflowId(payload.workflowId());
        if (workflowOpt.isEmpty()) {
            LOG.debug("No workflow found for workflowId={}, skipping (single-check flow)",
                payload.workflowId());
            return;
        }

        var workflow = workflowOpt.get();
        if (workflow.status() != WorkflowStatus.PENDING) {
            LOG.warn("Workflow {} is not PENDING (status={}), skipping duplicate event",
                workflow.workflowId(), workflow.status());
            return;
        }

        // Normalize the adapter score
        VerificationType type = payload.verificationType();
        AdapterScore adapterScore = scoreNormalizer.normalize(
            type, payload.outcome(), payload.auxiliaryData());

        // Update the workflow with the completed check
        Map<VerificationType, AdapterScore> updatedChecks = new HashMap<>(workflow.completedChecks());
        updatedChecks.put(type, adapterScore);

        var updatedWorkflow = new VerificationWorkflow(
            workflow.workflowId(),
            workflow.partnerId(),
            workflow.policyId(),
            workflow.expectedChecks(),
            updatedChecks,
            workflow.status(),
            workflow.createdAt(),
            workflow.completedAt()
        );

        workflowRepository.save(updatedWorkflow);

        // Check if all expected checks have completed
        if (updatedWorkflow.isComplete()) {
            triggerAggregation(updatedWorkflow);
        } else {
            LOG.info("Workflow {} progress: {}/{} checks completed",
                workflow.workflowId(),
                updatedChecks.size(),
                workflow.expectedChecks().size());
        }
    }

    /**
     * Triggers risk aggregation for a completed workflow. Used both when the
     * last check arrives and by the timeout handler.
     */
    public void triggerAggregation(VerificationWorkflow workflow) {
        LOG.info("Triggering aggregation for workflow {}", workflow.workflowId());

        // Mark workflow as SCORING
        var scoringWorkflow = new VerificationWorkflow(
            workflow.workflowId(),
            workflow.partnerId(),
            workflow.policyId(),
            workflow.expectedChecks(),
            workflow.completedChecks(),
            WorkflowStatus.SCORING,
            workflow.createdAt(),
            workflow.completedAt()
        );
        workflowRepository.save(scoringWorkflow);

        // Load scoring config
        RiskScoringConfig config = configRepository.findByPartnerId(workflow.partnerId())
            .orElseGet(() -> RiskScoringConfig.systemDefault(workflow.partnerId()));

        // Aggregate scores
        var scores = new ArrayList<>(workflow.completedChecks().values());
        var assessment = riskAggregator.assess(
            workflow.workflowId(), workflow.partnerId(), scores, config);

        // Store assessment
        assessmentRepository.save(assessment);

        // Mark workflow as COMPLETED
        var completedWorkflow = new VerificationWorkflow(
            workflow.workflowId(),
            workflow.partnerId(),
            workflow.policyId(),
            workflow.expectedChecks(),
            workflow.completedChecks(),
            WorkflowStatus.COMPLETED,
            workflow.createdAt(),
            Instant.now()
        );
        workflowRepository.save(completedWorkflow);

        // Publish event
        eventPublisher.publishRiskAssessmentCompleted(assessment);

        LOG.info("Risk assessment completed for workflow {}: score={}, tier={}, decision={}",
            workflow.workflowId(),
            assessment.compositeScore(),
            assessment.riskTier(),
            assessment.decision());
    }

    /**
     * Payload representing a verification completed event from Kinesis.
     */
    public record VerificationCompletedPayload(
        UUID workflowId,
        UUID commandId,
        VerificationType verificationType,
        VerificationOutcome outcome,
        Map<String, String> auxiliaryData,
        String partnerId
    ) {}

    /**
     * Interface for publishing risk assessment events.
     */
    public interface EventPublisher {
        void publishRiskAssessmentCompleted(
            verigate.riskengine.domain.models.RiskAssessment assessment);
    }
}
