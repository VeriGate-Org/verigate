/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.infrastructure.http.cipc.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Minimal CIPC company fields needed for document cross-validation — a narrower subset than
 * {@code verigate-adapter-cipc}'s {@code CipcCompanyDto}, by design (see story 2.1 notes).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DocumentCipcCompanyDto(
    @JsonProperty("enterprise_number") String enterpriseNumber,
    @JsonProperty("enterprise_name") String enterpriseName,
    @JsonProperty("enterprise_type_description") String enterpriseTypeDescription,
    @JsonProperty("enterprise_status_description") String enterpriseStatusDescription,
    @JsonProperty("office_address") List<DocumentCipcAddressDto> officeAddress,
    @JsonProperty("directors") List<DocumentCipcDirectorDto> directors
) {
}
