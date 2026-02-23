package crosscutting.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test class for the SAIdNumberValidityCheck.
 */
public class SAIdNumberValidityCheckTest {

  @Test
  void isValidSAIDShouldReturnTrue1() {
    assertTrue(SAIdNumberValidityCheck.check("0301017673189"));
  }

  @Test
  void isValidSAIDShouldReturnTrue2() {
    assertTrue(SAIdNumberValidityCheck.check("0301019755182"));
  }

  @Test
  void isValidSAIDShouldReturnFalse() {
    assertFalse(SAIdNumberValidityCheck.check("8001015009088"));
  }

  @Test
  void isValidSAIDShouldReturnFalseForInvalidLength() {
    assertFalse(SAIdNumberValidityCheck.check("8001015009"));
  }

  @Test
  void isValidSAIDShouldReturnFalseForNonNumeric() {
    assertFalse(SAIdNumberValidityCheck.check("800101500908a"));
  }

  @Test
  void isValidSAIDShouldReturnFalseForNull() {
    assertFalse(SAIdNumberValidityCheck.check(null));
  }
}
