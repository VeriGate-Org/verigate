package verigate.webbff.verification.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import verigate.webbff.verification.repository.DynamoDbMonitoredSubjectRepository;
import verigate.webbff.verification.repository.DynamoDbMonitoringAlertRepository;
import verigate.webbff.verification.repository.model.MonitoredSubjectDataModel;
import verigate.webbff.verification.repository.model.MonitoringAlertDataModel;

@Service
public class MonitoringService {

  private static final Logger logger = LoggerFactory.getLogger(MonitoringService.class);

  private final DynamoDbMonitoredSubjectRepository subjectRepository;
  private final DynamoDbMonitoringAlertRepository alertRepository;

  public MonitoringService(
      DynamoDbMonitoredSubjectRepository subjectRepository,
      DynamoDbMonitoringAlertRepository alertRepository) {
    this.subjectRepository = subjectRepository;
    this.alertRepository = alertRepository;
  }

  public MonitoredSubjectDataModel createSubject(
      String partnerId, String policyId, String subjectType,
      String subjectName, String subjectIdentifier,
      String metadataJson, String monitoringFrequency) {

    String subjectId = UUID.randomUUID().toString();
    String now = Instant.now().toString();
    String nextCheckAt = computeNextCheckAt(now, monitoringFrequency);

    MonitoredSubjectDataModel subject = new MonitoredSubjectDataModel();
    subject.setSubjectId(subjectId);
    subject.setPartnerId(partnerId);
    subject.setPolicyId(policyId);
    subject.setSubjectType(subjectType);
    subject.setSubjectName(subjectName);
    subject.setSubjectIdentifier(subjectIdentifier);
    subject.setMetadataJson(metadataJson);
    subject.setMonitoringFrequency(monitoringFrequency);
    subject.setStatus("ACTIVE");
    subject.setNextCheckAt(nextCheckAt);
    subject.setLastRiskScore(0);
    subject.setStatusNextCheck("ACTIVE#" + nextCheckAt);
    subject.setCreatedAt(now);
    subject.setUpdatedAt(now);

    subjectRepository.save(subject);
    logger.info("Created monitored subject {} for partner {}", subjectId, partnerId);
    return subject;
  }

  public MonitoredSubjectDataModel getSubject(String subjectId, String partnerId) {
    return subjectRepository.findById(subjectId)
        .filter(s -> partnerId.equals(s.getPartnerId()))
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Monitored subject not found"));
  }

  public List<MonitoredSubjectDataModel> listSubjects(
      String partnerId, String status, int limit) {
    String statusPrefix = status != null && !status.isBlank() ? status + "#" : null;
    return subjectRepository.findByPartnerId(partnerId, statusPrefix, limit);
  }

  public MonitoredSubjectDataModel updateSubject(
      String subjectId, String partnerId, Map<String, Object> updates) {
    MonitoredSubjectDataModel existing = getSubject(subjectId, partnerId);
    String now = Instant.now().toString();

    if (updates.containsKey("status")) {
      String newStatus = (String) updates.get("status");
      existing.setStatus(newStatus);

      if ("ACTIVE".equals(newStatus) && "PAUSED".equals(existing.getStatus())) {
        String nextCheckAt = computeNextCheckAt(now, existing.getMonitoringFrequency());
        existing.setNextCheckAt(nextCheckAt);
        existing.setStatusNextCheck(newStatus + "#" + nextCheckAt);
      } else {
        existing.setStatusNextCheck(
            newStatus + "#" + (existing.getNextCheckAt() != null
                ? existing.getNextCheckAt() : now));
      }
    }

    if (updates.containsKey("monitoringFrequency")) {
      String newFrequency = (String) updates.get("monitoringFrequency");
      existing.setMonitoringFrequency(newFrequency);

      if ("ACTIVE".equals(existing.getStatus())) {
        String nextCheckAt = computeNextCheckAt(now, newFrequency);
        existing.setNextCheckAt(nextCheckAt);
        existing.setStatusNextCheck(existing.getStatus() + "#" + nextCheckAt);
      }
    }

    existing.setUpdatedAt(now);
    subjectRepository.save(existing);
    logger.info("Updated monitored subject {} for partner {}", subjectId, partnerId);
    return existing;
  }

  public void deleteSubject(String subjectId, String partnerId) {
    MonitoredSubjectDataModel existing = getSubject(subjectId, partnerId);
    String now = Instant.now().toString();

    existing.setStatus("REMOVED");
    existing.setStatusNextCheck("REMOVED#" + (existing.getNextCheckAt() != null
        ? existing.getNextCheckAt() : now));
    existing.setUpdatedAt(now);

    subjectRepository.save(existing);
    logger.info("Removed monitored subject {} for partner {}", subjectId, partnerId);
  }

  public List<MonitoringAlertDataModel> listAlerts(
      String partnerId, String subjectId, int limit) {
    String subjectIdPrefix = subjectId != null && !subjectId.isBlank()
        ? subjectId + "#" : null;
    return alertRepository.findByPartnerId(partnerId, subjectIdPrefix, limit);
  }

  public MonitoringAlertDataModel acknowledgeAlert(
      String alertId, String partnerId, String acknowledgedBy) {
    MonitoringAlertDataModel alert = alertRepository.findById(alertId)
        .filter(a -> partnerId.equals(a.getPartnerId()))
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Monitoring alert not found"));

    if (alert.isAcknowledged()) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Alert has already been acknowledged");
    }

    String now = Instant.now().toString();
    alert.setAcknowledged(true);
    alert.setAcknowledgedBy(acknowledgedBy);
    alert.setAcknowledgedAt(now);

    alertRepository.save(alert);
    logger.info("Acknowledged alert {} by {} for partner {}", alertId, acknowledgedBy, partnerId);
    return alert;
  }

  private String computeNextCheckAt(String fromTimestamp, String frequency) {
    Instant from = Instant.parse(fromTimestamp);
    return switch (frequency) {
      case "DAILY" -> from.plus(1, ChronoUnit.DAYS).toString();
      case "WEEKLY" -> from.plus(7, ChronoUnit.DAYS).toString();
      case "MONTHLY" -> from.plus(30, ChronoUnit.DAYS).toString();
      case "QUARTERLY" -> from.plus(90, ChronoUnit.DAYS).toString();
      default -> from.plus(7, ChronoUnit.DAYS).toString();
    };
  }
}
