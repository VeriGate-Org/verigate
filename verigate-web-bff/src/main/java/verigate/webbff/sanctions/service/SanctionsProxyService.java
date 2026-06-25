package verigate.webbff.sanctions.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import verigate.webbff.verification.repository.CommandStatusRepository;
import verigate.webbff.verification.repository.model.VerificationCommandStoreItem;

@Service
public class SanctionsProxyService {

  private static final Logger logger = LoggerFactory.getLogger(SanctionsProxyService.class);

  private static final Duration REPORT_URL_TTL = Duration.ofMinutes(15);

  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;
  private final String apiBaseUrl;
  private final String apiKey;
  private final CommandStatusRepository commandStatusRepository;
  private final S3Presigner s3Presigner;
  private final String reportBucketName;

  public SanctionsProxyService(
      ObjectMapper objectMapper,
      @Value("${verigate.opensanctions.api-url:https://api.opensanctions.org}") String apiBaseUrl,
      @Value("${verigate.opensanctions.api-key:}") String apiKey,
      CommandStatusRepository commandStatusRepository,
      S3Presigner s3Presigner,
      @Value("${verigate.sanctions.report-bucket:verigate-documents}") String reportBucketName) {
    this.objectMapper = objectMapper;
    this.apiBaseUrl = apiBaseUrl;
    this.apiKey = apiKey;
    this.commandStatusRepository = commandStatusRepository;
    this.s3Presigner = s3Presigner;
    this.reportBucketName = reportBucketName;
    this.httpClient =
        HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).build();
  }

  public Map<String, Object> getEntity(String entityId) {
    try {
      String url = apiBaseUrl + "/entities/" + entityId;
      HttpRequest request = buildGetRequest(url);
      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      return objectMapper.readValue(response.body(), new TypeReference<>() {});
    } catch (Exception e) {
      logger.error("Failed to get entity {}", entityId, e);
      throw new RuntimeException("Failed to retrieve entity details", e);
    }
  }

  public Map<String, Object> getAdjacentEntities(String entityId) {
    try {
      String url = apiBaseUrl + "/entities/" + entityId + "/adjacent";
      HttpRequest request = buildGetRequest(url);
      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      return objectMapper.readValue(response.body(), new TypeReference<>() {});
    } catch (Exception e) {
      logger.error("Failed to get adjacent entities for {}", entityId, e);
      throw new RuntimeException("Failed to retrieve adjacent entities", e);
    }
  }

  public Map<String, Object> submitDisposition(
      Map<String, Object> disposition, String requestingPartnerId) {
    String entityId = String.valueOf(disposition.get("entityId"));
    String screeningId = String.valueOf(disposition.get("screeningId"));
    String action = String.valueOf(disposition.get("action"));
    String reason = String.valueOf(disposition.getOrDefault("reason", ""));

    UUID commandId;
    try {
      commandId = UUID.fromString(screeningId);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Invalid screeningId: " + screeningId);
    }

    VerificationCommandStoreItem item = commandStatusRepository.findById(commandId)
        .orElseThrow(() -> new RuntimeException("Screening not found: " + screeningId));
    if (!requestingPartnerId.equals(item.getPartnerId())) {
      throw new RuntimeException("Screening not found: " + screeningId);
    }

    String dispositionId = UUID.randomUUID().toString();
    String createdAt = Instant.now().toString();

    Map<String, String> dispositionData = new HashMap<>();
    dispositionData.put("disposition_id", dispositionId);
    dispositionData.put("disposition_action", action);
    dispositionData.put("disposition_entity_id", entityId);
    dispositionData.put("disposition_reason", reason);
    dispositionData.put("disposition_created_at", createdAt);
    commandStatusRepository.updateAuxiliaryData(commandId, dispositionData);

    Map<String, Object> result = new HashMap<>();
    result.put("dispositionId", dispositionId);
    result.put("entityId", entityId);
    result.put("action", action);
    result.put("createdAt", createdAt);
    return result;
  }

  public Map<String, Object> getScreeningHistory(String partnerId) {
    var page = commandStatusRepository.findByPartnerId(
        partnerId, null, "SANCTIONS_SCREENING", 50, null);

    List<Map<String, Object>> items = page.items().stream().map(item -> {
      Map<String, String> aux =
          item.getAuxiliaryData() != null ? item.getAuxiliaryData() : Map.of();
      int matchCount = 0;
      try {
        matchCount = Integer.parseInt(aux.getOrDefault("significant_matches_count", "0"));
      } catch (NumberFormatException ignored) {
      }
      String outcome =
          aux.getOrDefault("outcome",
              item.getStatus() != null ? item.getStatus().name() : "UNKNOWN");

      Map<String, Object> row = new HashMap<>();
      row.put("screeningId", item.getCommandId());
      row.put("subjectName", aux.getOrDefault("subject_name", "Unknown"));
      row.put("entityType", aux.getOrDefault("entity_type", "Person"));
      row.put("outcome", outcome);
      row.put("matchCount", matchCount);
      row.put("screenedAt", item.getCreatedAt() != null ? item.getCreatedAt() : "");
      row.put("provider", aux.getOrDefault("provider", "OpenSanctions"));
      return row;
    }).toList();

    Map<String, Object> result = new HashMap<>();
    result.put("items", items);
    result.put("total", items.size());
    return result;
  }

  public Map<String, Object> getReportPresignedUrl(UUID commandId, String requestingPartnerId) {
    VerificationCommandStoreItem item = commandStatusRepository.findById(commandId)
        .orElseThrow(() -> new RuntimeException("Screening not found: " + commandId));

    if (!requestingPartnerId.equals(item.getPartnerId())) {
      throw new RuntimeException("Screening not found: " + commandId);
    }

    Map<String, String> aux = item.getAuxiliaryData();
    if (aux == null || !aux.containsKey("reportDocumentId")) {
      throw new RuntimeException("No report available for screening: " + commandId);
    }

    String s3Key = aux.get("reportDocumentId");

    var presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(REPORT_URL_TTL)
        .getObjectRequest(GetObjectRequest.builder()
            .bucket(reportBucketName)
            .key(s3Key)
            .responseContentDisposition(
                "attachment; filename=\"sanctions-report-" + commandId + ".txt\"")
            .build())
        .build();

    String downloadUrl = s3Presigner.presignGetObject(presignRequest).url().toString();

    Map<String, Object> result = new HashMap<>();
    result.put("downloadUrl", downloadUrl);
    result.put("documentId", s3Key);
    result.put("expiresIn", (int) REPORT_URL_TTL.toSeconds());
    return result;
  }

  private HttpRequest buildGetRequest(String url) {
    HttpRequest.Builder builder =
        HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofSeconds(60))
            .GET();
    if (apiKey != null && !apiKey.isEmpty()) {
      builder.header("Authorization", "ApiKey " + apiKey);
    }
    return builder.build();
  }
}
