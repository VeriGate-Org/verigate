/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.event.handler.factories;

import domain.exceptions.PermanentException;
import domain.messages.MessageQueue;
import infrastructure.event.handler.RetryConfig;
import infrastructure.event.handler.RetryMessageQueue;
import infrastructure.messaging.RetryableOperation;

/**
 * A factory class that implements {@link ResilientQueueFactory}.
 *
 * @param <RawMessageT> the type of raw message the invalid message queue will handle
 * @param <EventT> the type of event the dead letter queue will handle
 */
public final class DefaultResilientQueueFactory<RawMessageT, EventT>
    implements ResilientQueueFactory<RawMessageT, EventT> {

  private final MessageQueue<RawMessageT> invalidMessageQueue;
  private final RetryableOperation<RawMessageT> invalidMessageRetryableOperation;
  private final MessageQueue<EventT> deadLetterQueue;
  private final RetryableOperation<EventT> deadLetterRetryableOperation;
  private final MessageQueue<EventT> reorderQueue;
  private final RetryableOperation<EventT> reorderQueueRetryableOperation;
  private final RetryConfig retryConfig;

  /**
   * Constructs a new instance with the specified components.
   *
   * @param invalidMessageQueue for handling invalid raw messages
   * @param invalidMessageRetryableOperation the retryable operation associated with publishing
   *     invalid messages
   * @param deadLetterQueue for handling events that should be moved to a dead
   *     letter queue
   * @param deadLetterRetryableOperation the retryable operation associated with publishing to the
   *     dead letter queue
   * @param retryConfig the configuration settings for retry operations
   */
  public DefaultResilientQueueFactory(
      MessageQueue<RawMessageT> invalidMessageQueue,
      RetryableOperation<RawMessageT> invalidMessageRetryableOperation,
      MessageQueue<EventT> deadLetterQueue,
      RetryableOperation<EventT> deadLetterRetryableOperation,
      MessageQueue<EventT> reorderQueue,
      RetryableOperation<EventT> reorderQueueRetryableOperation,
      RetryConfig retryConfig) {
    this.invalidMessageQueue = invalidMessageQueue;
    this.invalidMessageRetryableOperation = invalidMessageRetryableOperation;
    this.deadLetterQueue = deadLetterQueue;
    this.deadLetterRetryableOperation = deadLetterRetryableOperation;
    this.reorderQueue = reorderQueue;
    this.reorderQueueRetryableOperation = reorderQueueRetryableOperation;
    this.retryConfig = retryConfig;
  }

  /**
   * Constructs a new instance with the specified components and sets reorder queue to null.
   *
   * @param invalidMessageQueue for handling invalid raw messages
   * @param invalidMessageRetryableOperation the retryable operation associated with publishing
   *     invalid messages
   * @param deadLetterQueue for handling events that should be moved to a dead
   *     letter queue
   * @param deadLetterRetryableOperation the retryable operation associated with publishing to the
   *     dead letter queue
   * @param retryConfig the configuration settings for retry operations
   */
  public DefaultResilientQueueFactory(
      MessageQueue<RawMessageT> invalidMessageQueue,
      RetryableOperation<RawMessageT> invalidMessageRetryableOperation,
      MessageQueue<EventT> deadLetterQueue,
      RetryableOperation<EventT> deadLetterRetryableOperation,
      RetryConfig retryConfig) {
    this.invalidMessageQueue = invalidMessageQueue;
    this.invalidMessageRetryableOperation = invalidMessageRetryableOperation;
    this.deadLetterQueue = deadLetterQueue;
    this.deadLetterRetryableOperation = deadLetterRetryableOperation;
    this.retryConfig = retryConfig;

    this.reorderQueue = null;
    this.reorderQueueRetryableOperation = null;
  }

  /**
   * Creates and returns a queue for dead letter messages, incorporating retry
   * mechanisms.
   */
  @Override
  public MessageQueue<EventT> createDeadLetterQueue() {
    return new RetryMessageQueue<>(
        this.deadLetterQueue, this.deadLetterRetryableOperation, this.retryConfig);
  }

  /**
   * Creates and returns a queue for invalid raw messages, incorporating
   * retry mechanisms.
   */
  @Override
  public MessageQueue<RawMessageT> createInvalidMessageQueue() {
    return new RetryMessageQueue<>(
        this.invalidMessageQueue, this.invalidMessageRetryableOperation, this.retryConfig);
  }

  /**
   * Creates and returns a queue reordering messages, incorporating retry
   * mechanisms.
   */
  @Override
  public MessageQueue<EventT> createReorderQueue(EventT event) {
    if (this.reorderQueue == null) {
      throw new PermanentException("Reorder queue is not supported");
    }

    return new RetryMessageQueue<>(
        this.reorderQueue, this.reorderQueueRetryableOperation, this.retryConfig);
  }
}
