
/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.application.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import verigate.watchlist.domain.commands.CreateWatchlistScreeningCommand;
import verigate.watchlist.domain.factories.CreateWatchlistScreeningFactory;
import verigate.watchlist.domain.models.WatchlistScreeningAggregateRoot;
import verigate.watchlist.domain.repositories.WatchlistScreeningRepository;
import verigate.watchlist.domain.services.WatchlistScreeningService;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DefaultCreateWatchlistScreeningCommandHandler.
 * Verifies that the handler correctly creates and persists watchlist screenings.
 */
class DefaultCreateWatchlistScreeningCommandHandlerTest {

    private WatchlistScreeningService screeningService;
    private WatchlistScreeningRepository screeningRepository;
    private CreateWatchlistScreeningFactory screeningFactory;
    private DefaultCreateWatchlistScreeningCommandHandler handler;

    @BeforeEach
    void setUp() {
        screeningService = mock(WatchlistScreeningService.class);
        screeningRepository = mock(WatchlistScreeningRepository.class);
        screeningFactory = mock(CreateWatchlistScreeningFactory.class);
        handler = new DefaultCreateWatchlistScreeningCommandHandler(
                screeningService, screeningRepository, screeningFactory
        );
    }

    @Test
    void shouldCreateScreeningAndPersistIt() {
        CreateWatchlistScreeningCommand command = mock(CreateWatchlistScreeningCommand.class);
        WatchlistScreeningAggregateRoot screening = mock(WatchlistScreeningAggregateRoot.class);

        when(screeningFactory.create(command)).thenReturn(screening);

        WatchlistScreeningAggregateRoot result = handler.handle(command);

        assertSame(screening, result);

        // Verify factory is called
        verify(screeningFactory, times(1)).create(command);

        // Verify repository is called with the created screening
        verify(screeningRepository, times(1)).addOrUpdate(screening);
    }

    @Test
    void shouldReturnScreeningFromFactory() {
        CreateWatchlistScreeningCommand command = mock(CreateWatchlistScreeningCommand.class);
        WatchlistScreeningAggregateRoot expectedScreening = mock(WatchlistScreeningAggregateRoot.class);

        when(screeningFactory.create(command)).thenReturn(expectedScreening);

        WatchlistScreeningAggregateRoot actual = handler.handle(command);

        assertEquals(expectedScreening, actual);
    }

    @Test
    void shouldNotCallServiceDirectly() {
        CreateWatchlistScreeningCommand command = mock(CreateWatchlistScreeningCommand.class);
        WatchlistScreeningAggregateRoot screening = mock(WatchlistScreeningAggregateRoot.class);

        when(screeningFactory.create(command)).thenReturn(screening);

        handler.handle(command);

        // The handler should not call any methods on the service directly
        verifyNoInteractions(screeningService);
    }

    @Test
    void shouldPassCorrectScreeningToRepository() {
        CreateWatchlistScreeningCommand command = mock(CreateWatchlistScreeningCommand.class);
        WatchlistScreeningAggregateRoot screening = mock(WatchlistScreeningAggregateRoot.class);

        when(screeningFactory.create(command)).thenReturn(screening);

        handler.handle(command);

        ArgumentCaptor<WatchlistScreeningAggregateRoot> captor = ArgumentCaptor.forClass(WatchlistScreeningAggregateRoot.class);
        verify(screeningRepository).addOrUpdate(captor.capture());
        assertSame(screening, captor.getValue());
    }
}