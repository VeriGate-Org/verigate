/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for DHA identity verification API response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DhaIdentityResponseDto(
    @JsonProperty("verification_status") String verificationStatus,
    @JsonProperty("citizenship_status") String citizenshipStatus,
    @JsonProperty("marital_status") String maritalStatus,
    @JsonProperty("vital_status") String vitalStatus,
    @JsonProperty("match_details") String matchDetails,
    @JsonProperty("first_name_match") Boolean firstNameMatch,
    @JsonProperty("last_name_match") Boolean lastNameMatch,
    @JsonProperty("date_of_birth_match") Boolean dateOfBirthMatch,
    @JsonProperty("gender_match") Boolean genderMatch
) {
}
