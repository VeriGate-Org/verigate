/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.reactor.config;

import com.google.inject.AbstractModule;
import verigate.watchlist.domain.factories.CreateWatchlistScreeningFactory;
import verigate.watchlist.domain.factories.DefaultCreateWatchlistScreeningFactory;
import verigate.watchlist.domain.handlers.CreateWatchlistScreeningCommandHandler;
import verigate.watchlist.domain.handlers.DefaultCreateWatchlistScreeningCommandHandler;
import verigate.watchlist.domain.handlers.DefaultWatchlistScreeningCommandHandler;
import verigate.watchlist.domain.handlers.WatchlistScreeningCommandHandler;
import verigate.watchlist.domain.repositories.WatchlistScreeningRepository;
import verigate.watchlist.domain.services.DecisionMatrixService;
import verigate.watchlist.domain.services.DecisionMatrixServiceImpl;
import verigate.watchlist.domain.services.DefaultWatchlistScreeningService;
import verigate.watchlist.domain.services.WatchlistScreeningService;
import verigate.watchlist.infrastructure.repositories.InMemoryWatchlistScreeningRepository;
import verigate.watchlist.reactor.application.DefaultWatchlistScreeningReactorService;
import verigate.watchlist.reactor.application.WatchlistScreeningReactorService;

/**
 * Guice module for configuring the VeriGate Watchlist Screening Reactor.
 * This module binds all interfaces to their implementations and follows
 * the existing architectural patterns of the core modules.
 */
public class WatchlistScreeningReactorModule extends AbstractModule {
    
    @Override
    protected void configure() {
        // Reactor module bindings
        bind(WatchlistScreeningReactorService.class).to(DefaultWatchlistScreeningReactorService.class);
        
        // Domain layer - Command Handlers
        bind(CreateWatchlistScreeningCommandHandler.class).to(DefaultCreateWatchlistScreeningCommandHandler.class);
        bind(WatchlistScreeningCommandHandler.class).to(DefaultWatchlistScreeningCommandHandler.class);
        
        // Domain layer - Services
        bind(WatchlistScreeningService.class).to(DefaultWatchlistScreeningService.class);
        bind(DecisionMatrixService.class).to(DecisionMatrixServiceImpl.class);
        
        // Domain layer - Factories
        bind(CreateWatchlistScreeningFactory.class).to(DefaultCreateWatchlistScreeningFactory.class);
        
        // Infrastructure layer
        bind(WatchlistScreeningRepository.class).to(InMemoryWatchlistScreeningRepository.class);
    }
}
