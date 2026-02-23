/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.fraudwatchlist.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO for fraud watchlist check API response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FraudCheckResponseDto(
    @JsonProperty("status") String status,
    @JsonProperty("alerts") List<FraudAlertDto> alerts,
    @JsonProperty("overall_risk_score") double overallRiskScore,
    @JsonProperty("matched_sources") List<String> matchedSources,
    @JsonProperty("screening_summary") String screeningSummary
) {

  /**
   * DTO for individual fraud alert entries.
   */
  @JsonIgnoreProperties(ignoreUnknown = true)
  public record FraudAlertDto(
      @JsonProperty("source") String source,
      @JsonProperty("alert_type") String alertType,
      @JsonProperty("alert_date") String alertDate,
      @JsonProperty("severity") double severity,
      @JsonProperty("description") String description,
      @JsonProperty("reference_number") String referenceNumber
  ) {
  }
}
