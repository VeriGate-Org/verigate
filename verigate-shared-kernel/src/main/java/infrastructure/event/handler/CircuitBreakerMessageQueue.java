/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.event.handler;

import crosscutting.resiliency.CircuitBreaker;
import domain.exceptions.CircuitState;
import domain.exceptions.TransientException;
import domain.messages.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles the enqueueing of messages with circuit breaking capabilities. This class
 * wraps a {@link MessageQueue}
 * to break the . It is designed to increase the
 * robustness of message delivery systems by ensuring that messages are retried according to the
 * specified retry policy.
 *
 * @param <T> the type of message this handles
 * @see RetryableMessagePublisher
 */
public final class CircuitBreakerMessageQueue<T> extends MessageQueueDecorator<T> {

  private static final Logger logger =
      LoggerFactory.getLogger(CircuitBreakerMessageQueue.class);

  private final CircuitBreaker circuitBreaker;

  /**
   * Constructs a new instance.
   *
   * @param messageQueue the original message queue that will attempt to enqueue messages
   */
  public CircuitBreakerMessageQueue(MessageQueue<T> messageQueue, CircuitBreaker circuitBreaker) {
    super(messageQueue);
    this.circuitBreaker = circuitBreaker;
    logger.debug("Initialized CircuitBreakerMessageQueue with circuit breaker: {}", circuitBreaker);
  }

  /** {@inheritDoc} */
  @Override
  public void enqueue(T message) {
    try {
      logger.debug("Attempting to enqueue message through circuit breaker");

      circuitBreaker.executeRunnable(() -> super.enqueue(message));

      logger.debug("Message successfully enqueued through circuit breaker: {}", message);

    } catch (CircuitBreaker.CircuitBreakerOpenException e) {
      logger.error("Circuit breaker is open: {}", e.getMessage(), e);
      throw new TransientException(e, CircuitState.OPEN, "Circuit breaker is open");
    }
  }
}
