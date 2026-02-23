/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.services;

import verigate.watchlist.domain.events.WatchlistScreeningEvent;
import verigate.watchlist.domain.models.ProviderResult;
import verigate.watchlist.domain.models.WatchlistScreeningAggregateRoot;
import verigate.watchlist.domain.models.ScreeningRequest;

import java.util.List;
import java.util.UUID;

/**
 * Core domain service for managing watchlist screening aggregate roots.
 */
public interface WatchlistScreeningService {
    
    /**
     * Creates a new watchlist screening aggregate root.
     */
    WatchlistScreeningAggregateRoot createScreeningJob(String partnerId, ScreeningRequest request);
    
    /**
     * Processes raw results from a provider and updates the screening aggregate.
     */
    List<WatchlistScreeningEvent> processProviderResult(UUID screeningId, ProviderResult result);
    
    /**
     * Re-evaluates a screening aggregate, optionally with updated configuration.
     */
    List<WatchlistScreeningEvent> reEvaluateScreening(UUID screeningId);
    
    /**
     * Gets the current status of a screening aggregate.
     */
    WatchlistScreeningAggregateRoot getScreeningJob(UUID screeningId);
}