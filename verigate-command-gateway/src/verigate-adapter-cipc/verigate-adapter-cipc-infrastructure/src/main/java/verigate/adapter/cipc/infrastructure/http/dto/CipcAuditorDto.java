/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for CIPC auditor data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CipcAuditorDto(
    @JsonProperty("aud_name") String auditorName,
    @JsonProperty("aud_type_descr") String auditorTypeDescription,
    @JsonProperty("aud_status_descr") String auditorStatusDescription,
    @JsonProperty("profession_descr") String professionDescription,
    @JsonProperty("act_start_date") String activityStartDate,
    @JsonProperty("act_end_date") String activityEndDate
) {
}