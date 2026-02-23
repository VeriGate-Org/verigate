/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.event.handler;

import domain.events.BaseEvent;
import domain.exceptions.HandlerDelayException;
import java.util.function.Function;

/**
 * The {@code InfrastructureEventHandler} interface specifies a method for handling events that
 * extend from {@code BaseEvent}, within the infrastructure layer of the VeriGate application.
 * Implementations of this interface are responsible for the processing of these events, which may
 * involve operations such as logging, message forwarding, or any other infrastructure-related
 * tasks.
 *
 * <p>This interface ensures that any event passed to its {@code handle} method adheres to the base
 * structure defined by {@code BaseEvent}, thus providing a consistent approach to event handling
 * across the application. Implementors can focus on the specific handling logic for each type of
 * event without concerning themselves with the underlying event infrastructure.
 *
 * @param <EventT> the type of the event to be handled by this handler, extending {@code BaseEvent},
 *     allows for flexibility in handling different kinds of events with specific attributes and
 *     behaviors defined in their respective classes.
 */
public interface InfrastructureEventHandler<RawMessageT, EventT extends BaseEvent<?>> {

  /**
   * Handles an event of type {@code EventT}, performing operations defined by the implementation of
   * this method. These operations could range from logging the event for audit purposes, initiating
   * a series of business operations in response to the event, or forwarding the event to another
   * part of the application's infrastructure.
   *
   * <p>Implementations must define how exceptions are handled, specifically {@code
   * HandlerDelayException}, which indicates a delay or failure in processing the event. This allows
   * for robust error handling and recovery mechanisms to be put in place.
   *
   * @param extractEventFromRawMessage the extractEventFromRawMessage to handle. Must not be {@code
   *     null}.
   * @throws HandlerDelayException if there is a delay or failure in handling the event, allowing
   *     for error handling and recovery procedures to be initiated.
   */
  void handle(RawMessageT message, Function<RawMessageT, EventT> extractEventFromRawMessage)
      throws HandlerDelayException;
}
