/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.messaging;

import domain.exceptions.CircuitState;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import infrastructure.event.handler.RetryConfig;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@code BlockingRetryableOperation} class implements the {@link RetryableOperation} interface,
 * providing a mechanism to retry operations on messages with a configurable number of attempts and
 * delays. This class uses a blocking approach to wait between retries, utilizing a specified {@link
 * BackoffStrategy} to determine the delay periods, thus effectively handling transient failures in
 * message processing.
 *
 * @param <MessageT> the type of the message to be processed, allowing for flexibility in the types
 *     of messages that can be handled by this operation.
 */
public final class BlockingRetryableOperation<MessageT> implements RetryableOperation<MessageT> {

  private static final Logger logger = LoggerFactory.getLogger(BlockingRetryableOperation.class);

  private final BackoffStrategy backoffStrategy; // Strategy to calculate delay between retries

  /**
   * Constructs a new {@code BlockingRetryableOperation} with the given backoff strategy.
   *
   * @param backoffStrategy the {@link BackoffStrategy} to use for calculating delay periods between
   *     retry attempts.
   */
  public BlockingRetryableOperation(BackoffStrategy backoffStrategy) {
    this.backoffStrategy = backoffStrategy;
  }

  /**
   * Processes the given message using the provided operation. If the operation fails due to a
   * {@link TransientException}, this method will retry the operation according to the configured
   * backoff strategy until either the operation succeeds or the maximum number of retry attempts is
   * reached.
   *
   * @param message the message to be processed.
   * @param operation the consumer operation that processes the message.
   * @throws PermanentException if the operation consistently fails after the maximum number of
   *     retries or if a {@link TransientException} occurs with a circuit state that is not {@link
   *     CircuitState#CLOSED}.
   */
  @Override
  public void process(MessageT message, Consumer<MessageT> operation, RetryConfig retryConfig)
      throws PermanentException {
    int attempt = 0;
    boolean successful = false;
    long delay = retryConfig.baseDelayMs();

    logger.info(
        "Starting message processing. Max retry attempts: "
            + retryConfig.maxRetryAttempts()
            + ". Base delay: "
            + retryConfig.baseDelayMs()
            + "ms. Max delay: "
            + retryConfig.maxDelayMs()
            + "ms.");

    while (attempt < retryConfig.maxRetryAttempts() && !successful) {
      try {
        logger.debug("Attempting operation. Attempt number: " + (attempt + 1));

        operation.accept(message);
        successful = true; // Operation succeeded, exit loop
        logger.debug("Message processing successful on attempt " + (attempt + 1));
      } catch (TransientException e) {
        logger.warn(
            "Transient exception caught on attempt "
                + (attempt + 1)
                + ". Exception: "
                + e.getMessage());
        if (e.getCircuitState() != CircuitState.CLOSED) {
          logger.warn("Circuit state is not CLOSED. Escalating exception and aborting retries.");
          throw e; // Non-retriable state, escalate exception
        }

        attempt++;
        if (attempt >= retryConfig.maxRetryAttempts()) {
          // Exceeded max retries, rethrowing to TransientException
          logger.error("Max retry attempts exceeded. Throwing TransientException.");
          throw e;
        }

        delay =
            backoffStrategy.calculateDelay(
                attempt, delay, retryConfig.maxDelayMs()); // Calculate next delay
        logger.warn(
            String.format(
                "Event processing failure on attempt %d. Implementing a backoff strategy with a "
                    + "%dms delay before the next retry. Maximum allowed delay is %dms.",
                attempt, delay, retryConfig.maxDelayMs()));
        sleepUntil(delay); // Wait for the calculated delay before retrying
      }
    }

    if (!successful) {
      logger.error(
          "Message processing failed after " + retryConfig.maxRetryAttempts() + " attempts.");
    }
  }

  /**
   * Pauses the current thread for the specified delay period.
   *
   * @param delay the delay period in milliseconds.
   */
  private static void sleepUntil(long delay) {
    logger.debug("Preparing to sleep for " + delay + "ms to implement backoff strategy.");

    try {
      TimeUnit.MILLISECONDS.sleep(delay); // Block current thread for the delay period
      logger.debug("Successfully slept for " + delay + "ms, resuming operations.");
    } catch (InterruptedException ie) {
      logger.error(
          "Sleep interrupted unexpectedly. Interrupting current thread and aborting retry.", ie);
      Thread.currentThread()
          .interrupt(); // Re-interrupt the thread to preserve the interrupted status
      throw new RuntimeException("Publish retry interrupted", ie);
    }
  }
}
