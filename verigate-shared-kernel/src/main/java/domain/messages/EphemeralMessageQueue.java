/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.messages;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A message queue implementation (supporting a couple of message queue interfaces) which
 * stores messages in an in-memory queue. This backing queue can either be supplied, or will
 * default to {@link ConcurrentLinkedQueue}.
 */
public class EphemeralMessageQueue<T>
    implements MessageQueue<T>, InvalidMessageQueue<T>, DeadLetterQueue<T> {

  private final Queue<T> queue;

  public EphemeralMessageQueue() {
    this(new ConcurrentLinkedQueue<>());
  }

  public EphemeralMessageQueue(Queue<T> queue) {
    this.queue = queue;
  }

  @Override
  public void enqueue(T message) {
    queue.offer(message);
  }

  @Override
  public void dequeue(T message) {
    queue.remove(message);
  }

  @Override
  public T peek() {
    return queue.peek();
  }
}
