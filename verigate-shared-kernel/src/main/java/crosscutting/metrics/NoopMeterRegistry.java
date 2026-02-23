/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.metrics;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Meter.Id;
import io.micrometer.core.instrument.Meter.Type;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.noop.NoopCounter;
import io.micrometer.core.instrument.noop.NoopDistributionSummary;
import io.micrometer.core.instrument.noop.NoopFunctionCounter;
import io.micrometer.core.instrument.noop.NoopFunctionTimer;
import io.micrometer.core.instrument.noop.NoopGauge;
import io.micrometer.core.instrument.noop.NoopMeter;
import io.micrometer.core.instrument.noop.NoopTimer;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A No-Operation implementation of {@link MeterRegistry} that returns noop implementations for all
 * the meters. This class is used when a metrics collection is disabled or not required.
 */
public final class NoopMeterRegistry extends MeterRegistry {

  /**
   * Constructs a new NoopMeterRegistry with the given clock.
   *
   * @param clock the clock to use for timing measurements
   */
  public NoopMeterRegistry(Clock clock) {
    super(clock);
  }

  /**
   * Creates a new {@link Gauge} that performs no operations.
   *
   * @param id the identifier of the gauge
   * @param t the object to measure
   * @param toDoubleFunction a function that measures the value of the object
   * @param <T> the type of the object being measured
   * @return a new {@link NoopGauge}
   */
  @Override
  protected <T> @NotNull Gauge newGauge(
      @NotNull Id id, T t, @NotNull ToDoubleFunction<T> toDoubleFunction) {
    return new NoopGauge(id);
  }

  /**
   * Creates a new {@link Counter} that performs no operations.
   *
   * @param id the identifier of the counter
   * @return a new {@link NoopCounter}
   */
  @Override
  protected @NotNull Counter newCounter(@NotNull Id id) {
    return new NoopCounter(id);
  }

  /**
   * Creates a new {@link Timer} that performs no operations.
   *
   * @param id the identifier of the timer
   * @param distributionStatisticConfig the distribution statistic configuration
   * @param pauseDetector the pause detector
   * @return a new {@link NoopTimer}
   */
  @Override
  protected @NotNull Timer newTimer(
      @NotNull Id id,
      @NotNull DistributionStatisticConfig distributionStatisticConfig,
      @NotNull PauseDetector pauseDetector) {
    return new NoopTimer(id);
  }

  /**
   * Creates a new {@link DistributionSummary} that performs no operations.
   *
   * @param id the identifier of the distribution summary
   * @param distributionStatisticConfig the distribution statistic configuration
   * @param scale the scaling factor
   * @return a new {@link NoopDistributionSummary}
   */
  @Override
  protected @NotNull DistributionSummary newDistributionSummary(
      @NotNull Id id,
      @NotNull DistributionStatisticConfig distributionStatisticConfig,
      double scale) {
    return new NoopDistributionSummary(id);
  }

  /**
   * Creates a new {@link Meter} that performs no operations.
   *
   * @param id the identifier of the meter
   * @param type the type of the meter
   * @param measurements the measurements of the meter
   * @return a new {@link NoopMeter}
   */
  @Override
  protected @NotNull Meter newMeter(
      @NotNull Id id, @NotNull Type type, @NotNull Iterable<Measurement> measurements) {
    return new NoopMeter(id);
  }

  /**
   * Creates a new {@link FunctionTimer} that performs no operations.
   *
   * @param id the identifier of the function timer
   * @param t the object to measure
   * @param toLongFunction a function that measures the count of the object
   * @param toDoubleFunction a function that measures the total time of the object
   * @param timeUnit the time unit of the total time
   * @param <T> the type of the object being measured
   * @return a new {@link NoopFunctionTimer}
   */
  @Override
  protected <T> @NotNull FunctionTimer newFunctionTimer(
      @NotNull Id id,
      @NotNull T t,
      @NotNull ToLongFunction<T> toLongFunction,
      @NotNull ToDoubleFunction<T> toDoubleFunction,
      @NotNull TimeUnit timeUnit) {
    return new NoopFunctionTimer(id);
  }

  /**
   * Creates a new {@link FunctionCounter} that performs no operations.
   *
   * @param id the identifier of the function counter
   * @param t the object to measure
   * @param toDoubleFunction a function that measures the count of the object
   * @param <T> the type of the object being measured
   * @return a new {@link NoopFunctionCounter}
   */
  @Override
  protected <T> @NotNull FunctionCounter newFunctionCounter(
      @NotNull Id id, @NotNull T t, @NotNull ToDoubleFunction<T> toDoubleFunction) {
    return new NoopFunctionCounter(id);
  }

  /**
   * Returns the base time unit used by this registry, which is seconds.
   *
   * @return the base time unit
   */
  @Contract(pure = true)
  @Override
  protected @Nullable TimeUnit getBaseTimeUnit() {
    return TimeUnit.SECONDS;
  }

  /**
   * Returns the default histogram configuration used by this registry.
   *
   * @return the default histogram configuration
   */
  @Contract(pure = true)
  @Override
  protected @Nullable DistributionStatisticConfig defaultHistogramConfig() {
    return new DistributionStatisticConfig();
  }
}
