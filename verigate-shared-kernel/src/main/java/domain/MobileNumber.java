/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain;

import crosscutting.serialization.DataContract;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Represents a mobile phone number with an international structure, comprising a country code and a
 * local number part. This record is annotated with {@code @DataContract} to indicate its
 * suitability for data serialization frameworks, making it ideal for use in systems that require
 * cross-platform compatibility and data interchange.
 *
 * <p>Fields include:
 *
 * <ul>
 *   <li>{@code phoneCountryCode} - The international country code associated with the mobile
 *       number, indicating the country of the phone number's origin.
 *   <li>{@code localNumber} - The specific number within the country, excluding the country code.
 * </ul>
 * This structure allows for easy breakdown and validation of international phone numbers.
 *
 * @param phoneCountryCode the country code part of the mobile number, stored as an instance of
 *     {@code PhoneCountryCode}.
 * @param localNumber the local part of the mobile number as a {@code String}, which should follow
 *     the local numbering plan and conventions.
 */
public record MobileNumber(
    @DataContract PhoneCountryCode phoneCountryCode, @DataContract String localNumber) {

  /**
   * Constructs a {@code MobileNumber} by parsing the given number into its country code and local
   * number components.
   *
   * <p>This constructor takes a single string representing the full mobile number in E.164 format
   * and parses it to extract the country code and the local number. The E.164 format ensures that
   * the mobile number is in a standardized international format.
   *
   * <p>The parsing is done using the {@code parseCountryCode} and {@code parseLocalNumber} methods,
   * which are responsible for extracting the respective parts of the number.
   *
   * <p>For example, given the mobile number "+1234567890":
   *
   * <ul>
   *   <li>{@code parseCountryCode("+1234567890")} will return "+1".
   *   <li>{@code parseLocalNumber("+1234567890")} will return "234567890".
   * </ul>
   *
   * @param number The full mobile number in E.164 format to be parsed.
   * @throws IllegalArgumentException if the provided number is not in a valid E.164 format.
   */
  public MobileNumber(String number) {
    this(parseCountryCode(number), parseLocalNumber(number));
  }

  private static PhoneCountryCode parseCountryCode(String number) {
    if (number == null || !number.startsWith("+")) {
      throw new IllegalArgumentException("Number must be in E.164 format and start with '+'");
    }

    String withoutPlus = number.substring(1); // Remove the leading '+'
    for (int i = 1; i <= 3; i++) {
      String code = "+" + withoutPlus.substring(0, i);
      for (PhoneCountryCode cc : PhoneCountryCode.values()) {
        if (cc.getCode().equals(code)) {
          return cc;
        }
      }
    }
    throw new IllegalArgumentException("Invalid country code in number: " + number);
  }

  private static String parseLocalNumber(String number) {
    if (number == null || !number.startsWith("+")) {
      throw new IllegalArgumentException("Number must be in E.164 format and start with '+'");
    }

    String withoutPlus = number.substring(1); // Remove the leading '+'
    for (int i = 1; i <= 3; i++) {
      String code = withoutPlus.substring(0, i);
      for (PhoneCountryCode cc : PhoneCountryCode.values()) {
        if (cc.getCode().equals("+" + code)) {
          return withoutPlus.substring(i);
        }
      }
    }
    throw new IllegalArgumentException("Invalid country code in number: " + number);
  }

  /**
   * Return the international number in E.164 format.
   *
   * @return The mobile number in E.164 format.
   */
  public String internationalNumberE164() {
    return phoneCountryCode.getCode() + localNumber;
  }

  private void validateLocalNumber(String localNumber, PhoneCountryCode phoneCountryCode) {

    // Todo: add the exception descriptions to a resource
    // Todo: move some validations to a specification pattern, as perhaps not all countries have
    //       numeric numbers

    // Numeric validation
    if (!Pattern.matches("\\d+", localNumber)) {
      throw new IllegalArgumentException("Local number must be numeric");
    }

    // Length check (example range, should be adjusted per country specifications)
    if (localNumber.length() < 6 || localNumber.length() > 10) {
      throw new IllegalArgumentException("Local number length must be between 6 and 10 digits");
    }

    // Todo: refactor to use a factory and strategy pattern
    // Specific country checks (example)
    switch (phoneCountryCode.getCode()) {
      case "ZAR":
        if (localNumber.length() != 9) {
          throw new IllegalArgumentException(
              "South African mobile numbers must be 9 digits (excluding the leading '0')");
        }
        break;
      case "USA":
        if (localNumber.length() != 7) {
          throw new IllegalArgumentException("USA local numbers must be 7 digits");
        }
        break;
      case "DEN":
        if (!localNumber.startsWith("1") && localNumber.length() == 10) {
          throw new IllegalArgumentException(
              "German local numbers starting with '1' must be 10 digits long");
        }
        break;
      default:
        // More...
    }
  }

  public Set<String> getCountriesForMobileCountryCode() {
    return PhoneCountryCode.findByCode(phoneCountryCode);
  }
}
