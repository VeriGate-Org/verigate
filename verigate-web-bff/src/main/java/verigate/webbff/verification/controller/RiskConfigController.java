package verigate.webbff.verification.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import verigate.webbff.auth.PartnerContextHolder;
import verigate.webbff.verification.repository.DynamoDbPolicyRepository;
import verigate.webbff.verification.repository.DynamoDbRiskConfigRepository;
import verigate.webbff.verification.repository.model.PolicyDataModel;
import verigate.webbff.verification.repository.model.RiskScoringConfigDataModel;

/**
 * REST controller for partner risk scoring configuration and policy management.
 */
@RestController
@RequestMapping("/api/partner")
public class RiskConfigController {

    private static final Logger logger = LoggerFactory.getLogger(RiskConfigController.class);

    private final DynamoDbPolicyRepository policyRepository;
    private final DynamoDbRiskConfigRepository riskConfigRepository;
    private final ObjectMapper objectMapper;

    public RiskConfigController(
            DynamoDbPolicyRepository policyRepository,
            DynamoDbRiskConfigRepository riskConfigRepository,
            ObjectMapper objectMapper) {
        this.policyRepository = policyRepository;
        this.riskConfigRepository = riskConfigRepository;
        this.objectMapper = objectMapper;
    }

    // --- Risk Scoring Config ---

    @GetMapping("/risk-config")
    public ResponseEntity<Map<String, Object>> getRiskConfig() {
        String partnerId = PartnerContextHolder.requirePartnerId();
        logger.info("Fetching risk scoring config for partner {}", partnerId);

        return riskConfigRepository.findByPartnerId(partnerId)
            .map(this::toRiskConfigMap)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.ok(getDefaultRiskConfig()));
    }

    @PutMapping("/risk-config")
    public ResponseEntity<Map<String, Object>> updateRiskConfig(
            @RequestBody Map<String, Object> config) {
        String partnerId = PartnerContextHolder.requirePartnerId();
        logger.info("Updating risk scoring config for partner {}", partnerId);

        RiskScoringConfigDataModel model = new RiskScoringConfigDataModel();
        model.setPartnerId(partnerId);
        model.setStrategy((String) config.getOrDefault("strategy", "WEIGHTED_AVERAGE"));
        model.setWeightsJson(toJson(config.get("weights")));
        model.setTiersJson(toJson(config.get("tiers")));
        model.setOverrideRulesJson(toJson(config.get("overrideRules")));
        model.setUpdatedAt(Instant.now().toString());

        riskConfigRepository.save(model);
        return ResponseEntity.ok(config);
    }

    @GetMapping("/risk-config/default")
    public ResponseEntity<Map<String, Object>> getDefaultRiskConfigEndpoint() {
        return ResponseEntity.ok(getDefaultRiskConfig());
    }

    // --- Policies ---

    @GetMapping("/policies")
    public ResponseEntity<List<Map<String, Object>>> listPolicies() {
        String partnerId = PartnerContextHolder.requirePartnerId();
        logger.info("Listing policies for partner {}", partnerId);

        List<Map<String, Object>> policies = policyRepository.findByPartnerId(partnerId).stream()
            .map(this::toPolicyMap)
            .toList();
        return ResponseEntity.ok(policies);
    }

    @PostMapping("/policies")
    public ResponseEntity<Map<String, Object>> createPolicy(
            @RequestBody Map<String, Object> policy) {
        String partnerId = PartnerContextHolder.requirePartnerId();
        String policyId = UUID.randomUUID().toString();
        String now = Instant.now().toString();
        logger.info("Creating policy {} for partner {}", policyId, partnerId);

        PolicyDataModel model = new PolicyDataModel();
        model.setPartnerPolicyId(policyId);
        model.setPartnerId(partnerId);
        model.setName((String) policy.getOrDefault("name", ""));
        model.setDescription((String) policy.getOrDefault("description", ""));
        model.setStepsJson(toJson(policy.get("steps")));
        model.setScoringConfigJson(toJson(policy.get("scoringConfig")));
        model.setStatus("DRAFT");
        model.setVersion(1);
        model.setCreatedAt(now);
        model.setUpdatedAt(now);

        policyRepository.save(model);

        return ResponseEntity.status(HttpStatus.CREATED).body(toPolicyMap(model));
    }

    @GetMapping("/policies/{policyId}")
    public ResponseEntity<Map<String, Object>> getPolicy(@PathVariable String policyId) {
        String partnerId = PartnerContextHolder.requirePartnerId();
        logger.info("Fetching policy {} for partner {}", policyId, partnerId);

        PolicyDataModel policy = policyRepository.findById(policyId)
            .filter(p -> partnerId.equals(p.getPartnerId()))
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Policy not found"));

        return ResponseEntity.ok(toPolicyMap(policy));
    }

    @PutMapping("/policies/{policyId}")
    public ResponseEntity<Map<String, Object>> updatePolicy(
            @PathVariable String policyId,
            @RequestBody Map<String, Object> policy) {
        String partnerId = PartnerContextHolder.requirePartnerId();
        logger.info("Updating policy {} for partner {}", policyId, partnerId);

        PolicyDataModel existing = policyRepository.findById(policyId)
            .filter(p -> partnerId.equals(p.getPartnerId()))
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Policy not found"));

        if (!"DRAFT".equals(existing.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Only DRAFT policies can be updated");
        }

        existing.setName((String) policy.getOrDefault("name", existing.getName()));
        existing.setDescription(
            (String) policy.getOrDefault("description", existing.getDescription()));
        if (policy.containsKey("steps")) {
            existing.setStepsJson(toJson(policy.get("steps")));
        }
        if (policy.containsKey("scoringConfig")) {
            existing.setScoringConfigJson(toJson(policy.get("scoringConfig")));
        }
        existing.setUpdatedAt(Instant.now().toString());

        policyRepository.save(existing);
        return ResponseEntity.ok(toPolicyMap(existing));
    }

    @PostMapping("/policies/{policyId}/publish")
    public ResponseEntity<Map<String, Object>> publishPolicy(@PathVariable String policyId) {
        String partnerId = PartnerContextHolder.requirePartnerId();
        logger.info("Publishing policy {} for partner {}", policyId, partnerId);

        PolicyDataModel existing = policyRepository.findById(policyId)
            .filter(p -> partnerId.equals(p.getPartnerId()))
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Policy not found"));

        if (!"DRAFT".equals(existing.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Only DRAFT policies can be published");
        }

        existing.setStatus("PUBLISHED");
        existing.setUpdatedAt(Instant.now().toString());
        policyRepository.save(existing);

        return ResponseEntity.ok(toPolicyMap(existing));
    }

    @DeleteMapping("/policies/{policyId}")
    public ResponseEntity<Void> deletePolicy(@PathVariable String policyId) {
        String partnerId = PartnerContextHolder.requirePartnerId();
        logger.info("Deleting policy {} for partner {}", policyId, partnerId);

        PolicyDataModel existing = policyRepository.findById(policyId)
            .filter(p -> partnerId.equals(p.getPartnerId()))
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Policy not found"));

        if (!"DRAFT".equals(existing.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Only DRAFT policies can be deleted");
        }

        policyRepository.delete(policyId);
        return ResponseEntity.noContent().build();
    }

    // --- Helpers ---

    private Map<String, Object> toPolicyMap(PolicyDataModel model) {
        Map<String, Object> map = new java.util.LinkedHashMap<>();
        map.put("policyId", model.getPartnerPolicyId());
        map.put("partnerId", model.getPartnerId());
        map.put("name", model.getName());
        map.put("description", model.getDescription());
        map.put("steps", fromJson(model.getStepsJson()));
        map.put("scoringConfig", fromJson(model.getScoringConfigJson()));
        map.put("status", model.getStatus());
        map.put("version", model.getVersion());
        map.put("createdAt", model.getCreatedAt());
        map.put("updatedAt", model.getUpdatedAt());
        return map;
    }

    private Map<String, Object> toRiskConfigMap(RiskScoringConfigDataModel model) {
        Map<String, Object> map = new java.util.LinkedHashMap<>();
        map.put("strategy", model.getStrategy());
        map.put("weights", fromJson(model.getWeightsJson()));
        map.put("tiers", fromJson(model.getTiersJson()));
        map.put("overrideRules", fromJson(model.getOverrideRulesJson()));
        return map;
    }

    private String toJson(Object value) {
        if (value == null) return "{}";
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            logger.warn("Failed to serialize to JSON", e);
            return "{}";
        }
    }

    private Object fromJson(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readValue(json, new TypeReference<Object>() {});
        } catch (JsonProcessingException e) {
            logger.warn("Failed to deserialize JSON", e);
            return null;
        }
    }

    private Map<String, Object> getDefaultRiskConfig() {
        return Map.of(
            "strategy", "WEIGHTED_AVERAGE",
            "weights", Map.of(),
            "tiers", List.of(
                Map.of("name", "HIGH_RISK", "lowerBound", 0, "upperBound", 50, "decision", "REJECT"),
                Map.of("name", "MEDIUM_RISK", "lowerBound", 50, "upperBound", 80, "decision", "MANUAL_REVIEW"),
                Map.of("name", "LOW_RISK", "lowerBound", 80, "upperBound", 101, "decision", "APPROVE")
            ),
            "overrideRules", List.of()
        );
    }
}
