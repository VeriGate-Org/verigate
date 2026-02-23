/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.domain.models;

/**
 * Represents a request to retrieve company profile information from CIPC.
 */
public record CompanyProfileRequest(
    String enterpriseNumber // CIPC enterprise number (e.g., "2020/939681/07")
) {

  /**
   * Builder for convenient request construction.
   */
  public static class Builder {
    private String enterpriseNumber;

    public Builder enterpriseNumber(String enterpriseNumber) {
      this.enterpriseNumber = enterpriseNumber;
      return this;
    }

    /**
     * Builds the immutable company profile request instance.
     */
    public CompanyProfileRequest build() {
      return new CompanyProfileRequest(enterpriseNumber);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
