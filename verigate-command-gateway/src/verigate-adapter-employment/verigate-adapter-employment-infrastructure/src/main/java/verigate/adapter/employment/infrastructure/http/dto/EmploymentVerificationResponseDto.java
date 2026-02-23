/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.employment.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for Employment Verification API response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record EmploymentVerificationResponseDto(
    @JsonProperty("status") String status,
    @JsonProperty("employer_name") String employerName,
    @JsonProperty("employment_type") String employmentType,
    @JsonProperty("job_title") String jobTitle,
    @JsonProperty("start_date") String startDate,
    @JsonProperty("end_date") String endDate,
    @JsonProperty("department") String department
) {
}
