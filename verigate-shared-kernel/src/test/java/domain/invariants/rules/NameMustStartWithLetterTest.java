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
final class NameMustStartWithLetterTest {

  @Test
  @DisplayName("Should pass when field is not null")
  void shouldPassWhenFieldIsValid() {
    final var specification =
        new NameMustStartWithLetterSpecification<>("theFieldName", Foo::theFieldName);
    SpecificationResult result = specification.isSatisfiedBy(new Foo("John"));

    assertTrue(result.satisfied());
  }

  @Test
  @DisplayName("Should fail when field is null")
  void shouldFailWithNonLetter() {
    final var specification =
        new NameMustStartWithLetterSpecification<>("theFieldName", Foo::theFieldName);
    SpecificationResult result = specification.isSatisfiedBy(new Foo("-John"));

    assertFalse(result.satisfied());
  }

  @Test
  @DisplayName("Should fail when field is null")
  void shouldPassWhenNull() {
    final var specification =
        new NameMustStartWithLetterSpecification<>("theFieldName", Foo::theFieldName);
    SpecificationResult result = specification.isSatisfiedBy(new Foo(null));

    assertTrue(result.satisfied());
  }

  @Test
  @DisplayName("Should fail when field is empty")
  void shouldFailIfStringIsEmpty() {
    final var specification =
        new NameMustStartWithLetterSpecification<>("theFieldName", Foo::theFieldName);
    SpecificationResult result = specification.isSatisfiedBy(new Foo(""));

    assertFalse(result.satisfied());
  }

  private record Foo(String theFieldName) {}
}
