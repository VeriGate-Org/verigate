/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.event.handler;

import java.util.Iterator;

/**
 * Provides an iterator over events that were undeliverable or unprocessable within a system. This
 * interface extends the {@link Iterator} to iterate through such dead-letter events.
 *
 * <p>A dead-letter event typically refers to messages or events that could not be delivered,
 * processed, or forwarded to their intended destination and require special handling.
 *
 * @param <EventT> the type of events this iterator will return
 */
public interface DeadLetterIterator<EventT> extends Iterator<EventT> {

  /**
   * Returns {@code true} if the iteration has more dead-letter events. (In other words, returns
   * {@code true} if {@link #next} would return an event rather than throwing an exception.)
   *
   * @return {@code true} if the iterator has more events
   */
  @Override
  boolean hasNext();

  /**
   * Returns the next dead-letter event in the iteration.
   *
   * <p>This method should be called only if {@link #hasNext()} returns {@code true}.xx
   *
   * @return the next event in the iteration
   */
  @Override
  EventT next();
}
