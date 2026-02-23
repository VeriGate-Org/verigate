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
 * This class contains unit tests for the RequiredFieldSpecification class.
 */
final class RequiredFieldSpecificationTest {

  @Test
  @DisplayName("Should pass when field is not null")
  void shouldPassWhenFieldIsNotNull() {
    final var requiredFieldSpecification = new RequiredFieldSpecification<>("theFieldName", Foo::theFieldName);
    SpecificationResult result = requiredFieldSpecification.isSatisfiedBy(new Foo("John"));

    assertTrue(result.satisfied());
  }

  @Test
  @DisplayName("Should fail when field is null")
  void shouldFailWhenFieldIsNull() {
    final var requiredFieldSpecification = new RequiredFieldSpecification<>("theFieldName", Foo::theFieldName);
    SpecificationResult result = requiredFieldSpecification.isSatisfiedBy(new Foo(null));

    assertFalse(result.satisfied());
  }

  private record Foo(String theFieldName) {}
}
