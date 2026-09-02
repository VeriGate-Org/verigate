/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.datanamix.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Response DTO for {@code POST /v1/cipc/company-search}. {@code Header} is present in
 * the real response but not needed by story 2.2's logic, so it isn't modeled.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CompanySearchResponseDto(
    @JsonProperty("CommercialDetails") List<CompanyMatchDto> commercialDetails,
    @JsonProperty("Success") boolean success,
    @JsonProperty("Messages") List<String> messages,
    @JsonProperty("ResponseCode") int responseCode
) {

  /**
   * Compact constructor — defaults null lists to empty lists.
   */
  public CompanySearchResponseDto {
    if (commercialDetails == null) {
      commercialDetails = List.of();
    }
    if (messages == null) {
      messages = List.of();
    }
  }
}
