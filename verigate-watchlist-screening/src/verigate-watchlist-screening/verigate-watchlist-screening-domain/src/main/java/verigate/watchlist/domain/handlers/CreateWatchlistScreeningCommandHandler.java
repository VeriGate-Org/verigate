/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.handlers;

import verigate.watchlist.domain.commands.CreateWatchlistScreeningCommand;
import verigate.watchlist.domain.models.WatchlistScreeningAggregateRoot;

import domain.commands.CommandHandler;

/**
 * Application service for handling watchlist screening commands.
 */
public interface CreateWatchlistScreeningCommandHandler 
        extends CommandHandler<CreateWatchlistScreeningCommand, WatchlistScreeningAggregateRoot> {}