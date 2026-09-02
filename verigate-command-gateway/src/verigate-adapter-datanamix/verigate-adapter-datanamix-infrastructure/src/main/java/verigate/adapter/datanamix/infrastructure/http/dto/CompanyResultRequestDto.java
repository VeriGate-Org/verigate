/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.datanamix.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request DTO for {@code POST /v1/cipc/company-result}. {@code OutputFormat} is always
 * {@code JSON} for this integration — no PDF report is needed, so
 * {@code PDFEncryptionPassword} is never populated.
 */
public record CompanyResultRequestDto(
    @JsonProperty("EnvironmentType") String environmentType,
    @JsonProperty("ClientReference") String clientReference,
    @JsonProperty("OutputFormat") String outputFormat,
    @JsonProperty("EnquiryID") Long enquiryId,
    @JsonProperty("EnquiryResultID") Long enquiryResultId
) {
}
