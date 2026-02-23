/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.fraudwatchlist.domain.models;

import java.util.List;

/**
 * Represents the response from a fraud watchlist screening check.
 */
public record FraudCheckResponse(
    FraudCheckStatus status,
    List<FraudAlert> alerts,
    double overallRiskScore,
    List<FraudSource> matchedSources,
    String screeningSummary
) {

  /**
   * Creates a clear response indicating no fraud alerts were found.
   */
  public static FraudCheckResponse clear(String screeningSummary) {
    return new FraudCheckResponse(
        FraudCheckStatus.CLEAR,
        List.of(),
        0.0,
        List.of(),
        screeningSummary);
  }

  /**
   * Creates a flagged response indicating potential fraud alerts were found.
   */
  public static FraudCheckResponse flagged(
      List<FraudAlert> alerts,
      double overallRiskScore,
      List<FraudSource> matchedSources,
      String screeningSummary) {
    return new FraudCheckResponse(
        FraudCheckStatus.FLAGGED,
        alerts,
        overallRiskScore,
        matchedSources,
        screeningSummary);
  }

  /**
   * Creates a confirmed fraud response indicating definite fraud match.
   */
  public static FraudCheckResponse confirmedFraud(
      List<FraudAlert> alerts,
      double overallRiskScore,
      List<FraudSource> matchedSources,
      String screeningSummary) {
    return new FraudCheckResponse(
        FraudCheckStatus.CONFIRMED_FRAUD,
        alerts,
        overallRiskScore,
        matchedSources,
        screeningSummary);
  }

  /**
   * Creates an error response indicating a system error occurred during screening.
   */
  public static FraudCheckResponse error(String errorMessage) {
    return new FraudCheckResponse(
        FraudCheckStatus.ERROR,
        List.of(),
        0.0,
        List.of(),
        errorMessage);
  }
}
