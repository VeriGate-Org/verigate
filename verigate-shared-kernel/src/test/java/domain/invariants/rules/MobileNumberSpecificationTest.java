/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.invariants.rules;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Test class for the MobileNumberSpecification.
 */
final class MobileNumberSpecificationTest {

  @Test
  void mobileNumberShouldPass() {
    final var mobileNumberSpecification =
        new MobileNumberSpecification<>("mobile number", MockMobileNumber::mobileNumber);
    var mobileNumber = "+27831234567";
    var result = mobileNumberSpecification.isSatisfiedBy(new MockMobileNumber(mobileNumber));
    assertTrue(result.satisfied());
  }

  @Test
  void mobileNumberShouldFail() {
    final var mobileNumberSpecification =
        new MobileNumberSpecification<>("mobile number", MockMobileNumber::mobileNumber);
    var mobileNumber = "+0378291";
    var result = mobileNumberSpecification.isSatisfiedBy(new MockMobileNumber(mobileNumber));
    assertFalse(result.satisfied());
  }

  public record MockMobileNumber(String mobileNumber) {}
}
