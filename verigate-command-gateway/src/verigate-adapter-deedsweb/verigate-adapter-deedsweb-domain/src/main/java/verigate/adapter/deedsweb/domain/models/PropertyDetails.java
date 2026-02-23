/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.domain.models;

import java.time.LocalDate;

/**
 * Represents property details extracted from DeedsWeb deed records.
 * Contains registration, ownership, and mortgage information for a property.
 */
public class PropertyDetails {

  private final String deedNumber;
  private final String titleDeedReference;
  private final String propertyDescription;
  private final String registrationDivision;
  private final String province;
  private final String extent;
  private final String registeredOwnerName;
  private final String registeredOwnerIdNumber;
  private final LocalDate registrationDate;
  private final LocalDate transferDate;
  private final Double purchasePrice;
  private final String bondHolder;
  private final Double bondAmount;

  private PropertyDetails(Builder builder) {
    this.deedNumber = builder.deedNumber;
    this.titleDeedReference = builder.titleDeedReference;
    this.propertyDescription = builder.propertyDescription;
    this.registrationDivision = builder.registrationDivision;
    this.province = builder.province;
    this.extent = builder.extent;
    this.registeredOwnerName = builder.registeredOwnerName;
    this.registeredOwnerIdNumber = builder.registeredOwnerIdNumber;
    this.registrationDate = builder.registrationDate;
    this.transferDate = builder.transferDate;
    this.purchasePrice = builder.purchasePrice;
    this.bondHolder = builder.bondHolder;
    this.bondAmount = builder.bondAmount;
  }

  // Getters
  public String getDeedNumber() {
    return deedNumber;
  }

  public String getTitleDeedReference() {
    return titleDeedReference;
  }

  public String getPropertyDescription() {
    return propertyDescription;
  }

  public String getRegistrationDivision() {
    return registrationDivision;
  }

  public String getProvince() {
    return province;
  }

  public String getExtent() {
    return extent;
  }

  public String getRegisteredOwnerName() {
    return registeredOwnerName;
  }

  public String getRegisteredOwnerIdNumber() {
    return registeredOwnerIdNumber;
  }

  public LocalDate getRegistrationDate() {
    return registrationDate;
  }

  public LocalDate getTransferDate() {
    return transferDate;
  }

  public Double getPurchasePrice() {
    return purchasePrice;
  }

  public String getBondHolder() {
    return bondHolder;
  }

  public Double getBondAmount() {
    return bondAmount;
  }

  /**
   * Checks whether this property has a mortgage (bond) registered against it.
   *
   * @return true if a bond holder and bond amount are present
   */
  public boolean hasMortgage() {
    return bondHolder != null && !bondHolder.trim().isEmpty()
        && bondAmount != null && bondAmount > 0;
  }

  /**
   * Checks whether this property is registered to the person with the given ID number.
   *
   * @param idNumber the ID number to check against the registered owner
   * @return true if the ID number matches the registered owner's ID number
   */
  public boolean isRegisteredTo(String idNumber) {
    if (idNumber == null || registeredOwnerIdNumber == null) {
      return false;
    }
    return registeredOwnerIdNumber.trim().equalsIgnoreCase(idNumber.trim());
  }

  /**
   * Builder for PropertyDetails.
   */
  public static class Builder {
    private String deedNumber;
    private String titleDeedReference;
    private String propertyDescription;
    private String registrationDivision;
    private String province;
    private String extent;
    private String registeredOwnerName;
    private String registeredOwnerIdNumber;
    private LocalDate registrationDate;
    private LocalDate transferDate;
    private Double purchasePrice;
    private String bondHolder;
    private Double bondAmount;

    public Builder deedNumber(String deedNumber) {
      this.deedNumber = deedNumber;
      return this;
    }

    public Builder titleDeedReference(String titleDeedReference) {
      this.titleDeedReference = titleDeedReference;
      return this;
    }

    public Builder propertyDescription(String propertyDescription) {
      this.propertyDescription = propertyDescription;
      return this;
    }

    public Builder registrationDivision(String registrationDivision) {
      this.registrationDivision = registrationDivision;
      return this;
    }

    public Builder province(String province) {
      this.province = province;
      return this;
    }

    public Builder extent(String extent) {
      this.extent = extent;
      return this;
    }

    public Builder registeredOwnerName(String registeredOwnerName) {
      this.registeredOwnerName = registeredOwnerName;
      return this;
    }

    public Builder registeredOwnerIdNumber(String registeredOwnerIdNumber) {
      this.registeredOwnerIdNumber = registeredOwnerIdNumber;
      return this;
    }

    public Builder registrationDate(LocalDate registrationDate) {
      this.registrationDate = registrationDate;
      return this;
    }

    public Builder transferDate(LocalDate transferDate) {
      this.transferDate = transferDate;
      return this;
    }

    public Builder purchasePrice(Double purchasePrice) {
      this.purchasePrice = purchasePrice;
      return this;
    }

    public Builder bondHolder(String bondHolder) {
      this.bondHolder = bondHolder;
      return this;
    }

    public Builder bondAmount(Double bondAmount) {
      this.bondAmount = bondAmount;
      return this;
    }

    public PropertyDetails build() {
      return new PropertyDetails(this);
    }
  }
}
