/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.events;

import application.handlers.EventHandler;

/**
 * The {@code EventConsumer} interface defines a contract for consuming events via an {@link
 * EventHandler}. It allows implementing classes to process or handle events of a specific type.
 *
 * @param <T> the type of event that this consumer handles
 */
public interface EventConsumer<T> {

  /**
   * Consumes an event using the provided {@link EventHandler}. This method defines how the event
   * will be processed or handled.
   *
   * @param handler the event handler used to process the event
   */
  void consume(EventHandler<T> handler);
}
