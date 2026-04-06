/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.domain.models;

/**
 * Details of a VAT vendor as returned by the SARS VAT Vendor Search service.
 *
 * @param vatNumber        the VAT registration number
 * @param vendorName       the registered vendor name
 * @param tradingName      the trading name (may differ from vendor name)
 * @param registrationDate the VAT registration date
 * @param vendorStatus     the current vendor status description
 * @param activityCode     the economic activity code
 * @param physicalAddress  the registered physical address
 */
public record VatVendorDetails(
    String vatNumber,
    String vendorName,
    String tradingName,
    String registrationDate,
    String vendorStatus,
    String activityCode,
    String physicalAddress) {

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private String vatNumber;
    private String vendorName;
    private String tradingName;
    private String registrationDate;
    private String vendorStatus;
    private String activityCode;
    private String physicalAddress;

    public Builder vatNumber(String vatNumber) {
      this.vatNumber = vatNumber;
      return this;
    }

    public Builder vendorName(String vendorName) {
      this.vendorName = vendorName;
      return this;
    }

    public Builder tradingName(String tradingName) {
      this.tradingName = tradingName;
      return this;
    }

    public Builder registrationDate(String registrationDate) {
      this.registrationDate = registrationDate;
      return this;
    }

    public Builder vendorStatus(String vendorStatus) {
      this.vendorStatus = vendorStatus;
      return this;
    }

    public Builder activityCode(String activityCode) {
      this.activityCode = activityCode;
      return this;
    }

    public Builder physicalAddress(String physicalAddress) {
      this.physicalAddress = physicalAddress;
      return this;
    }

    public VatVendorDetails build() {
      return new VatVendorDetails(
          vatNumber, vendorName, tradingName, registrationDate,
          vendorStatus, activityCode, physicalAddress);
    }
  }
}
