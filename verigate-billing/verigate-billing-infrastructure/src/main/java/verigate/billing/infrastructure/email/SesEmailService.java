/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.email;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.RawMessage;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;
import verigate.billing.domain.models.Invoice;

/**
 * Sends invoice emails with PDF attachments using AWS SES SendRawEmail.
 * Downloads the invoice PDF from S3, constructs a MIME multipart message,
 * and delivers it via SES.
 */
public class SesEmailService {

    private static final Logger LOG = LoggerFactory.getLogger(SesEmailService.class);
    private static final DateTimeFormatter PERIOD_FORMAT = DateTimeFormatter.ofPattern("MMMM yyyy");
    private static final String MIME_BOUNDARY = "----=_VeriGateBoundary_";

    private final SesClient sesClient;
    private final S3Client s3Client;
    private final String bucketName;
    private final String senderEmail;

    @Inject
    public SesEmailService(
        SesClient sesClient,
        S3Client s3Client,
        @Named("invoiceBucketName") String bucketName,
        @Named("invoiceSenderEmail") String senderEmail) {
        this.sesClient = sesClient;
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.senderEmail = senderEmail;
    }

    /**
     * Sends an invoice email with the PDF attached to the specified recipient.
     *
     * @param invoice        the invoice whose PDF should be attached
     * @param recipientEmail the recipient email address
     */
    public void sendInvoiceEmail(Invoice invoice, String recipientEmail) {
        LOG.info("Sending invoice email: invoiceId={}, recipient={}", invoice.invoiceId(), recipientEmail);

        try {
            byte[] pdfBytes = downloadPdfFromS3(invoice.pdfS3Key());
            byte[] rawMessage = buildMimeMessage(invoice, recipientEmail, pdfBytes);

            SendRawEmailRequest request = SendRawEmailRequest.builder()
                .rawMessage(RawMessage.builder()
                    .data(SdkBytes.fromByteArray(rawMessage))
                    .build())
                .build();

            sesClient.sendRawEmail(request);

            LOG.info("Invoice email sent successfully: invoiceId={}, invoiceNumber={}, recipient={}",
                invoice.invoiceId(), invoice.invoiceNumber(), recipientEmail);

        } catch (Exception e) {
            LOG.error("Failed to send invoice email: invoiceId={}, recipient={}",
                invoice.invoiceId(), recipientEmail, e);
            throw new RuntimeException("Failed to send invoice email for invoice: " + invoice.invoiceId(), e);
        }
    }

    private byte[] downloadPdfFromS3(String s3Key) {
        LOG.debug("Downloading invoice PDF from S3: bucket={}, key={}", bucketName, s3Key);

        GetObjectRequest getRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(s3Key)
            .build();

        try {
            return s3Client.getObjectAsBytes(getRequest).asByteArray();
        } catch (Exception e) {
            LOG.error("Failed to download PDF from S3: bucket={}, key={}", bucketName, s3Key, e);
            throw new RuntimeException("Failed to download invoice PDF from S3: " + s3Key, e);
        }
    }

    private byte[] buildMimeMessage(Invoice invoice, String recipientEmail, byte[] pdfBytes) {
        String boundary = MIME_BOUNDARY + UUID.randomUUID().toString().replace("-", "");
        String subject = "VeriGate Invoice " + invoice.invoiceNumber()
            + " - " + invoice.billingPeriod().format(PERIOD_FORMAT);
        String pdfFilename = invoice.invoiceNumber() + ".pdf";
        String htmlBody = buildHtmlBody(invoice);
        String encodedPdf = Base64.getMimeEncoder(76, "\r\n".getBytes(StandardCharsets.UTF_8))
            .encodeToString(pdfBytes);

        StringBuilder message = new StringBuilder();

        // Headers
        message.append("From: VeriGate Billing <").append(senderEmail).append(">\r\n");
        message.append("To: ").append(recipientEmail).append("\r\n");
        message.append("Subject: ").append(subject).append("\r\n");
        message.append("MIME-Version: 1.0\r\n");
        message.append("Content-Type: multipart/mixed; boundary=\"").append(boundary).append("\"\r\n");
        message.append("\r\n");

        // HTML body part
        message.append("--").append(boundary).append("\r\n");
        message.append("Content-Type: text/html; charset=UTF-8\r\n");
        message.append("Content-Transfer-Encoding: 7bit\r\n");
        message.append("\r\n");
        message.append(htmlBody).append("\r\n");
        message.append("\r\n");

        // PDF attachment part
        message.append("--").append(boundary).append("\r\n");
        message.append("Content-Type: application/pdf; name=\"").append(pdfFilename).append("\"\r\n");
        message.append("Content-Disposition: attachment; filename=\"").append(pdfFilename).append("\"\r\n");
        message.append("Content-Transfer-Encoding: base64\r\n");
        message.append("\r\n");
        message.append(encodedPdf).append("\r\n");
        message.append("\r\n");

        // Closing boundary
        message.append("--").append(boundary).append("--\r\n");

        return message.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String buildHtmlBody(Invoice invoice) {
        String formattedTotal = "R " + invoice.total().toPlainString();
        String formattedDueDate = invoice.dueDate() != null
            ? invoice.dueDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
            : "N/A";
        String formattedPeriod = invoice.billingPeriod().format(PERIOD_FORMAT);

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8" />
                <style>
                    body { font-family: Helvetica, Arial, sans-serif; color: #333333; line-height: 1.6; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 0 auto; padding: 30px; }
                    .header { background-color: #00B3D9; padding: 20px 30px; }
                    .header h1 { color: #ffffff; margin: 0; font-size: 22px; }
                    .content { padding: 30px 0; }
                    .invoice-details { background-color: #f8f9fa; border-radius: 6px; padding: 20px; margin: 20px 0; }
                    .invoice-details table { width: 100%%; border-collapse: collapse; }
                    .invoice-details td { padding: 6px 0; font-size: 14px; }
                    .invoice-details td:first-child { color: #666666; width: 140px; }
                    .invoice-details td:last-child { font-weight: bold; }
                    .total { font-size: 20px; color: #00B3D9; font-weight: bold; }
                    .cta { margin: 25px 0; }
                    .footer { border-top: 1px solid #e0e0e0; padding-top: 20px; margin-top: 30px; font-size: 12px; color: #999999; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>VeriGate</h1>
                    </div>
                    <div class="content">
                        <p>Dear %s,</p>
                        <p>Please find attached your invoice for the billing period <strong>%s</strong>.</p>
                        <div class="invoice-details">
                            <table>
                                <tr>
                                    <td>Invoice Number:</td>
                                    <td>%s</td>
                                </tr>
                                <tr>
                                    <td>Billing Period:</td>
                                    <td>%s</td>
                                </tr>
                                <tr>
                                    <td>Due Date:</td>
                                    <td>%s</td>
                                </tr>
                                <tr>
                                    <td>Amount Due:</td>
                                    <td class="total">%s</td>
                                </tr>
                            </table>
                        </div>
                        <p>The full invoice is attached as a PDF document for your records.</p>
                        <p>If you have any questions regarding this invoice, please contact our billing support team.</p>
                        <p>Kind regards,<br/>VeriGate Billing</p>
                    </div>
                    <div class="footer">
                        <p>VeriGate (Pty) Ltd | 12 Long Street, Cape Town, 8001, South Africa</p>
                        <p>This is an automated email. Please do not reply directly to this message.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                escapeHtml(invoice.partnerName() != null ? invoice.partnerName() : "Partner"),
                escapeHtml(formattedPeriod),
                escapeHtml(invoice.invoiceNumber()),
                escapeHtml(formattedPeriod),
                escapeHtml(formattedDueDate),
                escapeHtml(formattedTotal)
            );
    }

    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
