/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.domain.events;

import verigate.verification.domain.models.VerificationResult;
import verigate.verification.domain.models.VerificationType;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain event published when a verification flow is completed.
 */
public class VerificationCompletedEvent extends VerificationEvent {
    
    private final String verificationRequestId;
    private final VerificationType verificationType;
    private final VerificationResult result;
    
    public VerificationCompletedEvent(UUID verificationId, String partnerId, String verificationRequestId,
                                    VerificationType verificationType, VerificationResult result) {
        super(verificationId, partnerId);
        this.verificationRequestId = verificationRequestId;
        this.verificationType = verificationType;
        this.result = result;
    }
    
    @Override
    public String getEventType() {
        return "VerificationCompleted";
    }
    
    public String getVerificationRequestId() { return verificationRequestId; }
    public VerificationType getVerificationType() { return verificationType; }
    public VerificationResult getResult() { return result; }
}