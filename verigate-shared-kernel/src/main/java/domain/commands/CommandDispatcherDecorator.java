/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.commands;

import lombok.AllArgsConstructor;

/**
 * Abstract decorator class for handling the dispatching of commands with retry capabilities.
 *
 * @param <T> the type of command this handles
 */
@AllArgsConstructor
public abstract class CommandDispatcherDecorator<T> implements CommandDispatcher<T> {

  private final CommandDispatcher<T> delegate;

  /**
   * Dispatches a command by delegating the call to the underlying dispatcher.
   *
   * @param command the command to be dispatched
   */
  @Override
  public void dispatch(T command) {
    delegate.dispatch(command);
  }
}
