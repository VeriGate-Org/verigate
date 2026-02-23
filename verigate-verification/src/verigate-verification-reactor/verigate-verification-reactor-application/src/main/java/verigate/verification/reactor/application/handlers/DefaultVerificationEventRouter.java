/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.reactor.application.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.verification.domain.events.VerificationStepCompletedEvent;
import verigate.verification.domain.models.VerificationResult;
import verigate.verification.domain.models.VerificationStepType;
import verigate.verification.reactor.domain.handlers.VerificationEventRouter;
import verigate.verification.reactor.domain.handlers.VerificationStepCompletedEventHandler;
import verigate.verification.reactor.domain.handlers.VerificationStepFailedEventHandler;

import java.util.UUID;

public class DefaultVerificationEventRouter implements VerificationEventRouter {

    private static final Logger logger = LoggerFactory.getLogger(
        DefaultVerificationEventRouter.class);

    private final VerificationStepCompletedEventHandler stepCompletedHandler;
    private final VerificationStepFailedEventHandler stepFailedHandler;
    private final ObjectMapper objectMapper;

    public DefaultVerificationEventRouter(
            VerificationStepCompletedEventHandler stepCompletedHandler,
            VerificationStepFailedEventHandler stepFailedHandler,
            ObjectMapper objectMapper) {
        this.stepCompletedHandler = stepCompletedHandler;
        this.stepFailedHandler = stepFailedHandler;
        this.objectMapper = objectMapper;
    }

    @Override
    public void routeEvent(String eventType, String eventPayload) {
        logger.info("Routing event of type: {}", eventType);

        try {
            switch (eventType) {
                case "VerificationStepCompleted" -> {
                    VerificationStepCompletedEvent event = objectMapper.readValue(
                        eventPayload, VerificationStepCompletedEvent.class);
                    stepCompletedHandler.handle(event);
                }
                case "VerificationStepFailed" -> {
                    JsonNode node = objectMapper.readTree(eventPayload);
                    UUID verificationId = UUID.fromString(node.get("verificationId").asText());
                    UUID stepId = UUID.fromString(node.get("stepId").asText());
                    String reason = node.has("failureReason")
                        ? node.get("failureReason").asText() : "Unknown failure";
                    stepFailedHandler.handle(verificationId, stepId, reason);
                }
                case "VerificationCompleted", "VerificationFailed" -> {
                    logger.info("Received terminal verification event: {}, no action needed",
                        eventType);
                }
                default -> logger.warn("Unknown event type: {}, ignoring", eventType);
            }
        } catch (Exception e) {
            logger.error("Failed to process event of type: {}", eventType, e);
            throw new RuntimeException("Failed to process verification event", e);
        }
    }
}
