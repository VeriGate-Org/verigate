/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.domain.models;

import java.time.LocalDate;

/**
 * Represents a company auditor in CIPC.
 */
public record Auditor(
    String auditorName,
    String auditorTypeDescription,
    String auditorStatusDescription,
    String professionDescription,
    LocalDate activityStartDate,
    LocalDate activityEndDate
) {

  /**
   * Builder for convenient auditor construction.
   */
  public static class Builder {
    private String auditorName;
    private String auditorTypeDescription;
    private String auditorStatusDescription;
    private String professionDescription;
    private LocalDate activityStartDate;
    private LocalDate activityEndDate;

    public Builder auditorName(String auditorName) {
      this.auditorName = auditorName;
      return this;
    }

    public Builder auditorTypeDescription(String auditorTypeDescription) {
      this.auditorTypeDescription = auditorTypeDescription;
      return this;
    }

    public Builder auditorStatusDescription(String auditorStatusDescription) {
      this.auditorStatusDescription = auditorStatusDescription;
      return this;
    }

    public Builder professionDescription(String professionDescription) {
      this.professionDescription = professionDescription;
      return this;
    }

    public Builder activityStartDate(LocalDate activityStartDate) {
      this.activityStartDate = activityStartDate;
      return this;
    }

    public Builder activityEndDate(LocalDate activityEndDate) {
      this.activityEndDate = activityEndDate;
      return this;
    }

    /**
     * Builds the immutable auditor instance.
     */
    public Auditor build() {
      return new Auditor(
          auditorName,
          auditorTypeDescription,
          auditorStatusDescription,
          professionDescription,
          activityStartDate,
          activityEndDate);
    }
  }

  /**
   * Creates a new auditor builder.
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Checks if the auditor is currently active.
   */
  public boolean isActive() {
    return activityEndDate == null || activityEndDate.isAfter(LocalDate.now());
  }
}
