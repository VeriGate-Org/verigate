/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.employment.domain.models;

/**
 * Represents a request to verify employment details.
 */
public record EmploymentVerificationRequest(
    String idNumber,
    String employerName,
    String employeeNumber,
    String startDate
) {

  /**
   * Builder for convenient request construction.
   */
  public static class Builder {
    private String idNumber;
    private String employerName;
    private String employeeNumber;
    private String startDate;

    public Builder idNumber(String idNumber) {
      this.idNumber = idNumber;
      return this;
    }

    public Builder employerName(String employerName) {
      this.employerName = employerName;
      return this;
    }

    public Builder employeeNumber(String employeeNumber) {
      this.employeeNumber = employeeNumber;
      return this;
    }

    public Builder startDate(String startDate) {
      this.startDate = startDate;
      return this;
    }

    /**
     * Builds the immutable employment verification request instance.
     */
    public EmploymentVerificationRequest build() {
      return new EmploymentVerificationRequest(idNumber, employerName, employeeNumber, startDate);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
