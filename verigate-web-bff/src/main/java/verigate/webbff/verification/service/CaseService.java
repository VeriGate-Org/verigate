package verigate.webbff.verification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import verigate.webbff.verification.repository.DynamoDbCaseRepository;
import verigate.webbff.verification.repository.model.CaseDataModel;

@Service
public class CaseService {

  private static final Logger logger = LoggerFactory.getLogger(CaseService.class);

  private final DynamoDbCaseRepository caseRepository;
  private final ObjectMapper objectMapper;

  public CaseService(DynamoDbCaseRepository caseRepository, ObjectMapper objectMapper) {
    this.caseRepository = caseRepository;
    this.objectMapper = objectMapper;
  }

  public CaseDataModel createCase(String partnerId, String verificationId, String workflowId,
      int compositeRiskScore, String riskTier, String subjectName, String subjectId) {
    String caseId = UUID.randomUUID().toString();
    String now = Instant.now().toString();

    CaseDataModel caseItem = new CaseDataModel();
    caseItem.setCaseId(caseId);
    caseItem.setPartnerId(partnerId);
    caseItem.setVerificationId(verificationId);
    caseItem.setWorkflowId(workflowId);
    caseItem.setStatus("OPEN");
    caseItem.setPriority("MEDIUM");
    caseItem.setCompositeRiskScore(compositeRiskScore);
    caseItem.setRiskTier(riskTier);
    caseItem.setSubjectName(subjectName);
    caseItem.setSubjectId(subjectId);
    caseItem.setStatusCreatedAt("OPEN#" + now);
    caseItem.setCommentsJson("[]");
    caseItem.setTimelineJson(toJson(List.of(
        Map.of("event", "CASE_CREATED", "timestamp", now, "actor", "system"))));
    caseItem.setCreatedAt(now);
    caseItem.setUpdatedAt(now);

    caseRepository.save(caseItem);
    logger.info("Created case {} for verification {} (partner {})", caseId, verificationId, partnerId);
    return caseItem;
  }

  public CaseDataModel getCase(String caseId, String partnerId) {
    return caseRepository.findById(caseId)
        .filter(c -> partnerId.equals(c.getPartnerId()))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Case not found"));
  }

  public List<CaseDataModel> listCases(String partnerId, String status, int limit) {
    String statusPrefix = status != null && !status.isBlank() ? status + "#" : null;
    return caseRepository.findByPartnerId(partnerId, statusPrefix, limit);
  }

  public CaseDataModel updateCase(String caseId, String partnerId, Map<String, Object> updates) {
    CaseDataModel existing = getCase(caseId, partnerId);
    String now = Instant.now().toString();

    if (updates.containsKey("assignee")) {
      existing.setAssignee((String) updates.get("assignee"));
      addTimelineEntry(existing, "ASSIGNED", now, (String) updates.get("assignee"));
    }

    if (updates.containsKey("status")) {
      String newStatus = (String) updates.get("status");
      existing.setStatus(newStatus);
      existing.setStatusCreatedAt(newStatus + "#" + existing.getCreatedAt());
      addTimelineEntry(existing, "STATUS_CHANGED", now, "system");

      if ("RESOLVED".equals(newStatus)) {
        existing.setResolvedAt(now);
      }
    }

    if (updates.containsKey("decision")) {
      existing.setDecision((String) updates.get("decision"));
      existing.setDecisionReason((String) updates.getOrDefault("decisionReason", ""));
      addTimelineEntry(existing, "DECISION_MADE", now, "system");
    }

    if (updates.containsKey("priority")) {
      existing.setPriority((String) updates.get("priority"));
    }

    existing.setUpdatedAt(now);
    caseRepository.save(existing);
    return existing;
  }

  public CaseDataModel addComment(String caseId, String partnerId, String author, String text) {
    CaseDataModel existing = getCase(caseId, partnerId);
    String now = Instant.now().toString();

    List<Map<String, String>> comments = parseComments(existing.getCommentsJson());
    comments.add(Map.of(
        "id", UUID.randomUUID().toString(),
        "author", author,
        "text", text,
        "createdAt", now));
    existing.setCommentsJson(toJson(comments));

    addTimelineEntry(existing, "COMMENT_ADDED", now, author);
    existing.setUpdatedAt(now);
    caseRepository.save(existing);
    return existing;
  }

  private void addTimelineEntry(CaseDataModel caseItem, String event, String timestamp, String actor) {
    List<Map<String, String>> timeline = parseTimeline(caseItem.getTimelineJson());
    timeline.add(Map.of("event", event, "timestamp", timestamp, "actor", actor));
    caseItem.setTimelineJson(toJson(timeline));
  }

  @SuppressWarnings("unchecked")
  private List<Map<String, String>> parseComments(String json) {
    if (json == null || json.isBlank()) return new ArrayList<>();
    try {
      return new ArrayList<>(objectMapper.readValue(json, new TypeReference<List<Map<String, String>>>() {}));
    } catch (JsonProcessingException e) {
      return new ArrayList<>();
    }
  }

  @SuppressWarnings("unchecked")
  private List<Map<String, String>> parseTimeline(String json) {
    if (json == null || json.isBlank()) return new ArrayList<>();
    try {
      return new ArrayList<>(objectMapper.readValue(json, new TypeReference<List<Map<String, String>>>() {}));
    } catch (JsonProcessingException e) {
      return new ArrayList<>();
    }
  }

  private String toJson(Object value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      return "[]";
    }
  }
}
