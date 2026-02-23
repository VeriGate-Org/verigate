/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for CIPC capital data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CipcCapitalDto(
    @JsonProperty("capital_type_descr") String capitalTypeDescription,
    @JsonProperty("cap_no_shares") Integer numberOfShares,
    @JsonProperty("cap_par") Integer parValue,
    @JsonProperty("cap_amt_share") Integer shareAmount,
    @JsonProperty("cap_prem") Integer premium
) {
}