/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.events;

import verigate.watchlist.domain.models.ScreeningRequest;

import java.util.UUID;

/**
 * Event emitted when watchlist screening encounters non-recoverable failure.
 */
public class WatchlistScreeningHardFailedEvent extends WatchlistScreeningEvent {
    
    private final ScreeningRequest request;
    private final String failureReason;
    private final String errorCode;
    
    public WatchlistScreeningHardFailedEvent(UUID screeningId, String partnerId, 
                                           ScreeningRequest request, String failureReason, 
                                           String errorCode) {
        super(screeningId, partnerId);
        this.request = request;
        this.failureReason = failureReason;
        this.errorCode = errorCode;
    }
    
    @Override
    public String getEventType() {
        return "WatchlistScreeningHardFailed";
    }
    
    // Getters
    public ScreeningRequest getRequest() { return request; }
    public String getFailureReason() { return failureReason; }
    public String getErrorCode() { return errorCode; }
}