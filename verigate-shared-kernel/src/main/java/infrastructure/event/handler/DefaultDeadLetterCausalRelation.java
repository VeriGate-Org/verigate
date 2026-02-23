/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.event.handler;

import java.util.function.Function;

/**
 * Implements the {@link DeadLetterCausalRelation} interface to determine if a specific event has a
 * causally related event in the dead letter queue based on a key. This class uses a DAO (Data
 * Access Object) to check the presence of an event's key in a tracking system, enabling the
 * identification of causally related dead letter events.
 *
 * @param <EventT> the type of event this relation checks
 * @param <KeyT> the type of key used to identify events in the dead letter tracking system
 */
public final class DefaultDeadLetterCausalRelation<EventT, KeyT>
    implements DeadLetterCausalRelation<EventT> {

  private final DeadLetterTracker<KeyT> deadLetterTracking;
  private final Function<EventT, KeyT> keyFunction;

  /**
   * Constructs a new instance of {@code DefaultDeadLetterCausalRelation}.
   *
   * @param deadLetterTracker the DAO responsible for accessing the dead letter tracking data
   * @param keyFunction a function that extracts a key from an event, which is used to check for
   *     causal relations in the dead letter queue
   */
  public DefaultDeadLetterCausalRelation(
      DeadLetterTracker<KeyT> deadLetterTracker,
      Function<EventT, KeyT> keyFunction,
      DeadLetterIterator<EventT> deadLetterIterator) {
    this.deadLetterTracking = deadLetterTracker;
    this.keyFunction = keyFunction;

    if (this.deadLetterTracking.isTransient()) {
      // Iterate through the events in the dead letter queue
      while (deadLetterIterator.hasNext()) {
        var event = deadLetterIterator.next();
        deadLetterTracker.trackKey(keyFunction.apply(event));
      }
    }
  }

  /**
   * Checks if the given event has a causally related event in the dead letter queue by using a key
   * derived from the event.
   *
   * @param event the event to check for a causal relationship
   * @return true if there is a causally related event in the dead letter queue; false otherwise
   */
  @Override
  public boolean isPresent(EventT event) {
    return this.deadLetterTracking.isKeyPresent(this.keyFunction.apply(event));
  }

  @Override
  public void addEvent(EventT event) {
    this.deadLetterTracking.trackKey(keyFunction.apply(event));
  }

  // TODO: this must be implemented after the atomic clock has been added to the BaseEvent
  // at present there is no mechanism for telling if things are out of order using this approach
  @Override
  public boolean isOutOfOrder(EventT event) {
    return false;
  }
}
