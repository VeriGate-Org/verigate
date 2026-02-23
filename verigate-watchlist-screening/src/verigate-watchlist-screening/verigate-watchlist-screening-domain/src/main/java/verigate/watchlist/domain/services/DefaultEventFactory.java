/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.services;

import verigate.watchlist.domain.events.*;
import verigate.watchlist.domain.models.MatchedEntity;
import verigate.watchlist.domain.models.ScreeningDecision;
import verigate.watchlist.domain.models.ScreeningRequest;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of event factory service for creating domain events based on screening decisions.
 */
public class DefaultEventFactory implements EventFactoryService {
    
    /**
     * Creates appropriate domain event based on screening decision.
     */
    @Override
    public WatchlistScreeningEvent createEvent(UUID screeningId, String partnerId, 
                                             ScreeningRequest request, ScreeningDecision decision) {
        return switch (decision.getType()) {
            case CLEARED -> new WatchlistScreeningClearedEvent(
                screeningId, partnerId, request, decision.getReason()
            );
            
            case MATCH_CONFIRMED -> new WatchlistMatchConfirmedEvent(
                screeningId, partnerId, request, decision.getHighestMatch(),
                decision.getConfidenceScore(), decision.getReason()
            );
            
            case REVIEW_REQUIRED -> new WatchlistMatchReviewRequiredEvent(
                screeningId, partnerId, request, 
                List.of(decision.getHighestMatch()), // Convert single match to list
                decision.getConfidenceScore(), decision.getReason()
            );
            
            case SOFT_FAILED -> new WatchlistScreeningSoftFailedEvent(
                screeningId, partnerId, request, decision.getReason(), true // Soft fails are retryable
            );
            
            case SYSTEM_OUTAGE -> new WatchlistScreeningHardFailedEvent(
                screeningId, partnerId, request, decision.getReason(), "SYSTEM_OUTAGE"
            );
        };
    }
    
    /**
     * Creates review required event with multiple matches.
     */
    @Override
    public WatchlistMatchReviewRequiredEvent createReviewEvent(UUID screeningId, String partnerId,
                                                             ScreeningRequest request, 
                                                             List<MatchedEntity> matches,
                                                             String reason) {
        double highestScore = matches.stream()
            .mapToDouble(MatchedEntity::getConfidenceScore)
            .max()
            .orElse(0.0);
        
        return new WatchlistMatchReviewRequiredEvent(
            screeningId, partnerId, request, matches, highestScore, reason
        );
    }
}