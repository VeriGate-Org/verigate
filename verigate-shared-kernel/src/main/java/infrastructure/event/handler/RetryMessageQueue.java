/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.event.handler;

import domain.exceptions.HandleDelay;
import domain.exceptions.HandlerDelayException;
import domain.messages.MessageQueue;
// TODO: refactor this with crosscutting.resiliency
import infrastructure.messaging.RetryableOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles the enqueueing of messages with retry capabilities. This class
 * wraps a {@link MessageQueue} and a {@link RetryableOperation} along with a {@link RetryConfig}
 * to manage retries of message enqueueing in case of failures. It is designed to increase the
 * robustness of message delivery systems by ensuring that messages are retried according to the
 * specified retry policy.
 *
 * @param <T> the type of message this handles
 * @see domain.events.RetryEventPublisher
 * @see domain.commands.RetryCommandDispatcher
 */
public final class RetryMessageQueue<T> extends MessageQueueDecorator<T> {

  private static final Logger logger = LoggerFactory.getLogger(RetryMessageQueue.class);

  private final RetryableOperation<T> retryableOperation;
  private final RetryConfig retryConfig;

  /**
   * Constructs a new instance.
   *
   * @param messageQueue the original message queue that will attempt to enqueue messages
   * @param retryableOperation the operation that encapsulates retry logic
   * @param retryConfig configuration settings for retries, including max attempts and delay
   *     parameters
   */
  public RetryMessageQueue(
      MessageQueue<T> messageQueue,
      RetryableOperation<T> retryableOperation,
      RetryConfig retryConfig) {

    super(messageQueue);
    this.retryableOperation = retryableOperation;
    this.retryConfig = retryConfig;
  }

  /**
   * Attempts to enqueue a message with retry capabilities. If initial attempt fails, the message
   * is retried according to the retry configuration provided.
   *
   * @param message the message to be enqueued
   * @throws HandlerDelayException if the enqueuing fails even after retries, encapsulating the
   *     failure reason and suggesting a retry delay based on {@link RetryConfig#maxDelayMs()}.
   */
  @Override
  public void enqueue(T message) {
    logger.debug("Initiating retryable enqueueing for message: " + message);
    try {
      this.retryableOperation.process(message, super::enqueue, this.retryConfig);
    } catch (Exception e) {
      logger.error("Failed to enqueue message: " + message, e);
      // If enqueueing fails, encapsulate the error in a custom exception.
      throw new HandlerDelayException(e, HandleDelay.retryAfter(retryConfig.maxDelayMs()));
    }
  }

  
  /**
   * Attempts to dequeue a message with retry capabilities. If initial attempt fails, the message
   * is retried according to the retry configuration provided.
   *
   * @param message the message to be dequeued
   * @throws HandlerDelayException if the dequeueing fails even after retries, encapsulating the
   *     failure reason and suggesting a retry delay based on {@link RetryConfig#maxDelayMs()}.
   */
  @Override
  public void dequeue(T message) {
    logger.debug("Initiating retryable dequeueing for message: " +  message);
    try {
      this.retryableOperation.process(message, super::dequeue, this.retryConfig);
    } catch (Exception e) {
      logger.error("Failed to dequeue message: " + message, e);
      // If dequeueing fails, encapsulate the error in a custom exception.
      throw new HandlerDelayException(e, HandleDelay.retryAfter(retryConfig.maxDelayMs()));
    }
  }

  /**
   * Attempts to peek at the top message with retry capabilities. If initial attempt fails, the
   * message is retried according to the retry configuration provided.
   *
   * @throws HandlerDelayException if the peeking fails even after retries, encapsulating the
   *     failure reason and suggesting a retry delay based on {@link RetryConfig#maxDelayMs()}.
   */
  @Override
  public T peek() {
    logger.debug("Initiating retryable peek for queue");
    try {
      // TODO: implement this with retry
      return super.peek();
    } catch (Exception e) {
      logger.error("Failed to peek at message");
      // If peeking fails, encapsulate the error in a custom exception.
      throw new HandlerDelayException(e, HandleDelay.retryAfter(retryConfig.maxDelayMs()));
    }
  }
}
