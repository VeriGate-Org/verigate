package verigate.watchlist.application;

import com.google.inject.Inject;
import verigate.watchlist.domain.handlers.CreateWatchlistScreeningCommandHandler;
import verigate.watchlist.domain.handlers.WatchlistScreeningCommandHandler;

/**
 * Implementation of the application service following Clean Architecture principles.
 * This class orchestrates the interaction between the domain layer and the infrastructure layer.
 */
public class DefaultWatchlistScreeningApplicationService implements WatchlistScreeningApplicationService {
    
    private final CreateWatchlistScreeningCommandHandler createWatchlistScreeningCommandHandler;
    private final WatchlistScreeningCommandHandler watchlistScreeningCommandHandler;
    
    /**
     * Constructor with dependencies injected via Guice.
     * 
     * @param createWatchlistScreeningCommandHandler Command handler for creating watchlist screenings
     * @param watchlistScreeningCommandHandler Command handler for other watchlist screening operations
     */
    @Inject
    public DefaultWatchlistScreeningApplicationService(
            CreateWatchlistScreeningCommandHandler createWatchlistScreeningCommandHandler,
            WatchlistScreeningCommandHandler watchlistScreeningCommandHandler) {
        this.createWatchlistScreeningCommandHandler = createWatchlistScreeningCommandHandler;
        this.watchlistScreeningCommandHandler = watchlistScreeningCommandHandler;
    }
    
    @Override
    public void start() {
        System.out.println("Starting Watchlist Screening Application...");
        // Initialize components, start services, etc.
    }
    
    @Override
    public void stop() {
        System.out.println("Stopping Watchlist Screening Application...");
        // Cleanup resources, stop services, etc.
    }
}
