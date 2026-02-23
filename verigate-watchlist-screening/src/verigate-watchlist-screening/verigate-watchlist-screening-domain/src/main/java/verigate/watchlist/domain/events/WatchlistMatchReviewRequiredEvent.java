/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.events;

import verigate.watchlist.domain.models.MatchedEntity;
import verigate.watchlist.domain.models.ScreeningRequest;

import java.util.List;
import java.util.UUID;

/**
 * Event emitted when medium-confidence matches require manual review.
 */
public class WatchlistMatchReviewRequiredEvent extends WatchlistScreeningEvent {
    
    private final ScreeningRequest request;
    private final List<MatchedEntity> matchesForReview;
    private final double highestConfidenceScore;
    private final String reviewReason;
    
    public WatchlistMatchReviewRequiredEvent(UUID screeningId, String partnerId, 
                                           ScreeningRequest request, List<MatchedEntity> matchesForReview,
                                           double highestConfidenceScore, String reviewReason) {
        super(screeningId, partnerId);
        this.request = request;
        this.matchesForReview = matchesForReview;
        this.highestConfidenceScore = highestConfidenceScore;
        this.reviewReason = reviewReason;
    }
    
    @Override
    public String getEventType() {
        return "WatchlistMatchReviewRequired";
    }
    
    // Getters
    public ScreeningRequest getRequest() { return request; }
    public List<MatchedEntity> getMatchesForReview() { return matchesForReview; }
    public double getHighestConfidenceScore() { return highestConfidenceScore; }
    public String getReviewReason() { return reviewReason; }
}