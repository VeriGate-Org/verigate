/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.fraudwatchlist.domain.mappers;

import verigate.adapter.fraudwatchlist.domain.models.FraudCheckRequest;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Mapper for converting VerifyPartyCommand to fraud watchlist screening requests.
 */
public interface FraudWatchlistMapper {

  /**
   * Maps a VerifyPartyCommand to a FraudCheckRequest.
   *
   * @param command the verification command
   * @return the mapped fraud check request
   */
  FraudCheckRequest mapToFraudCheckRequest(VerifyPartyCommand command);

  /**
   * Default implementation providing mapping logic.
   */
  static FraudCheckRequest mapToFraudCheckRequestDefault(VerifyPartyCommand command) {

    String idNumber = extractField(command,
        "idNumber", "id_number", "identityNumber", "identity_number");
    String firstName = extractField(command,
        "firstName", "first_name", "givenName", "given_name");
    String lastName = extractField(command,
        "lastName", "last_name", "surname", "family_name");
    String dateOfBirth = extractField(command,
        "dateOfBirth", "date_of_birth", "dob");
    String phoneNumber = extractField(command,
        "phoneNumber", "phone_number", "mobileNumber", "mobile_number");

    if (idNumber == null || idNumber.trim().isEmpty()) {
      throw new IllegalArgumentException("ID number is required for fraud watchlist screening");
    }

    return FraudCheckRequest.builder()
        .idNumber(idNumber.trim())
        .firstName(firstName != null ? firstName.trim() : null)
        .lastName(lastName != null ? lastName.trim() : null)
        .dateOfBirth(dateOfBirth != null ? dateOfBirth.trim() : null)
        .phoneNumber(phoneNumber != null ? phoneNumber.trim() : null)
        .build();
  }

  /**
   * Extracts a field value from the command metadata, trying multiple possible field names.
   */
  static String extractField(VerifyPartyCommand command, String... fieldNames) {
    if (command.getMetadata() == null) {
      return null;
    }

    for (String fieldName : fieldNames) {
      Object value = command.getMetadata().get(fieldName);
      if (value != null) {
        return value.toString();
      }
    }

    return null;
  }
}
