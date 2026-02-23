/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.domain.models;

import java.time.LocalDate;

/**
 * Represents a company secretary in CIPC.
 */
public record Secretary(
    String firstNames,
    String surname,
    String initials,
    LocalDate dateOfBirth,
    String status,
    String type,
    String designation,
    LocalDate appointmentDate,
    LocalDate resignationDate,
    Address residentialAddress,
    String countryCode
) {

  /**
   * Builder for convenient secretary construction.
   */
  public static class Builder {
    private String firstNames;
    private String surname;
    private String initials;
    private LocalDate dateOfBirth;
    private String status;
    private String type;
    private String designation;
    private LocalDate appointmentDate;
    private LocalDate resignationDate;
    private Address residentialAddress;
    private String countryCode;

    public Builder firstNames(String firstNames) {
      this.firstNames = firstNames;
      return this;
    }

    public Builder surname(String surname) {
      this.surname = surname;
      return this;
    }

    public Builder initials(String initials) {
      this.initials = initials;
      return this;
    }

    public Builder dateOfBirth(LocalDate dateOfBirth) {
      this.dateOfBirth = dateOfBirth;
      return this;
    }

    public Builder status(String status) {
      this.status = status;
      return this;
    }

    public Builder type(String type) {
      this.type = type;
      return this;
    }

    public Builder designation(String designation) {
      this.designation = designation;
      return this;
    }

    public Builder appointmentDate(LocalDate appointmentDate) {
      this.appointmentDate = appointmentDate;
      return this;
    }

    public Builder resignationDate(LocalDate resignationDate) {
      this.resignationDate = resignationDate;
      return this;
    }

    public Builder residentialAddress(Address residentialAddress) {
      this.residentialAddress = residentialAddress;
      return this;
    }

    public Builder countryCode(String countryCode) {
      this.countryCode = countryCode;
      return this;
    }

    /**
     * Builds the immutable secretary instance.
     */
    public Secretary build() {
      return new Secretary(
          firstNames,
          surname,
          initials,
          dateOfBirth,
          status,
          type,
          designation,
          appointmentDate,
          resignationDate,
          residentialAddress,
          countryCode);
    }
  }

  /**
   * Creates a new secretary builder.
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Returns the full name of the secretary.
   */
  public String getFullName() {
    StringBuilder sb = new StringBuilder();

    if (firstNames != null && !firstNames.trim().isEmpty()) {
      sb.append(firstNames.trim());
    }
    if (surname != null && !surname.trim().isEmpty()) {
      if (sb.length() > 0) {
        sb.append(" ");
      }
      sb.append(surname.trim());
    }

    return sb.toString();
  }
}
