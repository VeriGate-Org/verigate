/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package application.handlers;

/**
 * The {@code EventHandler} interface defines a generic contract for handling events of a specific
 * type. Implementors of this interface are required to define the behavior for handling an event
 * when it occurs. This generic interface allows for flexible implementation details depending on
 * the type of event being handled.
 *
 * @param <EventT> The type of the event this handler is designed for. This allows the event
 *     handling mechanism to be type-safe and flexible, catering to various kinds of events without
 *     requiring multiple interfaces or methods.
 */
public interface EventHandler<EventT> {

  /**
   * Handles the specified event. This method is called when an event of type {@code EventT} occurs.
   * Implementors should define the specific actions to be taken in response to the event inside
   * this method.
   *
   * @param event The event to be handled. It is an instance of {@code EventT}, providing all the
   *     necessary information required to adequately respond to the event.
   */
  void handle(EventT event);
}
