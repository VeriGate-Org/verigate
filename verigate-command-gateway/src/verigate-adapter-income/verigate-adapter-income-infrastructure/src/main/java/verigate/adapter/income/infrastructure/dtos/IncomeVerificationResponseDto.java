/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.income.infrastructure.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for Income Verification API response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IncomeVerificationResponseDto(
    @JsonProperty("status") String status,
    @JsonProperty("verified_monthly_income") BigDecimal verifiedMonthlyIncome,
    @JsonProperty("declared_monthly_income") BigDecimal declaredMonthlyIncome,
    @JsonProperty("variance") BigDecimal variance,
    @JsonProperty("confidence_level") String confidenceLevel,
    @JsonProperty("evidence_sources") List<String> evidenceSources,
    @JsonProperty("affordability_confirmed") boolean affordabilityConfirmed,
    @JsonProperty("reason") String reason,
    @JsonProperty("verified_at") String verifiedAt
) {
}
