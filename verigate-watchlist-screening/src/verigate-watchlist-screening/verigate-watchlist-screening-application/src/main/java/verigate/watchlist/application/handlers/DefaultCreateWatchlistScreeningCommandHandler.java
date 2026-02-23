/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.application.handlers;

import verigate.watchlist.domain.commands.CreateWatchlistScreeningCommand;
import verigate.watchlist.domain.factories.CreateWatchlistScreeningFactory;
import verigate.watchlist.domain.handlers.CreateWatchlistScreeningCommandHandler;
import verigate.watchlist.domain.models.WatchlistScreeningAggregateRoot;
import verigate.watchlist.domain.repositories.WatchlistScreeningRepository;
import verigate.watchlist.domain.services.WatchlistScreeningService;

/**
 * Default implementation of the WatchlistScreeningCommandHandler interface.
 * Delegates to the WatchlistScreeningService for business logic execution.
 */

public class DefaultCreateWatchlistScreeningCommandHandler implements CreateWatchlistScreeningCommandHandler {

    private final WatchlistScreeningService screeningService;
    private final WatchlistScreeningRepository screeningRepository;
    private final CreateWatchlistScreeningFactory screeningFactory;

    public DefaultCreateWatchlistScreeningCommandHandler(WatchlistScreeningService screeningService, WatchlistScreeningRepository screeningRepository, CreateWatchlistScreeningFactory screeningFactory) {
        this.screeningService = screeningService;
        this.screeningRepository = screeningRepository;
        this.screeningFactory = screeningFactory;
    }

    @Override
    public WatchlistScreeningAggregateRoot handle(CreateWatchlistScreeningCommand command) {
        
        var screening = screeningFactory.create(command);

        screeningRepository.addOrUpdate(screening);

        //Emit WatchlistScreeningCreatedEvent

        return screening;
    }
}
