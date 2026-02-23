/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.income.domain.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import verigate.adapter.income.domain.enums.ConfidenceLevel;

/**
 * Represents the detailed assessment of an income verification,
 * comparing declared income against verified income with supporting evidence.
 *
 * @param verifiedMonthlyIncome the monthly income amount confirmed through verification
 * @param declaredMonthlyIncome the monthly income amount declared by the applicant
 * @param variance the percentage variance between declared and verified income
 * @param confidence the confidence level of the verification assessment
 * @param evidenceSources the list of evidence sources used in the assessment
 * @param affordabilityConfirmed whether the income supports the affordability assessment
 */
public record IncomeAssessment(
    BigDecimal verifiedMonthlyIncome,
    BigDecimal declaredMonthlyIncome,
    BigDecimal variance,
    ConfidenceLevel confidence,
    List<String> evidenceSources,
    boolean affordabilityConfirmed
) {

  /**
   * Calculates the variance percentage between declared and verified income.
   *
   * <p>The variance is calculated as:
   * {@code ((declared - verified) / declared) * 100}
   *
   * <p>A positive variance means declared income exceeds verified income.
   * A negative variance means verified income exceeds declared income.
   *
   * @param declared the declared monthly income
   * @param verified the verified monthly income
   * @return the variance as a percentage, or {@link BigDecimal#ZERO} if declared is zero or null
   */
  public static BigDecimal calculateVariance(BigDecimal declared, BigDecimal verified) {
    if (declared == null || verified == null
        || declared.compareTo(BigDecimal.ZERO) == 0) {
      return BigDecimal.ZERO;
    }

    return declared.subtract(verified)
        .divide(declared, 4, RoundingMode.HALF_UP)
        .multiply(new BigDecimal("100"))
        .setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * Creates an assessment indicating insufficient evidence for verification.
   *
   * @param declaredMonthlyIncome the declared monthly income
   * @return an assessment with NONE confidence and no verified amount
   */
  public static IncomeAssessment insufficientEvidence(BigDecimal declaredMonthlyIncome) {
    return new IncomeAssessment(
        BigDecimal.ZERO,
        declaredMonthlyIncome,
        BigDecimal.ZERO,
        ConfidenceLevel.NONE,
        List.of(),
        false);
  }

  /**
   * Creates an error assessment when verification could not be completed.
   *
   * @param declaredMonthlyIncome the declared monthly income
   * @return an assessment representing an error state
   */
  public static IncomeAssessment error(BigDecimal declaredMonthlyIncome) {
    return new IncomeAssessment(
        BigDecimal.ZERO,
        declaredMonthlyIncome != null ? declaredMonthlyIncome : BigDecimal.ZERO,
        BigDecimal.ZERO,
        ConfidenceLevel.NONE,
        List.of(),
        false);
  }
}
