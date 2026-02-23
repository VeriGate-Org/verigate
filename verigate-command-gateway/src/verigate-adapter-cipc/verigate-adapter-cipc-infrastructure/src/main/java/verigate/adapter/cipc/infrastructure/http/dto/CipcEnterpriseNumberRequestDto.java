/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for CIPC enterprise number request.
 */
public record CipcEnterpriseNumberRequestDto(
    @JsonProperty("enterprise_number") String enterpriseNumber
) {
}