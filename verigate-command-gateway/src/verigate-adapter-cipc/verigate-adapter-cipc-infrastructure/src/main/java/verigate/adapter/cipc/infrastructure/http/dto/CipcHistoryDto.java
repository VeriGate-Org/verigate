/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for CIPC change history data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CipcHistoryDto(
    @JsonProperty("change_date") String changeDate,
    @JsonProperty("change_description") String changeDescription
) {
}