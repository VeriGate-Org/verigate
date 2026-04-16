/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.domain.models;

/**
 * Request value object describing a property search against the DeedsWeb registry.
 *
 * <p>{@code officeCode} is optional. When {@code null}, blank, or {@code "all"} the
 * adapter fans the search out across every office returned by
 * {@code getOfficeRegistryList()} and merges the results.</p>
 */
public final class PropertySearchRequest {

  private final String searchType;
  private final String query;
  private final String province;
  private final String officeCode;

  private PropertySearchRequest(Builder builder) {
    this.searchType = builder.searchType;
    this.query = builder.query;
    this.province = builder.province;
    this.officeCode = builder.officeCode;
  }

  public String getSearchType() {
    return searchType;
  }

  public String getQuery() {
    return query;
  }

  public String getProvince() {
    return province;
  }

  public String getOfficeCode() {
    return officeCode;
  }

  /**
   * Returns true if the request explicitly targets a single office. A {@code null}, blank,
   * or {@code "all"} office code indicates a fan-out across every office.
   */
  public boolean targetsSingleOffice() {
    return officeCode != null
        && !officeCode.isBlank()
        && !"all".equalsIgnoreCase(officeCode.trim());
  }

  public static Builder builder() {
    return new Builder();
  }

  /** Builder for PropertySearchRequest. */
  public static final class Builder {
    private String searchType;
    private String query;
    private String province;
    private String officeCode;

    public Builder searchType(String searchType) {
      this.searchType = searchType;
      return this;
    }

    public Builder query(String query) {
      this.query = query;
      return this;
    }

    public Builder province(String province) {
      this.province = province;
      return this;
    }

    public Builder officeCode(String officeCode) {
      this.officeCode = officeCode;
      return this;
    }

    public PropertySearchRequest build() {
      return new PropertySearchRequest(this);
    }
  }
}
