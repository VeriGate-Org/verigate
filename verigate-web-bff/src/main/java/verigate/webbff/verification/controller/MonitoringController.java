package verigate.webbff.verification.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import verigate.webbff.auth.PartnerContextHolder;
import verigate.webbff.verification.repository.model.MonitoredSubjectDataModel;
import verigate.webbff.verification.repository.model.MonitoringAlertDataModel;
import verigate.webbff.verification.service.MonitoringService;

@RestController
@RequestMapping("/api/partner/monitoring")
public class MonitoringController {

  private static final Logger logger = LoggerFactory.getLogger(MonitoringController.class);

  private final MonitoringService monitoringService;

  public MonitoringController(MonitoringService monitoringService) {
    this.monitoringService = monitoringService;
  }

  @GetMapping("/subjects")
  public ResponseEntity<List<Map<String, Object>>> listSubjects(
      @RequestParam(required = false) String status,
      @RequestParam(defaultValue = "50") int pageSize) {
    String partnerId = PartnerContextHolder.requirePartnerId();
    logger.info("Listing monitored subjects for partner {} (status={}, pageSize={})",
        partnerId, status, pageSize);

    List<Map<String, Object>> subjects = monitoringService
        .listSubjects(partnerId, status, pageSize)
        .stream()
        .map(this::toSubjectMap)
        .toList();
    return ResponseEntity.ok(subjects);
  }

  @PostMapping("/subjects")
  public ResponseEntity<Map<String, Object>> addSubject(
      @RequestBody Map<String, Object> body) {
    String partnerId = PartnerContextHolder.requirePartnerId();
    logger.info("Adding monitored subject for partner {}", partnerId);

    String policyId = (String) body.get("policyId");
    String subjectType = (String) body.get("subjectType");
    String subjectName = (String) body.get("subjectName");
    String subjectIdentifier = (String) body.get("subjectIdentifier");
    String metadataJson = body.get("metadata") != null
        ? body.get("metadata").toString() : null;
    String monitoringFrequency = (String) body.getOrDefault(
        "monitoringFrequency", "WEEKLY");

    MonitoredSubjectDataModel created = monitoringService.createSubject(
        partnerId, policyId, subjectType, subjectName,
        subjectIdentifier, metadataJson, monitoringFrequency);
    return ResponseEntity.status(HttpStatus.CREATED).body(toSubjectMap(created));
  }

  @GetMapping("/subjects/{id}")
  public ResponseEntity<Map<String, Object>> getSubject(@PathVariable String id) {
    String partnerId = PartnerContextHolder.requirePartnerId();
    logger.info("Fetching monitored subject {} for partner {}", id, partnerId);

    MonitoredSubjectDataModel subject = monitoringService.getSubject(id, partnerId);
    return ResponseEntity.ok(toSubjectMap(subject));
  }

  @PatchMapping("/subjects/{id}")
  public ResponseEntity<Map<String, Object>> updateSubject(
      @PathVariable String id,
      @RequestBody Map<String, Object> updates) {
    String partnerId = PartnerContextHolder.requirePartnerId();
    logger.info("Updating monitored subject {} for partner {}", id, partnerId);

    MonitoredSubjectDataModel updated = monitoringService.updateSubject(
        id, partnerId, updates);
    return ResponseEntity.ok(toSubjectMap(updated));
  }

  @DeleteMapping("/subjects/{id}")
  public ResponseEntity<Void> deleteSubject(@PathVariable String id) {
    String partnerId = PartnerContextHolder.requirePartnerId();
    logger.info("Removing monitored subject {} for partner {}", id, partnerId);

    monitoringService.deleteSubject(id, partnerId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/alerts")
  public ResponseEntity<List<Map<String, Object>>> listAlerts(
      @RequestParam(required = false) String subjectId,
      @RequestParam(required = false) String severity,
      @RequestParam(defaultValue = "50") int pageSize) {
    String partnerId = PartnerContextHolder.requirePartnerId();
    logger.info("Listing monitoring alerts for partner {} (subjectId={}, severity={})",
        partnerId, subjectId, severity);

    List<MonitoringAlertDataModel> alerts = monitoringService.listAlerts(
        partnerId, subjectId, pageSize);

    List<Map<String, Object>> result = alerts.stream()
        .filter(a -> severity == null || severity.isBlank()
            || severity.equals(a.getSeverity()))
        .map(this::toAlertMap)
        .toList();
    return ResponseEntity.ok(result);
  }

  @PostMapping("/alerts/{id}/acknowledge")
  public ResponseEntity<Map<String, Object>> acknowledgeAlert(
      @PathVariable String id,
      @RequestBody Map<String, String> body) {
    String partnerId = PartnerContextHolder.requirePartnerId();
    String acknowledgedBy = body.getOrDefault("acknowledgedBy", "unknown");
    logger.info("Acknowledging alert {} for partner {} by {}", id, partnerId, acknowledgedBy);

    MonitoringAlertDataModel acknowledged = monitoringService.acknowledgeAlert(
        id, partnerId, acknowledgedBy);
    return ResponseEntity.ok(toAlertMap(acknowledged));
  }

  private Map<String, Object> toSubjectMap(MonitoredSubjectDataModel model) {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("subjectId", model.getSubjectId());
    map.put("partnerId", model.getPartnerId());
    map.put("policyId", model.getPolicyId());
    map.put("subjectType", model.getSubjectType());
    map.put("subjectName", model.getSubjectName());
    map.put("subjectIdentifier", model.getSubjectIdentifier());
    map.put("metadataJson", model.getMetadataJson());
    map.put("monitoringFrequency", model.getMonitoringFrequency());
    map.put("status", model.getStatus());
    map.put("lastCheckedAt", model.getLastCheckedAt());
    map.put("nextCheckAt", model.getNextCheckAt());
    map.put("lastRiskScore", model.getLastRiskScore());
    map.put("lastRiskDecision", model.getLastRiskDecision());
    map.put("createdAt", model.getCreatedAt());
    map.put("updatedAt", model.getUpdatedAt());
    return map;
  }

  private Map<String, Object> toAlertMap(MonitoringAlertDataModel model) {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("alertId", model.getAlertId());
    map.put("subjectId", model.getSubjectId());
    map.put("partnerId", model.getPartnerId());
    map.put("severity", model.getSeverity());
    map.put("alertType", model.getAlertType());
    map.put("title", model.getTitle());
    map.put("description", model.getDescription());
    map.put("previousRiskScore", model.getPreviousRiskScore());
    map.put("currentRiskScore", model.getCurrentRiskScore());
    map.put("previousDecision", model.getPreviousDecision());
    map.put("currentDecision", model.getCurrentDecision());
    map.put("acknowledged", model.isAcknowledged());
    map.put("acknowledgedBy", model.getAcknowledgedBy());
    map.put("acknowledgedAt", model.getAcknowledgedAt());
    map.put("createdAt", model.getCreatedAt());
    return map;
  }
}
