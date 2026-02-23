/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file via any medium is strictly prohibited.
 * Proprietary and confidential.
 *
 */

package infrastructure.event.handler;

import java.util.Queue;

/** Ephemeral dead letter iterator. */
public final class EphemeralDeadLetterIterator<T> implements DeadLetterIterator<T> {

  private final Queue<T> queue;

  /** Creates an instance using the specific queue. */
  public EphemeralDeadLetterIterator(Queue<T> queue) {
    this.queue = queue;
  }

  /** {@inheritDoc} */
  @Override
  public boolean hasNext() {
    if (queue == null) {
      return false;
    }

    var iterator = queue.iterator();
    return iterator.hasNext();
  }

  /** {@inheritDoc} */
  @Override
  public T next() {
    if (queue == null) {
      return null;
    }

    var iterator = queue.iterator();
    return iterator.next();
  }
}
