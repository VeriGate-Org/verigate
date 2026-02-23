/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.reactor.application.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.verification.domain.services.VerificationOrchestrationService;
import verigate.verification.reactor.domain.handlers.VerificationStepFailedEventHandler;

import java.util.UUID;

public class DefaultVerificationStepFailedEventHandler
        implements VerificationStepFailedEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(
        DefaultVerificationStepFailedEventHandler.class);

    private final VerificationOrchestrationService orchestrationService;

    public DefaultVerificationStepFailedEventHandler(
            VerificationOrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    @Override
    public void handle(UUID verificationId, UUID stepId, String failureReason) {
        logger.info("Processing step failure for verification: {}, step: {}, reason: {}",
            verificationId, stepId, failureReason);

        orchestrationService.processStepCompletion(
            verificationId,
            stepId,
            false,
            failureReason
        );

        logger.info("Step failure processed for verification: {}", verificationId);
    }
}
