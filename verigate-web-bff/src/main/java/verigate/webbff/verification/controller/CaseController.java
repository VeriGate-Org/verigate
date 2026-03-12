package verigate.webbff.verification.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import verigate.webbff.auth.PartnerContextHolder;
import verigate.webbff.verification.repository.model.CaseDataModel;
import verigate.webbff.verification.service.CaseService;

@RestController
@RequestMapping("/api/partner/cases")
public class CaseController {

    private static final Logger logger = LoggerFactory.getLogger(CaseController.class);

    private final CaseService caseService;
    private final ObjectMapper objectMapper;

    public CaseController(CaseService caseService, ObjectMapper objectMapper) {
        this.caseService = caseService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listCases(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "50") int pageSize) {
        String partnerId = PartnerContextHolder.requirePartnerId();
        logger.info("Listing cases for partner {} (status={}, pageSize={})", partnerId, status, pageSize);

        List<Map<String, Object>> cases = caseService.listCases(partnerId, status, pageSize)
            .stream()
            .map(this::toCaseMap)
            .toList();
        return ResponseEntity.ok(cases);
    }

    @GetMapping("/{caseId}")
    public ResponseEntity<Map<String, Object>> getCase(@PathVariable String caseId) {
        String partnerId = PartnerContextHolder.requirePartnerId();
        logger.info("Fetching case {} for partner {}", caseId, partnerId);

        CaseDataModel caseItem = caseService.getCase(caseId, partnerId);
        return ResponseEntity.ok(toCaseMap(caseItem));
    }

    @PatchMapping("/{caseId}")
    public ResponseEntity<Map<String, Object>> updateCase(
            @PathVariable String caseId,
            @RequestBody Map<String, Object> updates) {
        String partnerId = PartnerContextHolder.requirePartnerId();
        logger.info("Updating case {} for partner {}", caseId, partnerId);

        CaseDataModel updated = caseService.updateCase(caseId, partnerId, updates);
        return ResponseEntity.ok(toCaseMap(updated));
    }

    @PostMapping("/{caseId}/comments")
    public ResponseEntity<Map<String, Object>> addComment(
            @PathVariable String caseId,
            @RequestBody Map<String, String> body) {
        String partnerId = PartnerContextHolder.requirePartnerId();
        String author = body.getOrDefault("author", "unknown");
        String text = body.getOrDefault("text", "");
        logger.info("Adding comment to case {} for partner {}", caseId, partnerId);

        CaseDataModel updated = caseService.addComment(caseId, partnerId, author, text);
        return ResponseEntity.ok(toCaseMap(updated));
    }

    private Map<String, Object> toCaseMap(CaseDataModel model) {
        Map<String, Object> map = new java.util.LinkedHashMap<>();
        map.put("caseId", model.getCaseId());
        map.put("verificationId", model.getVerificationId());
        map.put("workflowId", model.getWorkflowId());
        map.put("partnerId", model.getPartnerId());
        map.put("status", model.getStatus());
        map.put("assignee", model.getAssignee());
        map.put("priority", model.getPriority());
        map.put("decision", model.getDecision());
        map.put("decisionReason", model.getDecisionReason());
        map.put("compositeRiskScore", model.getCompositeRiskScore());
        map.put("riskTier", model.getRiskTier());
        map.put("subjectName", model.getSubjectName());
        map.put("subjectId", model.getSubjectId());
        map.put("comments", fromJson(model.getCommentsJson()));
        map.put("timeline", fromJson(model.getTimelineJson()));
        map.put("createdAt", model.getCreatedAt());
        map.put("updatedAt", model.getUpdatedAt());
        map.put("resolvedAt", model.getResolvedAt());
        return map;
    }

    private Object fromJson(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readValue(json, new TypeReference<Object>() {});
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
