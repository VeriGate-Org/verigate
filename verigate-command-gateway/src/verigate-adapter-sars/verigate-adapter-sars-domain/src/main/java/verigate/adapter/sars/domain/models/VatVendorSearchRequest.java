/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.sars.domain.models;

/**
 * Request for a SARS VAT Vendor Search.
 *
 * @param vatNumber   the 10-digit VAT registration number (required)
 * @param description optional vendor description filter
 */
public record VatVendorSearchRequest(String vatNumber, String description) {

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private String vatNumber;
    private String description;

    public Builder vatNumber(String vatNumber) {
      this.vatNumber = vatNumber;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public VatVendorSearchRequest build() {
      return new VatVendorSearchRequest(vatNumber, description);
    }
  }
}
