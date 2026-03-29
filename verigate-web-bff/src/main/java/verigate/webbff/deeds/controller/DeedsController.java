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

@RestController
@RequestMapping("/api/partner/deeds")
public class DeedsController {

  private final DeedsPortfolioService deedsPortfolioService;

  public DeedsController(DeedsPortfolioService deedsPortfolioService) {
    this.deedsPortfolioService = deedsPortfolioService;
  }

  @PostMapping("/reports/area")
  public DeedsModels.AreaReportResponse generateAreaReport(
      @RequestBody(required = false) DeedsModels.AreaReportRequest request) {
    return deedsPortfolioService.generateAreaReport(requirePartnerId(), request);
  }

  @PostMapping("/reports/area/export")
  public DeedsModels.ExportResponse exportAreaReport(
      @RequestBody(required = false) DeedsModels.AreaReportRequest request,
      @RequestParam(defaultValue = "csv") String format) {
    return deedsPortfolioService.exportAreaReport(requirePartnerId(), request, format);
  }

  @GetMapping("/reports/saved")
  public List<DeedsModels.SavedReportResponse> listSavedReports() {
    return deedsPortfolioService.listSavedReports(requirePartnerId());
  }

  @PostMapping("/reports/saved")
  public ResponseEntity<DeedsModels.SavedReportResponse> createSavedReport(
      @RequestBody DeedsModels.SavedReportRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(deedsPortfolioService.createSavedReport(requirePartnerId(), request));
  }

  @GetMapping("/reports/saved/{reportId}")
  public DeedsModels.SavedReportResponse getSavedReport(@PathVariable String reportId) {
    return deedsPortfolioService.getSavedReport(requirePartnerId(), reportId);
  }

  @PatchMapping("/reports/saved/{reportId}")
  public DeedsModels.SavedReportResponse updateSavedReport(
      @PathVariable String reportId,
      @RequestBody DeedsModels.SavedReportUpdateRequest request) {
    return deedsPortfolioService.updateSavedReport(requirePartnerId(), reportId, request);
  }

  @PostMapping("/reports/saved/{reportId}/refresh")
  public DeedsModels.SavedReportResponse refreshSavedReport(@PathVariable String reportId) {
    return deedsPortfolioService.refreshSavedReport(requirePartnerId(), reportId);
  }

  @PostMapping("/reports/saved/{reportId}/export")
  public DeedsModels.ExportResponse exportSavedReport(
      @PathVariable String reportId,
      @RequestParam(defaultValue = "csv") String format) {
    return deedsPortfolioService.exportSavedReport(requirePartnerId(), reportId, format);
  }

  @DeleteMapping("/reports/saved/{reportId}")
  public ResponseEntity<Void> deleteSavedReport(@PathVariable String reportId) {
    deedsPortfolioService.deleteSavedReport(requirePartnerId(), reportId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/exports")
  public List<DeedsModels.ExportHistoryResponse> listExports() {
    return deedsPortfolioService.listExportHistory(requirePartnerId());
  }

  @GetMapping("/team")
  public List<DeedsModels.TeamMemberResponse> listTeamMembers() {
    return deedsPortfolioService.listTeamMembers(requirePartnerId());
  }

  @PostMapping("/team")
  public ResponseEntity<DeedsModels.TeamMemberResponse> createTeamMember(
      @RequestBody DeedsModels.TeamMemberRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(deedsPortfolioService.createTeamMember(requirePartnerId(), request));
  }

  @PatchMapping("/team/{memberId}")
  public DeedsModels.TeamMemberResponse updateTeamMember(
      @PathVariable String memberId,
      @RequestBody DeedsModels.TeamMemberRequest request) {
    return deedsPortfolioService.updateTeamMember(requirePartnerId(), memberId, request);
  }

  @DeleteMapping("/team/{memberId}")
  public ResponseEntity<Void> deleteTeamMember(@PathVariable String memberId) {
    deedsPortfolioService.deleteTeamMember(requirePartnerId(), memberId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/audit")
  public List<DeedsModels.AuditEventResponse> listAuditEvents() {
    return deedsPortfolioService.listAuditEvents(requirePartnerId());
  }

  @GetMapping("/reports/property/{propertyId}")
  public DeedsModels.PropertyReportResponse getPropertyReport(@PathVariable String propertyId) {
    return deedsPortfolioService.getPropertyReport(requirePartnerId(), propertyId);
  }

  @PostMapping("/map/search")
  public DeedsModels.MapSearchResponse searchMap(
      @RequestBody(required = false) DeedsModels.MapSearchRequest request) {
    return deedsPortfolioService.searchMap(requirePartnerId(), request);
  }

  @PostMapping("/conversion")
  public DeedsModels.ConversionResponse convertPropertyReference(
      @RequestBody DeedsModels.ConversionRequest request) {
    return deedsPortfolioService.convertPropertyReference(requirePartnerId(), request);
  }

  @PostMapping("/valuation")
  public DeedsModels.ValuationResponse estimateValue(
      @RequestBody DeedsModels.ValuationRequest request) {
    return deedsPortfolioService.estimateValue(requirePartnerId(), request);
  }

  @GetMapping("/documents/manifest")
  public DeedsModels.DocumentManifestResponse getDocumentManifest(@RequestParam String propertyId) {
    return deedsPortfolioService.getDocumentManifest(requirePartnerId(), propertyId);
  }

  @GetMapping("/watches")
  public List<DeedsModels.WatchResponse> listWatches() {
    return deedsPortfolioService.listWatches(requirePartnerId());
  }

  @PostMapping("/watches")
  public ResponseEntity<DeedsModels.WatchResponse> createWatch(
      @RequestBody DeedsModels.WatchRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(deedsPortfolioService.createWatch(requirePartnerId(), request));
  }

  @PatchMapping("/watches/{subjectId}")
  public DeedsModels.WatchResponse updateWatch(
      @PathVariable String subjectId,
      @RequestBody DeedsModels.WatchUpdateRequest request) {
    return deedsPortfolioService.updateWatch(requirePartnerId(), subjectId, request);
  }

  @DeleteMapping("/watches/{subjectId}")
  public ResponseEntity<Void> deleteWatch(@PathVariable String subjectId) {
    deedsPortfolioService.deleteWatch(requirePartnerId(), subjectId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/watches/alerts")
  public List<DeedsModels.WatchAlertResponse> listWatchAlerts() {
    return deedsPortfolioService.listWatchAlerts(requirePartnerId());
  }

  @PostMapping("/operations/refresh")
  public DeedsModels.OperationsRefreshResponse runRefreshCycle() {
    return deedsPortfolioService.runRefreshCycle(requirePartnerId());
  }

  private String requirePartnerId() {
    return PartnerContextHolder.requirePartnerId();
  }
}
