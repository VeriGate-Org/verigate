/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.domain.services;

import verigate.verification.domain.models.VerificationAggregateRoot;

import java.util.Map;
import java.util.UUID;

/**
 * Domain service for orchestrating verification flows.
 */
public interface VerificationOrchestrationService {
    
    /**
     * Starts a verification flow by dispatching commands for the first step.
     */
    void startVerification(VerificationAggregateRoot verification, Map<String, Object> metadata);
    
    /**
     * Retries a failed verification flow.
     */
    void retryVerification(VerificationAggregateRoot verification);
    
    /**
     * Processes the completion of a verification step and starts the next step if needed.
     */
    void processStepCompletion(UUID verificationId, UUID stepId, boolean success, String details);
    
    /**
     * Cancels an in-progress verification flow.
     */
    void cancelVerification(UUID verificationId, String reason);
}