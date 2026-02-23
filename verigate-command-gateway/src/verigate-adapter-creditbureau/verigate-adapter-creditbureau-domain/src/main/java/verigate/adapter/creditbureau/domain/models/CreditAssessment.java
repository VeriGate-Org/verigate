/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.creditbureau.domain.models;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a credit assessment from a credit bureau provider.
 */
public record CreditAssessment(
    int creditScore,
    CreditScoreBand scoreBand,
    CreditBureauProvider provider,
    LocalDate assessmentDate,
    double totalDebt,
    double monthlyIncome,
    double debtToIncomeRatio,
    boolean hasJudgments,
    boolean hasDefaults,
    List<String> fraudIndicators
) {
}
