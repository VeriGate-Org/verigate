/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.application.services;

import com.google.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.billing.domain.constants.DomainConstants;
import verigate.billing.domain.enums.InvoiceStatus;
import verigate.billing.domain.models.BillingPlan;
import verigate.billing.domain.models.Invoice;
import verigate.billing.domain.models.InvoiceLineItem;
import verigate.billing.domain.models.UsageSummary;
import verigate.billing.domain.services.BillingService;
import verigate.billing.domain.services.InvoiceService;
import verigate.billing.domain.services.LedgerService;

/**
 * Default implementation of {@link InvoiceService}.
 * Generates invoices from usage/cost data, creates PDFs, and manages invoice lifecycle.
 */
public class DefaultInvoiceService implements InvoiceService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultInvoiceService.class);

    private final BillingService billingService;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceNumberGenerator invoiceNumberGenerator;
    private final InvoicePdfGenerator invoicePdfGenerator;
    private final LedgerService ledgerService;

    @Inject
    public DefaultInvoiceService(
        BillingService billingService,
        InvoiceRepository invoiceRepository,
        InvoiceNumberGenerator invoiceNumberGenerator,
        InvoicePdfGenerator invoicePdfGenerator,
        LedgerService ledgerService) {
        this.billingService = billingService;
        this.invoiceRepository = invoiceRepository;
        this.invoiceNumberGenerator = invoiceNumberGenerator;
        this.invoicePdfGenerator = invoicePdfGenerator;
        this.ledgerService = ledgerService;
    }

    @Override
    public Invoice generateInvoice(String partnerId, YearMonth period) {
        LOG.info("Generating invoice for partnerId={}, period={}", partnerId, period);

        List<UsageSummary> billedSummaries = billingService.calculateBilling(partnerId, period);
        Optional<BillingPlan> planOpt = billingService.getBillingPlan(partnerId);
        BigDecimal monthlyMinimum = planOpt
            .map(BillingPlan::monthlyMinimum)
            .orElse(DomainConstants.DEFAULT_MONTHLY_MINIMUM);

        BigDecimal vatRate = DomainConstants.VAT_RATE;
        List<InvoiceLineItem> lineItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (UsageSummary summary : billedSummaries) {
            BigDecimal unitPrice = summary.totalCost()
                .divide(BigDecimal.valueOf(Math.max(summary.totalCount(), 1)), 4, RoundingMode.HALF_UP);
            BigDecimal lineSubtotal = summary.totalCost();
            BigDecimal lineVat = lineSubtotal.multiply(vatRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal lineTotal = lineSubtotal.add(lineVat);

            String description = formatVerificationType(summary.verificationType())
                + " (" + summary.totalCount() + " verifications)";

            InvoiceLineItem lineItem = new InvoiceLineItem(
                UUID.randomUUID().toString(),
                summary.verificationType(),
                description,
                summary.totalCount(),
                unitPrice,
                lineSubtotal,
                lineVat,
                lineTotal
            );

            lineItems.add(lineItem);
            subtotal = subtotal.add(lineSubtotal);
        }

        boolean minimumApplied = false;
        if (subtotal.compareTo(monthlyMinimum) < 0) {
            BigDecimal adjustmentAmount = monthlyMinimum.subtract(subtotal);
            BigDecimal adjustmentVat = adjustmentAmount.multiply(vatRate).setScale(2, RoundingMode.HALF_UP);

            InvoiceLineItem minimumLineItem = new InvoiceLineItem(
                UUID.randomUUID().toString(),
                "MONTHLY_MINIMUM_ADJUSTMENT",
                "Monthly minimum adjustment",
                1,
                adjustmentAmount,
                adjustmentAmount,
                adjustmentVat,
                adjustmentAmount.add(adjustmentVat)
            );

            lineItems.add(minimumLineItem);
            subtotal = monthlyMinimum;
            minimumApplied = true;
        }

        BigDecimal invoiceVat = subtotal.multiply(vatRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(invoiceVat);

        String invoiceId = DomainConstants.INVOICE_ID_PREFIX + UUID.randomUUID();
        String invoiceNumber = invoiceNumberGenerator.generateNextNumber(period);
        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(DomainConstants.PAYMENT_TERMS_DAYS);
        Instant now = Instant.now();

        Invoice invoice = new Invoice(
            invoiceId,
            invoiceNumber,
            partnerId,
            resolvePartnerName(partnerId),
            period,
            InvoiceStatus.DRAFT,
            issueDate,
            dueDate,
            lineItems,
            subtotal,
            vatRate,
            invoiceVat,
            total,
            minimumApplied,
            DomainConstants.DEFAULT_CURRENCY,
            null,
            null,
            now,
            now,
            null
        );

        invoiceRepository.save(invoice);
        LOG.info("Invoice generated: invoiceId={}, invoiceNumber={}, total={}",
            invoiceId, invoiceNumber, total);

        return invoice;
    }

    @Override
    public Invoice issueInvoice(String invoiceId) {
        LOG.info("Issuing invoice: invoiceId={}", invoiceId);

        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + invoiceId));

        if (invoice.status() != InvoiceStatus.DRAFT) {
            throw new IllegalStateException("Can only issue DRAFT invoices, current status: " + invoice.status());
        }

        String pdfS3Key = invoicePdfGenerator.generatePdf(invoice);

        Invoice issuedInvoice = new Invoice(
            invoice.invoiceId(),
            invoice.invoiceNumber(),
            invoice.partnerId(),
            invoice.partnerName(),
            invoice.billingPeriod(),
            InvoiceStatus.ISSUED,
            invoice.issueDate(),
            invoice.dueDate(),
            invoice.lineItems(),
            invoice.subtotal(),
            invoice.vatRate(),
            invoice.vatAmount(),
            invoice.total(),
            invoice.monthlyMinimumApplied(),
            invoice.currency(),
            pdfS3Key,
            invoice.paymentId(),
            invoice.createdAt(),
            Instant.now(),
            invoice.notes()
        );

        invoiceRepository.save(issuedInvoice);

        try {
            ledgerService.recordInvoiceIssued(issuedInvoice);
        } catch (Exception e) {
            LOG.warn("Failed to record ledger entry for invoice: {}", invoiceId, e);
        }

        LOG.info("Invoice issued: invoiceId={}, pdfKey={}", invoiceId, pdfS3Key);
        return issuedInvoice;
    }

    @Override
    public Invoice updateStatus(String invoiceId, InvoiceStatus status) {
        LOG.info("Updating invoice status: invoiceId={}, newStatus={}", invoiceId, status);

        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + invoiceId));

        Invoice updated = new Invoice(
            invoice.invoiceId(),
            invoice.invoiceNumber(),
            invoice.partnerId(),
            invoice.partnerName(),
            invoice.billingPeriod(),
            status,
            invoice.issueDate(),
            invoice.dueDate(),
            invoice.lineItems(),
            invoice.subtotal(),
            invoice.vatRate(),
            invoice.vatAmount(),
            invoice.total(),
            invoice.monthlyMinimumApplied(),
            invoice.currency(),
            invoice.pdfS3Key(),
            invoice.paymentId(),
            invoice.createdAt(),
            Instant.now(),
            invoice.notes()
        );

        invoiceRepository.save(updated);
        return updated;
    }

    @Override
    public Optional<Invoice> getInvoice(String invoiceId) {
        return invoiceRepository.findById(invoiceId);
    }

    @Override
    public List<Invoice> getInvoicesForPartner(String partnerId) {
        return invoiceRepository.findByPartnerId(partnerId);
    }

    @Override
    public String generatePdf(String invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + invoiceId));
        return invoicePdfGenerator.generatePdf(invoice);
    }

    private String formatVerificationType(String type) {
        if (type == null) return "Unknown";
        return type.replace("_", " ")
            .toLowerCase()
            .substring(0, 1).toUpperCase()
            + type.replace("_", " ").toLowerCase().substring(1);
    }

    private String resolvePartnerName(String partnerId) {
        return partnerId;
    }

    /**
     * Repository interface for invoice persistence.
     */
    public interface InvoiceRepository {

        void save(Invoice invoice);

        Optional<Invoice> findById(String invoiceId);

        List<Invoice> findByPartnerId(String partnerId);

        Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    }

    /**
     * Generates sequential invoice numbers per period.
     */
    public interface InvoiceNumberGenerator {

        String generateNextNumber(YearMonth period);
    }

    /**
     * Generates PDF documents from invoices.
     */
    public interface InvoicePdfGenerator {

        String generatePdf(Invoice invoice);
    }
}
