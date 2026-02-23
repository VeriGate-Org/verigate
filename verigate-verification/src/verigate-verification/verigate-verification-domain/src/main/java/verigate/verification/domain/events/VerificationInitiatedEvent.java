/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.domain.events;

import verigate.verification.domain.models.VerificationType;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event published when a verification flow is initiated.
 */
public class VerificationInitiatedEvent extends VerificationEvent {
    
    private final String verificationRequestId;
    private final VerificationType verificationType;
    
    public VerificationInitiatedEvent(UUID verificationId, String partnerId, String verificationRequestId,
                                    VerificationType verificationType) {
        super(verificationId, partnerId);
        this.verificationRequestId = verificationRequestId;
        this.verificationType = verificationType;
    }
    
    @Override
    public String getEventType() {
        return "VerificationInitiated";
    }
    
    public String getVerificationRequestId() { return verificationRequestId; }
    public VerificationType getVerificationType() { return verificationType; }
}