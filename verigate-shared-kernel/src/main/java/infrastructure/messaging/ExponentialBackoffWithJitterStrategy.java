/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.messaging;

import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements an exponential backoff strategy with jitter to prevent simultaneous retry spikes by
 * spreading out retries using a sigmoid-based delay calculation with added randomness.
 * This approach helps in mitigating the thundering herd problem in client retries.
 * References:
 * AWS Prescriptive Guidance - <a href="https://docs.aws.amazon.com/prescriptive-guidance/latest/cloud-design-patterns/retry-backoff.html">Retry Backoff</a>,
 * Wikipedia - <a href="https://en.wikipedia.org/wiki/Exponential_backoff">Exponential Backoff</a>.
 */
public final class ExponentialBackoffWithJitterStrategy implements BackoffStrategy {

  private static final Logger logger =
      LoggerFactory.getLogger(ExponentialBackoffWithJitterStrategy.class);

  /**
   * Calculates the delay before the next attempt, incorporating jitter.
   *
   * @param attempt The number of the current retry attempt, starting from 1.
   * @param baseDelayMs The base delay in milliseconds for the backoff calculations.
   * @param maxDelay The maximum allowable delay.
   * @return The calculated delay, in milliseconds, before the next retry attempt.
   * @throws IllegalArgumentException if the attempt number is less than or equal to 0, or if the
   *     baseDelayMs is less than or equal to 0.
   */
  @Override
  public long calculateDelay(int attempt, long baseDelayMs, long maxDelay) {
    if (attempt <= 0) {
      logger.error("Attempt must be greater than 0. Provided: {}", attempt);
      throw new IllegalArgumentException("Attempt must be greater than 0");
    }

    if (baseDelayMs <= 0) {
      logger.error("Base delay must be greater than 0. Provided: {}", baseDelayMs);
      throw new IllegalArgumentException("Base delay must be greater than 0");
    }

    // Calculate the maximum number of attempts before delay exceeds Long.MAX_VALUE
    int maxAttempt = (int) (Math.log((double) Long.MAX_VALUE / baseDelayMs) / Math.log(2) + 1);
    attempt = Math.min(attempt, maxAttempt);

    logger.debug("Calculating delay for attempt number {}", attempt);

    // Parameters for the sigmoid function
    double k = 0.7; // Steepness of the curve
    double x0 = 10; // Midpoint of the sigmoid curve
    long delay = calculateSigmoidDelay(attempt, maxDelay, k, x0);

    long jitterBound = delay / 3; // Calculate bound for jitter

    // Ensure jitterBound is at least 1
    jitterBound = Math.max(1, jitterBound);

    // Calculate jitter within the bounds
    long jitter = ThreadLocalRandom.current().nextLong(-jitterBound, jitterBound + 1);

    logger.debug("Calculated delay: {}, Jitter: {}", delay, jitter);

    // Ensure the final delay is at least 1 ms
    return Math.max(1, delay + jitter);
  }

  /**
   * Calculates delay using a sigmoid function to smoothly increase delay as attempts increase,
   * capped at the specified maxDelay. See: <a
   * href="https://en.wikipedia.org/wiki/Sigmoid_function">...</a>
   *
   * @param attempt The current retry attempt.
   * @param maxDelay The maximum delay allowed.
   * @param k The steepness of the sigmoid curve.
   * @param x0 The midpoint of the sigmoid curve where delay growth changes pace.
   * @return Calculated delay time in milliseconds based on the sigmoid function.
   */
  public static long calculateSigmoidDelay(int attempt, double maxDelay, double k, double x0) {
    // Calculate the delay using a sigmoid function for smooth increase
    double delay = maxDelay / (1 + Math.exp(-k * (attempt - x0)));

    logger.debug("Sigmoid delay calculation for attempt {}: {}", attempt, delay);

    // Ensure at least a minimal delay
    return Math.max(1, (long) delay);
  }
}
