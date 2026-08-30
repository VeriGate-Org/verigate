/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.datanamix.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO for {@code POST /v1/cipc/company-result}. {@code Header} and
 * {@code PDFReport} are present in the real response but not needed by story 2.2's
 * logic (no PDF is ever requested), so neither is modeled.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CompanyResultResponseDto(
    @JsonProperty("CIPCResult") CipcResultDto cipcResult
) {
}
