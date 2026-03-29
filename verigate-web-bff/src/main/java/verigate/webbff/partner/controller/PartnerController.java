package verigate.webbff.partner.controller;

import jakarta.validation.Valid;
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
import verigate.webbff.admin.model.ApiKeyListResponse;
import verigate.webbff.admin.model.ApiKeyResponse;
import verigate.webbff.auth.ApiKeyService;
import verigate.webbff.auth.ApiKeyService.GeneratedApiKey;
import verigate.webbff.auth.PartnerContextHolder;
import verigate.webbff.partner.model.BulkActionRequest;
import verigate.webbff.partner.model.BulkActionResponse;
import verigate.webbff.partner.model.NotificationPreferences;
import verigate.webbff.partner.model.PartnerProfileResponse;
import verigate.webbff.partner.model.PartnerProfileUpdateRequest;
import verigate.webbff.partner.model.PolicyListResponse;
import verigate.webbff.partner.model.PolicyRequest;
import verigate.webbff.partner.model.PolicyResponse;
import verigate.webbff.partner.model.ReportListResponse;
import verigate.webbff.partner.model.ReportRequest;
import verigate.webbff.partner.model.ReportResponse;
import verigate.webbff.partner.features.PartnerFeatureAccessService;
import static verigate.webbff.partner.features.PartnerFeatureCatalog.API_KEYS;
import static verigate.webbff.partner.features.PartnerFeatureCatalog.DATA_EXPORT;
import static verigate.webbff.partner.features.PartnerFeatureCatalog.POLICY_BUILDER;
import static verigate.webbff.partner.features.PartnerFeatureCatalog.REPORTING;
import verigate.webbff.partner.service.PartnerFeatureService;

@RestController
@RequestMapping("/api/partner")
public class PartnerController {

  private static final Logger logger = LoggerFactory.getLogger(PartnerController.class);

  private final PartnerFeatureService featureService;
  private final ApiKeyService apiKeyService;
  private final PartnerFeatureAccessService featureAccessService;

  public PartnerController(
      PartnerFeatureService featureService,
      ApiKeyService apiKeyService,
      PartnerFeatureAccessService featureAccessService) {
    this.featureService = featureService;
    this.apiKeyService = apiKeyService;
    this.featureAccessService = featureAccessService;
  }

  private String requirePartnerId() {
    return PartnerContextHolder.requirePartnerId();
  }

  // ── Policies ──────────────────────────────────────────────────────

  @GetMapping("/policies")
  public PolicyListResponse listPolicies() {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, POLICY_BUILDER);
    return featureService.listPolicies(partnerId);
  }

  @PostMapping("/policies")
  public ResponseEntity<PolicyResponse> createPolicy(@Valid @RequestBody PolicyRequest request) {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, POLICY_BUILDER);
    logger.info("Creating policy: partnerId={}, name={}", partnerId, request.name());
    var response = featureService.savePolicy(partnerId, request, null);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/policies/{policyId}")
  public PolicyResponse getPolicy(@PathVariable String policyId) {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, POLICY_BUILDER);
    try {
      return featureService.getPolicy(partnerId, policyId);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @PutMapping("/policies/{policyId}")
  public PolicyResponse updatePolicy(
      @PathVariable String policyId,
      @Valid @RequestBody PolicyRequest request) {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, POLICY_BUILDER);
    logger.info("Updating policy: partnerId={}, policyId={}", partnerId, policyId);
    return featureService.savePolicy(partnerId, request, policyId);
  }

  @DeleteMapping("/policies/{policyId}")
  public ResponseEntity<Void> deletePolicy(@PathVariable String policyId) {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, POLICY_BUILDER);
    featureService.deletePolicy(partnerId, policyId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/policies/{policyId}/publish")
  public PolicyResponse publishPolicy(@PathVariable String policyId) {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, POLICY_BUILDER);
    try {
      return featureService.publishPolicy(partnerId, policyId);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  // ── Reports ───────────────────────────────────────────────────────

  @GetMapping("/reports")
  public ReportListResponse listReports() {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, REPORTING);
    return featureService.listReports(partnerId);
  }

  @PostMapping("/reports/generate")
  public ResponseEntity<ReportResponse> generateReport(@Valid @RequestBody ReportRequest request) {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, REPORTING);
    logger.info("Generating report: partnerId={}, type={}", partnerId, request.type());
    var response = featureService.generateReport(partnerId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/reports/{reportId}/schedule")
  public ReportResponse scheduleReport(
      @PathVariable String reportId,
      @Valid @RequestBody ReportRequest.ScheduleConfig schedule) {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, REPORTING);
    try {
      return featureService.scheduleReport(partnerId, reportId, schedule);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @DeleteMapping("/reports/{reportId}")
  public ResponseEntity<Void> deleteReport(@PathVariable String reportId) {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, REPORTING);
    featureService.deleteReport(partnerId, reportId);
    return ResponseEntity.noContent().build();
  }

  // ── Profile ───────────────────────────────────────────────────────

  @GetMapping("/profile")
  public PartnerProfileResponse getProfile() {
    return featureService.getProfile(requirePartnerId());
  }

  @PutMapping("/profile")
  public PartnerProfileResponse updateProfile(@RequestBody PartnerProfileUpdateRequest request) {
    if (request.billingPlan() != null || request.enabledFeatures() != null) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          "Billing plan and feature entitlements are managed by platform administrators");
    }
    return featureService.updateProfile(requirePartnerId(), request);
  }

  // ── API Keys (partner-scoped) ─────────────────────────────────────

  @GetMapping("/api-keys")
  public ApiKeyListResponse listApiKeys() {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, API_KEYS);
    var records = apiKeyService.listApiKeys(partnerId);
    var items = records.stream()
        .map(r -> new ApiKeyListResponse.ApiKeyItem(
            r.keyPrefix(),
            r.status(),
            r.createdAt() != null ? r.createdAt().toString() : null,
            r.createdBy()))
        .toList();
    return new ApiKeyListResponse(partnerId, items);
  }

  @PostMapping("/api-keys")
  public ResponseEntity<ApiKeyResponse> generateApiKey() {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, API_KEYS);
    logger.info("Generating API key for partner: {}", partnerId);
    GeneratedApiKey generated = apiKeyService.generateApiKey(partnerId, "partner-portal");
    var record = generated.record();
    var response = new ApiKeyResponse(
        generated.rawApiKey(),
        record.partnerId(),
        record.keyPrefix(),
        record.status(),
        record.createdAt());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @DeleteMapping("/api-keys/{keyPrefix}")
  public ResponseEntity<Void> revokeApiKey(@PathVariable String keyPrefix) {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, API_KEYS);
    logger.info("Revoking API key: partner={}, prefix={}", partnerId, keyPrefix);
    var records = apiKeyService.listApiKeys(partnerId);
    var match = records.stream()
        .filter(r -> keyPrefix.equals(r.keyPrefix()))
        .findFirst();
    if (match.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    apiKeyService.revokeApiKey(match.get().lookupHash());
    return ResponseEntity.noContent().build();
  }

  // ── Notifications ─────────────────────────────────────────────────

  @GetMapping("/notifications")
  public NotificationPreferences getNotifications() {
    return featureService.getNotifications(requirePartnerId());
  }

  @PutMapping("/notifications")
  public NotificationPreferences updateNotifications(@RequestBody NotificationPreferences prefs) {
    return featureService.updateNotifications(requirePartnerId(), prefs);
  }

  // ── Bulk Verification Operations ──────────────────────────────────

  @PostMapping("/verifications/export")
  public BulkActionResponse exportVerifications(@Valid @RequestBody BulkActionRequest request) {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, DATA_EXPORT);
    return featureService.exportVerifications(partnerId, request.commandIds(), request.format());
  }

  @PostMapping("/verifications/retry")
  public BulkActionResponse retryVerifications(@Valid @RequestBody BulkActionRequest request) {
    return featureService.retryVerifications(requirePartnerId(), request.commandIds());
  }

  @PostMapping("/verifications/archive")
  public BulkActionResponse archiveVerifications(@Valid @RequestBody BulkActionRequest request) {
    return featureService.archiveVerifications(requirePartnerId(), request.commandIds());
  }

  @DeleteMapping("/verifications")
  public BulkActionResponse deleteVerifications(@Valid @RequestBody BulkActionRequest request) {
    return featureService.deleteVerifications(requirePartnerId(), request.commandIds());
  }
}
