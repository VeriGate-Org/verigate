/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.events;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import domain.exceptions.InvariantViolationException;
import domain.invariants.DefaultErrorRecord;
import domain.invariants.GenericInvariantError;
import domain.invariants.Specification;
import domain.invariants.SpecificationResult;
import java.io.IOException;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link BaseEvent}.
 */
class BaseEventTest {

  BaseEvent<String> mockEvent = new BaseEvent<>() {};

  @Test
  void validate_failure() throws IOException {
    Specification<BaseEvent<String>> failingSpec =
        new Specification<>() {
          @Override
          public SpecificationResult isSatisfiedBy(BaseEvent<String> entity) {
            return SpecificationResult.failure(
                Set.of(
                    new DefaultErrorRecord("testSpec", GenericInvariantError.FIELD_IS_REQUIRED)));
          }
        };

    final InvariantViolationException ex =
        assertThrows(InvariantViolationException.class, () -> mockEvent.validate(failingSpec));

    assertTrue(ex.getErrorMessages().size() == 1);
  }

  @Test
  void validate_success() {
    Specification<BaseEvent<String>> successSpec = entity -> SpecificationResult.success();

    mockEvent.validate(successSpec); // no exception thrown
  }
}
