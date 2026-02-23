/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.event.handler;

import application.handlers.EventHandler;
import domain.events.BaseEvent;
import domain.exceptions.HandleDelay;
import domain.exceptions.HandlerDelayException;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import infrastructure.event.handler.factories.ResilientQueueFactory;
import infrastructure.messaging.RetryableOperation;
import infrastructure.messaging.RetryableOperationFactory;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for handling events in a resilient manner. It is capable of retrying
 * event processing in the event of transient failures, and it can handle permanent failures by
 * publishing the event to a dead-letter queue. It also provides mechanisms for handling
 * out-of-order events and reordering them for processing.
 *
 * @param <RawMessageT> the type of the raw message that the event is extracted from
 * @param <EventT> the type of event that is processed by the handler
 */
public final class ResilientEventHandler<RawMessageT, EventT extends BaseEvent<?>>
    implements InfrastructureEventHandler<RawMessageT, EventT> {

  private static final Logger logger = LoggerFactory.getLogger(ResilientEventHandler.class);

  // TODO: replace with crosscutting.resiliency.Retryble
  private final RetryConfig retryConfig;
  private final RetryableOperationFactory retryableOperationFactory;

  // The actual event handler that processes the event.
  private final Consumer<EventT> applicationEventHandler;

  private final ResilientQueueFactory<RawMessageT, EventT> resilientQueueFactory;

  private final Predicate<EventT> isOutOfOrder;

  private final boolean reorderEvents;

  /** Constructs a new ResilientEventHandler which reorders out of order events. */
  public ResilientEventHandler(
      RetryConfig retryConfig,
      RetryableOperationFactory retryableOperationFactory,
      EventHandler<EventT> applicationEventHandler,
      ResilientQueueFactory<RawMessageT, EventT> resilientQueueFactory,
      Predicate<EventT> isOutOfOrder) {
    this.retryConfig = retryConfig;
    this.retryableOperationFactory = retryableOperationFactory;
    this.applicationEventHandler = applicationEventHandler::handle;
    this.resilientQueueFactory = resilientQueueFactory;
    this.reorderEvents = true;
    this.isOutOfOrder = isOutOfOrder;
  }

  /** Constructs a new ResilientEventHandler which doesn't reorder events. */
  public ResilientEventHandler(
      RetryConfig retryConfig,
      RetryableOperationFactory retryableOperationFactory,
      EventHandler<EventT> applicationEventHandler,
      ResilientQueueFactory<RawMessageT, EventT> resilientQueueFactory) {
    this.retryConfig = retryConfig;
    this.retryableOperationFactory = retryableOperationFactory;
    this.applicationEventHandler = applicationEventHandler::handle;
    this.resilientQueueFactory = resilientQueueFactory;
    this.reorderEvents = false;
    this.isOutOfOrder = null;
  }

  @Override
  public void handle(
      RawMessageT message, Function<RawMessageT, EventT> extractEventFromRawMessage) {
    EventT event;

    // Extract the event from the raw message
    // If the message is invalid (unmarshallable), the event is null, or we cannot
    // extract it due to resource constraints (OutOfMemoryError), it will be placed
    // in the invalid message queue
    try {
      // extractEventFromRawMessage must be configured to filter events of particular types
      // and return one of these types after extracting the event from the raw message
      event = extractEventFromRawMessage.apply(message);
    } catch (Exception e) {
      enqueueToInvalidMessageQueue(message);
      return;
    }

    if (event == null) {
      // unsupported event type pulled off of the queue
      return;
    }

    // TODO: Use crosscutting.resiliency.Retryble
    var retryableOperation = retryableOperationFactory.<EventT>create();
    try {
      retryableOperation.process(
          event,
          // only check order if the handler is configured to do so
          (x) -> {
            if (this.reorderEvents) {
              processWithReordering(x, retryableOperation);
            } else {
              this.applicationEventHandler.accept(x);
            }
          },
          this.retryConfig);
    } catch (TransientException e) {
      // TODO: post-MVP we should consider how we can we can monitor the rate at which
      // transient exceptions are occurring and implement mechanisms for pausing the
      // transport adapter if the rate exceeds a certain threshold for the same
      // types of errors.
      // For example, is 10 transient exceptions occur in 1 minute, but they are all
      // for different reasons, we can keep processing normally, but it they are all
      // because of an interruption in SQS we should pause the transport adapter for
      // 1 minute.
      logger.warn("TransientException encountered. Event handling delayed. Event: {}", event, e);

      // Throw custom exception to signal a delay to the transport adapter
      throw new HandlerDelayException(e, HandleDelay.retryAfter(this.retryConfig.maxDelayMs()));
    } catch (PermanentException e) {
      logger.error(
          "PermanentException encountered. Publishing event to the dead-letter queue. Event: {}",
          event,
          e);
      retryableOperation.process(event, this::enqueueToDeadLetterQueue, this.retryConfig);
    } catch (Exception e) { // publish to dead-letter to avoid poisoned event scenario
      logger.error(
          "Unexpected exception encountered. Publishing event to the dead-letter queue. Event: {}",
          event,
          e);
      retryableOperation.process(event, this::enqueueToDeadLetterQueue, this.retryConfig);
    }
  }

  private void processWithReordering(EventT event, RetryableOperation<EventT> retryableOperation) {
    if (this.isOutOfOrder.test(event)) {
      logger.info("Event is out of order. Skipping processing. Event: {}", event);

      // Causal key from event is passed to queue factory at creation
      retryableOperation.process(event, this::enqueueToReorderQueue, this.retryConfig);
    } else {
      // NOTE: Idempotency checks need to be implemented at the application level.
      // Duplicates should be ignored and no exceptions should be thrown,
      // There are two types of duplication that will need handling: duplication
      // arising from Kinesis (two events with all data identical, including
      // event ID and atomic clock) and duplication from our own system (two
      // events with the same data but different event IDs and atomic clocks).
      this.applicationEventHandler.accept(event);
      retryableOperation.process(event, this::processNextEventFromReorderQueue, this.retryConfig);
    }
  }

  private void processNextEventFromReorderQueue(EventT currentEvent) {
    var nextEvent = peekAtReorderQueue(currentEvent);
    boolean dequeueNextEvent = false;
    // TODO: Use crosscutting.resiliency.Retryble
    var retryableOperation = retryableOperationFactory.<EventT>create();

    if (nextEvent != null && !isOutOfOrder.test(nextEvent)) {
      try {
        // we're processing events off of the reorder queue, so we don't need to check the order
        // when processing
        retryableOperation.process(
            nextEvent, (x) -> this.applicationEventHandler.accept(x), this.retryConfig);
        dequeueNextEvent = true;
      } catch (PermanentException pe) {
        try {
          retryableOperation.process(nextEvent, this::enqueueToDeadLetterQueue, this.retryConfig);
          dequeueNextEvent = true;
        } catch (TransientException te) {
          // In this case we leave the event in the reorder queue
          // The transient exception will be thrown and we will re-process the
          // currentEvent
          // The currentEvent will be recognised as a duplicate and ignored
          // We will inspect the reorderQueue again and process the nextEvent, which will
          // throw the permanent exception again and we will try and put it on the DLQ
          // again
          throw te;
        }
      }
    }

    if (dequeueNextEvent) {
      try {
        retryableOperation.process(nextEvent, this::dequeueFromReorderQueue, this.retryConfig);
      } catch (TransientException te) {
        // In this case we leave the event in the reorder queue
        // The transient exception will be thrown and we will re-process the
        // currentEvent
        // The currentEvent will be recognised as a duplicate and ignored
        // We will inspect the reorderQueue again and process the nextEvent, which will
        // also be recognised as a duplicate and ignored before we try to dequeue it
        // again
        throw te;
      }
    }
  }

  private EventT peekAtReorderQueue(EventT event) {
    // Fix: Fine tune the creation lifetime for the queue
    // Fix: Find a better name? createInvalidMessageQueue seems that a queue is
    // being created
    var queue = this.resilientQueueFactory.createReorderQueue(event);
    try {
      return queue.peek();
    } catch (PermanentException e) {
      // if a permanent exception occurs while trying to publish to the dead-letter queue
      // we should log the error and rethrow the exception
      logger.error("Permanent exception when peeking at reorder queue.", e);
      throw e;
    } catch (TransientException e) {
      // This should only be thrown if the enqueue method isn't configured to retry on a
      // transient exception, or if there are too many transient exceptions
      logger.error("Transient exception when peeking at reorder queue.", e);
      throw e;
    } catch (Exception e) {
      // We should only throw Transient and Permanent Exceptions, but let's log any
      // unclassified ones for investigation
      logger.error("Unclassified exception when peeking at reorder queue. Event: {}", event, e);
      throw e;
    }
  }

  private void dequeueFromReorderQueue(EventT event) {
    // Fix: Fine tune the creation lifetime for the queue
    // Fix: Find a better name? createInvalidMessageQueue it seems that a queue is
    // being created
    var queue = this.resilientQueueFactory.createReorderQueue(event);
    try {
      queue.dequeue(event);
    } catch (PermanentException e) {
      // if a permanent exception occurs while trying to publish to the dead-letter queue,
      // we should log the error and rethrow the exception
      logger.error("Permanent exception when de-queuing from reorder queue. Event: {}", event, e);
      throw e;
    } catch (TransientException e) {
      // This should only be thrown if the enqueue method isn't configured to retry on a
      // transient exception, or if there are too many transient exceptions
      logger.error("Transient exception when de-queuing from reorder queue. Event: {}", event, e);
      throw e;
    } catch (Exception e) {
      // We should only throw Transient and Permanent Exceptions, but let's log any
      // unclassified ones for investigation
      logger.error(
          "Unclassified exception when de-queuing event from reorder queue. Event: {}", event, e);
      throw e;
    }
  }

  private void enqueueToReorderQueue(EventT event) {
    // TODO: Fine tune the creation lifetime for the queue connection
    var queue = this.resilientQueueFactory.createReorderQueue(event);
    try {
      queue.enqueue(event);
    } catch (PermanentException e) {
      // if a permanent exception occurs while trying to publish to the reorder queue
      // we should log the error and rethrow the exception
      logger.error("Permanent exception when enqueuing to reorder queue. Event: {}", event, e);
      throw e;
    } catch (TransientException e) {
      // This should only be thrown if the enqueue method isn't configured to retry on a
      // transient exception, or if there are too many transient exceptions
      logger.error("Transient exception when enqueuing to reorder queue. Event: {}", event, e);
      throw e;
    } catch (Exception e) {
      // We should only throw Transient and Permanent Exceptions, but let's log any
      // unclassified ones for investigation
      logger.error(
          "Unclassified exception when enqueuing event to reorder queue. Event: {}", event, e);
      throw e;
    }
  }

  private void enqueueToInvalidMessageQueue(RawMessageT message) {
    // TODO: Fine tune the creation lifetime for the queue connection
    var queue = this.resilientQueueFactory.createInvalidMessageQueue();
    try {
      queue.enqueue(message);
    } catch (PermanentException e) {
      // if a permanent exception occurs while trying to publish to the invalid
      // message queue we should log the error and rethrow the exception
      logger.error(
          "Permanent exception when enqueuing event to to invalid message queue. Message: {}",
          message,
          e);
      throw e;
    } catch (TransientException e) {
      // This should only be thrown if the enqueue method isn't configured to retry on a
      // transient exception, or if there are too many transient exceptions
      logger.error(
          "Transient exception when enqueuing event to invalid message queue. Message: {}",
          message,
          e);
      throw e;
    } catch (Exception e) {
      // We should only throw Transient and Permanent Exceptions, but let's log any
      // unclassified ones for investigation
      logger.error(
          "Unclassified exception when enqueuing event to invalid message queue. Message: {}",
          message,
          e);
      throw e;
    }
  }

  private void enqueueToDeadLetterQueue(EventT event) {
    // TODO: Fine tune the creation lifetime for the queue connection
    var queue = this.resilientQueueFactory.createDeadLetterQueue();
    try {
      queue.enqueue(event);
    } catch (PermanentException e) {
      // if a permanent exception occurs while trying to publish to the dead-letter queue
      // we should log the error and rethrow the exception
      logger.error("Permanent exception when enqueuing event to to DLQ. Event: {}", event, e);
      throw e;
    } catch (TransientException e) {
      // This should only be thrown if the enqueue method isn't configured to retry on a
      // transient exception, or if there are too many transient exceptions
      logger.error("Transient exception when enqueuing event to DLQ. Event: {}", event, e);
      throw e;
    } catch (Exception e) {
      // We should only throw Transient and Permanent Exceptions, but let's log any
      // unclassified ones for investigation
      logger.error("Unclassified exception when enqueuing event to DLQ. Event: {}", event, e);
      throw e;
    }
  }
}
