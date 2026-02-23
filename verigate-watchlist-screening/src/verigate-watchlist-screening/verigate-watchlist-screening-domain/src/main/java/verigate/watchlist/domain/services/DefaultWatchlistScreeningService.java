/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.services;

import verigate.watchlist.domain.events.WatchlistScreeningEvent;
import verigate.watchlist.domain.factories.CreateWatchlistScreeningFactory;
import verigate.watchlist.domain.models.ProviderResult;
import verigate.watchlist.domain.models.ScreeningRequest;
import verigate.watchlist.domain.models.WatchlistScreeningAggregateRoot;
import verigate.watchlist.domain.repositories.WatchlistScreeningRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of the WatchlistScreeningService interface.
 * Manages watchlist screening operations with persistence via the repository.
 */
public class DefaultWatchlistScreeningService implements WatchlistScreeningService {

    private final WatchlistScreeningRepository repository;
    private final EventFactoryService eventFactory;
    private final CreateWatchlistScreeningFactory screeningFactory;

    public DefaultWatchlistScreeningService(
            WatchlistScreeningRepository repository, 
            EventFactoryService eventFactory,
            CreateWatchlistScreeningFactory screeningFactory) {
        this.repository = repository;
        this.eventFactory = eventFactory;
        this.screeningFactory = screeningFactory;
    }

    @Override
    public WatchlistScreeningAggregateRoot createScreeningJob(String partnerId, ScreeningRequest request) {
        // Create new screening job with a unique ID
        UUID screeningId = UUID.randomUUID();

        WatchlistScreeningAggregateRoot aggregateRoot = screeningFactory.createWatchlistScreening(screeningId, partnerId, request);

        // Persist and return
        return repository.addOrUpdate(aggregateRoot);
    }

    @Override
    public List<WatchlistScreeningEvent> processProviderResult(UUID screeningId, ProviderResult result) {
        // Get the existing screening job
        WatchlistScreeningAggregateRoot aggregateRoot = repository.get(screeningId)
            .orElseThrow(() -> new IllegalArgumentException("Screening job not found: " + screeningId));
        
        // Process the result (this updates the aggregate's state)
        aggregateRoot.addProviderResult(result);
        
        // Persist the updated state
        repository.addOrUpdate(aggregateRoot);
        
        // Create and return appropriate events based on the updated state
        return createEventsFromAggregate(aggregateRoot);
    }

    @Override
    public List<WatchlistScreeningEvent> reEvaluateScreening(UUID screeningId) {
        // Get the existing screening job
        WatchlistScreeningAggregateRoot aggregateRoot = repository.get(screeningId)
            .orElseThrow(() -> new IllegalArgumentException("Screening job not found: " + screeningId));
        
        // Force re-evaluation
        aggregateRoot.evaluateResults();
        
        // Persist the updated state
        repository.addOrUpdate(aggregateRoot);
        
        // Create and return appropriate events
        return createEventsFromAggregate(aggregateRoot);
    }

    @Override
    public WatchlistScreeningAggregateRoot getScreeningJob(UUID screeningId) {
        return repository.get(screeningId)
            .orElseThrow(() -> new IllegalArgumentException("Screening job not found: " + screeningId));
    }
    
    /**
     * Helper method to create appropriate domain events based on the current state of the screening job.
     */
    private List<WatchlistScreeningEvent> createEventsFromAggregate(WatchlistScreeningAggregateRoot aggregateRoot) {
        List<WatchlistScreeningEvent> events = new ArrayList<>();
        
        // If there's a decision, create an event based on its type
        if (aggregateRoot.getDecision() != null) {
            events.add(eventFactory.createEvent(
                aggregateRoot.getScreeningId(),
                aggregateRoot.getPartnerId(),
                aggregateRoot.getRequest(),
                aggregateRoot.getDecision()
            ));
        }
        
        return events;
    }
}
