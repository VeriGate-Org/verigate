/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.event.handler;

import java.util.Set;

/**
 * The {@code DeadLetterTrackingDao} interface defines data access operations for managing dead
 * letter events associated with specific keys in the VeriGate application. Implementations of
 * this interface must handle the storage, retrieval, and deletion of dead letter event tracking
 * entries. These operations are crucial for monitoring and resolving issues related to event
 * delivery failures.
 *
 * @param <KeyT> the type of the key used to identify and track dead letter events, allowing for a
 *     flexible integration with various types of identifiers.
 */
public interface DeadLetterTracker<KeyT> {

  /**
   * Checks if a given key is already associated with any dead letter events in the tracking system.
   *
   * @param key the key to check for presence in the dead letter event tracking system. Must not be
   *     {@code null}.
   * @return {@code true} if the key is associated with one or more dead letter events; {@code
   *     false} otherwise.
   */
  boolean isKeyPresent(KeyT key);

  /**
   * Associates a specific dead letter event, identified by an {@code eventId}, with the provided
   * {@code key}. This method is used to track new instances of delivery failures for further
   * investigation and resolution.
   *
   * @param key the key with which the dead letter event is to be associated. Must not be {@code
   *     null}.
   */
  void trackKey(KeyT key);

  /**
   * Removes the association of a dead letter event, identified by an {@code eventId}, from the
   * provided {@code key}. This method is typically called once the issue causing the event to
   * become a dead letter has been resolved, indicating that the event does not need to be tracked
   * anymore.
   *
   * @param key the key from which the association with the dead letter event is to be removed. Must
   *     not be {@code null}.
   */
  void untrackKey(KeyT key);

  /**
   * Retrieves all keys currently associated with any dead letter events in the tracking system.
   * Returns an immutable set of keys to prevent modification outside of the tracker control.
   *
   * @return an immutable Set of keys that are currently tracked, each associated with one or more
   *     dead letter events.
   */
  Set<KeyT> fetchAllKeys();

  boolean isTransient();

  boolean isEmpty();
}
