/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.util;

/**
 * Utility class for checking the validity of a South African ID number using the Luhn algorithm.
 */
public class SAIdNumberValidityCheck {
  /**
   * Checks if the given South African ID number is valid using the Luhn algorithm.
   *
   * @param idNumber the South African ID number to check.
   * @return true if the ID number is valid, false otherwise.
   */
  public static boolean check(String idNumber) {
    try {
      if (idNumber == null || idNumber.length() != 13 || !idNumber.matches("\\d{13}")) {
        return false;
      }

      int sum = 0;
      boolean alternate = false;

      for (int i = idNumber.length() - 1; i >= 0; i--) {
        int n = Integer.parseInt(idNumber.substring(i, i + 1));
        if (alternate) {
          n *= 2;
          if (n > 9) {
            n = (n % 10) + 1;
          }
        }
        sum += n;
        alternate = !alternate;
      }

      return (sum % 10 == 0);
    } catch (Exception e) {
      return false;
    }
  }
}
