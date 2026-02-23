/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.event.handler.builders;

import application.handlers.EventHandler;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import domain.events.BaseEvent;
import domain.messages.MessageQueue;
import infrastructure.event.handler.DeadLetterIterator;
import infrastructure.event.handler.DeadLetterTracker;
import infrastructure.event.handler.DefaultDeadLetterCausalRelation;
import infrastructure.event.handler.RetryConfig;
import infrastructure.event.handler.factories.DefaultInfrastructureEventHandlerFactory;
import infrastructure.event.handler.factories.DefaultResilientQueueFactory;
import infrastructure.event.handler.factories.InfrastructureEventHandlerFactory;
import infrastructure.messaging.BlockingRetryableOperation;
import infrastructure.messaging.ExponentialBackoffWithJitterStrategy;
import java.util.function.Function;
import java.util.function.Predicate;

/** InfrastructureEventHandlerFactoryBuilder. */
public final class InfrastructureEventHandlerFactoryBuilder<
        RawMessageT, EventT extends BaseEvent<?>, KeyT>
    implements ModuleBuilder {

  private EventHandler<EventT> eventHandler;
  private MessageQueue<RawMessageT> invalidMessageQueue;
  private MessageQueue<EventT> deadLetterQueue;
  private DeadLetterTracker<KeyT> deadLetterTracker;
  private Function<EventT, KeyT> keyFunction;
  private DeadLetterIterator<EventT> deadLetterIterator;
  private final TypeLiteral<InfrastructureEventHandlerFactory<RawMessageT, EventT>>
      factoryTypeLiteral;
  private MessageQueue<EventT> reorderQueue;

  public InfrastructureEventHandlerFactoryBuilder(
      TypeLiteral<InfrastructureEventHandlerFactory<RawMessageT, EventT>> factoryTypeLiteral) {
    this.factoryTypeLiteral = factoryTypeLiteral;
  }

  private RetryConfig retryConfig = new RetryConfig(3, 10, 150);

  public InfrastructureEventHandlerFactoryBuilder<RawMessageT, EventT, KeyT> setEventHandler(
      EventHandler<EventT> eventHandler) {
    this.eventHandler = eventHandler;
    return this;
  }

  public InfrastructureEventHandlerFactoryBuilder<RawMessageT, EventT, KeyT> setInvalidMessageQueue(
      MessageQueue<RawMessageT> invalidMessageQueue) {
    this.invalidMessageQueue = invalidMessageQueue;
    return this;
  }

  public InfrastructureEventHandlerFactoryBuilder<RawMessageT, EventT, KeyT> setDeadLetterQueue(
      MessageQueue<EventT> deadLetterQueue) {
    this.deadLetterQueue = deadLetterQueue;
    return this;
  }

  public InfrastructureEventHandlerFactoryBuilder<RawMessageT, EventT, KeyT> setDeadLetterTracker(
      DeadLetterTracker<KeyT> deadLetterTracker) {
    this.deadLetterTracker = deadLetterTracker;
    return this;
  }

  public InfrastructureEventHandlerFactoryBuilder<RawMessageT, EventT, KeyT> setKeyFunction(
      Function<EventT, KeyT> keyFunction) {
    this.keyFunction = keyFunction;
    return this;
  }

  public InfrastructureEventHandlerFactoryBuilder<RawMessageT, EventT, KeyT> setDeadLetterIterator(
      DeadLetterIterator<EventT> deadLetterIterator) {
    this.deadLetterIterator = deadLetterIterator;
    return this;
  }

  public InfrastructureEventHandlerFactoryBuilder<RawMessageT, EventT, KeyT> setRetryConfig(
      RetryConfig retryConfig) {
    this.retryConfig = retryConfig;
    return this;
  }

  public InfrastructureEventHandlerFactoryBuilder<RawMessageT, EventT, KeyT> setReorderQueue(
      MessageQueue<EventT> reorderQueue) {
    this.reorderQueue = reorderQueue;
    return this;
  }

  /** build. */
  public AbstractModule build() {
    return new AbstractModule() {

      @Override
      protected void configure() {
        bind(factoryTypeLiteral)
            .toProvider(
                () ->
                    new DefaultInfrastructureEventHandlerFactory<>(
                        eventHandler,
                        buildResilientQueueFactory(),
                        new DefaultDeadLetterCausalRelation<>(
                            deadLetterTracker, keyFunction, deadLetterIterator),
                        true));
      }

      private DefaultResilientQueueFactory<RawMessageT, EventT> buildResilientQueueFactory() {
        return new DefaultResilientQueueFactory<>(
            invalidMessageQueue,
            new BlockingRetryableOperation<>(new ExponentialBackoffWithJitterStrategy()),
            deadLetterQueue,
            new BlockingRetryableOperation<>(new ExponentialBackoffWithJitterStrategy()),
            reorderQueue,
            new BlockingRetryableOperation<>(new ExponentialBackoffWithJitterStrategy()),
            retryConfig);
      }
    };
  }
}
