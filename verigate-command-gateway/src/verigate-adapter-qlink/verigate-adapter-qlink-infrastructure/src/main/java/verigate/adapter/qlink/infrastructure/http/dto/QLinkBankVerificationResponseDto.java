/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.qlink.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for QLink bank verification API response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record QLinkBankVerificationResponseDto(
    @JsonProperty("status") String status,
    @JsonProperty("account_holder_name") String accountHolderName,
    @JsonProperty("branch_name") String branchName,
    @JsonProperty("account_type") String accountType,
    @JsonProperty("match_score") double matchScore,
    @JsonProperty("error_message") String errorMessage
) {
}
