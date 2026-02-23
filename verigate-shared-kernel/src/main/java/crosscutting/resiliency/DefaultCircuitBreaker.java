/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.resiliency;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import java.util.concurrent.Callable;
import java.util.function.Supplier;


/**
 * Default implementation that uses Resilience4J.
 * See <a href="https://resilience4j.readme.io/docs/circuitbreaker">Resilience4J Circuit Breaker</a>.
 */
public final class DefaultCircuitBreaker implements crosscutting.resiliency.CircuitBreaker {

  /** Resilience4J circuit breaker. */
  private final CircuitBreaker circuitBreaker;

  /** Creates a new instance using a Resilience4J circuit breaker. */
  public DefaultCircuitBreaker(CircuitBreaker circuitBreaker) {
    this.circuitBreaker = circuitBreaker;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isOpen() {
    return CircuitBreaker.State.OPEN.equals(circuitBreaker.getState());
  }

  /** {@inheritDoc} */
  @Override
  public void open() {
    circuitBreaker.transitionToOpenState();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isClosed() {
    return CircuitBreaker.State.CLOSED.equals(circuitBreaker.getState());
  }

  /** {@inheritDoc} */
  @Override
  public void close() {
    circuitBreaker.transitionToClosedState();
  }

  /** {@inheritDoc} */
  @Override
  public void reset() {
    circuitBreaker.reset();
  }

  /** {@inheritDoc} */
  @Override
  public <T> T executeCallable(Callable<T> callable) throws Exception {
    try {
      return circuitBreaker.executeCallable(callable);
    } catch (CallNotPermittedException e) {
      throw new CircuitBreakerOpenException(e.getMessage());
    }
  }

  /** {@inheritDoc} */
  @Override
  public void executeRunnable(Runnable runnable) throws CircuitBreakerOpenException {
    try {
      circuitBreaker.executeRunnable(runnable);
    } catch (CallNotPermittedException e) {
      throw new CircuitBreakerOpenException(e.getMessage());
    }
  }

  /** {@inheritDoc} */
  @Override
  public <T> Supplier<T> decorate(Supplier<T> supplier) {

    return circuitBreaker.decorateSupplier(() -> {
      try {
        return supplier.get();
      } catch (CallNotPermittedException e) {
        throw new CircuitBreakerOpenException(e.getMessage());
      }
    });
  }
}
