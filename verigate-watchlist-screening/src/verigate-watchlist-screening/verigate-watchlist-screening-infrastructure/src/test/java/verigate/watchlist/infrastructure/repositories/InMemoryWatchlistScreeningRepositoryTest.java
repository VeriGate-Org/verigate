/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.infrastructure.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import verigate.watchlist.domain.models.ProviderResult;
import verigate.watchlist.domain.models.ScreeningRequest;
import verigate.watchlist.domain.models.WatchlistScreeningAggregateRoot;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryWatchlistScreeningRepositoryTest {

    private InMemoryWatchlistScreeningRepository repository;
    private UUID screeningId;
    private String partnerId;
    private WatchlistScreeningAggregateRoot screeningRoot;

    @BeforeEach
    void setUp() {
        repository = new InMemoryWatchlistScreeningRepository();
        screeningId = UUID.randomUUID();
        partnerId = "TEST_PARTNER";
        ScreeningRequest request = ScreeningRequest.person("John", "Doe", "1980-01-01", "US");
        screeningRoot = new WatchlistScreeningAggregateRoot(screeningId, partnerId, request);
    }

    @Test
    void shouldReturnEmptyOptionalWhenScreeningNotFound() {
        Optional<WatchlistScreeningAggregateRoot> result = repository.get(UUID.randomUUID());
        
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldAddAndRetrieveScreening() {
        // Add the screening
        WatchlistScreeningAggregateRoot added = repository.addOrUpdate(screeningRoot);
        
        // Verify it was added correctly
        assertEquals(screeningRoot, added);
        
        // Retrieve and verify
        Optional<WatchlistScreeningAggregateRoot> retrieved = repository.get(screeningId);
        
        assertTrue(retrieved.isPresent());
        assertEquals(screeningId, retrieved.get().getScreeningId());
        assertEquals(partnerId, retrieved.get().getPartnerId());
    }

    @Test
    void shouldUpdateExistingScreening() {
        // Add initial version
        repository.addOrUpdate(screeningRoot);
        
        // Add a provider result to make evaluateResults work correctly
        ProviderResult providerResult = ProviderResult.success("TEST_PROVIDER", List.of());
        screeningRoot.addProviderResult(providerResult);
        
        // Modify (would normally happen via methods on the aggregate)
        screeningRoot.evaluateResults();
        
        // Log the state for debugging
        System.out.println("Before addOrUpdate - isCompleted: " + screeningRoot.isCompleted());
        
        // Update
        WatchlistScreeningAggregateRoot updated = repository.addOrUpdate(screeningRoot);
        
        // Log the state for debugging
        System.out.println("After addOrUpdate - updated.isCompleted: " + updated.isCompleted());
        
        // Verify status directly on the returned object first
        assertTrue(updated.isCompleted(), "Updated object should be completed");
        
        // Retrieve and verify update was applied
        Optional<WatchlistScreeningAggregateRoot> retrieved = repository.get(screeningId);
        
        assertTrue(retrieved.isPresent(), "Retrieved object should be present");
        assertTrue(retrieved.get().isCompleted(), "Retrieved object should be completed");
    }

    @Test
    void clearShouldRemoveAllScreenings() {
        // Add a screening
        repository.addOrUpdate(screeningRoot);
        
        // Verify it exists
        assertTrue(repository.get(screeningId).isPresent());
        
        // Clear the repository
        repository.clear();
        
        // Verify it's empty
        assertTrue(repository.get(screeningId).isEmpty());
    }
}
