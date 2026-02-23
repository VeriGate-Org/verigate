/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.domain.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Base class for all verification domain events.
 */
public abstract class VerificationEvent {
    
    protected final UUID eventId;
    protected final UUID verificationId;
    protected final String partnerId;
    protected final Instant occurredAt;
    
    protected VerificationEvent(UUID verificationId, String partnerId) {
        this.eventId = UUID.randomUUID();
        this.verificationId = verificationId;
        this.partnerId = partnerId;
        this.occurredAt = Instant.now();
    }
    
    // Getters
    public UUID getEventId() { return eventId; }
    public UUID getVerificationId() { return verificationId; }
    public String getPartnerId() { return partnerId; }
    public Instant getOccurredAt() { return occurredAt; }
    
    /**
     * Gets the event type for routing and processing.
     */
    public abstract String getEventType();
}