/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.events;

import crosscutting.resiliency.Retryable;
import domain.commands.RetryCommandDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Decorator class that adds retry functionality to an {@link EventPublisher}.
 *
 * <p>This class ensures that events are published with retry capabilities.
 *
 * @param <EventT> the type of the event to be published
 * @see RetryCommandDispatcher
 * @see infrastructure.event.handler.RetryableMessageQueue
 */
public final class RetryEventPublisher<EventT> extends EventPublisherDecorator<EventT> {

  private static final Logger logger = LoggerFactory.getLogger(RetryEventPublisher.class);

  private final Retryable retryable;
  private final String retryName;

  /**
   * Constructs a new {@code RetryEventPublisherDecorator} with the specified decorated publisher,
   * retryable, and retry name.
   *
   * @param decoratedPublisher the event publisher to be decorated
   * @param retryable the retryable strategy to be used for retrying event publishing
   * @param retryName the name of the retry strategy
   */
  public RetryEventPublisher(
      final EventPublisher<EventT> decoratedPublisher,
      final Retryable retryable,
      final String retryName) {
    super(decoratedPublisher);
    this.retryable = retryable;
    this.retryName = retryName;
    logger.debug(
        "Initialized RetryEventPublisherDecorator with retryable: {}, retry name: {}",
        retryable,
        retryName);
  }

  /**
   * Publishes an event with retry capabilities.
   *
   * <p>If the initial publishing attempt fails, the retry strategy will retry the operation based
   * on the specified retry policy.
   *
   * @param event the event to be published
   */
  @Override
  public void publish(final EventT event) {
    logger.debug("Publishing event with retry: {}", event);

    Runnable originalRunnable =
        () -> {
          decoratedPublisher.publish(event);
          logger.debug("Event published successfully: {}", event);
        };
    Runnable retryableRunnable = retryable.createRunnable(retryName, originalRunnable);

    try {
      retryableRunnable.run();
    } catch (Exception e) {
      logger.error("Failed to publish event after retries: {}", event, e);
      throw e;
    }
  }
}
