/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.exceptions;

/**
 * Represents a delay before a certain action can be retried or proceeded with. This record is
 * useful for handling operations that require a waiting period, such as retrying a failed request
 * or enforcing a pause between tasks. The delay is specified in milliseconds.
 *
 * <p>This record provides a concise way to express waiting times, improving code readability and
 * making it easier to work with delay-related logic.
 *
 * <p>Static factory methods {@link #immediate()} and {@link #retryAfter(long)} are provided for
 * convenience and enhanced code readability, allowing for the expressive creation of {@code
 * HandleDelay} instances.
 *
 * @param delayToNext The delay in milliseconds before the next action should be taken. A delay of 0
 *     indicates that the action can be taken immediately.
 */
public record HandleDelay(long delayToNext) {

  /**
   * Creates a {@code HandleDelay} instance representing no delay, allowing for immediate action.
   * This is particularly useful for operations that can proceed without any waiting time.
   *
   * @return A {@code HandleDelay} instance with a delay of 0 milliseconds, indicating an immediate
   *     action.
   */
  public static HandleDelay immediate() {
    return new HandleDelay(0);
  }

  /**
   * Creates a {@code HandleDelay} instance with a specified delay. This factory method enhances
   * readability when creating {@code HandleDelay} instances, clearly indicating the intention to
   * retry an action after a specified waiting period.
   *
   * @param delayMs The delay in milliseconds before retrying or proceeding with an action.
   * @return A {@code HandleDelay} instance representing the specified delay.
   */
  public static HandleDelay retryAfter(long delayMs) {
    return new HandleDelay(delayMs);
  }
}
