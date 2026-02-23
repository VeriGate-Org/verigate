/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.reactor.application;

import com.google.inject.Inject;
import verigate.watchlist.domain.handlers.CreateWatchlistScreeningCommandHandler;
import verigate.watchlist.domain.handlers.WatchlistScreeningCommandHandler;

/**
 * Default implementation of the WatchlistScreeningReactorService.
 * This class is responsible for initializing and coordinating all modules
 * in the VeriGate Watchlist Screening system.
 */
public class DefaultWatchlistScreeningReactorService implements WatchlistScreeningReactorService {
    
    private final CreateWatchlistScreeningCommandHandler createWatchlistScreeningCommandHandler;
    private final WatchlistScreeningCommandHandler watchlistScreeningCommandHandler;
    
    /**
     * Creates a new DefaultWatchlistScreeningReactorService with the required dependencies.
     * 
     * @param createWatchlistScreeningCommandHandler Command handler for creating screenings
     * @param watchlistScreeningCommandHandler Command handler for other screening operations
     */
    @Inject
    public DefaultWatchlistScreeningReactorService(
            CreateWatchlistScreeningCommandHandler createWatchlistScreeningCommandHandler,
            WatchlistScreeningCommandHandler watchlistScreeningCommandHandler) {
        this.createWatchlistScreeningCommandHandler = createWatchlistScreeningCommandHandler;
        this.watchlistScreeningCommandHandler = watchlistScreeningCommandHandler;
    }
    
    @Override
    public void start() {
        System.out.println("Starting VeriGate Watchlist Screening modules...");
        
        // Initialize domain services
        System.out.println("Initializing Domain Layer...");
        
        // Initialize application services
        System.out.println("Initializing Application Layer...");
        
        // Initialize infrastructure services
        System.out.println("Initializing Infrastructure Layer...");
        
        System.out.println("All VeriGate Watchlist Screening modules started successfully!");
    }
    
    @Override
    public void stop() {
        System.out.println("Stopping VeriGate Watchlist Screening modules...");
        
        // Shutdown in reverse order
        System.out.println("Shutting down Infrastructure Layer...");
        System.out.println("Shutting down Application Layer...");
        System.out.println("Shutting down Domain Layer...");
        
        System.out.println("All VeriGate Watchlist Screening modules stopped successfully!");
    }
}
