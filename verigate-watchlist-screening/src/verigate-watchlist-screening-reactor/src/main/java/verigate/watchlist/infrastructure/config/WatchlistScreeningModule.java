package verigate.watchlist.infrastructure.config;

import com.google.inject.AbstractModule;
import verigate.watchlist.application.DefaultWatchlistScreeningApplicationService;
import verigate.watchlist.application.WatchlistScreeningApplicationService;
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

/**
 * Guice module configuration that binds interfaces to their implementations.
 * This follows Clean Architecture principles by configuring dependencies
 * at the infrastructure layer.
 */
public class WatchlistScreeningModule extends AbstractModule {
    
    @Override
    protected void configure() {
        // Application layer
        bind(WatchlistScreeningApplicationService.class).to(DefaultWatchlistScreeningApplicationService.class);
        
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
