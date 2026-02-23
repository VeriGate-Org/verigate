/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.application.handlers;

import domain.commands.CommandHandler;
import domain.events.EventPublisher;
import verigate.verification.domain.commands.incoming.InitiateVerificationCommand;
import verigate.verification.domain.events.VerificationInitiatedEvent;
import verigate.verification.domain.models.VerificationAggregateRoot;
import verigate.verification.domain.repositories.VerificationRepository;
import verigate.verification.domain.services.VerificationOrchestrationService;

import javax.inject.Inject;
import java.time.Instant;
import java.util.UUID;

/**
 * Command handler for initiating verification flows.
 */
public class InitiateVerificationCommandHandler implements CommandHandler<InitiateVerificationCommand, Void> {
    
    private final VerificationRepository verificationRepository;
    private final VerificationOrchestrationService orchestrationService;
    private final EventPublisher<Object> eventPublisher;
    
    @Inject
    public InitiateVerificationCommandHandler(VerificationRepository verificationRepository,
                                            VerificationOrchestrationService orchestrationService,
                                            EventPublisher<Object> eventPublisher) {
        this.verificationRepository = verificationRepository;
        this.orchestrationService = orchestrationService;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public Void handle(InitiateVerificationCommand command) {
        // Create verification aggregate
        UUID verificationId = UUID.randomUUID();
        VerificationAggregateRoot verification = new VerificationAggregateRoot(
            verificationId,
            command.getPartnerId(),
            command.getVerificationRequestId(),
            command.getVerificationType()
        );
        
        // Start the verification flow
        verification.start();
        
        // Save the verification
        verificationRepository.addOrUpdate(verification);

        // Publish domain event
        VerificationInitiatedEvent event = new VerificationInitiatedEvent(
            verificationId,
            command.getPartnerId(),
            command.getVerificationRequestId(),
            command.getVerificationType()
        );
        eventPublisher.publish(event);
        
        // Start orchestration
        orchestrationService.startVerification(verification, command.getMetadata());
        
        return null;
    }
}