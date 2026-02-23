/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.creditbureau.domain.models;

/**
 * Represents a request to perform a credit check.
 */
public record CreditCheckRequest(
    String idNumber,
    String firstName,
    String lastName,
    String dateOfBirth,
    String consentReference
) {

  /**
   * Builder for convenient request construction.
   */
  public static class Builder {
    private String idNumber;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String consentReference;

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

    public Builder consentReference(String consentReference) {
      this.consentReference = consentReference;
      return this;
    }

    /**
     * Builds the immutable credit check request instance.
     */
    public CreditCheckRequest build() {
      return new CreditCheckRequest(idNumber, firstName, lastName, dateOfBirth, consentReference);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
