/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.exceptions;

import java.util.function.Function;

/**
 * Represents a transient error that occurred during the processing of a request. Transient errors
 * are temporary in nature and usually indicate conditions that are expected to resolve on their
 * own. Thus, operations encountering such errors are typically good candidates for retrying after a
 * short delay or under slightly different conditions.
 *
 * <p>This class extends {@link RuntimeException}, inheriting its properties and methods, and adding
 * the context specific to errors deemed transient. Use this exception to signify that an
 * encountered error condition is not permanent and that retrying the same operation might succeed
 * at a later time or after a number of retries.
 *
 * <p>It is particularly useful in distributed systems where network issues, temporary
 * unavailability of services, or concurrency issues might cause an operation to fail temporarily
 * but succeed upon subsequent attempts. Implementing robust error handling and retry mechanisms
 * around operations that could throw this exception can significantly improve the resilience and
 * reliability of your application.
 *
 * <p>Example usage (pseudo-code):
 *
 * <pre>
 * try {
 *     performNetworkOperation();
 * } catch (TransientException e) {
 *     retryOperation();
 * }
 * </pre>
 *
 * @see RuntimeException for base exception behavior.
 */
public class TransientException extends RuntimeException {

  private final CircuitState circuitState;

  /**
   * Constructs a new TransientException with the given cause, circuit breaker state, and resource
   * failure information.
   *
   * @param message Describes the resource failure that contributed to the circuit
   *                               state.
   * @param cause                  The cause of this exception.
   */
  public TransientException(final String message, final Throwable cause) {
    super(message, cause);
    this.circuitState = CircuitState.CLOSED;
  }

  /**
   * Constructs a new TransientException with the given cause, circuit breaker state, and resource
   * failure information.
   *
   * @param cause                  The cause of this exception.
   * @param circuitState           The state of the circuit at the time of the exception.
   * @param message Describes the resource failure that contributed to the circuit state.
   */
  public TransientException(
      final Throwable cause, final CircuitState circuitState, final String message) {
    super(message, cause);
    this.circuitState = circuitState;
  }

  /**
   * Constructs a new {@code TransientException} with the specified cause, circuit state, and
   * implicitly sets the resource name to {@code null}. This constructor is useful for cases where
   * you have a throwable cause for the exception and a known circuit state, but the resource name
   * is either unknown or not applicable.
   *
   * @param e            The cause of this exception. This is the throwable that triggered this
   *                     exception to be thrown.
   * @param circuitState The state of the circuit when the exception occurred. The circuit state can
   *                     influence the decision on whether to retry the operation immediately, after
   *                     a delay, or not at all.
   */
  public TransientException(final Throwable e, final CircuitState circuitState) {
    this(e, circuitState, null);
  }

  /**
   * Constructs a new {@code TransientException} with the specified detail message. This constructor
   * is used to create an exception when a specific error message is available to describe the
   * transient error condition, but no underlying cause is identified. It implicitly sets both the
   * circuit state and the resource name to {@code null}.
   *
   * @param message The detail message. The detail message is saved for later retrieval by the
   *                {@link #getMessage()} method.
   */
  public TransientException(final String message) {
    super(message);
    this.circuitState = CircuitState.CLOSED;
  }

  /**
   * Constructs a new {@code TransientException}. Please consider using one of the other
   * constructors to give more context.
   */
  public TransientException() {
    super((String) null);
    this.circuitState = CircuitState.CLOSED;
  }

  /**
   * Constructs a new {@code TransientException} with the specified cause. This constructor assumes
   * the default circuit state of {@code CircuitState.CLOSED} and sets the resource name to
   * {@code null}. It is suitable for general cases where an operation fails due to a transient
   * error, and the specific state of the circuit is either unknown or deemed not critical for the
   * exception handling logic.
   *
   * @param cause The cause of this exception.
   */
  public TransientException(final Throwable cause) {
    this(cause, CircuitState.CLOSED, null);
  }

  /**
   * Retrieves the state of the circuit breaker.
   *
   * @return The circuit breaker state.
   */
  public final CircuitState getCircuitState() {
    return circuitState;
  }

  /**
   * A convenience function to convert a supplied checked exception into a TransientException.
   *
   * @param message The message to apply to the new TransientException.
   */
  public static Function<Exception, TransientException> wrapInTransientException(String message) {
    return e -> new TransientException(message, e);
  }

}
