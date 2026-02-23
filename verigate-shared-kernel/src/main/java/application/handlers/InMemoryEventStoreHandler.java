/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package application.handlers;

import java.util.Collection;

/**
 * An {@link EventHandler} which stores all handled events in a supplied {@link Collection}.
 * Note that this implementation does not make any attempt to curb collection growth. It is
 * up to the user of this handler to remove entries as and when needed.
 */
public class InMemoryEventStoreHandler<EventT> implements EventHandler<EventT> {

  private final Collection<EventT> events;

  public InMemoryEventStoreHandler(Collection<EventT> events) {
    this.events = events;
  }

  @Override
  public void handle(EventT event) {
    events.add(event);
  }

}
