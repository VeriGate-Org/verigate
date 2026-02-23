/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.creditbureau.domain.models;

import verigate.adapter.creditbureau.domain.constants.DomainConstants;

/**
 * Enumeration of credit score bands with associated score ranges.
 */
public enum CreditScoreBand {
  EXCELLENT("750+"),
  GOOD("700-749"),
  FAIR("650-699"),
  POOR("600-649"),
  VERY_POOR("below 600");

  private final String rangeDescription;

  CreditScoreBand(String rangeDescription) {
    this.rangeDescription = rangeDescription;
  }

  /**
   * Returns the human-readable score range description for this band.
   */
  public String getRangeDescription() {
    return rangeDescription;
  }

  /**
   * Determines the credit score band for a given numerical credit score.
   *
   * @param score the numerical credit score
   * @return the corresponding credit score band
   */
  public static CreditScoreBand fromScore(int score) {
    if (score >= DomainConstants.SCORE_THRESHOLD_EXCELLENT) {
      return EXCELLENT;
    } else if (score >= DomainConstants.SCORE_THRESHOLD_GOOD) {
      return GOOD;
    } else if (score >= DomainConstants.SCORE_THRESHOLD_FAIR) {
      return FAIR;
    } else if (score >= DomainConstants.SCORE_THRESHOLD_POOR) {
      return POOR;
    } else {
      return VERY_POOR;
    }
  }
}
