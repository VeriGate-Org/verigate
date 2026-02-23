/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.events;

import domain.events.EventPublisher;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A decorator class for an {@link EventPublisher} that allows a callback action to be performed on
 * each event before it is published. This class implements the {@link EventPublisher} interface and
 * delegates the actual publishing of events to the wrapped {@link EventPublisher} instance.
 *
 * @param <EventT> the type of event to be published
 */
public final class CallbackEventPublisher<EventT> implements EventPublisher<EventT> {
  private static final Logger logger = LoggerFactory.getLogger(CallbackEventPublisher.class);
  private final Consumer<EventT> callback;
  private final EventPublisher<EventT> eventPublisher;

  /**
   * Constructs a {@code CallbackEventPublisher} with the specified callback and event publisher.
   *
   * @param callback       the callback action to be performed on each event before it is published
   * @param eventPublisher the event publisher to which the event will be delegated after the
   *                       callback is invoked
   */
  public CallbackEventPublisher(Consumer<EventT> callback, EventPublisher<EventT> eventPublisher) {
    this.callback = callback;
    this.eventPublisher = eventPublisher;
  }

  /**
   * Publishes the specified event by first invoking the callback and then delegating the actual
   * publishing to the wrapped {@link EventPublisher}.
   *
   * @param event the event to be published
   */
  @Override
  public void publish(EventT event) {
    logger.info("Publishing event: {}", event);
    callback.accept(event);
    logger.info("Invoking event publisher {}", event);
    eventPublisher.publish(event);
  }
}
