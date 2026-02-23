/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.domain.events;

import verigate.verification.domain.models.VerificationResult;
import verigate.verification.domain.models.VerificationStepType;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event published when a verification step is completed.
 */
public class VerificationStepCompletedEvent extends VerificationEvent {
    
    private final UUID stepId;
    private final VerificationStepType stepType;
    private final VerificationResult result;
    
    public VerificationStepCompletedEvent(UUID verificationId, UUID stepId, String partnerId,
                                        VerificationStepType stepType, VerificationResult result) {
        super(verificationId, partnerId);
        this.stepId = stepId;
        this.stepType = stepType;
        this.result = result;
    }
    
    @Override
    public String getEventType() {
        return "VerificationStepCompleted";
    }
    
    public UUID getStepId() { return stepId; }
    public VerificationStepType getStepType() { return stepType; }
    public VerificationResult getResult() { return result; }
}