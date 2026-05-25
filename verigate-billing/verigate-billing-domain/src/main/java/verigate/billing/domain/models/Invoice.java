/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.models;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import verigate.billing.domain.enums.InvoiceStatus;

/**
 * Represents a billing invoice for a partner.
 *
 * @param invoiceId              unique identifier (UUID-based)
 * @param invoiceNumber          sequential display number (INV-YYYYMM-NNNN)
 * @param partnerId              the billed partner
 * @param partnerName            display name of the partner
 * @param billingPeriod          the month this invoice covers
 * @param status                 current invoice status
 * @param issueDate              date the invoice was issued
 * @param dueDate                payment due date
 * @param lineItems              itemised line items
 * @param subtotal               sum of line subtotals (ex-VAT)
 * @param vatRate                applicable VAT rate (e.g. 0.15)
 * @param vatAmount              total VAT amount
 * @param total                  subtotal + vatAmount
 * @param monthlyMinimumApplied  whether the monthly minimum was applied
 * @param currency               currency code (e.g. ZAR)
 * @param pdfS3Key               S3 object key for the generated PDF
 * @param paymentId              linked payment identifier
 * @param createdAt              creation timestamp
 * @param updatedAt              last update timestamp
 * @param notes                  optional notes
 */
public record Invoice(
    String invoiceId,
    String invoiceNumber,
    String partnerId,
    String partnerName,
    YearMonth billingPeriod,
    InvoiceStatus status,
    LocalDate issueDate,
    LocalDate dueDate,
    List<InvoiceLineItem> lineItems,
    BigDecimal subtotal,
    BigDecimal vatRate,
    BigDecimal vatAmount,
    BigDecimal total,
    boolean monthlyMinimumApplied,
    String currency,
    String pdfS3Key,
    String paymentId,
    Instant createdAt,
    Instant updatedAt,
    String notes
) {
    public Invoice {
        if (invoiceId == null || invoiceId.isBlank()) {
            throw new IllegalArgumentException("invoiceId must not be null or blank");
        }
        if (partnerId == null || partnerId.isBlank()) {
            throw new IllegalArgumentException("partnerId must not be null or blank");
        }
        if (billingPeriod == null) {
            throw new IllegalArgumentException("billingPeriod must not be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("subtotal must not be null or negative");
        }
        if (vatRate == null || vatRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("vatRate must not be null or negative");
        }
        if (vatAmount == null || vatAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("vatAmount must not be null or negative");
        }
        if (total == null || total.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("total must not be null or negative");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("currency must not be null or blank");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("createdAt must not be null");
        }
        lineItems = lineItems != null ? List.copyOf(lineItems) : List.of();
    }
}
