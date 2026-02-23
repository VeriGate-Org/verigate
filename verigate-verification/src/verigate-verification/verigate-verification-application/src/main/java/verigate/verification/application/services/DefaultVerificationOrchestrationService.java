/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.application.services;

import domain.commands.CommandDispatcher;
import domain.events.EventPublisher;
import verigate.verification.domain.commands.outgoing.CreateWatchlistScreeningCommand;
import verigate.verification.domain.commands.outgoing.VerifyBankAccountCommand;
import verigate.verification.domain.commands.outgoing.VerifyIdentityCommand;
import verigate.verification.domain.events.VerificationStepStartedEvent;
import verigate.verification.domain.models.VerificationAggregateRoot;
import verigate.verification.domain.models.VerificationResult;
import verigate.verification.domain.models.VerificationStep;
import verigate.verification.domain.repositories.VerificationRepository;
import verigate.verification.domain.services.VerificationOrchestrationService;
import javax.inject.Inject;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Default implementation of verification orchestration service.
 */
public class DefaultVerificationOrchestrationService implements VerificationOrchestrationService {
    
    private final VerificationRepository verificationRepository;
    private final CommandDispatcher<Object> commandDispatcher;
    private final EventPublisher<Object> eventPublisher;
    
    @Inject
    public DefaultVerificationOrchestrationService(VerificationRepository verificationRepository,
                                                 CommandDispatcher<Object> commandDispatcher,
                                                 EventPublisher<Object> eventPublisher) {
        this.verificationRepository = verificationRepository;
        this.commandDispatcher = commandDispatcher;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public void startVerification(VerificationAggregateRoot verification, Map<String, Object> metadata) {
        // Get the first step and dispatch appropriate command
        if (!verification.getSteps().isEmpty()) {
            VerificationStep firstStep = verification.getSteps().get(0);
            dispatchStepCommand(verification, firstStep, metadata);
        }
    }
    
    @Override
    public void retryVerification(VerificationAggregateRoot verification) {
        // For retry, we start with the first step again
        startVerification(verification, new HashMap<>());
    }
    
    @Override
    public void processStepCompletion(UUID verificationId, UUID stepId, boolean success, String details) {
        VerificationAggregateRoot verification = verificationRepository.get(verificationId);
        if (verification == null) {
            throw new IllegalArgumentException("Verification not found: " + verificationId);
        }
        
        if (success) {
            verification.completeStep(stepId, VerificationResult.PASSED);
        } else {
            verification.failStep(stepId, details);
        }

        verificationRepository.addOrUpdate(verification);

        // If verification is still in progress, start next step
        if (verification.getStatus().toString().equals("IN_PROGRESS")) {
            startNextStep(verification, stepId);
        }
    }
    
    @Override
    public void cancelVerification(UUID verificationId, String reason) {
        VerificationAggregateRoot verification = verificationRepository.get(verificationId);
        if (verification == null) {
            throw new IllegalArgumentException("Verification not found: " + verificationId);
        }
        
        verification.cancel();
        verificationRepository.addOrUpdate(verification);
    }
    
    private void startNextStep(VerificationAggregateRoot verification, UUID completedStepId) {
        // Find the completed step and start the next one
        for (int i = 0; i < verification.getSteps().size(); i++) {
            if (verification.getSteps().get(i).getStepId().equals(completedStepId)) {
                if (i + 1 < verification.getSteps().size()) {
                    VerificationStep nextStep = verification.getSteps().get(i + 1);
                    dispatchStepCommand(verification, nextStep, new HashMap<>());
                }
                break;
            }
        }
    }
    
    private void dispatchStepCommand(VerificationAggregateRoot verification, VerificationStep step, Map<String, Object> metadata) {
        // Publish step started event
        VerificationStepStartedEvent stepStartedEvent = new VerificationStepStartedEvent(
            verification.getVerificationId(),
            step.getStepId(),
            verification.getPartnerId(),
            step.getStepType()
        );
        eventPublisher.publish(stepStartedEvent);
        
        // Dispatch appropriate command based on step type
        switch (step.getStepType()) {
            case BANK_ACCOUNT_VERIFICATION:
                dispatchBankAccountVerificationCommand(verification, step, metadata);
                break;
            case IDENTITY_VERIFICATION:
                dispatchIdentityVerificationCommand(verification, step, metadata);
                break;
            case WATCHLIST_SCREENING:
                dispatchWatchlistScreeningCommand(verification, step, metadata);
                break;
            default:
                throw new IllegalArgumentException("Unsupported step type: " + step.getStepType());
        }
    }
    
    private void dispatchBankAccountVerificationCommand(VerificationAggregateRoot verification, VerificationStep step, Map<String, Object> metadata) {
        VerifyBankAccountCommand command = new VerifyBankAccountCommand(
            UUID.randomUUID(),
            Instant.now(),
            "verification-service",
            verification.getVerificationId(),
            step.getStepId(),
            verification.getPartnerId(),
            getStringFromMetadata(metadata, "accountNumber"),
            getStringFromMetadata(metadata, "routingNumber"),
            getStringFromMetadata(metadata, "accountHolderName"),
            metadata
        );
        commandDispatcher.dispatch(command);
    }
    
    private void dispatchIdentityVerificationCommand(VerificationAggregateRoot verification, VerificationStep step, Map<String, Object> metadata) {
        VerifyIdentityCommand command = new VerifyIdentityCommand(
            UUID.randomUUID(),
            Instant.now(),
            "verification-service",
            verification.getVerificationId(),
            step.getStepId(),
            verification.getPartnerId(),
            getStringFromMetadata(metadata, "firstName"),
            getStringFromMetadata(metadata, "lastName"),
            getStringFromMetadata(metadata, "dateOfBirth"),
            getStringFromMetadata(metadata, "idNumber"),
            getStringFromMetadata(metadata, "countryOfResidence"),
            metadata
        );
        commandDispatcher.dispatch(command);
    }
    
    private void dispatchWatchlistScreeningCommand(VerificationAggregateRoot verification, VerificationStep step, Map<String, Object> metadata) {
        CreateWatchlistScreeningCommand command = new CreateWatchlistScreeningCommand(
            UUID.randomUUID(),
            Instant.now(),
            "verification-service",
            verification.getVerificationId(),
            step.getStepId(),
            verification.getPartnerId(),
            getStringFromMetadata(metadata, "firstName"),
            getStringFromMetadata(metadata, "lastName"),
            getStringFromMetadata(metadata, "dateOfBirth"),
            getStringFromMetadata(metadata, "countryOfResidence"),
            getStringFromMetadata(metadata, "entityType", "PERSON"),
            metadata
        );
        commandDispatcher.dispatch(command);
    }
    
    private String getStringFromMetadata(Map<String, Object> metadata, String key) {
        return getStringFromMetadata(metadata, key, null);
    }
    
    private String getStringFromMetadata(Map<String, Object> metadata, String key, String defaultValue) {
        Object value = metadata.get(key);
        return value != null ? value.toString() : defaultValue;
    }
}