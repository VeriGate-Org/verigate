/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.domain.models;

/**
 * Represents an address in CIPC (both office and postal addresses).
 */
public record Address(
    String addressLine1,
    String addressLine2,
    String addressLine3,
    String addressLine4,
    String city,
    String region,
    String postalCode,
    String country
) {

  /**
   * Builder for convenient address construction.
   */
  public static class Builder {
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String addressLine4;
    private String city;
    private String region;
    private String postalCode;
    private String country;

    public Builder addressLine1(String addressLine1) {
      this.addressLine1 = addressLine1;
      return this;
    }

    public Builder addressLine2(String addressLine2) {
      this.addressLine2 = addressLine2;
      return this;
    }

    public Builder addressLine3(String addressLine3) {
      this.addressLine3 = addressLine3;
      return this;
    }

    public Builder addressLine4(String addressLine4) {
      this.addressLine4 = addressLine4;
      return this;
    }

    public Builder city(String city) {
      this.city = city;
      return this;
    }

    public Builder region(String region) {
      this.region = region;
      return this;
    }

    public Builder postalCode(String postalCode) {
      this.postalCode = postalCode;
      return this;
    }

    public Builder country(String country) {
      this.country = country;
      return this;
    }

    /**
     * Builds the immutable address instance.
     */
    public Address build() {
      return new Address(
          addressLine1,
          addressLine2,
          addressLine3,
          addressLine4,
          city,
          region,
          postalCode,
          country);
    }
  }

  /**
   * Creates a new address builder.
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Returns a formatted single-line address string.
   */
  public String toFormattedString() {
    StringBuilder sb = new StringBuilder();

    appendPart(sb, addressLine1, false);
    appendPart(sb, addressLine2, false);
    appendPart(sb, addressLine3, false);
    appendPart(sb, addressLine4, false);
    appendPart(sb, city, false);
    appendPart(sb, region, false);
    appendPart(sb, postalCode, true);
    appendPart(sb, country, false);

    return sb.toString();
  }

  private static void appendPart(StringBuilder sb, String value, boolean prependSpaceOnly) {
    if (value == null || value.trim().isEmpty()) {
      return;
    }

    if (sb.length() > 0) {
      sb.append(prependSpaceOnly ? " " : ", ");
    }

    sb.append(value.trim());
  }
}
