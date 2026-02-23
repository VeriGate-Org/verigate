/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.events;

import application.handlers.EventHandler;
import crosscutting.resiliency.Retryable;
import domain.exceptions.PermanentException;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles events with retry capabilities.
 *
 * @param <EventT> the type of event this handler processes
 */
public final class RetryEventHandler<EventT> extends EventHandlerDecorator<EventT> {
  private static final Logger logger = LoggerFactory.getLogger(RetryEventHandler.class);

  private final Retryable retryable;
  private final String retryName;

  /**
   * Constructs a new instance.
   *
   * @param eventHandler the handler that this instruments with retry capabilities
   * @param retryable the retryable strategy to be used for retrying event handling
   * @param retryName the name of the retry strategy
   */
  public RetryEventHandler(
      EventHandler<EventT> eventHandler, Retryable retryable, String retryName) {
    super(eventHandler);
    this.retryable = retryable;
    this.retryName = retryName;
    logger.debug(
        "Initialized RetryEventHandler with retryable: {}, retry name: {}", retryable, retryName);
  }

  @Override
  public void handle(EventT event) {
    logger.debug("Handling event with retry: {}", event);

    final Callable<Void> callable =
        retryable.createCallable(
            retryName,
            () -> {
              super.handle(event);
              logger.debug("Event handled successfully: {}", event);
              return null;
            });

    try {
      callable.call();
    } catch (Exception e) {
      logger.error("Failed to handle event after retries: {}", event, e);
      throw new PermanentException(retryName, e);
    }
  }
}
