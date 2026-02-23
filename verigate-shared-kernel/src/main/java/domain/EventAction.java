/*
 * VeriGate (c) 2024. All rights reserved. Unauthorized copying of this file, via any medium
 * is strictly prohibited. Proprietary and confidential.
 */

package domain;

import domain.exceptions.PermanentException;

/**
 * The {@code EventAction} functional interface represents an action that can be performed
 * in response to an event in a domain-driven context. This action is executed within
 * the context of an aggregate, and it may throw a {@link PermanentException} if a
 * non-recoverable error occurs during the execution of the action.
 *
 * <p>This interface is typically used in scenarios where idempotency and consistency are critical,
 * particularly in distributed systems or event-driven architectures. It allows actions to
 * be associated with specific domain events, ensuring that the action is only performed
 * if the event meets certain conditions (e.g., it has not been processed before).</p>
 *
 * <p>Example usage:</p>
 *
 * <pre>
 * {@code
 * EventAction action = () -> {
 *     // perform domain-specific action
 *     myAggregate.performSomeAction();
 * };
 *
 * // execute the action, handling PermanentException if necessary
 * try {
 *     action.execute();
 * } catch (PermanentException e) {
 *     // handle the exception
 *     log.error("Failed to execute the event action", e);
 * }
 * }
 * </pre>
 *
 * <p>This interface allows for flexibility in defining the actions that need to be
 * performed on events, and can be used in conjunction with domain aggregates to
 * ensure that business rules and invariants are upheld.</p>
 *
 * <h2>Thread Safety</h2>
 * Implementations of this interface should be thread-safe if used in concurrent contexts.
 * Care must be taken to ensure that state changes made within the {@code execute()}
 * method are properly synchronized or atomic, as required by the application.
 *
 * @author VeriGate
 * @version 1.0
 * @since 2024
 *
 * @see domain.exceptions.PermanentException
 */
@FunctionalInterface
public interface EventAction {
  void execute() throws PermanentException;
}
