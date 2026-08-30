/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.datanamix.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A single entry in {@code CIPCResult.CommercialDirectorInformation[]} from
 * {@code POST /v1/cipc/company-result}. {@code Initials}, {@code BirthDate},
 * {@code DirectorStatusDate}, {@code MemberSize}, and {@code PhysicalAddress} are
 * present in the real response but not needed by story 2.2's logic, so none are
 * modeled here.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CommercialDirectorInformationDto(
    @JsonProperty("IDNumber") String idNumber,
    @JsonProperty("FullName") String fullName,
    @JsonProperty("DirectorStatusCode") String directorStatusCode,
    @JsonProperty("AppointmentDate") String appointmentDate
) {
}
