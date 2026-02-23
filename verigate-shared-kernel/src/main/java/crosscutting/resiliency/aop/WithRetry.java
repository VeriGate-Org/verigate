/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.resiliency.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate that a method should be retried.
 * <p/>
 * Example usage:
 * <pre>
 *   &#64;WithRetry(ignoreExceptions = {PermanentException.class})
 *   public GreetingDto get() {
 *     ...
 * </pre>
 * <p/>
 * <b>Note: Guice AOP places specific constraints on classes for annotations to be intercepted.</b>
 * Specifically, Guice needs to be able to override the method (via subclassing) and instantiate
 * the class itself.
 * See <a href="https://github.com/google/guice/wiki/AOP">Guice AOP</a> for more information.
 * <p/>
 * To bind in Guice:
 * <pre>
 *     bindInterceptor(Matchers.any(), new WithRetryMethodMatcher(),
 *         new WithRetryMethodInterceptor());
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.METHOD)
public @interface WithRetry {

  /**
   * The maximum number of retry attempts.
   */
  int maxRetries() default 3;

  /**
   * The duration to wait between retry attempts.
   * This is used to seed the backoff strategy.
   */
  int waitDurationMs() default 500;

  /**
   * If / how to back off between retries.
   */
  BackoffStrategy backoffStrategy() default BackoffStrategy.EXPONENTIAL;

  /**
   * Exceptions that should trigger a retry.
   * If empty, all exceptions will trigger a retry.
   */
  Class<? extends Throwable>[] retryExceptions() default {};

  /**
   * Exceptions that should not trigger a retry.
   */
  Class<? extends Throwable>[] ignoreExceptions() default {};
}
