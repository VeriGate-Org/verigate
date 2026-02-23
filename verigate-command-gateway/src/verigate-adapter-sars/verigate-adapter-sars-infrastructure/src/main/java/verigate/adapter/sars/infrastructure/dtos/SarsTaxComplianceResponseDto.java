/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.infrastructure.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for SARS Tax Compliance API response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SarsTaxComplianceResponseDto(
    @JsonProperty("status") String status,
    @JsonProperty("reason") String reason,
    @JsonProperty("certificate_number") String certificateNumber,
    @JsonProperty("issue_date") String issueDate,
    @JsonProperty("expiry_date") String expiryDate,
    @JsonProperty("clearance_type") String clearanceType,
    @JsonProperty("certificate_valid") Boolean certificateValid
) {
}
