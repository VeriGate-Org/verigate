/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.saqa.domain.models;

/**
 * Represents a request to verify a qualification through the SAQA registry.
 */
public record QualificationVerificationRequest(
    String idNumber,
    String firstName,
    String lastName,
    String qualificationTitle,
    String institution,
    int yearCompleted
) {

  /**
   * Builder for convenient request construction.
   */
  public static class Builder {
    private String idNumber;
    private String firstName;
    private String lastName;
    private String qualificationTitle;
    private String institution;
    private int yearCompleted;

    public Builder idNumber(String idNumber) {
      this.idNumber = idNumber;
      return this;
    }

    public Builder firstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    public Builder lastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    public Builder qualificationTitle(String qualificationTitle) {
      this.qualificationTitle = qualificationTitle;
      return this;
    }

    public Builder institution(String institution) {
      this.institution = institution;
      return this;
    }

    public Builder yearCompleted(int yearCompleted) {
      this.yearCompleted = yearCompleted;
      return this;
    }

    /**
     * Builds the immutable qualification verification request instance.
     */
    public QualificationVerificationRequest build() {
      return new QualificationVerificationRequest(
          idNumber, firstName, lastName, qualificationTitle, institution, yearCompleted);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
