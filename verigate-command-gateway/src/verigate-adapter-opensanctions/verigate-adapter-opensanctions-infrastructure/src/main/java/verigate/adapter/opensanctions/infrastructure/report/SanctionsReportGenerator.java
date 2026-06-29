/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.opensanctions.infrastructure.report;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import verigate.adapter.opensanctions.domain.services.SanctionsReportService;

/**
 * Generates a plain-text sanctions screening report and uploads it to S3.
 *
 * <p>The S3 key is returned so the caller can persist it in the command store
 * as {@code auxiliaryData["reportDocumentId"]}, making it retrievable by the BFF
 * for partner download.
 */
public class SanctionsReportGenerator implements SanctionsReportService {

  private static final Logger LOGGER = Logger.getLogger(SanctionsReportGenerator.class.getName());
  private static final DateTimeFormatter DATE_FMT =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")
          .withZone(ZoneId.of("Africa/Johannesburg"));
  private static final String S3_KEY_PREFIX = "sanctions-screening/";

  private final S3Client s3Client;
  private final String bucketName;

  public SanctionsReportGenerator(S3Client s3Client, String bucketName) {
    this.s3Client = s3Client;
    this.bucketName = bucketName;
  }

  @Override
  public String generateReport(
      String commandId, String partnerId, Map<String, String> resultDetails) {

    String s3Key = S3_KEY_PREFIX + commandId + ".txt";

    try {
      String content = buildReportContent(commandId, partnerId, resultDetails);

      s3Client.putObject(
          PutObjectRequest.builder()
              .bucket(bucketName)
              .key(s3Key)
              .contentType("text/plain; charset=UTF-8")
              .build(),
          RequestBody.fromString(content, StandardCharsets.UTF_8));

      LOGGER.info("Sanctions report stored: bucket=" + bucketName + ", key=" + s3Key);
      return s3Key;

    } catch (Exception e) {
      LOGGER.log(Level.SEVERE,
          "Failed to generate sanctions report for commandId: " + commandId, e);
      throw new RuntimeException("Report generation failed", e);
    }
  }

  private String buildReportContent(
      String commandId, String partnerId, Map<String, String> d) {

    final String outcome = d.getOrDefault("outcome", "UNKNOWN");
    final String significantMatches = d.getOrDefault("significant_matches_count", "0");
    final int matchCount = parseIntSafe(significantMatches);
    final String subjectName = d.getOrDefault("subject_name", "Unknown");
    final String entityType = d.getOrDefault("entity_type", "Person");

    StringBuilder sb = new StringBuilder();

    sb.append("SANCTIONS & PEP SCREENING REPORT\n");
    sb.append("═══════════════════════════════════════════════════════\n\n");
    sb.append("Report Generated:   ").append(DATE_FMT.format(Instant.now())).append("\n");
    sb.append("VeriGate Reference: ").append(commandId).append("\n");
    sb.append("Partner ID:         ").append(partnerId).append("\n\n");

    sb.append("SUBJECT DETAILS\n");
    sb.append("───────────────────────────────────────────────────────\n");
    sb.append("Subject Name:       ").append(subjectName).append("\n");
    sb.append("Entity Type:        ").append(entityType).append("\n\n");

    sb.append("SCREENING OUTCOME\n");
    sb.append("───────────────────────────────────────────────────────\n");
    sb.append("Outcome:            ").append(outcome).append("\n");
    sb.append("Classification:     ").append(classifyOutcome(outcome)).append("\n");
    sb.append("Provider:           ")
        .append(d.getOrDefault("provider", "OpenSanctions")).append("\n");
    sb.append("Algorithm:          ").append(d.getOrDefault("algorithm", "logic-v2")).append("\n");
    sb.append("Significant Hits:   ").append(significantMatches).append("\n\n");

    sb.append("MATCH DETAILS\n");
    sb.append("───────────────────────────────────────────────────────\n");

    if (matchCount == 0) {
      sb.append("No matches found against OpenSanctions sanctions and PEP registers.\n");
    } else {
      for (int i = 0; i < matchCount; i++) {
        String prefix = "match_" + i + "_";
        String caption = d.getOrDefault(prefix + "caption", "Unknown entity");
        String entityId = d.getOrDefault(prefix + "id", "—");
        String scoreRaw = d.getOrDefault(prefix + "score", "0");
        String matchType = d.getOrDefault(prefix + "type", "SANCTIONS");
        String datasets = d.getOrDefault(prefix + "datasets", "—");
        String target = d.getOrDefault(prefix + "target", "false");

        double score = parseDoubleSafe(scoreRaw);
        int scorePercent = (int) Math.round(score * 100);
        String confidence = scorePercent >= 90 ? "High" : "Medium";

        sb.append("Match ").append(i + 1).append(" of ").append(matchCount).append(":\n");
        sb.append("  Entity:           ").append(caption).append("\n");
        sb.append("  OpenSanctions ID: ").append(entityId).append("\n");
        sb.append("  Match Score:      ").append(scorePercent)
            .append("/100 (").append(confidence).append(" confidence)\n");
        sb.append("  Type:             ").append(matchType).append("\n");
        sb.append("  Datasets:         ").append(datasets).append("\n");
        sb.append("  Risk Target:      ").append("true".equals(target) ? "Yes" : "No").append("\n");
        if (i < matchCount - 1) {
          sb.append("\n");
        }
      }
    }

    sb.append("\n═══════════════════════════════════════════════════════\n");
    sb.append("IMPORTANT NOTICE\n");
    sb.append("───────────────────────────────────────────────────────\n");
    sb.append("Match scores reflect algorithmic confidence in entity identity,\n");
    sb.append("not confirmed risk status. A score ≥90 (HARD_FAIL) requires\n");
    sb.append("manual verification before proceeding. A score ≥70 (SOFT_FAIL)\n");
    sb.append("requires review. This report was generated automatically by\n");
    sb.append("VeriGate using OpenSanctions data and is subject to the data\n");
    sb.append("licensing terms of OpenSanctions (opensanctions.org).\n");

    return sb.toString();
  }

  private static String classifyOutcome(String outcome) {
    return switch (outcome) {
      case "HARD_FAIL" -> "Hit — sanctions or PEP match confirmed (score ≥90)";
      case "SOFT_FAIL" -> "Possible match — manual review required (score ≥70)";
      case "SUCCEEDED" -> "Clear — no significant matches found";
      default -> outcome;
    };
  }

  private static int parseIntSafe(String value) {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  private static double parseDoubleSafe(String value) {
    try {
      return Double.parseDouble(value);
    } catch (NumberFormatException e) {
      return 0.0;
    }
  }
}
