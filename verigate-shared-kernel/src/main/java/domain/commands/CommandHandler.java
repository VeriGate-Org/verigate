/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.commands;

import domain.exceptions.InvariantViolationException;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;

/**
 * Base interface for command handlers, declaring the default method. The generic T represents the
 * type of command to be handled.
 */
public interface CommandHandler<CommandT, ReturnT> {

  /** Default method to handle a command. */
  ReturnT handle(CommandT command)
      throws TransientException, PermanentException, InvariantViolationException;
}
