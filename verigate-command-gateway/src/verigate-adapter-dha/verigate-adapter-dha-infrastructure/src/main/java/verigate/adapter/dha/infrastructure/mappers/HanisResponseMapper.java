/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.infrastructure.mappers;

import verigate.adapter.dha.domain.models.CitizenshipStatus;
import verigate.adapter.dha.domain.models.HanisPersonDetails;
import verigate.adapter.dha.domain.models.IdVerificationStatus;
import verigate.adapter.dha.domain.models.IdentityVerificationResponse;
import verigate.adapter.dha.domain.models.VitalStatus;

/**
 * Maps HANIS NPR person details to the domain {@link IdentityVerificationResponse}.
 */
public class HanisResponseMapper {

  /**
   * Maps HANIS person details to the standard identity verification response.
   *
   * @param details the HANIS person details from the NPR
   * @return the mapped identity verification response
   */
  public IdentityVerificationResponse mapToVerificationResponse(HanisPersonDetails details) {
    if (details == null || !details.isSuccess()) {
      return IdentityVerificationResponse.notFound();
    }

    // Deceased
    if (details.deadIndicator()) {
      String deathInfo = buildDeathDetails(details);
      return IdentityVerificationResponse.deceased(deathInfo);
    }

    // Blocked
    if (details.idnBlocked()) {
      return IdentityVerificationResponse.error(
          IdVerificationStatus.BLOCKED,
          "ID number is blocked in the population register");
    }

    // Determine citizenship from birth country code
    CitizenshipStatus citizenship = mapCitizenship(details.birthPlaceCountryCode());

    // Determine vital status
    VitalStatus vitalStatus = details.deadIndicator() ? VitalStatus.DECEASED : VitalStatus.ALIVE;

    // Build match details
    String matchDetails = buildMatchDetails(details);

    return IdentityVerificationResponse.verified(
        citizenship, details.maritalStatus(), vitalStatus, matchDetails);
  }

  private CitizenshipStatus mapCitizenship(String birthPlaceCountryCode) {
    if (birthPlaceCountryCode == null || birthPlaceCountryCode.isBlank()) {
      return CitizenshipStatus.UNKNOWN;
    }
    if ("ZA".equalsIgnoreCase(birthPlaceCountryCode)
        || "RSA".equalsIgnoreCase(birthPlaceCountryCode)) {
      return CitizenshipStatus.CITIZEN;
    }
    return CitizenshipStatus.PERMANENT_RESIDENT;
  }

  private String buildMatchDetails(HanisPersonDetails details) {
    StringBuilder sb = new StringBuilder();
    sb.append("Name: ").append(nullSafe(details.name()));
    sb.append(", Surname: ").append(nullSafe(details.surname()));
    sb.append(", Smart Card: ").append(details.smartCardIssued() ? "Yes" : "No");
    if (details.idIssueDate() != null) {
      sb.append(", ID Issue Date: ").append(details.idIssueDate());
    }
    sb.append(", On HANIS: ").append(details.onHanis() ? "Yes" : "No");
    sb.append(", On NPR: ").append(details.onNpr() ? "Yes" : "No");
    if (details.birthPlaceCountryCode() != null) {
      sb.append(", Birth Country: ").append(details.birthPlaceCountryCode());
    }
    return sb.toString();
  }

  private String buildDeathDetails(HanisPersonDetails details) {
    StringBuilder sb = new StringBuilder("Individual is recorded as deceased.");
    if (details.dateOfDeath() != null && !details.dateOfDeath().isBlank()) {
      sb.append(" Date of death: ").append(details.dateOfDeath()).append(".");
    }
    return sb.toString();
  }

  private String nullSafe(String value) {
    return value != null ? value : "N/A";
  }
}
