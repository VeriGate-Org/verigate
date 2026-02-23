/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.invariants.rules;

import static org.junit.jupiter.api.Assertions.*;

import domain.invariants.SpecificationResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * This class contains unit tests for the Specification class.
 */
final class SurnameSpecificationTest {

  @Test
  @DisplayName("Should pass when field is not null")
  void shouldPassWhenFieldIsValid() {

    final var specification =
        new SurnameSpecification<Foo>("theFieldName", Foo::theFieldName, 1, 100);
    SpecificationResult result = specification.isSatisfiedBy(new Foo("Van biljon"));

    assertTrue(result.satisfied());
  }

  private record Foo(String theFieldName) {}
}
