/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.domain.models;

/**
 * Person details returned by the DeedsWeb registry. Mirrors the
 * {@code personDetailsResponse} SOAP type minus the embedded error.
 */
public final class PersonDetails {

  private final String fullName;
  private final String formerName;
  private final String idNumber;
  private final String maritalStatus;
  private final String personType;
  private final String personTypeCode;

  private PersonDetails(Builder builder) {
    this.fullName = builder.fullName;
    this.formerName = builder.formerName;
    this.idNumber = builder.idNumber;
    this.maritalStatus = builder.maritalStatus;
    this.personType = builder.personType;
    this.personTypeCode = builder.personTypeCode;
  }

  public String getFullName() {
    return fullName;
  }

  public String getFormerName() {
    return formerName;
  }

  public String getIdNumber() {
    return idNumber;
  }

  public String getMaritalStatus() {
    return maritalStatus;
  }

  public String getPersonType() {
    return personType;
  }

  public String getPersonTypeCode() {
    return personTypeCode;
  }

  public static Builder builder() {
    return new Builder();
  }

  /** Builder for PersonDetails. */
  public static final class Builder {
    private String fullName;
    private String formerName;
    private String idNumber;
    private String maritalStatus;
    private String personType;
    private String personTypeCode;

    public Builder fullName(String fullName) {
      this.fullName = fullName;
      return this;
    }

    public Builder formerName(String formerName) {
      this.formerName = formerName;
      return this;
    }

    public Builder idNumber(String idNumber) {
      this.idNumber = idNumber;
      return this;
    }

    public Builder maritalStatus(String maritalStatus) {
      this.maritalStatus = maritalStatus;
      return this;
    }

    public Builder personType(String personType) {
      this.personType = personType;
      return this;
    }

    public Builder personTypeCode(String personTypeCode) {
      this.personTypeCode = personTypeCode;
      return this;
    }

    public PersonDetails build() {
      return new PersonDetails(this);
    }
  }
}
