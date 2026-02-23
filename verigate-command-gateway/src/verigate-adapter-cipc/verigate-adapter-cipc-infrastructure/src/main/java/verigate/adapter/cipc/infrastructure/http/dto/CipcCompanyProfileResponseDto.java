/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO for CIPC company profile API response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CipcCompanyProfileResponseDto(
    @JsonProperty("Company") List<CipcCompanyDto> company
) {
}