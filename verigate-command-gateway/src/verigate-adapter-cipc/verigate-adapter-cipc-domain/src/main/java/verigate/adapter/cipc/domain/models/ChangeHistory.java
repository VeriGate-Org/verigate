/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.domain.models;

import java.time.LocalDate;

/**
 * Represents a company change history entry in CIPC.
 */
public record ChangeHistory(
    LocalDate changeDate,
    String changeDescription
) {

  /**
   * Builder for convenient change history construction.
   */
  public static class Builder {
    private LocalDate changeDate;
    private String changeDescription;

    public Builder changeDate(LocalDate changeDate) {
      this.changeDate = changeDate;
      return this;
    }

    public Builder changeDescription(String changeDescription) {
      this.changeDescription = changeDescription;
      return this;
    }

    /**
     * Builds the immutable change history instance.
     */
    public ChangeHistory build() {
      return new ChangeHistory(changeDate, changeDescription);
    }
  }

  /**
   * Creates a new change history builder.
   */
  public static Builder builder() {
    return new Builder();
  }
}
