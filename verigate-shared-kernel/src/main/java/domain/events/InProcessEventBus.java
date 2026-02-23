/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.events;

import application.handlers.EventHandler;

/**
 * The {@code InProcessEventBus} class provides a simple, in-process implementation of an event bus.
 * It implements both the {@link EventPublisher} and {@link EventConsumer} interfaces, allowing it
 * to publish and consume events of type {@code T}.
 *
 * @param <T> the type of events that this event bus handles
 */
public final class InProcessEventBus<T> implements EventPublisher<T>, EventConsumer<T> {

  /**
   * The event handler that consumes the events published to this event bus. This field is volatile
   * to ensure visibility across threads.
   */
  private volatile EventHandler<T> handler;

  /**
   * Publishes the specified event to the current handler if it is not {@code null}.
   *
   * @param event the event to be published
   */
  @Override
  public void publish(final T event) {
    final EventHandler<T> currentHandler = handler;
    if (currentHandler != null) {
      currentHandler.handle(event);
    }
  }

  /**
   * Sets the event handler that will consume events published to this event bus.
   *
   * @param handler the event handler to be set
   */
  @Override
  public void consume(EventHandler<T> handler) {
    this.handler = handler;
  }
}
