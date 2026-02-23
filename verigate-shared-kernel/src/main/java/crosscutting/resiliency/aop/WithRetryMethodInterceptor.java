/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.resiliency.aop;

import crosscutting.resiliency.DefaultRetry;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.RetryConfig;
import java.time.Duration;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Interceptor of {@link WithRetry} annotation.
 *
 * @see WithRetry
 */
public final class WithRetryMethodInterceptor implements MethodInterceptor {


  /** See class-level description. */
  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {

    final var annotation = invocation.getMethod().getAnnotation(WithRetry.class);

    // configure the retry. See https://resilience4j.readme.io/docs/retry

    final var configBuilder = RetryConfig.custom()
        .maxAttempts(annotation.maxRetries())
        .retryExceptions(annotation.retryExceptions())
        .ignoreExceptions(annotation.ignoreExceptions());


    // configure backoff strategy as per annotation. If we need to support more complex config
    // we could add more parameters to the annotation, or introduce new annotations.

    switch (annotation.backoffStrategy()) {
      case NONE:
        configBuilder.waitDuration(Duration.ofMillis(annotation.waitDurationMs()));
        break;
      case EXPONENTIAL:
        configBuilder.intervalFunction(IntervalFunction.ofExponentialBackoff(
            Duration.ofMillis(annotation.waitDurationMs()), 2));
        break;
      default:
        throw new IllegalArgumentException(annotation.backoffStrategy().name());
    }

    final var retryConfig = configBuilder.build();

    // wrap the underlying method call in the configured retry logic, and call.

    return new DefaultRetry(retryConfig).createCallable(
        invocation.getClass().getName() + "." + invocation.getMethod().getName(), () -> {
          try {
            return invocation.proceed();
          } catch (Throwable e) { // declared to be thrown by MethodInvocation.proceed()
            sneakyThrow(e); // to avoid translating into Exception as per Callable.call() signature
            return Void.TYPE; // never reached
          }
        }
    ).call();
  }

  /**
   * Workaround to throw Throwable from within the Callable without transforming into Exception.
   * This unusual case is needed because:
   * <li>invocation.proceed() throws Throwable;</li>
   * <li>Callable.call() (in which invocation.proceed() is invoked) only throws Exception;</li>
   * <li>we want to honour the exception type being thrown without translating it,
   *   to honour the types specified in @WithRetry.retryExceptions and
   *   &#64;WithRetry.ignoreException.</li>
   * <p/>
   * An alternative would be to modify crosscutting.resiliency.DefaultRetry to accept
   * a (new) CheckedFunction, similar to that provided by Resilience4J and others.
   * However, this was seen as an isolated case.
   */
  private static <E extends Throwable> void sneakyThrow(Throwable e) throws E {
    throw (E) e;
  }
}
