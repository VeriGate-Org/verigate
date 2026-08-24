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
 * Response DTO for the CIPC {@code /companyprofile} lookup.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DocumentCipcLookupResponseDto(
    @JsonProperty("Company") List<DocumentCipcCompanyDto> company
) {
}
