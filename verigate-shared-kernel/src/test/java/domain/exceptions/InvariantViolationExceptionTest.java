/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import domain.invariants.DefaultErrorRecord;
import domain.invariants.ErrorRecord;
import domain.invariants.FieldInvariantError;
import domain.invariants.GenericInvariantError;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class InvariantViolationExceptionTest {

  /**
   * Tests that the getMessage() method includes any violation error details.
   * This is because without including such details, the message is not very helpful.
   * It has been observed that the loggers use getMessage() not toString() to log the exception.
   */
  @Test
  void testToStringAndGetMessage() {

    // constructed with null message

    assertEquals("domain.exceptions.InvariantViolationException",
        new InvariantViolationException((String)null).toString());

    assertNull(new InvariantViolationException((String)null).getMessage(),
        "Expecting null message as exception was created with null message");

    // constructed with null error record set

    assertEquals("domain.exceptions.InvariantViolationException: Invariant violation: []",
        new InvariantViolationException((Set<ErrorRecord>) null).toString());

    assertEquals("Invariant violation: []",
        new InvariantViolationException((Set<ErrorRecord>) null).getMessage());

    // constructed with empty error record set

    assertEquals("domain.exceptions.InvariantViolationException: Invariant violation: []",
        new InvariantViolationException(Set.of()).toString());

    assertEquals("Invariant violation: []",
        new InvariantViolationException(Set.of()).getMessage());

    // constructed with a single error record

    assertEquals("domain.exceptions.InvariantViolationException: Invariant violation: [specification:testSpec, code:FIELD_IS_REQUIRED, message:Field is mandatory : testField]",
        new InvariantViolationException(Set.of(
            new DefaultErrorRecord(
                "testSpec",
                new FieldInvariantError(GenericInvariantError.FIELD_IS_REQUIRED, "testField")
            )
        )).toString());

    assertEquals("Invariant violation: [specification:testSpec, code:FIELD_IS_REQUIRED, message:Field is mandatory : testField]",
        new InvariantViolationException(Set.of(
            new DefaultErrorRecord(
                "testSpec",
                new FieldInvariantError(GenericInvariantError.FIELD_IS_REQUIRED, "testField")
            )
        )).getMessage());
  }
}
