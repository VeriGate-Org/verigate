/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.infrastructure.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Validates South African ID numbers by checking format, Luhn checksum, and extracting
 * embedded demographic data (date of birth, gender, citizenship).
 *
 * <p>SA ID format: YYMMDD SSSS C A Z
 * <ul>
 *   <li>YYMMDD - Date of birth</li>
 *   <li>SSSS - Gender (0000-4999 = Female, 5000-9999 = Male)</li>
 *   <li>C - Citizenship (0 = SA citizen, 1 = Permanent resident)</li>
 *   <li>A - Was used for race, now always 8</li>
 *   <li>Z - Luhn checksum digit</li>
 * </ul>
 */
public final class SaIdValidator {

  private SaIdValidator() {
  }

  /**
   * Validates an SA ID number and returns all validation check results.
   *
   * @param idNumber the 13-digit SA ID number to validate
   * @return list of validation checks with pass/fail status
   */
  public static List<AiDocumentAnalyzer.ValidationCheck> validate(String idNumber) {
    List<AiDocumentAnalyzer.ValidationCheck> checks = new ArrayList<>();

    if (idNumber == null || idNumber.isBlank()) {
      checks.add(new AiDocumentAnalyzer.ValidationCheck(
          "FORMAT_CHECK", "FAIL", "ID number is empty"));
      return checks;
    }

    String cleaned = idNumber.replaceAll("\\s+", "");

    // Length check
    if (cleaned.length() != 13) {
      checks.add(new AiDocumentAnalyzer.ValidationCheck(
          "FORMAT_CHECK", "FAIL",
          "Expected 13 digits, got " + cleaned.length()));
      return checks;
    }

    // Digits-only check
    if (!cleaned.matches("\\d{13}")) {
      checks.add(new AiDocumentAnalyzer.ValidationCheck(
          "FORMAT_CHECK", "FAIL", "ID number must contain only digits"));
      return checks;
    }

    checks.add(new AiDocumentAnalyzer.ValidationCheck(
        "FORMAT_CHECK", "PASS", "Valid 13-digit format"));

    // Date of birth validation
    String dobPart = cleaned.substring(0, 6);
    LocalDate dob = parseDateOfBirth(dobPart);
    if (dob != null) {
      checks.add(new AiDocumentAnalyzer.ValidationCheck(
          "DOB_CHECK", "PASS",
          "Date of birth: " + dob.format(DateTimeFormatter.ISO_LOCAL_DATE)));
    } else {
      checks.add(new AiDocumentAnalyzer.ValidationCheck(
          "DOB_CHECK", "FAIL", "Invalid date of birth in ID number"));
    }

    // Gender extraction
    int genderDigits = Integer.parseInt(cleaned.substring(6, 10));
    String gender = genderDigits >= 5000 ? "Male" : "Female";
    checks.add(new AiDocumentAnalyzer.ValidationCheck(
        "GENDER_CHECK", "PASS", "Gender: " + gender));

    // Citizenship check
    char citizenshipDigit = cleaned.charAt(10);
    if (citizenshipDigit == '0') {
      checks.add(new AiDocumentAnalyzer.ValidationCheck(
          "CITIZENSHIP_CHECK", "PASS", "SA Citizen"));
    } else if (citizenshipDigit == '1') {
      checks.add(new AiDocumentAnalyzer.ValidationCheck(
          "CITIZENSHIP_CHECK", "PASS", "Permanent Resident"));
    } else {
      checks.add(new AiDocumentAnalyzer.ValidationCheck(
          "CITIZENSHIP_CHECK", "FAIL",
          "Invalid citizenship digit: " + citizenshipDigit));
    }

    // Luhn check
    if (luhnCheck(cleaned)) {
      checks.add(new AiDocumentAnalyzer.ValidationCheck(
          "LUHN_CHECK", "PASS", "Valid checksum"));
    } else {
      checks.add(new AiDocumentAnalyzer.ValidationCheck(
          "LUHN_CHECK", "FAIL", "Invalid Luhn checksum"));
    }

    return checks;
  }

  /**
   * Extracts demographic data from a valid SA ID number.
   *
   * @param idNumber the 13-digit SA ID number
   * @return map of extracted fields or empty map if invalid
   */
  public static Map<String, String> extractDemographics(String idNumber) {
    if (idNumber == null || idNumber.replaceAll("\\s+", "").length() != 13) {
      return Map.of();
    }

    String cleaned = idNumber.replaceAll("\\s+", "");
    if (!cleaned.matches("\\d{13}")) {
      return Map.of();
    }

    LocalDate dob = parseDateOfBirth(cleaned.substring(0, 6));
    int genderDigits = Integer.parseInt(cleaned.substring(6, 10));
    char citizenshipDigit = cleaned.charAt(10);

    return Map.of(
        "dateOfBirth", dob != null ? dob.format(DateTimeFormatter.ISO_LOCAL_DATE) : "unknown",
        "gender", genderDigits >= 5000 ? "Male" : "Female",
        "citizenship", citizenshipDigit == '0' ? "SA Citizen" : "Permanent Resident"
    );
  }

  /**
   * Performs the Luhn algorithm check on a numeric string.
   */
  static boolean luhnCheck(String number) {
    int sum = 0;
    boolean alternate = false;

    for (int i = number.length() - 1; i >= 0; i--) {
      int digit = number.charAt(i) - '0';

      if (alternate) {
        digit *= 2;
        if (digit > 9) {
          digit -= 9;
        }
      }

      sum += digit;
      alternate = !alternate;
    }

    return sum % 10 == 0;
  }

  private static LocalDate parseDateOfBirth(String yymmdd) {
    try {
      int yy = Integer.parseInt(yymmdd.substring(0, 2));
      int mm = Integer.parseInt(yymmdd.substring(2, 4));
      int dd = Integer.parseInt(yymmdd.substring(4, 6));

      // Determine century: if year > current 2-digit year, assume 1900s
      int currentTwoDigitYear = LocalDate.now().getYear() % 100;
      int year = yy > currentTwoDigitYear ? 1900 + yy : 2000 + yy;

      return LocalDate.of(year, mm, dd);
    } catch (NumberFormatException | java.time.DateTimeException e) {
      return null;
    }
  }
}
