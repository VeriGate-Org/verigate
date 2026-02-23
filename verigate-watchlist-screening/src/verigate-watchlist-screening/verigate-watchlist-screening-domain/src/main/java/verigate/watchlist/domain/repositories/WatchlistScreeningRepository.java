/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.repositories;

import verigate.watchlist.domain.models.WatchlistScreeningAggregateRoot;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for WatchlistScreeningAggregateRoot persistence operations.
 * Following the repository pattern from Domain-Driven Design, this interface
 * provides an abstraction layer over the actual data storage.
 */
public interface WatchlistScreeningRepository {
    
    /**
     * Retrieves a watchlist screening aggregate root by its unique identifier.
     *
     * @param screeningId The unique identifier of the watchlist screening
     * @return Optional containing the WatchlistScreeningAggregateRoot if found, or empty Optional if not found
     */
    Optional<WatchlistScreeningAggregateRoot> get(UUID screeningId);
    
    /**
     * Adds a new watchlist screening aggregate root or updates an existing one.
     * This method ensures the aggregate root's state is persisted in the underlying data store.
     *
     * @param aggregateRoot The watchlist screening aggregate root to be persisted
     * @return The persisted watchlist screening aggregate root (which may have updated metadata such as timestamps)
     */
    WatchlistScreeningAggregateRoot addOrUpdate(WatchlistScreeningAggregateRoot aggregateRoot);
}
