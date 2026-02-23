/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.invariants.rules;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/**
 * Test class for the MobileNumberSpecification.
 */
final class DateOfBirthSpecificationTest {

  public static class DateOfBirth {
    private final LocalDate dob;

    public DateOfBirth(LocalDate dob) {
      this.dob = dob;
    }

    public LocalDate getDateOfBirth() {
      return dob;
    }
  }

  @Test
  void dateOfBirthShouldPass() {
    final var dateOfBirthSpecification =
        new DateOfBirthSpecification<>("date of birth", DateOfBirth::getDateOfBirth, true);
    var result = dateOfBirthSpecification.isSatisfiedBy(new DateOfBirth(LocalDate.of(1991, 1, 1)));
    assertTrue(result.satisfied());
  }

  @Test
  void dateOfBirthShouldFail() {
    final var dateOfBirthSpecification =
        new DateOfBirthSpecification<>("date of birth", DateOfBirth::getDateOfBirth, true);
    var result = dateOfBirthSpecification.isSatisfiedBy(new DateOfBirth(LocalDate.of(3000, 1, 1)));
    assertFalse(result.satisfied());
  }

  @Test
  void dateOfBirthShouldFailIfRequiredAndIsNull() {
    final var dateOfBirthSpecification =
        new DateOfBirthSpecification<>("date of birth", DateOfBirth::getDateOfBirth, true);
    var result = dateOfBirthSpecification.isSatisfiedBy(new DateOfBirth(null));
    assertFalse(result.satisfied());
  }

  @Test
  void dateOfBirthShouldPassIfNotRequiredAndIsNullFail() {
    final var dateOfBirthSpecification =
        new DateOfBirthSpecification<>("date of birth", DateOfBirth::getDateOfBirth, false);
    var result = dateOfBirthSpecification.isSatisfiedBy(new DateOfBirth(null));
    assertTrue(result.satisfied());
  }
}
