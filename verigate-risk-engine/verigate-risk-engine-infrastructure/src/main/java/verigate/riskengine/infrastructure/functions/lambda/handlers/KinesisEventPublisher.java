/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.infrastructure.functions.lambda.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.riskengine.application.handlers.VerificationEventHandler;
import verigate.riskengine.domain.events.RiskAssessmentCompletedEvent;
import verigate.riskengine.domain.models.RiskAssessment;

/**
 * Publishes risk assessment completed events.
 * In production, this would publish to Kinesis/EventBridge.
 * For now, logs the event for downstream consumers.
 */
public class KinesisEventPublisher implements VerificationEventHandler.EventPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(KinesisEventPublisher.class);

    private final ObjectMapper objectMapper;

    public KinesisEventPublisher(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishRiskAssessmentCompleted(RiskAssessment assessment) {
        var event = new RiskAssessmentCompletedEvent(
            UUID.randomUUID(),
            assessment.assessmentId(),
            assessment.verificationId(),
            assessment.partnerId(),
            assessment.compositeScore(),
            assessment.riskTier(),
            assessment.decision(),
            assessment.overrideApplied(),
            assessment.assessedAt()
        );

        try {
            String payload = objectMapper.writeValueAsString(event);
            LOG.info("Publishing RiskAssessmentCompletedEvent: verificationId={}, score={}, decision={}",
                assessment.verificationId(), assessment.compositeScore(), assessment.decision());
            LOG.debug("Event payload: {}", payload);
            // TODO: Publish to Kinesis stream when event bus is configured
        } catch (Exception e) {
            LOG.error("Failed to publish RiskAssessmentCompletedEvent for verificationId={}",
                assessment.verificationId(), e);
        }
    }
}
