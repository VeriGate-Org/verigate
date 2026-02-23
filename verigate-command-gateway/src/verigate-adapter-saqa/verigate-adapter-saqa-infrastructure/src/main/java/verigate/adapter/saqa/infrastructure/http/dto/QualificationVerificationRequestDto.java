/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.saqa.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for SAQA qualification verification request.
 */
public record QualificationVerificationRequestDto(
    @JsonProperty("id_number") String idNumber,
    @JsonProperty("first_name") String firstName,
    @JsonProperty("last_name") String lastName,
    @JsonProperty("qualification_title") String qualificationTitle,
    @JsonProperty("institution") String institution,
    @JsonProperty("year_completed") int yearCompleted
) {
}
