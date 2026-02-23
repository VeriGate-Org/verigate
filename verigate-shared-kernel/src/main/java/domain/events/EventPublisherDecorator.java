/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract decorator class for {@link EventPublisher} that delegates event publishing to a
 * decorated publisher.
 *
 * @param <EventT> the type of the event to be published
 */
public abstract class EventPublisherDecorator<EventT> implements EventPublisher<EventT> {

  private static final Logger logger = LoggerFactory.getLogger(EventPublisherDecorator.class);

  protected final EventPublisher<EventT> decoratedPublisher;

  /**
   * Constructs a new {@code EventPublisherDecorator} with the specified decorated publisher.
   *
   * @param decoratedPublisher the event publisher to be decorated
   */
  public EventPublisherDecorator(final EventPublisher<EventT> decoratedPublisher) {
    this.decoratedPublisher = decoratedPublisher;
    logger.debug(
        "Initialized EventPublisherDecorator with decorated publisher: {}", decoratedPublisher);
  }

  /**
   * Publishes an event by delegating the call to the decorated publisher.
   *
   * @param event the event to be published
   */
  @Override
  public void publish(final EventT event) {
    logger.debug("Publishing event: {}", event);
    decoratedPublisher.publish(event);
    logger.debug("Event published successfully: {}", event);
  }
}
