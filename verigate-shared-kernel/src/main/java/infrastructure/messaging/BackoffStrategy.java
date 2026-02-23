/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.messaging;

import javax.validation.constraints.NotNull;

/**
 * The {@code BackoffStrategy} interface defines a method to calculate the delay before a subsequent
 * attempt to process or deliver a message, after a previous attempt has failed. Implementing
 * different backoff strategies allows the system to adapt its retry mechanisms according to the
 * nature of the failure and the current system load, thereby enhancing overall resilience and
 * efficiency.
 *
 * <p>The calculation of the delay can be based on various strategies such as fixed, exponential, or
 * even more sophisticated adaptive methods, depending on the requirements and behavior of the
 * system under different load conditions.
 *
 * @implNote Implementors of this interface are expected to ensure that the calculated delay is
 *     within the bounds of the specified {@code baseDelayMs} and {@code maxDelay}, and must return
 *     a non-negative value.
 */
public interface BackoffStrategy {

  /**
   * Calculates the delay in milliseconds before a subsequent attempt should be made, based on the
   * number of previous attempts, a base delay, and a maximum allowable delay. This method ensures
   * that the system dynamically adjusts its retry intervals, potentially preventing rapid
   * successive retries that could overwhelm the system or degrade its performance.
   *
   * @param attempt the number of the attempt, starting from 1 for the first retry.
   * @param baseDelayMs the base delay in milliseconds to be used as a reference for calculating the
   *     actual delay. This is often the delay for the first retry attempt.
   * @param maxDelay the maximum delay in milliseconds that can be applied, ensuring that the retry
   *     mechanism does not introduce excessive delays.
   * @return the calculated delay in milliseconds for the next attempt, which must be a non-negative
   *     value and should not exceed {@code maxDelay}.
   */
  @NotNull
  long calculateDelay(int attempt, long baseDelayMs, long maxDelay);
}
