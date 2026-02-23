/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.util;

import crosscutting.mappers.IsoDateStringMapper;
import java.util.regex.Pattern;

/**
 * Utility class for checking if a South African ID number matches a given date of birth.
 */
public class SAIDNumberMatchesDateOfBirth {

  /**
   * Checks if the given South African ID number matches the given date of birth.
   *
   * @param idNumber the South African ID number to check.
   * @param dateOfBirthString the date of birth to check.
   * @return true if the ID number matches the date of birth, false otherwise.
   */
  public static boolean check(String idNumber, String dateOfBirthString) {
    try {
      if (idNumber == null || dateOfBirthString == null) {
        return false;
      }

      var dateOfBirth = IsoDateStringMapper.map(dateOfBirthString);

      var idNrDobPattern = Pattern.compile("(\\d{2})(\\d{2})(\\d{2})");
      var matcher = idNrDobPattern.matcher(idNumber);

      if (matcher.find()) {
        var year = matcher.group(1);
        var month = matcher.group(2);
        var day = matcher.group(3);

        if (dateOfBirth.getYear() < 2000) {
          year = "19" + year;
        } else {
          year = "20" + year;
        }

        var idNrDateOfBirth = IsoDateStringMapper.map(year + "-" + month + "-" + day);

        return dateOfBirth.equals(idNrDateOfBirth);
      } else {
        throw new IllegalArgumentException("Invalid ID format");
      }
    } catch (Exception e) {
      return false;
    }
  }
}
