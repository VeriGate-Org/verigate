/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.events;

import verigate.watchlist.domain.models.ScreeningRequest;

import java.util.UUID;

/**
 * Event emitted when watchlist screening results are ambiguous or retryable.
 */
public class WatchlistScreeningSoftFailedEvent extends WatchlistScreeningEvent {
    
    private final ScreeningRequest request;
    private final String failureReason;
    private final boolean isRetryable;
    
    public WatchlistScreeningSoftFailedEvent(UUID screeningId, String partnerId, 
                                           ScreeningRequest request, String failureReason, 
                                           boolean isRetryable) {
        super(screeningId, partnerId);
        this.request = request;
        this.failureReason = failureReason;
        this.isRetryable = isRetryable;
    }
    
    @Override
    public String getEventType() {
        return "WatchlistScreeningSoftFailed";
    }
    
    // Getters
    public ScreeningRequest getRequest() { return request; }
    public String getFailureReason() { return failureReason; }
    public boolean isRetryable() { return isRetryable; }
}