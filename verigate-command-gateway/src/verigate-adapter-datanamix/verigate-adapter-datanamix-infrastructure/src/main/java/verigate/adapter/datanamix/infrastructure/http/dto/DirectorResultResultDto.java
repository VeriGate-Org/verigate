/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.datanamix.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {@code Result} in the {@code POST /v1/cipc/director-result} response — wraps
 * {@code DirectorResults}. Named {@code DirectorResultResultDto} (not just
 * {@code ResultDto}) to avoid an overly generic class name that would be ambiguous
 * across the codebase.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DirectorResultResultDto(
    @JsonProperty("DirectorResults") DirectorResultsDto directorResults
) {
}
