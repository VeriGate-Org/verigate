/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.datanamix.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request DTO for {@code POST /v1/cipc/director-search}.
 */
public record DirectorSearchRequestDto(
    @JsonProperty("EnvironmentType") String environmentType,
    @JsonProperty("ClientReference") String clientReference,
    @JsonProperty("IDNumber") String idNumber
) {
}
