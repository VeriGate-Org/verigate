/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.domain.models;

/**
 * Represents a request to verify an individual's identity with the DHA.
 */
public record IdentityVerificationRequest(
    String idNumber,      // South African ID number (13 digits: YYMMDDSSSSCZZ)
    String firstName,
    String lastName,
    String dateOfBirth,   // Date of birth (yyyy-MM-dd)
    String gender
) {

  /**
   * Builder for convenient request construction.
   */
  public static class Builder {
    private String idNumber;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String gender;

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

    public Builder dateOfBirth(String dateOfBirth) {
      this.dateOfBirth = dateOfBirth;
      return this;
    }

    public Builder gender(String gender) {
      this.gender = gender;
      return this;
    }

    /**
     * Builds the immutable identity verification request instance.
     */
    public IdentityVerificationRequest build() {
      return new IdentityVerificationRequest(idNumber, firstName, lastName, dateOfBirth, gender);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
