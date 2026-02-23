/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.exceptions;

/**
 * The {@code CircuitState} enumeration represents the possible states of a circuit in a circuit
 * breaker pattern. The circuit breaker pattern is used to prevent an application from performing
 * operations that are likely to fail by temporarily disabling the functionality. This pattern is
 * useful in maintaining system stability and resilience by stopping the cascade of failures in a
 * system.
 *
 * <p>Each state represents a specific mode of operation of the circuit breaker:
 *
 * <ul>
 *   <li>{@link #CLOSED}: The circuit is closed, and all requests are allowed through. This state
 *       indicates normal operation, where requests to the protected operation or service are
 *       successful.
 *   <li>{@link #OPEN}: The circuit is open, and no requests are allowed through. This state is
 *       activated when the failure threshold for the protected operation is reached, preventing any
 *       further potentially harmful operations.
 *   <li>{@link #HALF_OPEN}: The circuit is in a half-open state, allowing a limited number of test
 *       requests to pass through. This state is used to test if the underlying problem that caused
 *       the failure has been resolved. Based on the outcome of these test requests, the circuit can
 *       either return to the {@code CLOSED} state or remain in the {@code OPEN} state.
 * </ul>
 */
public enum CircuitState {
  /**
   * The {@code CLOSED} state indicates that the circuit is closed and operational. Requests to the
   * protected operation or service are allowed, reflecting normal operation conditions.
   */
  CLOSED,

  /**
   * The {@code OPEN} state indicates that the circuit is open, preventing any requests from being
   * made to the protected operation. This state is activated to prevent further failures and to
   * allow for the recovery of the service or operation.
   */
  OPEN,

  /**
   * The {@code HALF_OPEN} state represents a temporary state that allows for a limited number of
   * test requests to pass through. This is used to determine whether the conditions that caused the
   * circuit to open have been resolved.
   */
  HALF_OPEN;
}
