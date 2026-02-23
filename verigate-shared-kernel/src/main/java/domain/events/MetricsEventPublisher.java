/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.events;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: This is an example metric decorator which must be fine tuned in the future.

/**
 * A decorator for {@link EventPublisher} that adds metrics collection functionality. This class
 * tracks the number of events published and the time taken to publish each event.
 *
 * @param <EventT> the type of event being published
 */
public final class MetricsEventPublisher<EventT> extends EventPublisherDecorator<EventT> {

  private static final Logger logger =
      LoggerFactory.getLogger(MetricsEventPublisher.class);

  private final Timer timer;
  private final Counter counter;

  /**
   * Constructs a new {@code MetricsEventPublisherDecorator} with the specified {@code
   * EventPublisher}, {@code MetricRegistry}, and metric names.
   *
   * @param decoratedPublisher the event publisher to be decorated
   * @param metricRegistry the registry used to register the metrics
   * @param timerMetricName the name of the timer metric
   * @param counterMetricName the name of the counter metric
   */
  public MetricsEventPublisher(
      final EventPublisher<EventT> decoratedPublisher,
      final MetricRegistry metricRegistry,
      final String timerMetricName,
      final String counterMetricName) {
    super(decoratedPublisher);
    this.timer = metricRegistry.timer(timerMetricName);
    this.counter = metricRegistry.counter(counterMetricName);
  }

  /**
   * Publishes the given event, increments the counter, and records the time taken to publish the
   * event.
   *
   * @param event the event to be published
   */
  @Override
  public void publish(final EventT event) {
    logger.debug("Publishing event with metrics: {}", event);
    counter.inc();
    try (Timer.Context context = timer.time()) {
      super.publish(event);
      context.stop();
    }
  }
}
