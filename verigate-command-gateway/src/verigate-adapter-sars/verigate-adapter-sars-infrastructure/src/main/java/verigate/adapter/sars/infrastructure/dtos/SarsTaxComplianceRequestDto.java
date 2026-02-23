/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.infrastructure.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for SARS Tax Compliance API request.
 */
public record SarsTaxComplianceRequestDto(
    @JsonProperty("id_number") String idNumber,
    @JsonProperty("tax_reference_number") String taxReferenceNumber,
    @JsonProperty("company_registration_number") String companyRegistrationNumber,
    @JsonProperty("clearance_type") String clearanceType
) {
}
