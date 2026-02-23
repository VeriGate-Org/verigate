/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.employment.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for Employment Verification API request.
 */
public record EmploymentVerificationRequestDto(
    @JsonProperty("id_number") String idNumber,
    @JsonProperty("employer_name") String employerName,
    @JsonProperty("employee_number") String employeeNumber,
    @JsonProperty("start_date") String startDate
) {
}
