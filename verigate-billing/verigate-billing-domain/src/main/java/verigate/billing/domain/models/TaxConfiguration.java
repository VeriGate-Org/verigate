/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.models;

import java.math.BigDecimal;

/**
 * Tax configuration for invoice generation.
 *
 * @param vatRate                    the VAT rate (e.g. 0.15 for 15%)
 * @param sellerVatNumber            seller's VAT registration number
 * @param sellerCompanyName          seller's registered company name
 * @param sellerRegistrationNumber   seller's company registration number
 * @param sellerAddress              seller's registered address
 * @param vatRegistered              whether the seller is VAT-registered
 */
public record TaxConfiguration(
    BigDecimal vatRate,
    String sellerVatNumber,
    String sellerCompanyName,
    String sellerRegistrationNumber,
    String sellerAddress,
    boolean vatRegistered
) {
    public TaxConfiguration {
        if (vatRate == null || vatRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("vatRate must not be null or negative");
        }
        if (sellerCompanyName == null || sellerCompanyName.isBlank()) {
            throw new IllegalArgumentException("sellerCompanyName must not be null or blank");
        }
    }
}
