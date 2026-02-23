/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import verigate.watchlist.domain.events.WatchlistMatchConfirmedEvent;
import verigate.watchlist.domain.events.WatchlistScreeningClearedEvent;
import verigate.watchlist.domain.events.WatchlistScreeningEvent;
import verigate.watchlist.domain.factories.CreateWatchlistScreeningFactory;
import verigate.watchlist.domain.models.*;
import verigate.watchlist.domain.repositories.WatchlistScreeningRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WatchlistScreeningServiceImplTest {

    private WatchlistScreeningRepository repositoryMock;
    private EventFactoryService eventFactoryMock;
    private CreateWatchlistScreeningFactory screeningFactoryMock;
    private DefaultWatchlistScreeningService service;
    private UUID screeningId;
    private String partnerId;
    private ScreeningRequest request;
    private WatchlistScreeningAggregateRoot aggregateRoot;

    @BeforeEach
    void setUp() {
        repositoryMock = mock(WatchlistScreeningRepository.class);
        eventFactoryMock = mock(EventFactoryService.class);
        screeningFactoryMock = mock(CreateWatchlistScreeningFactory.class);
        service = new DefaultWatchlistScreeningService(repositoryMock, eventFactoryMock, screeningFactoryMock);
        
        screeningId = UUID.randomUUID();
        partnerId = "TEST_PARTNER";
        request = ScreeningRequest.person("John", "Doe", "1980-01-01", "US");
        aggregateRoot = new WatchlistScreeningAggregateRoot(screeningId, partnerId, request);
    }

    @Test
    void shouldCreateNewScreeningJob() {
        // Setup mock behavior
        when(repositoryMock.addOrUpdate(any(WatchlistScreeningAggregateRoot.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(screeningFactoryMock.createWatchlistScreening(any(UUID.class), eq(partnerId), eq(request)))
            .thenReturn(aggregateRoot);
        
        // Call the method
        WatchlistScreeningAggregateRoot result = service.createScreeningJob(partnerId, request);
        
        // Verify
        assertNotNull(result);
        assertEquals(partnerId, result.getPartnerId());
        assertEquals(request, result.getRequest());
        assertEquals(ScreeningStatus.PENDING, result.getStatus());
        
        // Verify repository and factory were called
        verify(screeningFactoryMock).createWatchlistScreening(any(UUID.class), eq(partnerId), eq(request));
        verify(repositoryMock).addOrUpdate(any(WatchlistScreeningAggregateRoot.class));
    }

    @Test
    void shouldProcessProviderResultAndCreateEvent() {
        // Setup mock behavior
        when(repositoryMock.get(screeningId)).thenReturn(Optional.of(aggregateRoot));
        when(repositoryMock.addOrUpdate(any(WatchlistScreeningAggregateRoot.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Create a high confidence match for the result
        MatchedEntity highMatch = new MatchedEntity(
            "entity123", "John Doe", 0.95, 
            List.of("Sanctions"), List.of("UN"), 
            "High-risk individual", Map.of()
        );
        ProviderResult providerResult = ProviderResult.success("OpenSanctions", List.of(highMatch));
        
        // Mock event factory
        WatchlistMatchConfirmedEvent confirmedEvent = new WatchlistMatchConfirmedEvent(
            screeningId, partnerId, request, highMatch, 0.95, "High confidence match"
        );
        when(eventFactoryMock.createEvent(eq(screeningId), eq(partnerId), eq(request), any(ScreeningDecision.class)))
            .thenReturn(confirmedEvent);
        
        // Call the method
        List<WatchlistScreeningEvent> events = service.processProviderResult(screeningId, providerResult);
        
        // Verify
        assertFalse(events.isEmpty());
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof WatchlistMatchConfirmedEvent);
        
        // Verify repository interactions
        verify(repositoryMock).get(screeningId);
        verify(repositoryMock).addOrUpdate(aggregateRoot);
        
        // Verify state changes
        assertEquals(ScreeningStatus.COMPLETED, aggregateRoot.getStatus());
        assertNotNull(aggregateRoot.getDecision());
    }

    @Test
    void shouldReEvaluateScreening() {
        // Setup aggregate with some provider results already
        MatchedEntity lowMatch = new MatchedEntity(
            "entity456", "John D", 0.65, 
            List.of("PEP"), List.of("Custom"), 
            "Low match", Map.of()
        );
        ProviderResult lowResult = ProviderResult.success("Provider1", List.of(lowMatch));
        aggregateRoot.addProviderResult(lowResult);
        
        // Setup mock behavior
        when(repositoryMock.get(screeningId)).thenReturn(Optional.of(aggregateRoot));
        when(repositoryMock.addOrUpdate(any(WatchlistScreeningAggregateRoot.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Mock event factory
        WatchlistScreeningClearedEvent clearedEvent = new WatchlistScreeningClearedEvent(
            screeningId, partnerId, request, "No significant matches"
        );
        when(eventFactoryMock.createEvent(eq(screeningId), eq(partnerId), eq(request), any(ScreeningDecision.class)))
            .thenReturn(clearedEvent);
        
        // Call the method
        List<WatchlistScreeningEvent> events = service.reEvaluateScreening(screeningId);
        
        // Verify
        assertFalse(events.isEmpty());
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof WatchlistScreeningClearedEvent);
        
        // Verify repository interactions
        verify(repositoryMock).get(screeningId);
        verify(repositoryMock).addOrUpdate(aggregateRoot);
    }

    @Test
    void shouldThrowExceptionWhenScreeningNotFound() {
        // Setup mock behavior
        when(repositoryMock.get(screeningId)).thenReturn(Optional.empty());
        
        // Verify exception is thrown
        assertThrows(IllegalArgumentException.class, () -> service.getScreeningJob(screeningId));
        assertThrows(IllegalArgumentException.class, () -> service.processProviderResult(screeningId, any(ProviderResult.class)));
        assertThrows(IllegalArgumentException.class, () -> service.reEvaluateScreening(screeningId));
    }
}
