package verigate.webbff.verification.controller;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import verigate.webbff.auth.PartnerContextHolder;
import verigate.webbff.verification.service.DocumentAnalyticsService;

/**
 * REST controller for Document AI analytics. Provides aggregated metrics on document
 * processing volume, classification accuracy, throughput, and confidence distribution.
 */
@RestController
@RequestMapping("/api/partner/document-analytics")
public class DocumentAnalyticsController {

  private static final Logger logger = LoggerFactory.getLogger(DocumentAnalyticsController.class);

  private final DocumentAnalyticsService analyticsService;

  public DocumentAnalyticsController(DocumentAnalyticsService analyticsService) {
    this.analyticsService = analyticsService;
  }

  @GetMapping("/summary")
  public ResponseEntity<Map<String, Object>> getSummary(
      @RequestParam(defaultValue = "today") String range) {
    String partnerId = PartnerContextHolder.requirePartnerId();
    logger.debug("Fetching document analytics summary: partnerId={}, range={}", partnerId, range);
    return ResponseEntity.ok(analyticsService.getSummary(partnerId, range));
  }

  @GetMapping("/volume-by-type")
  public ResponseEntity<Map<String, Integer>> getVolumeByType(
      @RequestParam(defaultValue = "today") String range) {
    String partnerId = PartnerContextHolder.requirePartnerId();
    logger.debug("Fetching volume by type: partnerId={}, range={}", partnerId, range);
    return ResponseEntity.ok(analyticsService.getVolumeByType(partnerId, range));
  }

  @GetMapping("/throughput")
  public ResponseEntity<List<Map<String, Object>>> getThroughput() {
    String partnerId = PartnerContextHolder.requirePartnerId();
    logger.debug("Fetching throughput: partnerId={}", partnerId);
    return ResponseEntity.ok(analyticsService.getThroughput(partnerId));
  }

  @GetMapping("/confidence-distribution")
  public ResponseEntity<Map<String, Integer>> getConfidenceDistribution(
      @RequestParam(defaultValue = "today") String range) {
    String partnerId = PartnerContextHolder.requirePartnerId();
    logger.debug("Fetching confidence distribution: partnerId={}, range={}", partnerId, range);
    return ResponseEntity.ok(analyticsService.getConfidenceDistribution(partnerId, range));
  }
}
