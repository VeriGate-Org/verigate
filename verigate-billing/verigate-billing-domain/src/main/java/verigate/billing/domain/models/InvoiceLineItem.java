/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.models;

import java.math.BigDecimal;

/**
 * A single line item on an invoice, representing usage of a verification type.
 *
 * @param lineItemId        unique identifier for the line item
 * @param verificationType  the type of verification
 * @param description       human-readable description
 * @param quantity          number of verifications
 * @param unitPriceExVat    unit price excluding VAT
 * @param lineSubtotal      quantity * unitPriceExVat
 * @param vatAmount         VAT amount for this line
 * @param lineTotal         lineSubtotal + vatAmount
 */
public record InvoiceLineItem(
    String lineItemId,
    String verificationType,
    String description,
    long quantity,
    BigDecimal unitPriceExVat,
    BigDecimal lineSubtotal,
    BigDecimal vatAmount,
    BigDecimal lineTotal
) {
    public InvoiceLineItem {
        if (lineItemId == null || lineItemId.isBlank()) {
            throw new IllegalArgumentException("lineItemId must not be null or blank");
        }
        if (verificationType == null || verificationType.isBlank()) {
            throw new IllegalArgumentException("verificationType must not be null or blank");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description must not be null or blank");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("quantity must not be negative");
        }
        if (unitPriceExVat == null || unitPriceExVat.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("unitPriceExVat must not be null or negative");
        }
        if (lineSubtotal == null || lineSubtotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("lineSubtotal must not be null or negative");
        }
        if (vatAmount == null || vatAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("vatAmount must not be null or negative");
        }
        if (lineTotal == null || lineTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("lineTotal must not be null or negative");
        }
    }
}
