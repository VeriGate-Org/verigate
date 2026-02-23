/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.messages;

/**
 * The {@code MessageQueue} interface defines a generic contract for enqueueing messages within the
 * system. Implementors of this interface are responsible for the mechanism by which messages of a
 * specific type are enqueued.
 *
 * @param <T> The type of the message this queue is designed to handle.
 */
public interface MessageQueue<T> {

  /**
   * Enqueues a message. The method of delivery and the specifics of how the message is
   * enqueued are determined by the implementations of this interface.
   *
   * <p>This method is designed to be asynchronous in nature, allowing the calling thread to
   * continue execution without waiting for the message to be enqueued. However, synchronous or
   * blocking implementations can also be provided depending on the requirements of the system.
   *
   * @param message The message to be enqueued.
   */
  void enqueue(T message);

  /**
   * dequeues a message. The method of delivery and the specifics of how the message is
   * dequeued are determined by the implementations of this interface.
   *
   * <p>This method is designed to be asynchronous in nature, allowing the calling thread to
   * continue execution without waiting for the message to be dequeued. However, synchronous or
   * blocking implementations can also be provided depending on the requirements of the system.
   *
   * @param message The message to be dequeued.
   */
  void dequeue(T message);

  /**
   * Peeks at the next message in the queue without removing it.
   */
  T peek();
}
