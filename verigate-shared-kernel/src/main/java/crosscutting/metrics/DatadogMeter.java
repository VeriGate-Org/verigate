/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.metrics;

import com.timgroup.statsd.NonBlockingStatsDClientBuilder;
import com.timgroup.statsd.StatsDClient;
import crosscutting.environment.Environment;

/**
 * {@code Meter} implementation that uses Datadog's {@code StatsDClient} for publishing metrics.
 * This implementation
 *
 * <ul>
 *   <li>will prefix the metric with the value of the <code>DD_SERVICE</code> environment variable,
 *   followed by a "."</li>
 *   <li>will replace all occurrences of "-" with "_" in the metric name</li>
 * </ul>
 * <p>
 * See <a href="https://docs.datadoghq.com/serverless/aws_lambda/metrics/">Datadog docs</a>.
 * <h2>Implementation Note</h2>
 * In our context of AWS Lambda functions, you should not invoke {@code count()} or {@code gauge()}
 * on {@code StatsDClient} but only {@code recordDistributionValue()} for achieving counters
 * and gauges respectively.
 * Datadog does not support counter metrics in serverless environments because of the undercounting
 * issue they cause.
 * See <a href="https://docs.datadoghq.com/serverless/aws_lambda/metrics/#understanding-distribution-metrics">here</a>.
 * This was confirmed by testing and by the Datadog team.
 * </p>
 * <p>
 * This implementation also supports prefixing all metrics with the standard Datadog "service" name
 * which it obtains from environment variables. If this is not present, metrics will not be
 * prefixed.
 * </p>
 * <p>
 * Note that metrics will also be formatted according to Datadog requirements already. This mostly
 * involves all lowercase and dashes replaced with underscores. It is done in this implementation
 * already to reduce the surprise factor when searching for metrics in Datadog.
 * </p>
 */
public final class DatadogMeter implements Meter {

  /**
   * The environment variable name used to indicate the "service" name for standard Datadog tags.
   */
  private static final String DATADOG_SERVICE_ENVIRONMENT = "DD_SERVICE";

  private final StatsDClient statsDClient;
  private final String metricNamePrefix;

  /**
   * Construct with the default StatsDClient.
   */
  public DatadogMeter(Environment environment) {
    this(environment, new NonBlockingStatsDClientBuilder().hostname("localhost").build());
  }

  /**
   * Ability to override the StatsDClient. Useful for testing.
   */
  public DatadogMeter(Environment environment, StatsDClient statsDClient) {
    this.metricNamePrefix = determineMetricPrefix(environment);
    this.statsDClient = statsDClient;
  }

  private static String determineMetricPrefix(Environment environment) {
    final String datadogService = environment.get(DATADOG_SERVICE_ENVIRONMENT, null);
    if (datadogService == null) {
      return "";
    }
    return datadogService.toLowerCase().replaceAll("-", "_") + ".";
  }

  private String customiseMetricName(String metricName) {
    return (metricNamePrefix + metricName).toLowerCase().replaceAll("-", "_");
  }

  @Override
  public void incrementCounter(String metricName, String... tags) {

    // see class-level description on why we don't call count() here
    statsDClient.recordDistributionValue(customiseMetricName(metricName), 1, tags);
  }

  @Override
  public void gauge(String metricName, double measure, String[] tags) {

    // see class-level description on why we don't call gauge() here
    statsDClient.recordDistributionValue(customiseMetricName(metricName), measure, tags);
  }
}
