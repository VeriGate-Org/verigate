/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.services;

import verigate.watchlist.domain.events.WatchlistMatchReviewRequiredEvent;
import verigate.watchlist.domain.events.WatchlistScreeningEvent;
import verigate.watchlist.domain.models.MatchedEntity;
import verigate.watchlist.domain.models.ScreeningDecision;
import verigate.watchlist.domain.models.ScreeningRequest;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for creating domain events based on screening decisions.
 */
public interface EventFactoryService {
    
    /**
     * Creates appropriate domain event based on screening decision.
     */
    WatchlistScreeningEvent createEvent(UUID screeningId, String partnerId, 
                                       ScreeningRequest request, ScreeningDecision decision);
    
    /**
     * Creates review required event with multiple matches.
     */
    WatchlistMatchReviewRequiredEvent createReviewEvent(UUID screeningId, String partnerId,
                                                       ScreeningRequest request, 
                                                       List<MatchedEntity> matches,
                                                       String reason);
}