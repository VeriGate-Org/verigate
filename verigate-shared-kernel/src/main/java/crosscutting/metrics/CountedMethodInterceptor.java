/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.metrics;

import static org.apache.commons.lang3.ArrayUtils.addAll;

import com.google.inject.Inject;
import io.micrometer.core.annotation.Counted;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Method interceptor that increments a Micrometer counter as configured in the
 * {@code io.micrometer.core.annotation.Counted} annotation.
 * This allows for a declarative approach - using annotations - to measuring.
 * <p/>
 * To bind in Guice:
 * <pre>
 *   bindInterceptor(Matchers.any(), Matchers.annotatedWith(Counted.class),
 *             countedMethodInterceptor);
 * </pre>
 * <p/>
 * Example use of annotation:
 * <pre>
 *   &#64;Counted(value = "process_spot_file", extraTags = "provider:qlink_xml",
 *       recordFailuresOnly = false)
 *   void someMethod() {
 *     ...
 * </pre>
 * This implementation:
 * <ul>
 *   <li>adds its own "result" tag with value "success" or "failure" depending on whether the
 *   invoked method returns normally or throws an exception.</li>
 *   <li>will not report on successes if "recordFailuresOnly = true"</li>
 * </ul>
 * If configured to use the {@link crosscutting.metrics.DatadogMeter}, it:
 * <ul>
 *   <li>will prefix the metric with the value of the "DD_SERVICE" environment variable,
 *   followed by a "."</li>
 *   <li>will replace all occurrences of "-" with "_" in the metric name</li>
 * </ul>
 * Note that the annotated method must be overridable for the interceptor to be invoked.
 * <p/>
 * Alternatively to using this annotation, you can invoke {@code Meter} directly.
 *
 * @see crosscutting.metrics.Meter
 * @see crosscutting.metrics.DatadogMeter
 */
public final class CountedMethodInterceptor implements MethodInterceptor {

  /** key:value tag indicating a failure result. */
  private static final String RESULT_FAILURE = "result:failure";

  /** key:value tag indicating a success result. */
  private static final String RESULT_SUCCESS = "result:success";

  private final Meter meter;

  @Inject
  public CountedMethodInterceptor(Meter meter) {
    this.meter = meter;
  }

  /** See class-level description. */
  @Override
  public Object invoke(MethodInvocation methodInvocation) throws Throwable {

    final var annotation = methodInvocation.getMethod().getAnnotation(Counted.class);
   
    final String name = annotation.value();
    final String[] tags = annotation.extraTags();
    final boolean failuresOnly = annotation.recordFailuresOnly();

    try {
      final var returnVal = methodInvocation.proceed();
      if (!failuresOnly) {
        increment(name, addAll(tags, RESULT_SUCCESS));
      }
      return returnVal;
    } catch (Throwable t) {
      increment(name, addAll(tags, RESULT_FAILURE));
      throw t;
    }
  }

  private void increment(String name, String[] tags) {
    meter.incrementCounter(name, tags);
  }
}
