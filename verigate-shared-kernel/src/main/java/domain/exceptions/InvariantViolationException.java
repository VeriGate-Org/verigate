/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.exceptions;

import domain.invariants.ErrorRecord;
import java.util.List;
import java.util.Set;

/**
 * An exception that represents a violation of an invariant. This exception should be thrown when an
 * invariant is violated. This is permanent and must not be retried.
 */
public class InvariantViolationException extends PermanentException {
  private Set<ErrorRecord> errorMessages;

  /**
   * Constructs a new InvariantViolationException with the given message.
   */
  public InvariantViolationException(String msg) {
    super(msg);
  }

  /**
   * Constructs a new instance with the given set of invariant violation details.
   */
  public InvariantViolationException(Set<ErrorRecord> errorMessages) {
    super("Invariant violation: " + buildMessage(errorMessages));
    this.errorMessages = errorMessages;
  }

  public Set<ErrorRecord> getErrorMessages() {
    return errorMessages;
  }

  private static String buildMessage(Set<ErrorRecord> errorMessages) {
    return errorMessages == null
        ? List.of().toString()
        : errorMessages.stream()
            .map(
                error ->
                    "specification:"
                        + error.getSpecificationName()
                        + ", code:"
                        + error.getError().getCode()
                        + ", message:"
                        + error.getError().getMessage())
            .toList()
            .toString();
  }
}
