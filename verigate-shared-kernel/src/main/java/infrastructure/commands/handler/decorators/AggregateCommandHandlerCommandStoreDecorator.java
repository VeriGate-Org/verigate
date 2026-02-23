/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.commands.handler.decorators;

import domain.commands.BaseCommand;
import domain.commands.CommandHandler;
import domain.commands.CommandHandlerDecorator;
import domain.exceptions.DeferredException;
import domain.exceptions.InvariantViolationException;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import infrastructure.commands.commandstore.AggregateCommandStoreDao;
import java.util.logging.Logger;

/**
 * A command handler decorator that stores command handling results in a command store.
 */
public class AggregateCommandHandlerCommandStoreDecorator<
        CommandT extends BaseCommand, CommandHandlerReturnT, CommandStoreRecordT>
    extends CommandHandlerDecorator<CommandT, CommandHandlerReturnT> {

  private static final Logger LOGGER =
      Logger.getLogger(AggregateCommandHandlerCommandStoreDecorator.class.getName());

  private final AggregateCommandStoreDao<CommandT, CommandHandlerReturnT, CommandStoreRecordT>
      commandStoreDao;

  /**
   * Construct a new decorator with all required dependencies.
   */
  public AggregateCommandHandlerCommandStoreDecorator(
      CommandHandler<CommandT, CommandHandlerReturnT> commandHandler,
      AggregateCommandStoreDao<CommandT, CommandHandlerReturnT, CommandStoreRecordT>
          commandStoreDao) {
    super(commandHandler);
    this.commandStoreDao = commandStoreDao;
  }

  @Override
  public CommandHandlerReturnT handle(CommandT command) {
    CommandHandlerReturnT handlerReturn = null;
    try {
      commandStoreDao.initialize(command);
      handlerReturn = super.handle(command);
      commandStoreDao.setSuccess(command, handlerReturn);
    } catch (DeferredException e) {
      commandStoreDao.setDeferredFailed(command, e, handlerReturn);
      throw e;
    } catch (TransientException e) {
      commandStoreDao.setTransientFailed(command, e, handlerReturn);
      throw e;
    } catch (InvariantViolationException e) {
      commandStoreDao.setInvariantFailed(command, e, handlerReturn);
      throw e;
    } catch (PermanentException e) {
      commandStoreDao.setFailed(command, e, handlerReturn);
      throw e;
    }
    return null;
  }
}
