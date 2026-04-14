package verigate.webbff.verification.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import verigate.webbff.verification.repository.CommandStatusRepository;
import verigate.webbff.verification.repository.model.PageResult;
import verigate.webbff.verification.repository.model.VerificationCommandStoreItem;

/**
 * Service for aggregating document verification analytics from command store data.
 */
@Service
public class DocumentAnalyticsService {

  private static final Logger logger = LoggerFactory.getLogger(DocumentAnalyticsService.class);

  private final CommandStatusRepository commandStatusRepository;

  public DocumentAnalyticsService(CommandStatusRepository commandStatusRepository) {
    this.commandStatusRepository = commandStatusRepository;
  }

  public Map<String, Object> getSummary(String partnerId, String range) {
    Instant from = computeFromInstant(range);
    List<VerificationCommandStoreItem> items = fetchDocumentVerifications(partnerId, from);

    long documentsProcessed = items.size();
    long fraudFlagged = items.stream()
        .filter(this::isFraudFlagged)
        .count();

    double avgConfidence = items.stream()
        .mapToDouble(this::extractConfidenceScore)
        .filter(c -> c > 0)
        .average()
        .orElse(0.0);

    double avgProcessingTimeMs = items.stream()
        .mapToLong(this::extractProcessingTimeMs)
        .filter(t -> t > 0)
        .average()
        .orElse(0.0);

    Map<String, Object> summary = new LinkedHashMap<>();
    summary.put("documentsProcessed", documentsProcessed);
    summary.put("classificationAccuracy", Math.round(avgConfidence * 100));
    summary.put("avgProcessingTimeMs", Math.round(avgProcessingTimeMs));
    summary.put("fraudFlagged", fraudFlagged);
    summary.put("range", range);
    return summary;
  }

  public Map<String, Integer> getVolumeByType(String partnerId, String range) {
    Instant from = computeFromInstant(range);
    List<VerificationCommandStoreItem> items = fetchDocumentVerifications(partnerId, from);

    Map<String, Integer> volumeByType = new LinkedHashMap<>();
    for (VerificationCommandStoreItem item : items) {
      String docType = extractDocumentType(item);
      volumeByType.merge(docType, 1, Integer::sum);
    }
    return volumeByType;
  }

  public List<Map<String, Object>> getThroughput(String partnerId) {
    Instant from = Instant.now().minus(24, ChronoUnit.HOURS);
    List<VerificationCommandStoreItem> items = fetchDocumentVerifications(partnerId, from);

    // Group into hourly buckets (24 buckets)
    Map<Integer, Integer> hourlyBuckets = new LinkedHashMap<>();
    for (int h = 0; h < 24; h++) {
      hourlyBuckets.put(h, 0);
    }

    Instant now = Instant.now();
    for (VerificationCommandStoreItem item : items) {
      try {
        Instant ts = Instant.parse(item.getCreatedAt());
        long hoursAgo = ChronoUnit.HOURS.between(ts, now);
        int bucket = (int) Math.max(0, Math.min(23, 23 - hoursAgo));
        hourlyBuckets.merge(bucket, 1, Integer::sum);
      } catch (Exception e) {
        // skip items with bad timestamps
      }
    }

    return hourlyBuckets.entrySet().stream()
        .map(e -> {
          Map<String, Object> point = new LinkedHashMap<>();
          point.put("hour", e.getKey());
          point.put("count", e.getValue());
          return point;
        })
        .toList();
  }

  public Map<String, Integer> getConfidenceDistribution(String partnerId, String range) {
    Instant from = computeFromInstant(range);
    List<VerificationCommandStoreItem> items = fetchDocumentVerifications(partnerId, from);

    int high = 0, medium = 0, low = 0, manualReview = 0;
    for (VerificationCommandStoreItem item : items) {
      double confidence = extractConfidenceScore(item);
      if (confidence >= 0.95) high++;
      else if (confidence >= 0.80) medium++;
      else if (confidence > 0) low++;
      else manualReview++;
    }

    Map<String, Integer> distribution = new LinkedHashMap<>();
    distribution.put("high", high);
    distribution.put("medium", medium);
    distribution.put("low", low);
    distribution.put("manualReview", manualReview);
    return distribution;
  }

  private List<VerificationCommandStoreItem> fetchDocumentVerifications(
      String partnerId, Instant from) {
    try {
      PageResult<VerificationCommandStoreItem> result = commandStatusRepository.findByPartnerId(
          partnerId, null, 1000, null);
      return result.items().stream()
          .filter(this::isDocumentVerification)
          .filter(item -> isAfter(item, from))
          .toList();
    } catch (Exception e) {
      logger.warn("Failed to fetch document verifications: {}", e.getMessage());
      return List.of();
    }
  }

  private boolean isDocumentVerification(VerificationCommandStoreItem item) {
    Map<String, String> aux = item.getAuxiliaryData();
    if (aux == null) return false;
    String type = aux.getOrDefault("verificationType",
        aux.getOrDefault("type", ""));
    return "DOCUMENT_VERIFICATION".equalsIgnoreCase(type)
        || "DOCUMENT".equalsIgnoreCase(type);
  }

  private boolean isAfter(VerificationCommandStoreItem item, Instant from) {
    try {
      String createdAt = item.getCreatedAt();
      if (createdAt == null) return false;
      return Instant.parse(createdAt).isAfter(from);
    } catch (Exception e) {
      return false;
    }
  }

  private boolean isFraudFlagged(VerificationCommandStoreItem item) {
    Map<String, String> aux = item.getAuxiliaryData();
    if (aux == null) return false;
    String tamperingScore = aux.getOrDefault("tamperingScore", "100");
    try {
      return Integer.parseInt(tamperingScore) < 50;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private double extractConfidenceScore(VerificationCommandStoreItem item) {
    Map<String, String> aux = item.getAuxiliaryData();
    if (aux == null) return 0.0;
    String score = aux.getOrDefault("overallConfidence",
        aux.getOrDefault("confidenceScore", "0"));
    try {
      return Double.parseDouble(score);
    } catch (NumberFormatException e) {
      return 0.0;
    }
  }

  private long extractProcessingTimeMs(VerificationCommandStoreItem item) {
    Map<String, String> aux = item.getAuxiliaryData();
    if (aux == null) return 0;
    String duration = aux.getOrDefault("processingTimeMs",
        aux.getOrDefault("durationMs", "0"));
    try {
      return Long.parseLong(duration);
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  private String extractDocumentType(VerificationCommandStoreItem item) {
    Map<String, String> aux = item.getAuxiliaryData();
    if (aux == null) return "UNKNOWN";
    return aux.getOrDefault("documentType", "UNKNOWN");
  }

  private Instant computeFromInstant(String range) {
    return switch (range != null ? range : "today") {
      case "7d" -> Instant.now().minus(7, ChronoUnit.DAYS);
      case "30d" -> Instant.now().minus(30, ChronoUnit.DAYS);
      default -> Instant.now().truncatedTo(ChronoUnit.DAYS);
    };
  }
}
