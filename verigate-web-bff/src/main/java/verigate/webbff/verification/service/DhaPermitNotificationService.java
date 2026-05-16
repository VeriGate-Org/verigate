package verigate.webbff.verification.service;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.RawMessage;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;
import verigate.webbff.config.properties.DhaVerificationProperties;

@Service
public class DhaPermitNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(DhaPermitNotificationService.class);
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z").withZone(ZoneId.of("Africa/Johannesburg"));

    private static final Map<String, String> PERMIT_TYPE_LABELS = Map.of(
            "asylum_seeker_permit", "Asylum Seeker Permit",
            "general_work_permit", "General Work Permit"
    );

    private final SesClient sesClient;
    private final S3Client s3Client;
    private final DhaVerificationProperties properties;

    public DhaPermitNotificationService(SesClient sesClient, S3Client s3Client,
                                        DhaVerificationProperties properties) {
        this.sesClient = sesClient;
        this.s3Client = s3Client;
        this.properties = properties;
    }

    /**
     * Sends a verification request email to DHA with the permit document(s) attached.
     * Supports asylum seeker permits and general work permits.
     * Downloads the documents from S3 and attaches them to a raw MIME email sent via SES.
     * Fire-and-forget: logs errors but does not throw so the verification submission still succeeds.
     *
     * @return true if the email was sent successfully, false otherwise
     */
    public boolean sendNotification(String documentType, String partnerId, String permitNumber,
                                    String nationality, String employerName,
                                    List<String> s3ObjectKeys, String s3BucketName, UUID commandId) {
        try {
            String timestamp = DATE_FMT.format(Instant.now());
            String permitLabel = PERMIT_TYPE_LABELS.getOrDefault(documentType, "Permit");

            String subject = "Request for " + permitLabel + " Verification - " + permitNumber;
            String bodyText = buildEmailBody(documentType, permitLabel, permitNumber, nationality,
                    employerName, timestamp, partnerId, commandId, s3ObjectKeys.size());

            String rawEmail = buildRawMimeMessage(
                    properties.getSenderEmail(),
                    properties.getNotificationEmail(),
                    subject,
                    bodyText,
                    s3ObjectKeys,
                    s3BucketName);

            SendRawEmailRequest rawEmailRequest = SendRawEmailRequest.builder()
                    .rawMessage(RawMessage.builder()
                            .data(SdkBytes.fromByteBuffer(
                                    ByteBuffer.wrap(rawEmail.getBytes(java.nio.charset.StandardCharsets.UTF_8))))
                            .build())
                    .build();

            sesClient.sendRawEmail(rawEmailRequest);
            logger.info("DHA permit verification request sent: type={}, permitNumber={}, commandId={}, attachments={}, recipient={}",
                    documentType, permitNumber, commandId, s3ObjectKeys.size(), properties.getNotificationEmail());
            return true;
        } catch (Exception e) {
            logger.error("Failed to send DHA permit verification request: type={}, permitNumber={}, commandId={}",
                    documentType, permitNumber, commandId, e);
            return false;
        }
    }

    private String buildEmailBody(String documentType, String permitLabel, String permitNumber,
                                  String nationality, String employerName, String timestamp,
                                  String partnerId, UUID commandId, int attachmentCount) {
        StringBuilder sb = new StringBuilder();
        sb.append("Dear Department of Home Affairs,\n\n");
        sb.append("We are writing to request verification of the following ").append(permitLabel.toLowerCase());
        sb.append(" on behalf of our client. Please find the permit document");
        if (attachmentCount > 1) {
            sb.append(" and supporting documents (including consent form)");
        }
        sb.append(" attached for your review.\n\n");

        sb.append("PERMIT DETAILS\n");
        sb.append("──────────────────────────────────\n");
        sb.append("Permit Type:       ").append(permitLabel).append("\n");
        sb.append("Permit Number:     ").append(permitNumber).append("\n");
        sb.append("Nationality:       ").append(nationality).append("\n");

        if ("general_work_permit".equals(documentType) && employerName != null && !employerName.isBlank()) {
            sb.append("Employer:          ").append(employerName).append("\n");
        }

        sb.append("Submission Date:   ").append(timestamp).append("\n");
        sb.append("Attachments:       ").append(attachmentCount).append("\n\n");

        sb.append("REFERENCE INFORMATION\n");
        sb.append("──────────────────────────────────\n");
        sb.append("Partner ID:        ").append(partnerId).append("\n");
        sb.append("VeriGate Ref:      ").append(commandId).append("\n\n");

        sb.append("REQUEST\n");
        sb.append("──────────────────────────────────\n");
        sb.append("We kindly request that the Department of Home Affairs verify the authenticity ");
        sb.append("and validity of the attached ").append(permitLabel.toLowerCase()).append(" document. ");
        sb.append("Please confirm whether the permit is genuine, currently valid, and correctly ");
        sb.append("issued to the holder identified above.");

        if ("general_work_permit".equals(documentType)) {
            sb.append(" Please also confirm that the work permit conditions, including the named ");
            sb.append("employer, are valid.");
        }

        sb.append("\n\n");
        sb.append("Please respond to this email with the verification outcome at your earliest convenience.\n\n");
        sb.append("Kind regards,\n");
        sb.append("VeriGate Verification Services\n");
        sb.append(properties.getSenderEmail()).append("\n");

        return sb.toString();
    }

    private byte[] downloadDocument(String bucketName, String s3ObjectKey) {
        try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3ObjectKey)
                        .build())) {
            return s3Object.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to download document from S3: " + s3ObjectKey, e);
        }
    }

    /**
     * Builds a raw RFC 2822 MIME multipart/mixed message with a text body and multiple attachments.
     */
    private String buildRawMimeMessage(String from, String to, String subject,
                                       String bodyText, List<String> s3ObjectKeys,
                                       String s3BucketName) {
        String boundary = "----=_Part_" + UUID.randomUUID().toString().replace("-", "");

        StringBuilder sb = new StringBuilder();
        sb.append("From: ").append(from).append("\r\n");
        sb.append("To: ").append(to).append("\r\n");
        sb.append("Subject: ").append(subject).append("\r\n");
        sb.append("MIME-Version: 1.0\r\n");
        sb.append("Content-Type: multipart/mixed; boundary=\"").append(boundary).append("\"\r\n");
        sb.append("\r\n");

        // Text body part
        sb.append("--").append(boundary).append("\r\n");
        sb.append("Content-Type: text/plain; charset=UTF-8\r\n");
        sb.append("Content-Transfer-Encoding: 7bit\r\n");
        sb.append("\r\n");
        sb.append(bodyText).append("\r\n");
        sb.append("\r\n");

        // Attachment parts
        for (String s3ObjectKey : s3ObjectKeys) {
            byte[] documentBytes = downloadDocument(s3BucketName, s3ObjectKey);
            String contentType = inferContentType(s3ObjectKey);
            String fileName = extractFileName(s3ObjectKey);
            String encodedAttachment = Base64.getMimeEncoder(76, "\r\n".getBytes())
                    .encodeToString(documentBytes);

            sb.append("--").append(boundary).append("\r\n");
            sb.append("Content-Type: ").append(contentType).append("; name=\"").append(fileName).append("\"\r\n");
            sb.append("Content-Disposition: attachment; filename=\"").append(fileName).append("\"\r\n");
            sb.append("Content-Transfer-Encoding: base64\r\n");
            sb.append("\r\n");
            sb.append(encodedAttachment).append("\r\n");
            sb.append("\r\n");
        }

        // Closing boundary
        sb.append("--").append(boundary).append("--\r\n");

        return sb.toString();
    }

    private static String inferContentType(String s3ObjectKey) {
        String lower = s3ObjectKey.toLowerCase();
        if (lower.endsWith(".pdf")) return "application/pdf";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".tiff") || lower.endsWith(".tif")) return "image/tiff";
        return "application/octet-stream";
    }

    private static String extractFileName(String s3ObjectKey) {
        int lastSlash = s3ObjectKey.lastIndexOf('/');
        return lastSlash >= 0 ? s3ObjectKey.substring(lastSlash + 1) : s3ObjectKey;
    }
}
