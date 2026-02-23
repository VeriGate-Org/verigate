/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.reactor.application.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.verification.domain.events.VerificationStepCompletedEvent;
import verigate.verification.domain.models.VerificationResult;
import verigate.verification.domain.services.VerificationOrchestrationService;
import verigate.verification.reactor.domain.handlers.VerificationStepCompletedEventHandler;

public class DefaultVerificationStepCompletedEventHandler
        implements VerificationStepCompletedEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(
        DefaultVerificationStepCompletedEventHandler.class);

    private final VerificationOrchestrationService orchestrationService;

    public DefaultVerificationStepCompletedEventHandler(
            VerificationOrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    @Override
    public void handle(VerificationStepCompletedEvent event) {
        logger.info("Processing step completed event for verification: {}, step: {}",
            event.getVerificationId(), event.getStepId());

        VerificationResult result = event.getResult();
        boolean success = result == VerificationResult.PASSED;
        String details = success ? "" : result.name();

        orchestrationService.processStepCompletion(
            event.getVerificationId(),
            event.getStepId(),
            success,
            details
        );

        logger.info("Step completion processed for verification: {}", event.getVerificationId());
    }
}
