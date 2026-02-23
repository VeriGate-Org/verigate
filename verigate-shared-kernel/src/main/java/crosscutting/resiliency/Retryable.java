/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.resiliency;

import java.util.concurrent.Callable;

/** Interface for creating retryable {@link Runnable} instances. */
public interface Retryable {

  /**
   * Creates a {@code Runnable} that can be retried according to a retry policy.
   *
   * @param name the name of the retry configuration
   * @param runnable the original {@code Runnable} to be retried
   * @return a new {@code Runnable} that will be retried according to the configuration
   */
  Runnable createRunnable(String name, Runnable runnable);

  /**
   * Creates a {@code Callable} that can be retried according to a retry policy.
   *
   * @param name the name of the retry configuration
   * @param callable the original {@code Callable} to be retried
   * @return a new {@code Callable} that will be retried according to the configuration
   */
  <T> Callable<T> createCallable(String name, Callable<T> callable);
}
