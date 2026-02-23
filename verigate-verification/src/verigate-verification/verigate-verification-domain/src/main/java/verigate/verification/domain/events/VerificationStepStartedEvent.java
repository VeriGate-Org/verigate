/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.domain.events;

import verigate.verification.domain.models.VerificationStepType;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event published when a verification step is started.
 */
public class VerificationStepStartedEvent extends VerificationEvent {
    
    private final UUID stepId;
    private final VerificationStepType stepType;
    
    public VerificationStepStartedEvent(UUID verificationId, UUID stepId, String partnerId,
                                      VerificationStepType stepType) {
        super(verificationId, partnerId);
        this.stepId = stepId;
        this.stepType = stepType;
    }
    
    @Override
    public String getEventType() {
        return "VerificationStepStarted";
    }
    
    public UUID getStepId() { return stepId; }
    public VerificationStepType getStepType() { return stepType; }
}