/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.infrastructure.mappers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.dha.domain.models.CitizenshipStatus;
import verigate.adapter.dha.domain.models.IdVerificationStatus;
import verigate.adapter.dha.domain.models.IdentityVerificationRequest;
import verigate.adapter.dha.domain.models.IdentityVerificationResponse;
import verigate.adapter.dha.domain.models.VitalStatus;
import verigate.adapter.dha.infrastructure.http.dto.DhaIdentityRequestDto;
import verigate.adapter.dha.infrastructure.http.dto.DhaIdentityResponseDto;

/**
 * Mapper for converting between DHA DTOs and domain models.
 */
public class DhaDtoMapper {

  private static final Logger logger = LoggerFactory.getLogger(DhaDtoMapper.class);

  /**
   * Maps a domain identity verification request to the DHA API request DTO.
   */
  public DhaIdentityRequestDto mapToRequestDto(IdentityVerificationRequest request) {
    if (request == null) {
      return null;
    }

    return new DhaIdentityRequestDto(
        request.idNumber(),
        request.firstName(),
        request.lastName(),
        request.dateOfBirth(),
        request.gender());
  }

  /**
   * Maps a DHA API response DTO to the domain identity verification response.
   */
  public IdentityVerificationResponse mapToIdentityVerificationResponse(
      DhaIdentityResponseDto dto) {
    if (dto == null) {
      return IdentityVerificationResponse.notFound();
    }

    IdVerificationStatus status =
        IdVerificationStatus.fromDescription(dto.verificationStatus());
    CitizenshipStatus citizenshipStatus =
        CitizenshipStatus.fromDescription(dto.citizenshipStatus());
    VitalStatus vitalStatus =
        VitalStatus.fromDescription(dto.vitalStatus());

    String matchDetails = buildMatchDetails(dto);

    logger.debug(
        "Mapped DHA response - status: {}, citizenship: {}, vital: {}",
        status,
        citizenshipStatus,
        vitalStatus);

    return new IdentityVerificationResponse(
        status,
        citizenshipStatus,
        dto.maritalStatus(),
        vitalStatus,
        matchDetails);
  }

  /**
   * Builds a human-readable match details string from the response DTO.
   */
  private String buildMatchDetails(DhaIdentityResponseDto dto) {
    if (dto.matchDetails() != null && !dto.matchDetails().trim().isEmpty()) {
      return dto.matchDetails();
    }

    StringBuilder details = new StringBuilder();

    if (dto.firstNameMatch() != null) {
      details.append("First name: ")
          .append(dto.firstNameMatch() ? "matched" : "not matched")
          .append(". ");
    }

    if (dto.lastNameMatch() != null) {
      details.append("Last name: ")
          .append(dto.lastNameMatch() ? "matched" : "not matched")
          .append(". ");
    }

    if (dto.dateOfBirthMatch() != null) {
      details.append("Date of birth: ")
          .append(dto.dateOfBirthMatch() ? "matched" : "not matched")
          .append(". ");
    }

    if (dto.genderMatch() != null) {
      details.append("Gender: ")
          .append(dto.genderMatch() ? "matched" : "not matched")
          .append(". ");
    }

    return details.length() > 0 ? details.toString().trim() : null;
  }
}
