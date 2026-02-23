/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;


public class SAIDNumberMatchesDobTest {
  @Test
  public void testIdNrMatchesDateOfBirth() {
    String idNumber = "0302047089188";
    String dateOfBirthString = "2003-02-04";

    var result = SAIDNumberMatchesDateOfBirth.check(idNumber, dateOfBirthString);

    assertTrue(result);
  }

  @Test
  public void testIdNrMatchesDateOfBirthNotMatching() {
    String idNumber = "0302037089188";
    String dateOfBirthString = "2003-01-01";

    var result = SAIDNumberMatchesDateOfBirth.check(idNumber, dateOfBirthString);

    assertFalse(result);
  }

  @Test
  public void testIdNrMatchesDateOfBirthInvalidDateOfBirth() {
    String idNumber = "0302037089188";
    String dateOfBirthString = "2003-01";

    var result = SAIDNumberMatchesDateOfBirth.check(idNumber, dateOfBirthString);

    assertFalse(result);
  }

  @Test
  public void testIdNrMatchesDateOfBirthInvalidIdNr() {
    String idNumber = null;
    String dateOfBirthString = "2003-01";

    var result = SAIDNumberMatchesDateOfBirth.check(idNumber, dateOfBirthString);

    assertFalse(result);
  }
}
