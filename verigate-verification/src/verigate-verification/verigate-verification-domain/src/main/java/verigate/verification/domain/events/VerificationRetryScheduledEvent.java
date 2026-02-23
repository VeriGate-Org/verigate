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
 * Domain event published when a verification retry is scheduled.
 */
public class VerificationRetryScheduledEvent extends VerificationEvent {
    
    private final String verificationRequestId;
    private final VerificationType verificationType;
    private final String reason;
    private final int retryCount;
    private final Instant scheduledFor;
    
    public VerificationRetryScheduledEvent(UUID verificationId, String partnerId, String verificationRequestId,
                                         VerificationType verificationType, String reason, int retryCount,
                                         Instant scheduledFor) {
        super(verificationId, partnerId);
        this.verificationRequestId = verificationRequestId;
        this.verificationType = verificationType;
        this.reason = reason;
        this.retryCount = retryCount;
        this.scheduledFor = scheduledFor;
    }
    
    @Override
    public String getEventType() {
        return "VerificationRetryScheduled";
    }
    
    public String getVerificationRequestId() { return verificationRequestId; }
    public VerificationType getVerificationType() { return verificationType; }
    public String getReason() { return reason; }
    public int getRetryCount() { return retryCount; }
    public Instant getScheduledFor() { return scheduledFor; }
}