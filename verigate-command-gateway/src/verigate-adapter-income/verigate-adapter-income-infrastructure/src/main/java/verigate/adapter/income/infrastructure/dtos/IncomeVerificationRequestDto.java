/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.income.infrastructure.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

/**
 * DTO for Income Verification API request.
 */
public record IncomeVerificationRequestDto(
    @JsonProperty("id_number") String idNumber,
    @JsonProperty("employer_name") String employerName,
    @JsonProperty("declared_monthly_income") BigDecimal declaredMonthlyIncome,
    @JsonProperty("income_source_type") String incomeSourceType,
    @JsonProperty("bank_account_number") String bankAccountNumber
) {
}
