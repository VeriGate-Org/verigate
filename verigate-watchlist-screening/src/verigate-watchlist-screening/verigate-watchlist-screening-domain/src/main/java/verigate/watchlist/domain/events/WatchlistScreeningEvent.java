/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Base class for all watchlist screening domain events.
 */
public abstract class WatchlistScreeningEvent {
    
    protected final UUID eventId;
    protected final UUID screeningId;
    protected final String partnerId;
    protected final Instant occurredAt;
    
    protected WatchlistScreeningEvent(UUID screeningId, String partnerId) {
        this.eventId = UUID.randomUUID();
        this.screeningId = screeningId;
        this.partnerId = partnerId;
        this.occurredAt = Instant.now();
    }
    
    // Getters
    public UUID getEventId() { return eventId; }
    public UUID getScreeningId() { return screeningId; }
    public String getPartnerId() { return partnerId; }
    public Instant getOccurredAt() { return occurredAt; }
    
    /**
     * Gets the event type for routing and processing.
     */
    public abstract String getEventType();
}