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
 * Domain event published when a verification flow fails.
 */
public class VerificationFailedEvent extends VerificationEvent {
    
    private final String verificationRequestId;
    private final VerificationType verificationType;
    private final String failureReason;
    private final int retryCount;
    
    public VerificationFailedEvent(UUID verificationId, String partnerId, String verificationRequestId,
                                 VerificationType verificationType, String failureReason, int retryCount) {
        super(verificationId, partnerId);
        this.verificationRequestId = verificationRequestId;
        this.verificationType = verificationType;
        this.failureReason = failureReason;
        this.retryCount = retryCount;
    }
    
    @Override
    public String getEventType() {
        return "VerificationFailed";
    }
    
    public String getVerificationRequestId() { return verificationRequestId; }
    public VerificationType getVerificationType() { return verificationType; }
    public String getFailureReason() { return failureReason; }
    public int getRetryCount() { return retryCount; }
}