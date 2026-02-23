/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.negativenews.domain.models;

import java.util.List;
import verigate.adapter.negativenews.domain.constants.DomainConstants;

/**
 * Represents a request for negative news screening of a subject.
 */
public record NegativeNewsScreeningRequest(
    String subjectName,
    String idNumber,
    List<String> additionalKeywords,
    int dateRangeMonths
) {

  /**
   * Builder for convenient request construction.
   */
  public static class Builder {
    private String subjectName;
    private String idNumber;
    private List<String> additionalKeywords = List.of();
    private int dateRangeMonths = DomainConstants.DEFAULT_DATE_RANGE_MONTHS;

    public Builder subjectName(String subjectName) {
      this.subjectName = subjectName;
      return this;
    }

    public Builder idNumber(String idNumber) {
      this.idNumber = idNumber;
      return this;
    }

    public Builder additionalKeywords(List<String> additionalKeywords) {
      this.additionalKeywords = additionalKeywords != null ? additionalKeywords : List.of();
      return this;
    }

    public Builder dateRangeMonths(int dateRangeMonths) {
      this.dateRangeMonths = dateRangeMonths;
      return this;
    }

    /**
     * Builds the immutable negative news screening request instance.
     */
    public NegativeNewsScreeningRequest build() {
      return new NegativeNewsScreeningRequest(
          subjectName, idNumber, additionalKeywords, dateRangeMonths);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
