/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.domain.models;

import java.time.LocalDate;

/**
 * Represents a company director in CIPC.
 */
public record Director(
    String firstNames,
    String surname,
    String initials,
    String identityNumber,
    LocalDate dateOfBirth,
    DirectorStatus directorStatus,
    DirectorType directorType,
    String designation,
    LocalDate appointmentDate,
    LocalDate resignationDate,
    Integer memberSizeInterest,
    Integer memberContribution,
    Address residentialAddress,
    String countryCode
) {

  /**
   * Builder for convenient director construction.
   */
  public static class Builder {
    private String firstNames;
    private String surname;
    private String initials;
    private String identityNumber;
    private LocalDate dateOfBirth;
    private DirectorStatus directorStatus;
    private DirectorType directorType;
    private String designation;
    private LocalDate appointmentDate;
    private LocalDate resignationDate;
    private Integer memberSizeInterest;
    private Integer memberContribution;
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

    public Builder identityNumber(String identityNumber) {
      this.identityNumber = identityNumber;
      return this;
    }

    public Builder dateOfBirth(LocalDate dateOfBirth) {
      this.dateOfBirth = dateOfBirth;
      return this;
    }

    public Builder directorStatus(DirectorStatus directorStatus) {
      this.directorStatus = directorStatus;
      return this;
    }

    public Builder directorType(DirectorType directorType) {
      this.directorType = directorType;
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

    public Builder memberSizeInterest(Integer memberSizeInterest) {
      this.memberSizeInterest = memberSizeInterest;
      return this;
    }

    public Builder memberContribution(Integer memberContribution) {
      this.memberContribution = memberContribution;
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
     * Builds the immutable director instance.
     */
    public Director build() {
      return new Director(
          firstNames,
          surname,
          initials,
          identityNumber,
          dateOfBirth,
          directorStatus,
          directorType,
          designation,
          appointmentDate,
          resignationDate,
          memberSizeInterest,
          memberContribution,
          residentialAddress,
          countryCode);
    }
  }

  /**
   * Creates a new director builder.
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Returns the full name of the director.
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

  /**
   * Checks if the director is currently active.
   */
  public boolean isActive() {
    return directorStatus == DirectorStatus.ACTIVE;
  }

  /**
   * Checks if the director has resigned.
   */
  public boolean hasResigned() {
    return directorStatus == DirectorStatus.RESIGNED && resignationDate != null;
  }
}
