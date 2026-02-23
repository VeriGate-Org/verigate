/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.application.handlers;

import domain.commands.CommandHandler;
import domain.events.EventPublisher;
import verigate.verification.domain.commands.incoming.RetryVerificationCommand;
import verigate.verification.domain.events.VerificationRetryScheduledEvent;
import verigate.verification.domain.models.VerificationAggregateRoot;
import verigate.verification.domain.repositories.VerificationRepository;
import verigate.verification.domain.services.VerificationOrchestrationService;

import javax.inject.Inject;
import java.time.Instant;
import java.util.UUID;

/**
 * Command handler for retrying verification flows.
 */
public class RetryVerificationCommandHandler implements CommandHandler<RetryVerificationCommand, Void> {
    
    private final VerificationRepository verificationRepository;
    private final VerificationOrchestrationService orchestrationService;
    private final EventPublisher<Object> eventPublisher;
    
    @Inject
    public RetryVerificationCommandHandler(VerificationRepository verificationRepository,
                                         VerificationOrchestrationService orchestrationService,
                                         EventPublisher<Object> eventPublisher) {
        this.verificationRepository = verificationRepository;
        this.orchestrationService = orchestrationService;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public Void handle(RetryVerificationCommand command) {
        // Load verification aggregate
        VerificationAggregateRoot verification = verificationRepository.get(command.getVerificationId());
        if (verification == null) {
            throw new IllegalArgumentException("Verification not found: " + command.getVerificationId());
        }
        
        // Retry the verification
        verification.retry();
        
        // Save the updated verification
        verificationRepository.addOrUpdate(verification);
        
        // Publish retry event
        VerificationRetryScheduledEvent event = new VerificationRetryScheduledEvent(
            verification.getVerificationId(),
            verification.getPartnerId(),
            verification.getVerificationRequestId(),
            verification.getVerificationType(),
            command.getReason(),
            verification.getRetryCount(),
            Instant.now()
        );
        eventPublisher.publish(event);
        
        // Restart orchestration
        orchestrationService.retryVerification(verification);
        
        return null;
    }
}