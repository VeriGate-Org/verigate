/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.creditbureau.domain.mappers;

import verigate.adapter.creditbureau.domain.models.CreditCheckRequest;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Mapper for converting VerifyPartyCommand metadata to credit check requests.
 */
public interface CreditCheckMapper {

  /**
   * Maps a VerifyPartyCommand to a CreditCheckRequest.
   *
   * @param command the verification command
   * @return the mapped credit check request
   */
  CreditCheckRequest mapToCreditCheckRequest(VerifyPartyCommand command);

  /**
   * Default implementation providing mapping logic.
   */
  static CreditCheckRequest mapToCreditCheckRequestDefault(VerifyPartyCommand command) {

    String idNumber = extractField(command, "idNumber", "id_number", "identityNumber");
    String firstName = extractField(command, "firstName", "first_name", "givenName");
    String lastName = extractField(command, "lastName", "last_name", "surname", "familyName");
    String dateOfBirth = extractField(command, "dateOfBirth", "date_of_birth", "dob");
    String consentReference = extractField(
        command, "consentReference", "consent_reference", "consentRef");

    if (idNumber == null || idNumber.trim().isEmpty()) {
      throw new IllegalArgumentException(
          "ID number is required for credit bureau verification");
    }

    return CreditCheckRequest.builder()
        .idNumber(idNumber.trim())
        .firstName(firstName != null ? firstName.trim() : null)
        .lastName(lastName != null ? lastName.trim() : null)
        .dateOfBirth(dateOfBirth != null ? dateOfBirth.trim() : null)
        .consentReference(consentReference != null ? consentReference.trim() : null)
        .build();
  }

  /**
   * Extracts a field value from the command metadata using multiple possible key names.
   */
  static String extractField(VerifyPartyCommand command, String... keys) {
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
}
