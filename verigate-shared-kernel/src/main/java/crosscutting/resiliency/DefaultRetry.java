/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.resiliency;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Default implementation of the {@link Retryable} interface. Provides a mechanism to retry
 * operations based on a specified configuration.
 */
public final class DefaultRetry implements Retryable {

  private final RetryConfig config;

  /**
   * Constructs a new {@code DefaultRetry} with the specified maximum attempts, initial wait
   * duration, and set of retryable exceptions. The actual wait duration increases exponentially
   * between requests, scaling like Math.pow(2, numberOfRetries).
   *
   * @param maxAttempts the maximum number of retry attempts
   * @param waitDuration the initial duration to wait between retry attempts. The actual duration
   *     between retries increases exponentially.
   * @param retryExceptions the set of exceptions that should trigger a retry
   */
  public DefaultRetry(
      int maxAttempts, Duration waitDuration, Set<Class<? extends Throwable>> retryExceptions) {

    this(maxAttempts, waitDuration, retryExceptions, Set.of());
  }

  /**
   * Constructs a new {@code DefaultRetry} with the specified maximum attempts, initial wait
   * duration, and set of retryable exceptions. The actual wait duration increases exponentially
   * between requests, scaling like Math.pow(2, numberOfRetries).
   *
   * @param maxAttempts the maximum number of retry attempts
   * @param waitDuration the initial duration to wait between retry attempts. The actual duration
   *     between retries increases exponentially.
   * @param retryExceptions the set of exceptions that should trigger a retry
   * @param ignoreExceptions the set of exceptions that should not trigger a retry
   */
  public DefaultRetry(
      int maxAttempts, Duration waitDuration,
      Set<Class<? extends Throwable>> retryExceptions,
      Set<Class<? extends Throwable>> ignoreExceptions) {

    var builder =
        RetryConfig.custom()
            .maxAttempts(maxAttempts)
            .intervalFunction(IntervalFunction.ofExponentialBackoff(waitDuration, 2));

    retryExceptions.forEach(builder::retryExceptions);
    ignoreExceptions.forEach(builder::ignoreExceptions);
    this.config = builder.build();
  }

  /**
   * Constructs a new {@code DefaultRetry} with the specified configuration.
   * This gives maximum flexibility to users of this class.
   */
  public DefaultRetry(RetryConfig retryConfig) {
    this.config = retryConfig;
  }

  /**
   * Creates a {@code Runnable} that will be retried according to the retry configuration.
   *
   * @param name the name of the retry configuration
   * @param runnable the original {@code Runnable} to be retried
   * @return a new {@code Runnable} that will be retried according to the configuration
   */
  @Override
  public Runnable createRunnable(String name, Runnable runnable) {
    Retry retry = Retry.of(name, this.config);
    return Retry.decorateRunnable(retry, runnable);
  }

  @Override
  public <T> Callable<T> createCallable(String name, Callable<T> callable) {
    Retry retry = Retry.of(name, this.config);
    return Retry.decorateCallable(retry, callable);
  }
}
