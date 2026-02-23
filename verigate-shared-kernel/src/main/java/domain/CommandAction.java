/*
 * VeriGate (c) 2024. All rights reserved. Unauthorized copying of this file, via any medium
 * is strictly prohibited. Proprietary and confidential.
 */

package domain;

import domain.exceptions.PermanentException;

/**
 * Functional interface for actions that can be executed as part of processing a command. This
 * interface is designed to encapsulate a block of logic that performs an operation which might
 * throw a {@link PermanentException}. It is typically used in scenarios where an operation needs to
 * be executed conditionally, such as after verifying that a certain command has not been processed
 * yet, to maintain idempotency in command processing.
 *
 * <p>The {@code execute} method is the single abstract method of this functional interface, and it
 * is intended to be implemented by lambda expressions or method references that conform to its
 * signature. The method is declared to throw a {@code PermanentException} to explicitly signal that
 * implementations can result in permanent error conditions that callers need to handle
 * appropriately. This exception handling capability is particularly important in transactional
 * operations or when specific error recovery mechanisms are in place.</p>
 *
 * <p>Usage example:</p>
 *
 * <pre>{@code
 * public void processCommand(BaseCommand command, CommandAction action) throws PermanentException {
 *     if (isNewCommand(command)) {
 *         action.execute();
 *     } else {
 *         // Handle already processed command
 *     }
 * }
 *
 * processCommand(myCommand, () -> {
 *     // Command processing logic here
 *     if (conditionMet) {
 *         throw new PermanentException(StringException.format("Condition met, cannot proceed."));
 *     }
 * });
 * }</pre>
 *
 * <p>Note: This interface is marked with {@code @FunctionalInterface} annotation to indicate
 * that it is intended to be used in contexts where functional programming patterns are applied,
 * such as lambda expressions or method references. The Java compiler enforces the single
 * abstract method constraint of this interface due to the annotation.</p>
 */
@FunctionalInterface
public interface CommandAction {
  void execute() throws PermanentException;
}
