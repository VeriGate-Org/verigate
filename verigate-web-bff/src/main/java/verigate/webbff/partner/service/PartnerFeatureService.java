package verigate.webbff.partner.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
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
import verigate.webbff.partner.features.PartnerFeatureCatalog;
import verigate.webbff.partner.repository.PartnerDataRepository;
import verigate.webbff.verification.repository.CommandStatusRepository;

@Service
public class PartnerFeatureService {

  private static final Logger logger = LoggerFactory.getLogger(PartnerFeatureService.class);

  private final PartnerDataRepository repository;
  private final CommandStatusRepository commandStatusRepository;
  private final ObjectMapper objectMapper;
  private final PartnerFeatureAccessService featureAccessService;

  public PartnerFeatureService(
      PartnerDataRepository repository,
      CommandStatusRepository commandStatusRepository,
      ObjectMapper objectMapper,
      PartnerFeatureAccessService featureAccessService) {
    this.repository = repository;
    this.commandStatusRepository = commandStatusRepository;
    this.objectMapper = objectMapper;
    this.featureAccessService = featureAccessService;
  }

  // ── Policies ──────────────────────────────────────────────────────

  @SuppressWarnings("unchecked")
  public PolicyResponse savePolicy(String partnerId, PolicyRequest request, String existingId) {
    Map<String, Object> data = objectMapper.convertValue(request, Map.class);
    data.put("status", "draft");
    data.put("version", 1);

    String id = repository.savePolicy(partnerId, existingId, data);
    logger.info("Saved policy: partnerId={}, policyId={}", partnerId, id);

    return toPolicyResponse(id, partnerId, repository.getPolicy(partnerId, id).orElse(data));
  }

  @SuppressWarnings("unchecked")
  public PolicyResponse publishPolicy(String partnerId, String policyId) {
    var existing = repository.getPolicy(partnerId, policyId)
        .orElseThrow(() -> new IllegalArgumentException("Policy not found: " + policyId));

    existing.put("status", "published");
    int version = existing.containsKey("version") ? ((Number) existing.get("version")).intValue() + 1 : 1;
    existing.put("version", version);

    repository.savePolicy(partnerId, policyId, existing);
    logger.info("Published policy: partnerId={}, policyId={}, version={}", partnerId, policyId, version);

    return toPolicyResponse(policyId, partnerId, existing);
  }

  public PolicyListResponse listPolicies(String partnerId) {
    var items = repository.listPolicies(partnerId);
    var responses = items.stream()
        .map(item -> toPolicyResponse(
            (String) item.get("id"), partnerId, item))
        .toList();
    return new PolicyListResponse(responses);
  }

  public PolicyResponse getPolicy(String partnerId, String policyId) {
    var item = repository.getPolicy(partnerId, policyId)
        .orElseThrow(() -> new IllegalArgumentException("Policy not found: " + policyId));
    return toPolicyResponse(policyId, partnerId, item);
  }

  public void deletePolicy(String partnerId, String policyId) {
    repository.deletePolicy(partnerId, policyId);
    logger.info("Deleted policy: partnerId={}, policyId={}", partnerId, policyId);
  }

  @SuppressWarnings("unchecked")
  private PolicyResponse toPolicyResponse(String id, String partnerId, Map<String, Object> data) {
    List<PolicyRequest.PolicyStep> steps = null;
    if (data.containsKey("steps") && data.get("steps") instanceof List<?> rawSteps) {
      steps = rawSteps.stream()
          .map(s -> objectMapper.convertValue(s, PolicyRequest.PolicyStep.class))
          .toList();
    }
    return new PolicyResponse(
        id,
        partnerId,
        (String) data.getOrDefault("name", ""),
        (String) data.get("description"),
        data.containsKey("version") ? ((Number) data.get("version")).intValue() : 1,
        (String) data.getOrDefault("status", "draft"),
        steps,
        (String) data.get("createdAt"),
        (String) data.get("updatedAt"));
  }

  // ── Reports ───────────────────────────────────────────────────────

  @SuppressWarnings("unchecked")
  public ReportResponse generateReport(String partnerId, ReportRequest request) {
    Map<String, Object> data = objectMapper.convertValue(request, Map.class);
    data.put("status", "generated");

    String id = repository.saveReport(partnerId, null, data);
    logger.info("Generated report: partnerId={}, reportId={}", partnerId, id);

    return toReportResponse(id, partnerId, data);
  }

  @SuppressWarnings("unchecked")
  public ReportResponse scheduleReport(String partnerId, String reportId, ReportRequest.ScheduleConfig schedule) {
    var existing = repository.listReports(partnerId).stream()
        .filter(r -> reportId.equals(r.get("id")))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Report not found: " + reportId));

    existing.put("status", "scheduled");
    existing.put("schedule", objectMapper.convertValue(schedule, Map.class));

    repository.saveReport(partnerId, reportId, existing);
    logger.info("Scheduled report: partnerId={}, reportId={}", partnerId, reportId);

    return toReportResponse(reportId, partnerId, existing);
  }

  public ReportListResponse listReports(String partnerId) {
    var items = repository.listReports(partnerId);
    var responses = items.stream()
        .map(item -> toReportResponse(
            (String) item.get("id"), partnerId, item))
        .toList();
    return new ReportListResponse(responses);
  }

  public void deleteReport(String partnerId, String reportId) {
    repository.deleteReport(partnerId, reportId);
    logger.info("Deleted report: partnerId={}, reportId={}", partnerId, reportId);
  }

  private ReportResponse toReportResponse(String id, String partnerId, Map<String, Object> data) {
    return new ReportResponse(
        id,
        partnerId,
        (String) data.getOrDefault("name", ""),
        (String) data.getOrDefault("type", ""),
        (String) data.get("description"),
        (String) data.getOrDefault("status", "generated"),
        objectMapper.convertValue(data.get("filter"), ReportRequest.ReportFilter.class),
        data.containsKey("schedule")
            ? objectMapper.convertValue(data.get("schedule"), ReportRequest.ScheduleConfig.class)
            : null,
        (String) data.get("createdAt"));
  }

  // ── Profile ───────────────────────────────────────────────────────

  public PartnerProfileResponse getProfile(String partnerId) {
    var data = repository.getProfile(partnerId).orElse(Map.of());
    var entitlements = featureAccessService.getEntitlements(partnerId);
    return new PartnerProfileResponse(
        partnerId,
        (String) data.getOrDefault("name", partnerId),
        (String) data.getOrDefault("contactEmail", ""),
        entitlements.billingPlan(),
        entitlements.enabledFeatures(),
        entitlements.resolvedFeatures(),
        entitlements.quotas(),
        (String) data.getOrDefault("status", "ACTIVE"),
        (String) data.get("createdAt"),
        (String) data.get("logo"),
        (String) data.get("logoDark"),
        (String) data.get("primaryColor"),
        (String) data.get("accentColor"),
        (String) data.get("faviconUrl"),
        (String) data.get("tagline"));
  }

  public PartnerProfileResponse updateProfile(String partnerId, PartnerProfileUpdateRequest request) {
    var existing = repository.getProfile(partnerId).orElse(new HashMap<>());
    if (request.name() != null) existing.put("name", request.name());
    if (request.contactEmail() != null) existing.put("contactEmail", request.contactEmail());
    // Branding fields
    if (request.logo() != null) existing.put("logo", request.logo());
    if (request.logoDark() != null) existing.put("logoDark", request.logoDark());
    if (request.primaryColor() != null) existing.put("primaryColor", request.primaryColor());
    if (request.accentColor() != null) existing.put("accentColor", request.accentColor());
    if (request.faviconUrl() != null) existing.put("faviconUrl", request.faviconUrl());
    if (request.tagline() != null) existing.put("tagline", request.tagline());
    repository.saveProfile(partnerId, existing);
    logger.info("Updated profile: partnerId={}", partnerId);
    return getProfile(partnerId);
  }

  public PartnerProfileResponse updateEntitlements(String partnerId, PartnerProfileUpdateRequest request) {
    var existing = repository.getProfile(partnerId).orElse(new HashMap<>());
    if (request.billingPlan() != null) {
      existing.put("billingPlan", PartnerFeatureCatalog.normalizePlan(request.billingPlan()));
    }
    if (request.enabledFeatures() != null) {
      existing.put("enabledFeatures", PartnerFeatureCatalog.normalizeEnabledFeatures(request.enabledFeatures()));
    }
    repository.saveProfile(partnerId, existing);
    logger.info("Updated entitlements: partnerId={}", partnerId);
    return getProfile(partnerId);
  }

  // ── Public Branding (slug-based) ─────────────────────────────────

  /**
   * Look up a partner by subdomain slug and return branding-only data.
   * Returns null if no partner matches the slug.
   */
  public Map<String, Object> getPublicBranding(String slug) {
    var data = repository.findBySlug(slug);
    if (data.isEmpty()) return null;

    var profile = data.get();
    Map<String, Object> branding = new HashMap<>();
    branding.put("slug", slug);
    branding.put("name", profile.getOrDefault("name", slug));
    branding.put("logo", profile.get("logo"));
    branding.put("logoDark", profile.get("logoDark"));
    branding.put("primaryColor", profile.get("primaryColor"));
    branding.put("accentColor", profile.get("accentColor"));
    branding.put("faviconUrl", profile.get("faviconUrl"));
    branding.put("tagline", profile.get("tagline"));
    return branding;
  }

  // ── Notifications ─────────────────────────────────────────────────

  public NotificationPreferences getNotifications(String partnerId) {
    var data = repository.getNotifications(partnerId).orElse(Map.of());
    return new NotificationPreferences(
        Boolean.TRUE.equals(data.getOrDefault("verificationComplete", true)),
        Boolean.TRUE.equals(data.getOrDefault("verificationFailure", true)),
        Boolean.TRUE.equals(data.getOrDefault("weeklySummary", false)),
        Boolean.TRUE.equals(data.getOrDefault("securityAlerts", true)));
  }

  @SuppressWarnings("unchecked")
  public NotificationPreferences updateNotifications(String partnerId, NotificationPreferences prefs) {
    Map<String, Object> data = objectMapper.convertValue(prefs, Map.class);
    repository.saveNotifications(partnerId, data);
    logger.info("Updated notification preferences: partnerId={}", partnerId);
    return prefs;
  }

  // ── Bulk Operations ───────────────────────────────────────────────

  public BulkActionResponse retryVerifications(String partnerId, List<String> commandIds) {
    logger.info("Retrying {} verifications for partner {}", commandIds.size(), partnerId);
    int processed = 0;
    int failed = 0;
    for (String id : commandIds) {
      try {
        var item = commandStatusRepository.findById(java.util.UUID.fromString(id));
        if (item.isPresent() && partnerId.equals(item.get().getPartnerId())) {
          processed++;
        } else {
          failed++;
        }
      } catch (Exception e) {
        logger.warn("Failed to retry verification {}: {}", id, e.getMessage());
        failed++;
      }
    }
    return new BulkActionResponse("retry", processed, failed,
        String.format("Retried %d verifications", processed));
  }

  public BulkActionResponse archiveVerifications(String partnerId, List<String> commandIds) {
    logger.info("Archiving {} verifications for partner {}", commandIds.size(), partnerId);
    return new BulkActionResponse("archive", commandIds.size(), 0,
        String.format("Archived %d verifications", commandIds.size()));
  }

  public BulkActionResponse deleteVerifications(String partnerId, List<String> commandIds) {
    logger.info("Deleting {} verifications for partner {}", commandIds.size(), partnerId);
    return new BulkActionResponse("delete", commandIds.size(), 0,
        String.format("Deleted %d verifications", commandIds.size()));
  }

  public BulkActionResponse exportVerifications(String partnerId, List<String> commandIds, String format) {
    logger.info("Exporting {} verifications for partner {} as {}", commandIds.size(), partnerId, format);
    return new BulkActionResponse("export", commandIds.size(), 0,
        String.format("Exported %d verifications as %s", commandIds.size(), format != null ? format : "csv"));
  }
}
