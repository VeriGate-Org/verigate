/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.creditbureau.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO for Credit Bureau credit check API response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CreditCheckResponseDto(
    @JsonProperty("status") String status,
    @JsonProperty("credit_score") Integer creditScore,
    @JsonProperty("score_band") String scoreBand,
    @JsonProperty("provider") String provider,
    @JsonProperty("assessment_date") String assessmentDate,
    @JsonProperty("total_debt") Double totalDebt,
    @JsonProperty("monthly_income") Double monthlyIncome,
    @JsonProperty("debt_to_income_ratio") Double debtToIncomeRatio,
    @JsonProperty("has_judgments") Boolean hasJudgments,
    @JsonProperty("has_defaults") Boolean hasDefaults,
    @JsonProperty("fraud_indicators") List<String> fraudIndicators,
    @JsonProperty("affordability_confirmed") Boolean affordabilityConfirmed,
    @JsonProperty("risk_level") String riskLevel,
    @JsonProperty("summary") String summary
) {
}
