package verigate.webbff.deeds.controller;

import java.util.List;
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
import verigate.webbff.deeds.model.DeedsModels;
import verigate.webbff.deeds.service.DeedsPortfolioService;
import verigate.webbff.partner.features.PartnerFeatureAccessService;
import static verigate.webbff.partner.features.PartnerFeatureCatalog.DATA_EXPORT;
import static verigate.webbff.partner.features.PartnerFeatureCatalog.DEEDS_ADMIN;
import static verigate.webbff.partner.features.PartnerFeatureCatalog.DEEDS_CONVERSION;
import static verigate.webbff.partner.features.PartnerFeatureCatalog.DEEDS_MAP;
import static verigate.webbff.partner.features.PartnerFeatureCatalog.DEEDS_REGISTRY;
import static verigate.webbff.partner.features.PartnerFeatureCatalog.DEEDS_VALUATION;
import static verigate.webbff.partner.features.PartnerFeatureCatalog.REPORTING;

@RestController
@RequestMapping("/api/partner/deeds")
public class DeedsController {

  private final DeedsPortfolioService deedsPortfolioService;
  private final PartnerFeatureAccessService featureAccessService;

  public DeedsController(
      DeedsPortfolioService deedsPortfolioService,
      PartnerFeatureAccessService featureAccessService) {
    this.deedsPortfolioService = deedsPortfolioService;
    this.featureAccessService = featureAccessService;
  }

  @PostMapping("/reports/area")
  public DeedsModels.AreaReportResponse generateAreaReport(
      @RequestBody(required = false) DeedsModels.AreaReportRequest request) {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, REPORTING);
    featureAccessService.requireFeature(partnerId, DEEDS_REGISTRY);
    return deedsPortfolioService.generateAreaReport(partnerId, request);
  }

  @PostMapping("/reports/area/export")
  public DeedsModels.ExportResponse exportAreaReport(
      @RequestBody(required = false) DeedsModels.AreaReportRequest request,
      @RequestParam(defaultValue = "csv") String format) {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, REPORTING);
    featureAccessService.requireFeature(partnerId, DATA_EXPORT);
    featureAccessService.requireFeature(partnerId, DEEDS_REGISTRY);
    return deedsPortfolioService.exportAreaReport(partnerId, request, format);
  }

  @GetMapping("/reports/saved")
  public List<DeedsModels.SavedReportResponse> listSavedReports() {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, REPORTING);
    featureAccessService.requireFeature(partnerId, DEEDS_REGISTRY);
    return deedsPortfolioService.listSavedReports(partnerId);
  }

  @PostMapping("/reports/saved")
  public ResponseEntity<DeedsModels.SavedReportResponse> createSavedReport(
      @RequestBody DeedsModels.SavedReportRequest request) {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, REPORTING);
    featureAccessService.requireFeature(partnerId, DEEDS_REGISTRY);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(deedsPortfolioService.createSavedReport(partnerId, request));
  }

  @GetMapping("/reports/saved/{reportId}")
  public DeedsModels.SavedReportResponse getSavedReport(@PathVariable String reportId) {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, REPORTING);
    featureAccessService.requireFeature(partnerId, DEEDS_REGISTRY);
    return deedsPortfolioService.getSavedReport(partnerId, reportId);
  }

  @PatchMapping("/reports/saved/{reportId}")
  public DeedsModels.SavedReportResponse updateSavedReport(
      @PathVariable String reportId,
      @RequestBody DeedsModels.SavedReportUpdateRequest request) {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, REPORTING);
    featureAccessService.requireFeature(partnerId, DEEDS_REGISTRY);
    return deedsPortfolioService.updateSavedReport(partnerId, reportId, request);
  }

  @PostMapping("/reports/saved/{reportId}/refresh")
  public DeedsModels.SavedReportResponse refreshSavedReport(@PathVariable String reportId) {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, REPORTING);
    featureAccessService.requireFeature(partnerId, DEEDS_REGISTRY);
    return deedsPortfolioService.refreshSavedReport(partnerId, reportId);
  }

  @PostMapping("/reports/saved/{reportId}/export")
  public DeedsModels.ExportResponse exportSavedReport(
      @PathVariable String reportId,
      @RequestParam(defaultValue = "csv") String format) {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, REPORTING);
    featureAccessService.requireFeature(partnerId, DATA_EXPORT);
    featureAccessService.requireFeature(partnerId, DEEDS_REGISTRY);
    return deedsPortfolioService.exportSavedReport(partnerId, reportId, format);
  }

  @DeleteMapping("/reports/saved/{reportId}")
  public ResponseEntity<Void> deleteSavedReport(@PathVariable String reportId) {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, REPORTING);
    featureAccessService.requireFeature(partnerId, DEEDS_REGISTRY);
    deedsPortfolioService.deleteSavedReport(partnerId, reportId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/exports")
  public List<DeedsModels.ExportHistoryResponse> listExports() {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, REPORTING);
    featureAccessService.requireFeature(partnerId, DATA_EXPORT);
    return deedsPortfolioService.listExportHistory(partnerId);
  }

  @GetMapping("/team")
  public List<DeedsModels.TeamMemberResponse> listTeamMembers() {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, DEEDS_ADMIN);
    return deedsPortfolioService.listTeamMembers(partnerId);
  }

  @PostMapping("/team")
  public ResponseEntity<DeedsModels.TeamMemberResponse> createTeamMember(
      @RequestBody DeedsModels.TeamMemberRequest request) {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, DEEDS_ADMIN);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(deedsPortfolioService.createTeamMember(partnerId, request));
  }

  @PatchMapping("/team/{memberId}")
  public DeedsModels.TeamMemberResponse updateTeamMember(
      @PathVariable String memberId,
      @RequestBody DeedsModels.TeamMemberRequest request) {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, DEEDS_ADMIN);
    return deedsPortfolioService.updateTeamMember(partnerId, memberId, request);
  }

  @DeleteMapping("/team/{memberId}")
  public ResponseEntity<Void> deleteTeamMember(@PathVariable String memberId) {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, DEEDS_ADMIN);
    deedsPortfolioService.deleteTeamMember(partnerId, memberId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/audit")
  public List<DeedsModels.AuditEventResponse> listAuditEvents() {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, DEEDS_ADMIN);
    return deedsPortfolioService.listAuditEvents(partnerId);
  }

  @GetMapping("/reports/property/{propertyId}")
  public DeedsModels.PropertyReportResponse getPropertyReport(@PathVariable String propertyId) {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, DEEDS_REGISTRY);
    return deedsPortfolioService.getPropertyReport(partnerId, propertyId);
  }

  @PostMapping("/map/search")
  public DeedsModels.MapSearchResponse searchMap(
      @RequestBody(required = false) DeedsModels.MapSearchRequest request) {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, DEEDS_MAP);
    return deedsPortfolioService.searchMap(partnerId, request);
  }

  @PostMapping("/conversion")
  public DeedsModels.ConversionResponse convertPropertyReference(
      @RequestBody DeedsModels.ConversionRequest request) {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, DEEDS_CONVERSION);
    return deedsPortfolioService.convertPropertyReference(partnerId, request);
  }

  @PostMapping("/valuation")
  public DeedsModels.ValuationResponse estimateValue(
      @RequestBody DeedsModels.ValuationRequest request) {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, DEEDS_VALUATION);
    return deedsPortfolioService.estimateValue(partnerId, request);
  }

  @GetMapping("/documents/manifest")
  public DeedsModels.DocumentManifestResponse getDocumentManifest(@RequestParam String propertyId) {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, DEEDS_REGISTRY);
    return deedsPortfolioService.getDocumentManifest(partnerId, propertyId);
  }

  @GetMapping("/watches")
  public List<DeedsModels.WatchResponse> listWatches() {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, DEEDS_REGISTRY);
    return deedsPortfolioService.listWatches(partnerId);
  }

  @PostMapping("/watches")
  public ResponseEntity<DeedsModels.WatchResponse> createWatch(
      @RequestBody DeedsModels.WatchRequest request) {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, DEEDS_REGISTRY);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(deedsPortfolioService.createWatch(partnerId, request));
  }

  @PatchMapping("/watches/{subjectId}")
  public DeedsModels.WatchResponse updateWatch(
      @PathVariable String subjectId,
      @RequestBody DeedsModels.WatchUpdateRequest request) {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, DEEDS_REGISTRY);
    return deedsPortfolioService.updateWatch(partnerId, subjectId, request);
  }

  @DeleteMapping("/watches/{subjectId}")
  public ResponseEntity<Void> deleteWatch(@PathVariable String subjectId) {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, DEEDS_REGISTRY);
    deedsPortfolioService.deleteWatch(partnerId, subjectId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/watches/alerts")
  public List<DeedsModels.WatchAlertResponse> listWatchAlerts() {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, DEEDS_REGISTRY);
    return deedsPortfolioService.listWatchAlerts(partnerId);
  }

  @PostMapping("/operations/refresh")
  public DeedsModels.OperationsRefreshResponse runRefreshCycle() {
    String partnerId = requirePartnerId();
    featureAccessService.requireFeature(partnerId, DEEDS_ADMIN);
    return deedsPortfolioService.runRefreshCycle(partnerId);
  }

  private String requirePartnerId() {
    return PartnerContextHolder.requirePartnerId();
  }
}
