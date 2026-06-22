package verigate.adapter.dharesponse.infrastructure.report;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import verigate.adapter.dharesponse.domain.models.DhaResponseVerificationResult;
import verigate.adapter.dharesponse.domain.services.DhaResponseReportService;

/** Generates DHA response verification reports and stores them in S3. */
public class DhaResponsePdfReportGenerator
    implements DhaResponseReportService {

  private static final Logger logger =
      LoggerFactory.getLogger(DhaResponsePdfReportGenerator.class);
  private static final DateTimeFormatter DATE_FMT =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")
          .withZone(ZoneId.of("Africa/Johannesburg"));

  private final S3Client s3Client;
  private final String bucketName;

  public DhaResponsePdfReportGenerator(
      S3Client s3Client, String bucketName) {
    this.s3Client = s3Client;
    this.bucketName = bucketName;
  }

  @Override
  public String generateReport(
      String commandId, String partnerId,
      DhaResponseVerificationResult result) {
    final String s3Key =
        "dha-permit-verification/" + commandId + ".txt";

    StringBuilder report = new StringBuilder();
    report.append("DHA PERMIT VERIFICATION REPORT\n");
    report.append(
        "═══════════════════════════════════════\n\n");
    report.append("Report Generated:   ")
        .append(DATE_FMT.format(Instant.now())).append("\n");
    report.append("VeriGate Reference: ")
        .append(commandId).append("\n");
    report.append("Partner ID:         ")
        .append(partnerId).append("\n\n");

    report.append("VERIFICATION OUTCOME\n");
    report.append(
        "───────────────────────────────────────\n");
    report.append("Outcome:            ")
        .append(result.outcome()).append("\n");
    report.append("Is Authentic:       ")
        .append(result.isAuthentic() ? "Yes" : "No")
        .append("\n");
    report.append("Currently Valid:    ")
        .append(result.isCurrentlyValid() ? "Yes" : "No")
        .append("\n");
    report.append("Confidence Score:   ")
        .append(String.format(
            "%.0f%%", result.confidenceScore() * 100))
        .append("\n\n");

    if (result.holderName() != null) {
      report.append("Holder Name:        ")
          .append(result.holderName()).append("\n");
    }
    if (result.nationality() != null) {
      report.append("Nationality:        ")
          .append(result.nationality()).append("\n");
    }
    if (result.expiryDate() != null) {
      report.append("Expiry Date:        ")
          .append(result.expiryDate()).append("\n");
    }
    if (result.employerMatch() != null) {
      report.append("Employer Match:     ")
          .append(result.employerMatch()).append("\n");
    }

    if (result.additionalNotes() != null
        && !result.additionalNotes().isBlank()) {
      report.append("\nADDITIONAL NOTES\n");
      report.append(
          "───────────────────────────────────────\n");
      report.append(result.additionalNotes()).append("\n");
    }

    report.append(
        "\n═══════════════════════════════════════\n");
    report.append(
        "This report was generated automatically "
            + "by VeriGate.\n");

    s3Client.putObject(
        PutObjectRequest.builder()
            .bucket(bucketName)
            .key(s3Key)
            .contentType("text/plain")
            .build(),
        RequestBody.fromString(
            report.toString(), StandardCharsets.UTF_8));

    logger.info("Report stored: bucket={}, key={}",
        bucketName, s3Key);
    return s3Key;
  }
}
