/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.pdf;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhtmlrenderer.pdf.ITextRenderer;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import verigate.billing.application.services.DefaultInvoiceService;
import verigate.billing.domain.constants.DomainConstants;
import verigate.billing.domain.models.Invoice;
import verigate.billing.domain.models.InvoiceLineItem;

/**
 * Generates PDF invoices from HTML templates using Flying Saucer (OpenPDF).
 * Uploads the generated PDF to S3 and returns the S3 key.
 */
public class HtmlInvoicePdfGenerator implements DefaultInvoiceService.InvoicePdfGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(HtmlInvoicePdfGenerator.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMMM yyyy");

    private final S3Client s3Client;
    private final String bucketName;

    @Inject
    public HtmlInvoicePdfGenerator(
        S3Client s3Client,
        @Named("invoiceBucketName") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public String generatePdf(Invoice invoice) {
        LOG.info("Generating PDF for invoice: {}", invoice.invoiceId());

        try {
            String html = buildHtml(invoice);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(baos);
            baos.close();

            byte[] pdfBytes = baos.toByteArray();
            String s3Key = DomainConstants.INVOICE_PDF_S3_PREFIX
                + invoice.billingPeriod() + "/"
                + invoice.invoiceNumber() + ".pdf";

            PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType("application/pdf")
                .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(pdfBytes));

            LOG.info("PDF uploaded to S3: bucket={}, key={}", bucketName, s3Key);
            return s3Key;

        } catch (Exception e) {
            LOG.error("Failed to generate PDF for invoice: {}", invoice.invoiceId(), e);
            throw new RuntimeException("PDF generation failed", e);
        }
    }

    private String buildHtml(Invoice invoice) {
        String template = loadTemplate();

        StringBuilder lineItemsHtml = new StringBuilder();
        for (InvoiceLineItem item : invoice.lineItems()) {
            lineItemsHtml.append("<tr>")
                .append("<td>").append(escapeHtml(item.description())).append("</td>")
                .append("<td style=\"text-align:right\">").append(item.quantity()).append("</td>")
                .append("<td style=\"text-align:right\">R ").append(item.unitPriceExVat().toPlainString()).append("</td>")
                .append("<td style=\"text-align:right\">R ").append(item.lineSubtotal().toPlainString()).append("</td>")
                .append("<td style=\"text-align:right\">R ").append(item.vatAmount().toPlainString()).append("</td>")
                .append("<td style=\"text-align:right\">R ").append(item.lineTotal().toPlainString()).append("</td>")
                .append("</tr>");
        }

        String taxInvoiceLabel = invoice.total().doubleValue() > 5000
            ? "TAX INVOICE" : "INVOICE";

        return template
            .replace("{{INVOICE_LABEL}}", taxInvoiceLabel)
            .replace("{{INVOICE_NUMBER}}", nullSafe(invoice.invoiceNumber()))
            .replace("{{ISSUE_DATE}}", invoice.issueDate() != null ? invoice.issueDate().format(DATE_FORMAT) : "")
            .replace("{{DUE_DATE}}", invoice.dueDate() != null ? invoice.dueDate().format(DATE_FORMAT) : "")
            .replace("{{PARTNER_NAME}}", nullSafe(invoice.partnerName()))
            .replace("{{PARTNER_ID}}", nullSafe(invoice.partnerId()))
            .replace("{{BILLING_PERIOD}}", nullSafe(invoice.billingPeriod() != null ? invoice.billingPeriod().toString() : ""))
            .replace("{{SELLER_NAME}}", DomainConstants.SELLER_COMPANY_NAME)
            .replace("{{SELLER_REG}}", DomainConstants.SELLER_REGISTRATION_NUMBER)
            .replace("{{SELLER_ADDRESS}}", DomainConstants.SELLER_ADDRESS)
            .replace("{{LINE_ITEMS}}", lineItemsHtml.toString())
            .replace("{{SUBTOTAL}}", "R " + invoice.subtotal().toPlainString())
            .replace("{{VAT_RATE}}", invoice.vatRate().multiply(java.math.BigDecimal.valueOf(100)).toPlainString() + "%")
            .replace("{{VAT_AMOUNT}}", "R " + invoice.vatAmount().toPlainString())
            .replace("{{TOTAL}}", "R " + invoice.total().toPlainString())
            .replace("{{CURRENCY}}", nullSafe(invoice.currency()));
    }

    private String loadTemplate() {
        try (InputStream is = getClass().getResourceAsStream("/templates/invoice-template.html")) {
            if (is == null) {
                return getDefaultTemplate();
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOG.warn("Failed to load invoice template, using default", e);
            return getDefaultTemplate();
        }
    }

    private String getDefaultTemplate() {
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
            <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <style type="text/css">
                    body { font-family: Helvetica, Arial, sans-serif; font-size: 10pt; color: #333; margin: 40px; }
                    h1 { color: #00B3D9; font-size: 24pt; margin-bottom: 5px; }
                    .header { display: flex; justify-content: space-between; margin-bottom: 30px; }
                    .meta { margin-bottom: 20px; }
                    .meta td { padding: 3px 10px 3px 0; }
                    table.items { width: 100%; border-collapse: collapse; margin: 20px 0; }
                    table.items th { background: #00B3D9; color: white; padding: 8px; text-align: left; font-size: 9pt; }
                    table.items td { padding: 8px; border-bottom: 1px solid #eee; font-size: 9pt; }
                    .totals { float: right; width: 300px; }
                    .totals td { padding: 5px 10px; }
                    .totals .total-row { font-weight: bold; font-size: 14pt; border-top: 2px solid #00B3D9; }
                    .footer { margin-top: 40px; font-size: 8pt; color: #999; border-top: 1px solid #eee; padding-top: 10px; }
                </style>
            </head>
            <body>
                <h1>{{INVOICE_LABEL}}</h1>
                <table class="meta">
                    <tr><td><b>Invoice Number:</b></td><td>{{INVOICE_NUMBER}}</td></tr>
                    <tr><td><b>Issue Date:</b></td><td>{{ISSUE_DATE}}</td></tr>
                    <tr><td><b>Due Date:</b></td><td>{{DUE_DATE}}</td></tr>
                    <tr><td><b>Billing Period:</b></td><td>{{BILLING_PERIOD}}</td></tr>
                </table>
                <table class="meta">
                    <tr><td><b>From:</b></td><td>{{SELLER_NAME}}</td></tr>
                    <tr><td></td><td>{{SELLER_REG}}</td></tr>
                    <tr><td></td><td>{{SELLER_ADDRESS}}</td></tr>
                </table>
                <table class="meta">
                    <tr><td><b>To:</b></td><td>{{PARTNER_NAME}}</td></tr>
                    <tr><td><b>Partner ID:</b></td><td>{{PARTNER_ID}}</td></tr>
                </table>
                <table class="items">
                    <thead>
                        <tr>
                            <th>Description</th>
                            <th style="text-align:right">Qty</th>
                            <th style="text-align:right">Unit Price</th>
                            <th style="text-align:right">Subtotal</th>
                            <th style="text-align:right">VAT</th>
                            <th style="text-align:right">Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        {{LINE_ITEMS}}
                    </tbody>
                </table>
                <table class="totals">
                    <tr><td>Subtotal (excl. VAT):</td><td style="text-align:right">{{SUBTOTAL}}</td></tr>
                    <tr><td>VAT ({{VAT_RATE}}):</td><td style="text-align:right">{{VAT_AMOUNT}}</td></tr>
                    <tr class="total-row"><td>Total ({{CURRENCY}}):</td><td style="text-align:right">{{TOTAL}}</td></tr>
                </table>
                <div class="footer">
                    <p>{{SELLER_NAME}} | {{SELLER_ADDRESS}}</p>
                    <p>Payment terms: 30 days from invoice date.</p>
                </div>
            </body>
            </html>
            """;
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
    }
}
