/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.event.handler;

/**
 * This interface provides an abstraction for checking whether a given event has a causally related
 * event in the dead letter queue. It is designed to help identify relationships between events that
 * may have contributed to their failure and subsequent placement in the dead letter queue, which is
 * critical for troubleshooting and resolving issues in event-driven architectures.
 */
public interface DeadLetterCausalRelation<EventT> {
  
  /**
   * Determines whether the specified event has arrived out of order compared to other causally 
   * related events in its sequence. For example, if this is the second event in a sequence of two,
   * but the first has yet to be processed, this method would return {@code true}.
   *
   * @param event the event to check for a causal relationship in the dead letter queue
   * @return {@code true} if the event has arrived out of order compared to others in its sequence;
   *     {@code false} otherwise
   */
  boolean isOutOfOrder(EventT event);
  
  /**
   * Determines whether the specified event has a causally related event in the dead letter queue.
   * This method can be used to trace error propagation and understand dependencies or sequences of
   * events that led to failures.
   *
   * @param event the event to check for a causal relationship in the dead letter queue
   * @return {@code true} if there is a causally related event present in the dead letter queue;
   *     {@code false} otherwise
   */
  boolean isPresent(EventT event);

  void addEvent(EventT event);
}
