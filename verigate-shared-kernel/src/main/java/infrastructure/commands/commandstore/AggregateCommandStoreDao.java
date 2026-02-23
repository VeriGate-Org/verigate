/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.commands.commandstore;

import domain.commands.BaseCommand;
import domain.exceptions.DeferredException;
import domain.exceptions.InvariantViolationException;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;

/**
 * Represents a generic command store data access object.
 */
public interface AggregateCommandStoreDao<
    CommandT extends BaseCommand, CommandHandlerReturnT, CommandStoreRecordT> {

  void initialize(CommandT command);

  void setSuccess(CommandT command, CommandHandlerReturnT handlerReturn);

  void setDeferredFailed(
      CommandT command, DeferredException e, CommandHandlerReturnT handlerReturn);

  void setTransientFailed(
      CommandT command, TransientException e, CommandHandlerReturnT handlerReturn);

  void setInvariantFailed(
      CommandT command, InvariantViolationException e, CommandHandlerReturnT handlerReturn);

  void setFailed(CommandT command, PermanentException e, CommandHandlerReturnT handlerReturn);
}
