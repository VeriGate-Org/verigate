/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.metrics;

/**
 * For publishing metrics.
 * Can be invoked directly, or using the {@code io.micrometer.core.annotation.Counted} annotation.
 *
 * @see crosscutting.metrics.CountedMethodInterceptor
 */
public interface Meter {

  /**
   * Increments a counter for the given metric name.
   *
   * @param tags Each element can be a simple tag, like "hello",
   *            or a named key:value, like "region:eu-west-1"
   */
  void incrementCounter(String metricName, String... tags);

  /**
   * Sets a value on a gauge.
   *
   * @param measure the current value of the gauge
   * @param tags Each element can be a simple tag, like "hello",
   *             or a named key:value, like "region:eu-west-1"
   */
  void gauge(String metricName, double measure, String... tags);
}
