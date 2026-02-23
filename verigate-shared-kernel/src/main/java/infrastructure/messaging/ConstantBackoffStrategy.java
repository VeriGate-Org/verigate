/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.messaging;

/**
 * The {@code ConstantBackoffStrategy} class implements the {@link BackoffStrategy} interface to
 * provide a constant delay between retries. This class always returns the maximum delay specified,
 * regardless of the attempt number or the base delay, effectively ignoring any escalation in delay
 * based on the number of retry attempts.
 *
 * <p>While simple, this approach can be beneficial in systems where a constant delay is deemed
 * appropriate for allowing the system or resource to recover from transient errors, without the
 * complexity of calculating escalating delays.
 */
public final class ConstantBackoffStrategy implements BackoffStrategy {

  /**
   * Calculates the delay before a subsequent retry attempt. In this implementation, the delay
   * returned is always the maximum delay specified, regardless of the current attempt number or the
   * base delay.
   *
   * @param attempt the number of the current attempt. This parameter is ignored in this
   *     implementation.
   * @param baseDelayMs the base delay in milliseconds, intended as the delay for the first attempt.
   *     This parameter is ignored in this implementation.
   * @param maxDelay the maximum delay in milliseconds. This value is returned as the delay for all
   *     attempts.
   * @return the constant delay in milliseconds to be used before the next retry attempt, which is
   *     the specified {@code maxDelay}.
   */
  @Override
  public long calculateDelay(int attempt, long baseDelayMs, long maxDelay) {
    return maxDelay; // Always return the maximum delay
  }
}
