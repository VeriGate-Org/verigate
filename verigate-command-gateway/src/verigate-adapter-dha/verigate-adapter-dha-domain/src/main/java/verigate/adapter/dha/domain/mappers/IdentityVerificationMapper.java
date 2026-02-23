/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.domain.mappers;

import verigate.adapter.dha.domain.models.IdentityVerificationRequest;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Mapper for converting VerifyPartyCommand to DHA identity verification requests.
 */
public interface IdentityVerificationMapper {

  /**
   * Maps a VerifyPartyCommand to an IdentityVerificationRequest.
   *
   * @param command the verification command
   * @return the mapped identity verification request
   */
  IdentityVerificationRequest mapToIdentityVerificationRequest(VerifyPartyCommand command);

  /**
   * Default implementation providing mapping logic.
   */
  static IdentityVerificationRequest mapToIdentityVerificationRequestDefault(
      VerifyPartyCommand command) {

    // Extract identity details from command metadata
    String idNumber = extractIdNumber(command);

    if (idNumber == null || idNumber.trim().isEmpty()) {
      throw new IllegalArgumentException("ID number is required for DHA identity verification");
    }

    String firstName = extractStringField(command, "firstName", "first_name");
    String lastName = extractStringField(command, "lastName", "last_name", "surname");
    String dateOfBirth = extractStringField(command, "dateOfBirth", "date_of_birth", "dob");
    String gender = extractStringField(command, "gender", "sex");

    return IdentityVerificationRequest.builder()
        .idNumber(idNumber.trim())
        .firstName(firstName)
        .lastName(lastName)
        .dateOfBirth(dateOfBirth)
        .gender(gender)
        .build();
  }

  /**
   * Extracts the ID number from the command metadata.
   */
  static String extractIdNumber(VerifyPartyCommand command) {
    if (command.getMetadata() == null) {
      return null;
    }

    // Try various possible field names for ID number
    Object idNumberObj = command.getMetadata().get("idNumber");
    if (idNumberObj != null) {
      return idNumberObj.toString();
    }

    idNumberObj = command.getMetadata().get("id_number");
    if (idNumberObj != null) {
      return idNumberObj.toString();
    }

    idNumberObj = command.getMetadata().get("identityNumber");
    if (idNumberObj != null) {
      return idNumberObj.toString();
    }

    idNumberObj = command.getMetadata().get("identity_number");
    if (idNumberObj != null) {
      return idNumberObj.toString();
    }

    idNumberObj = command.getMetadata().get("saIdNumber");
    if (idNumberObj != null) {
      return idNumberObj.toString();
    }

    idNumberObj = command.getMetadata().get("sa_id_number");
    return idNumberObj != null ? idNumberObj.toString() : null;
  }

  /**
   * Extracts a string field from command metadata, trying multiple key names.
   */
  static String extractStringField(VerifyPartyCommand command, String... keys) {
    if (command.getMetadata() == null) {
      return null;
    }

    for (String key : keys) {
      Object value = command.getMetadata().get(key);
      if (value != null) {
        return value.toString();
      }
    }
    return null;
  }

  /**
   * Validates South African ID number format.
   * SA ID numbers are 13 digits in the format YYMMDDSSSSCZZ.
   */
  static boolean isValidSaIdNumber(String idNumber) {
    if (idNumber == null || idNumber.trim().isEmpty()) {
      return false;
    }

    String trimmed = idNumber.trim();

    // Must be exactly 13 digits
    if (!trimmed.matches("\\d{13}")) {
      return false;
    }

    // Basic date validation from ID number (YYMMDD)
    int month = Integer.parseInt(trimmed.substring(2, 4));
    int day = Integer.parseInt(trimmed.substring(4, 6));

    if (month < 1 || month > 12) {
      return false;
    }

    if (day < 1 || day > 31) {
      return false;
    }

    // Citizenship digit (position 10): 0 = SA citizen, 1 = permanent resident
    int citizenshipDigit = Character.getNumericValue(trimmed.charAt(10));
    if (citizenshipDigit != 0 && citizenshipDigit != 1) {
      return false;
    }

    return true;
  }
}
