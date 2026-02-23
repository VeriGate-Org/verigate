/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.qlink.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for QLink bank verification request.
 */
public record QLinkBankVerificationRequestDto(
    @JsonProperty("account_number") String accountNumber,
    @JsonProperty("branch_code") String branchCode,
    @JsonProperty("account_holder_name") String accountHolderName,
    @JsonProperty("id_number") String idNumber
) {
}
