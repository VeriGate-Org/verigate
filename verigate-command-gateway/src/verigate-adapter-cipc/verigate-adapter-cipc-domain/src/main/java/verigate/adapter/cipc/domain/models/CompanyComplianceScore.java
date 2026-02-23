/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.domain.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a company's compliance score calculated from its CIPC profile data.
 * The overall score ranges from 0.0 (fully non-compliant) to 1.0 (fully compliant),
 * based on weighted evaluation of multiple compliance factors.
 */
public record CompanyComplianceScore(
    double overallScore,
    boolean hasActiveDirectors,
    boolean hasRegisteredAddress,
    boolean hasAuditor,
    boolean hasTaxNumber,
    boolean isInGoodStanding,
    int activeDirectorCount,
    int totalDirectorCount,
    String complianceSummary
) {

  /** Weight for company good standing status in overall score calculation. */
  private static final double WEIGHT_GOOD_STANDING = 0.30;

  /** Weight for having active directors in overall score calculation. */
  private static final double WEIGHT_ACTIVE_DIRECTORS = 0.25;

  /** Weight for having a registered office address in overall score calculation. */
  private static final double WEIGHT_REGISTERED_ADDRESS = 0.15;

  /** Weight for having an assigned auditor in overall score calculation. */
  private static final double WEIGHT_AUDITOR = 0.15;

  /** Weight for having a registered tax number in overall score calculation. */
  private static final double WEIGHT_TAX_NUMBER = 0.15;

  /** Minimum overall score threshold for a company to be considered compliant. */
  private static final double COMPLIANCE_THRESHOLD = 0.6;

  /**
   * Calculates a compliance score from the given company profile by evaluating
   * each compliance factor and computing a weighted overall score.
   *
   * @param profile the company profile to assess
   * @return the calculated compliance score
   */
  public static CompanyComplianceScore calculate(CompanyProfile profile) {
    boolean goodStanding = profile.isActive();

    List<Director> activeDirectors = profile.getActiveDirectors();
    boolean activeDirectorsPresent = !activeDirectors.isEmpty();
    int activeCount = activeDirectors.size();
    int totalCount = profile.directors() != null ? profile.directors().size() : 0;

    boolean registeredAddress = hasValidAddress(profile.officeAddress());

    boolean auditorAssigned = profile.auditors() != null && !profile.auditors().isEmpty();

    boolean taxNumberRegistered = profile.taxNumber() != null
        && !profile.taxNumber().trim().isEmpty();

    double score = 0.0;
    if (goodStanding) {
      score += WEIGHT_GOOD_STANDING;
    }
    if (activeDirectorsPresent) {
      score += WEIGHT_ACTIVE_DIRECTORS;
    }
    if (registeredAddress) {
      score += WEIGHT_REGISTERED_ADDRESS;
    }
    if (auditorAssigned) {
      score += WEIGHT_AUDITOR;
    }
    if (taxNumberRegistered) {
      score += WEIGHT_TAX_NUMBER;
    }

    String summary = buildComplianceSummary(
        score, goodStanding, activeDirectorsPresent, registeredAddress,
        auditorAssigned, taxNumberRegistered, activeCount, totalCount);

    return new CompanyComplianceScore(
        score,
        activeDirectorsPresent,
        registeredAddress,
        auditorAssigned,
        taxNumberRegistered,
        goodStanding,
        activeCount,
        totalCount,
        summary);
  }

  /**
   * Checks whether the overall compliance score meets the minimum compliance threshold.
   *
   * @return true if the score is at or above the compliance threshold
   */
  public boolean meetsComplianceThreshold() {
    return overallScore >= COMPLIANCE_THRESHOLD;
  }

  /**
   * Checks if the given address contains meaningful data.
   */
  private static boolean hasValidAddress(Address address) {
    if (address == null) {
      return false;
    }
    return (address.addressLine1() != null && !address.addressLine1().trim().isEmpty())
        || (address.city() != null && !address.city().trim().isEmpty());
  }

  /**
   * Builds a human-readable summary of the compliance assessment.
   */
  private static String buildComplianceSummary(
      double score,
      boolean goodStanding,
      boolean activeDirectors,
      boolean registeredAddress,
      boolean auditorAssigned,
      boolean taxNumber,
      int activeCount,
      int totalCount) {

    List<String> issues = new ArrayList<>();

    if (!goodStanding) {
      issues.add("company not in good standing");
    }
    if (!activeDirectors) {
      issues.add("no active directors");
    }
    if (!registeredAddress) {
      issues.add("no registered office address");
    }
    if (!auditorAssigned) {
      issues.add("no auditor assigned");
    }
    if (!taxNumber) {
      issues.add("no tax number registered");
    }

    String scorePercent = String.format("%.0f%%", score * 100);

    if (issues.isEmpty()) {
      return String.format(
          "Compliance score: %s. All checks passed. Active directors: %d/%d.",
          scorePercent, activeCount, totalCount);
    }

    return String.format(
        "Compliance score: %s. Issues: %s. Active directors: %d/%d.",
        scorePercent, String.join(", ", issues), activeCount, totalCount);
  }
}
