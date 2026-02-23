/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.events;

/**
 * The {@code EventPublisher} interface defines a generic contract for publishing events within the
 * system. Implementors of this interface are responsible for the mechanism by which events of a
 * specific type, extending from {@link BaseEvent}, are broadcasted or dispatched to interested
 * subscribers or handlers.
 *
 * <p>This interface facilitates a decoupled architecture by allowing components to emit events
 * without being concerned about the specifics of how these events are processed or who processes
 * them. It is a key component in the implementation of an event-driven architecture, where the flow
 * of the program is determined by events.
 *
 * @param <EventT> The type of the event this publisher is designed to handle. This ensures type
 *     safety by restricting the event to specific subclasses of {@link BaseEvent}, thus providing a
 *     flexible yet controlled mechanism for event publication.
 */
public interface EventPublisher<EventT> {

  /**
   * Publishes the given event to all registered subscribers or handlers that have expressed
   * interest in events of this type. The method of delivery and the specifics of how the event is
   * processed are determined by the implementation of this interface.
   *
   * <p>This method is designed to be asynchronous in nature, allowing the calling thread to
   * continue execution without waiting for the event to be processed. However, synchronous or
   * blocking implementations can also be provided depending on the requirements of the system.
   *
   * @param event The event to be published. It is an instance of {@code EventT}, encapsulating all
   *     the information necessary for subscribers or handlers to process the event appropriately.
   */
  void publish(EventT event);
}
