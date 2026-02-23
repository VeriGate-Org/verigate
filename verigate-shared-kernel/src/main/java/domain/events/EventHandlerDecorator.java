/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.events;

import application.handlers.EventHandler;

/**
 * Abstract decorator class for the handling of events.
 *
 * @param <EventT> the type of event this handles
 */
public abstract class EventHandlerDecorator<EventT> implements EventHandler<EventT> {

  private final EventHandler<EventT> delegate;

  protected EventHandlerDecorator(EventHandler<EventT> delegate) {
    this.delegate = delegate;
  }

  @Override
  public void handle(EventT event) {
    delegate.handle(event);
  }
}
