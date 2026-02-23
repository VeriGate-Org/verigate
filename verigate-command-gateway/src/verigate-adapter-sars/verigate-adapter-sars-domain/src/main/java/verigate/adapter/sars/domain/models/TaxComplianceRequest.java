/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.domain.models;

import verigate.adapter.sars.domain.enums.TaxClearanceType;

/**
 * Represents a request to verify tax compliance status with SARS.
 *
 * @param idNumber the South African identity number of the taxpayer
 * @param taxReferenceNumber the SARS tax reference number
 * @param companyRegistrationNumber the CIPC company registration number (for entities)
 * @param clearanceType the type of tax clearance certificate requested
 */
public record TaxComplianceRequest(
    String idNumber,
    String taxReferenceNumber,
    String companyRegistrationNumber,
    TaxClearanceType clearanceType
) {

  /**
   * Builder for convenient request construction.
   */
  public static class Builder {
    private String idNumber;
    private String taxReferenceNumber;
    private String companyRegistrationNumber;
    private TaxClearanceType clearanceType;

    public Builder idNumber(String idNumber) {
      this.idNumber = idNumber;
      return this;
    }

    public Builder taxReferenceNumber(String taxReferenceNumber) {
      this.taxReferenceNumber = taxReferenceNumber;
      return this;
    }

    public Builder companyRegistrationNumber(String companyRegistrationNumber) {
      this.companyRegistrationNumber = companyRegistrationNumber;
      return this;
    }

    public Builder clearanceType(TaxClearanceType clearanceType) {
      this.clearanceType = clearanceType;
      return this;
    }

    /**
     * Builds the immutable tax compliance request instance.
     */
    public TaxComplianceRequest build() {
      return new TaxComplianceRequest(
          idNumber, taxReferenceNumber, companyRegistrationNumber, clearanceType);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
