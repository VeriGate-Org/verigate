/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.saqa.domain.mappers;

import verigate.adapter.saqa.domain.models.QualificationVerificationRequest;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Mapper for converting VerifyPartyCommand to SAQA qualification verification requests.
 */
public interface QualificationVerificationMapper {

  /**
   * Maps a VerifyPartyCommand to a QualificationVerificationRequest.
   *
   * @param command the verification command
   * @return the mapped qualification verification request
   */
  QualificationVerificationRequest mapToQualificationVerificationRequest(
      VerifyPartyCommand command);

  /**
   * Default implementation providing mapping logic.
   */
  static QualificationVerificationRequest mapToQualificationVerificationRequestDefault(
      VerifyPartyCommand command) {

    if (command.getMetadata() == null) {
      throw new IllegalArgumentException(
          "Command metadata is required for SAQA qualification verification");
    }

    String idNumber = extractStringField(command, "idNumber", "id_number");
    String firstName = extractStringField(command, "firstName", "first_name");
    String lastName = extractStringField(command, "lastName", "last_name");
    String qualificationTitle = extractStringField(
        command, "qualificationTitle", "qualification_title");
    String institution = extractStringField(command, "institution", "institution_name");
    int yearCompleted = extractIntField(command, "yearCompleted", "year_completed");

    if (idNumber == null || idNumber.trim().isEmpty()) {
      throw new IllegalArgumentException(
          "ID number is required for SAQA qualification verification");
    }

    return QualificationVerificationRequest.builder()
        .idNumber(idNumber.trim())
        .firstName(firstName != null ? firstName.trim() : null)
        .lastName(lastName != null ? lastName.trim() : null)
        .qualificationTitle(qualificationTitle != null ? qualificationTitle.trim() : null)
        .institution(institution != null ? institution.trim() : null)
        .yearCompleted(yearCompleted)
        .build();
  }

  /**
   * Extracts a string field from command metadata, trying multiple key variations.
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
   * Extracts an integer field from command metadata, trying multiple key variations.
   */
  static int extractIntField(VerifyPartyCommand command, String... keys) {
    String value = extractStringField(command, keys);
    if (value == null || value.trim().isEmpty()) {
      return 0;
    }
    try {
      return Integer.parseInt(value.trim());
    } catch (NumberFormatException e) {
      return 0;
    }
  }
}
