/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.events;

import verigate.watchlist.domain.models.MatchedEntity;
import verigate.watchlist.domain.models.ScreeningRequest;

import java.util.UUID;

/**
 * Event emitted when a high-confidence watchlist match is found and auto-confirmed.
 */
public class WatchlistMatchConfirmedEvent extends WatchlistScreeningEvent {
    
    private final ScreeningRequest request;
    private final MatchedEntity confirmedMatch;
    private final double confidenceScore;
    private final String matchReason;
    
    public WatchlistMatchConfirmedEvent(UUID screeningId, String partnerId, 
                                      ScreeningRequest request, MatchedEntity confirmedMatch,
                                      double confidenceScore, String matchReason) {
        super(screeningId, partnerId);
        this.request = request;
        this.confirmedMatch = confirmedMatch;
        this.confidenceScore = confidenceScore;
        this.matchReason = matchReason;
    }
    
    @Override
    public String getEventType() {
        return "WatchlistMatchConfirmed";
    }
    
    // Getters
    public ScreeningRequest getRequest() { return request; }
    public MatchedEntity getConfirmedMatch() { return confirmedMatch; }
    public double getConfidenceScore() { return confidenceScore; }
    public String getMatchReason() { return matchReason; }
}