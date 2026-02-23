/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.commands;

import lombok.AllArgsConstructor;

/**
 * Abstract decorator class for the handling of commands.
 *
 * @param <T> the type of command this handles
 */
@AllArgsConstructor
public abstract class CommandHandlerDecorator<CommandT, ReturnT>
    implements CommandHandler<CommandT, ReturnT> {

  private final CommandHandler<CommandT, ReturnT> delegate;

  @Override
  public ReturnT handle(CommandT command) {
    return delegate.handle(command);
  }
}
