/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.events;

import verigate.watchlist.domain.models.ScreeningRequest;

import java.util.UUID;

/**
 * Event emitted when watchlist screening passes with no significant matches.
 */
public class WatchlistScreeningClearedEvent extends WatchlistScreeningEvent {
    
    private final ScreeningRequest request;
    private final String clearanceReason;
    
    public WatchlistScreeningClearedEvent(UUID screeningId, String partnerId, 
                                        ScreeningRequest request, String clearanceReason) {
        super(screeningId, partnerId);
        this.request = request;
        this.clearanceReason = clearanceReason;
    }
    
    @Override
    public String getEventType() {
        return "WatchlistScreeningCleared";
    }
    
    // Getters
    public ScreeningRequest getRequest() { return request; }
    public String getClearanceReason() { return clearanceReason; }
}