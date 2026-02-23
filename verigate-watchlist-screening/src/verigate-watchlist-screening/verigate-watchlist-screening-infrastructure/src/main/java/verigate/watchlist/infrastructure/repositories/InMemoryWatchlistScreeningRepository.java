/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.infrastructure.repositories;

import verigate.watchlist.domain.models.WatchlistScreeningAggregateRoot;
import verigate.watchlist.domain.repositories.WatchlistScreeningRepository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of the WatchlistScreeningRepository interface.
 * This implementation stores the aggregate roots in a ConcurrentHashMap.
 * It's suitable for development and testing purposes.
 */
public class InMemoryWatchlistScreeningRepository implements WatchlistScreeningRepository {
    
    private final Map<UUID, WatchlistScreeningAggregateRoot> screenings = new ConcurrentHashMap<>();
    
    @Override
    public Optional<WatchlistScreeningAggregateRoot> get(UUID screeningId) {
        return Optional.ofNullable(screenings.get(screeningId));
    }
    
    @Override
    public WatchlistScreeningAggregateRoot addOrUpdate(WatchlistScreeningAggregateRoot aggregateRoot) {
        screenings.put(aggregateRoot.getScreeningId(), aggregateRoot);
        return aggregateRoot;
    }
    
    /**
     * Utility method to clear all screenings (useful for testing).
     */
    public void clear() {
        screenings.clear();
    }
}
