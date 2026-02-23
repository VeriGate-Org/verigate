/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.cipc.domain.models;

import java.math.BigDecimal;

/**
 * Represents company capital information in CIPC.
 */
public record Capital(
    String capitalTypeDescription,
    Integer numberOfShares,
    BigDecimal parValue,
    BigDecimal shareAmount,
    BigDecimal premium
) {

  /**
   * Builder for convenient capital construction.
   */
  public static class Builder {
    private String capitalTypeDescription;
    private Integer numberOfShares;
    private BigDecimal parValue;
    private BigDecimal shareAmount;
    private BigDecimal premium;

    public Builder capitalTypeDescription(String capitalTypeDescription) {
      this.capitalTypeDescription = capitalTypeDescription;
      return this;
    }

    public Builder numberOfShares(Integer numberOfShares) {
      this.numberOfShares = numberOfShares;
      return this;
    }

    public Builder parValue(BigDecimal parValue) {
      this.parValue = parValue;
      return this;
    }

    public Builder shareAmount(BigDecimal shareAmount) {
      this.shareAmount = shareAmount;
      return this;
    }

    public Builder premium(BigDecimal premium) {
      this.premium = premium;
      return this;
    }

    /**
     * Builds the immutable capital instance.
     */
    public Capital build() {
      return new Capital(capitalTypeDescription, numberOfShares, parValue, shareAmount, premium);
    }
  }

  /**
   * Creates a new capital builder.
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Calculates the total capital value (share amount + premium).
   */
  public BigDecimal getTotalCapitalValue() {
    BigDecimal total = BigDecimal.ZERO;

    if (shareAmount != null) {
      total = total.add(shareAmount);
    }
    if (premium != null) {
      total = total.add(premium);
    }

    return total;
  }
}
