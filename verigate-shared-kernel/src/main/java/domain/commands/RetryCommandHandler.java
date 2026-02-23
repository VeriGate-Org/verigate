/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.commands;

import crosscutting.resiliency.Retryable;
import domain.exceptions.PermanentException;
import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles commands with retry capabilities.
 */
@Slf4j
public final class RetryCommandHandler<CommandT, ReturnT>
    extends CommandHandlerDecorator<CommandT, ReturnT> {
  private final Retryable retryable;
  private final String retryName;

  /**
   * Constructs a new instance.
   *
   * @param commandHandler the handler that this instruments with retry capabilities
   */
  public RetryCommandHandler(
      CommandHandler<CommandT, ReturnT> commandHandler, Retryable retryable, String retryName) {

    super(commandHandler);
    this.retryable = retryable;
    this.retryName = retryName;
    log.debug(
        "Initialized RetryCommandHandler with retryable: {}, retry name: {}", retryable, retryName);
  }

  @Override
  public ReturnT handle(CommandT command) {
    log.debug("Handling command with retry: {}", command);

    final Callable<ReturnT> callable =
        retryable.createCallable(
            retryName,
            () -> {
              ReturnT result = super.handle(command);
              log.debug("Command handled successfully: {}", command);
              return result;
            });

    try {
      return callable.call();
    } catch (Exception e) {
      log.error("Failed to handle command after retries: {}", command, e);
      // Note that this logic does not own higher-level resiliency features like moving
      // failed messages to dead-letter queues. Use a ResilientCommandLambdaHandler wrapping
      // this RetryCommandHandler if this is required.
      throw new PermanentException(retryName, e);
    }
  }
}
