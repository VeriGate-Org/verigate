/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.resiliency;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

/** An interface for implementing a circuit breaker pattern. */
public interface CircuitBreaker {

  /**
   * Checks if the circuit breaker is open.
   *
   * @return true if the circuit breaker is open, false otherwise
   */
  boolean isOpen();

  /**
   * Checks if the circuit breaker is closed.
   *
   * @return true if the circuit breaker is closed, false otherwise
   */
  boolean isClosed();

  /**
   * Executes the provided callable within the circuit breaker.
   *
   * @param callable the operation to be executed
   * @param <T> the type of the result
   * @return the result of the operation
   * @throws CircuitBreakerOpenException if the circuit breaker is open
   * @throws Exception for any other exception originating from the callable.
   */
  <T> T executeCallable(Callable<T> callable) throws CircuitBreakerOpenException, Exception;

  /**
   * Executes the provided runnable within the circuit breaker.
   *
   * @param runnable the operation to be executed
   * @throws CircuitBreakerOpenException if the circuit breaker is open
   */
  void executeRunnable(Runnable runnable) throws CircuitBreakerOpenException;

  /** Closes the circuit breaker. */
  void close();

  /** Opens the circuit breaker. */
  void open();

  /** Resets the circuit breaker. */
  void reset();

  /**
   * Decorates the given supplier to be executed within the circuit breaker.
   *
   * @param supplier the supplier to be decorated
   * @param <T> the type of the result
   * @return a decorated supplier
   */
  <T> Supplier<T> decorate(Supplier<T> supplier);

  /** An exception thrown when the circuit breaker is open. */
  class CircuitBreakerOpenException extends RuntimeException {
    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message
     */
    public CircuitBreakerOpenException(String message) {
      super(message);
    }
  }
}
