/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.fraudwatchlist.domain.models;

/**
 * Represents a request to perform a fraud watchlist check.
 */
public record FraudCheckRequest(
    String idNumber,
    String firstName,
    String lastName,
    String dateOfBirth,
    String phoneNumber
) {

  /**
   * Builder for convenient request construction.
   */
  public static class Builder {
    private String idNumber;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String phoneNumber;

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

    public Builder phoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
      return this;
    }

    /**
     * Builds the immutable fraud check request instance.
     */
    public FraudCheckRequest build() {
      return new FraudCheckRequest(idNumber, firstName, lastName, dateOfBirth, phoneNumber);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
