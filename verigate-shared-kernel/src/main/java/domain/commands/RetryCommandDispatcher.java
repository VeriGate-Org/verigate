/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.commands;

import crosscutting.resiliency.Retryable;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles the dispatching of commands with retry capabilities.
 *
 * @see domain.events.RetryEventPublisher
 */
@Slf4j
public final class RetryCommandDispatcher<T> extends CommandDispatcherDecorator<T> {
  private final Retryable retryable;
  private final String retryName;

  /**
   * Constructs a new instance.
   *
   * @param commandDispatcher the dispatcher that this instruments with retry capabilities
   */
  public RetryCommandDispatcher(
      CommandDispatcher<T> commandDispatcher,
      Retryable retryable,
      String retryName) {

    super(commandDispatcher);
    this.retryable = retryable;
    this.retryName = retryName;
    log.debug("Initialized RetryCommandDispatcher with retryable: {}, retry name: {}",
        retryable, retryName);
  }

  @Override
  public void dispatch(T command) {
    log.debug("Dispatching command with retry: {}", command);

    final Runnable retryableRunnable = retryable.createRunnable(
        retryName,
        () -> {
          super.dispatch(command);
          log.debug("Command dispatched successfully: {}", command);
        }
    );

    try {
      retryableRunnable.run();
    } catch (Exception e) {
      log.error("Failed to dispatch command after retries: {}", command, e);
      throw e;
    }
  }
}
