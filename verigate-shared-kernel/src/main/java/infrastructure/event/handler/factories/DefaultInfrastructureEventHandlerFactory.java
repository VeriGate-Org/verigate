/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.event.handler.factories;

import application.handlers.EventHandler;
import domain.events.BaseEvent;
import infrastructure.event.handler.DeadLetterCausalRelation;
import infrastructure.event.handler.InfrastructureEventHandler;
import infrastructure.event.handler.ResilientEventHandler;
import infrastructure.event.handler.RetryConfig;
import infrastructure.messaging.DefaultRetryableOperationFactory;

/**
 * A factory class for creating {@link InfrastructureEventHandler} instances. This factory
 * integrates various components necessary to handle infrastructure events, including an event
 * handler, a resilient publisher factory, and a mechanism to manage dead letter causal
 * relationships.
 *
 * @param <RawMessageT> the type of the raw message input to the event handler
 * @param <EventT> the type of event extending {@link BaseEvent} that the event handler processes
 */
public final class DefaultInfrastructureEventHandlerFactory<
        RawMessageT, EventT extends BaseEvent<?>>
    implements InfrastructureEventHandlerFactory<RawMessageT, EventT> {

  private final EventHandler<EventT> eventHandler;
  private final ResilientQueueFactory<RawMessageT, EventT> resilientQueueFactory;
  private final DeadLetterCausalRelation<EventT> deadLetterCausalRelation;
  private final boolean reorderEvents;

  /**
   * Constructs a new factory instance with the specified components.
   *
   * @param eventHandler the event handler responsible for processing events
   * @param resilientQueueFactory a factory for providing implementations that manage the enqueueing
   *     process, ensuring delivery even in adverse conditions
   * @param deadLetterCausalRelation a mechanism to determine causal relationships for events that
   *     end up in a dead letter queue
   * @param reorderEvents a flag indicating whether the event handler should check the order of
   *     events
   */
  public DefaultInfrastructureEventHandlerFactory(
      EventHandler<EventT> eventHandler,
      ResilientQueueFactory<RawMessageT, EventT> resilientQueueFactory,
      DeadLetterCausalRelation<EventT> deadLetterCausalRelation,
      boolean reorderEvents) {
    this.eventHandler = eventHandler;
    this.resilientQueueFactory = resilientQueueFactory;
    this.deadLetterCausalRelation = deadLetterCausalRelation;
    this.reorderEvents = reorderEvents;
  }

  /**
   * Creates and returns a new {@link InfrastructureEventHandler} instance. This handler is equipped
   * with a retry configuration and is capable of resiliently handling and publishing events,
   * including managing failures potentially resulting in messages being sent to a dead letter
   * queue.
   *
   * @return a new instance of {@link InfrastructureEventHandler} configured with retry capabilities
   *     and robust event handling and publishing mechanisms
   */
  @Override
  public InfrastructureEventHandler<RawMessageT, EventT> create() {
    var retryConfig = new RetryConfig(5, 10, 1000);
    if (this.reorderEvents) {
      // create resilient event handler which reorders events
      return new ResilientEventHandler<>(
          retryConfig,
          new DefaultRetryableOperationFactory(),
          this.eventHandler,
          this.resilientQueueFactory,
          this.deadLetterCausalRelation::isOutOfOrder);
    }
    // create resilient event handler which doesn't reorder events
    return new ResilientEventHandler<>(
        retryConfig,
        new DefaultRetryableOperationFactory(),
        this.eventHandler,
        this.resilientQueueFactory);
  }
}
