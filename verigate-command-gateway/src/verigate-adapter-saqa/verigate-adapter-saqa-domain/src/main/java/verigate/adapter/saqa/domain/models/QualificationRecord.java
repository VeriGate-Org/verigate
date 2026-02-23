/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.saqa.domain.models;

import java.time.LocalDate;

/**
 * Represents a single qualification record from the SAQA registry.
 */
public record QualificationRecord(
    String qualificationTitle,
    QualificationType qualificationType,
    String institution,
    int nqfLevel,
    LocalDate dateConferred,
    String saqaId,
    String status
) {

  /**
   * Builder for convenient record construction.
   */
  public static class Builder {
    private String qualificationTitle;
    private QualificationType qualificationType;
    private String institution;
    private int nqfLevel;
    private LocalDate dateConferred;
    private String saqaId;
    private String status;

    public Builder qualificationTitle(String qualificationTitle) {
      this.qualificationTitle = qualificationTitle;
      return this;
    }

    public Builder qualificationType(QualificationType qualificationType) {
      this.qualificationType = qualificationType;
      return this;
    }

    public Builder institution(String institution) {
      this.institution = institution;
      return this;
    }

    public Builder nqfLevel(int nqfLevel) {
      this.nqfLevel = nqfLevel;
      return this;
    }

    public Builder dateConferred(LocalDate dateConferred) {
      this.dateConferred = dateConferred;
      return this;
    }

    public Builder saqaId(String saqaId) {
      this.saqaId = saqaId;
      return this;
    }

    public Builder status(String status) {
      this.status = status;
      return this;
    }

    /**
     * Builds the immutable qualification record instance.
     */
    public QualificationRecord build() {
      return new QualificationRecord(
          qualificationTitle, qualificationType, institution,
          nqfLevel, dateConferred, saqaId, status);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
