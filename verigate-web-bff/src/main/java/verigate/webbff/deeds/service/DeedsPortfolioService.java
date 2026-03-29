package verigate.webbff.deeds.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import verigate.webbff.deeds.model.DeedsModels;
import verigate.webbff.partner.repository.PartnerDataRepository;
import verigate.webbff.verification.model.CommandStatus;
import verigate.webbff.verification.repository.CommandStatusRepository;
import verigate.webbff.verification.repository.model.MonitoredSubjectDataModel;
import verigate.webbff.verification.repository.model.VerificationCommandStoreItem;
import verigate.webbff.verification.service.MonitoringService;

@Service
public class DeedsPortfolioService {

  private static final Logger logger = LoggerFactory.getLogger(DeedsPortfolioService.class);
  private static final int DEFAULT_HISTORY_LIMIT = 200;
  private static final List<String> DEFAULT_ALERT_TYPES =
      List.of("TRANSFER", "BOND_CHANGE", "MUNICIPAL_CHANGE", "PORTION_CHANGE");
  private static final String DEEDS_REPORT_PREFIX = "DEEDS_REPORT#";
  private static final String DEEDS_EXPORT_PREFIX = "DEEDS_EXPORT#";
  private static final String DEEDS_TEAM_PREFIX = "DEEDS_TEAM#";
  private static final String DEEDS_AUDIT_PREFIX = "DEEDS_AUDIT#";

  private final CommandStatusRepository commandStatusRepository;
  private final MonitoringService monitoringService;
  private final PartnerDataRepository partnerDataRepository;
  private final ObjectMapper objectMapper;
  private final Counter reportsRefreshedCounter;
  private final Counter watchesRecalculatedCounter;
  private final Counter exportsCreatedCounter;

  public DeedsPortfolioService(
      CommandStatusRepository commandStatusRepository,
      MonitoringService monitoringService,
      PartnerDataRepository partnerDataRepository,
      ObjectMapper objectMapper,
      MeterRegistry meterRegistry) {
    this.commandStatusRepository = commandStatusRepository;
    this.monitoringService = monitoringService;
    this.partnerDataRepository = partnerDataRepository;
    this.objectMapper = objectMapper;
    this.reportsRefreshedCounter = Counter.builder("deeds.reports.refreshed")
        .description("Number of deeds reports refreshed")
        .register(meterRegistry);
    this.watchesRecalculatedCounter = Counter.builder("deeds.watches.recalculated")
        .description("Number of deeds watches recalculated")
        .register(meterRegistry);
    this.exportsCreatedCounter = Counter.builder("deeds.exports.created")
        .description("Number of deeds exports created")
        .register(meterRegistry);
  }

  public DeedsModels.AreaReportResponse generateAreaReport(
      String partnerId, DeedsModels.AreaReportRequest request) {
    List<PropertyObservation> filtered = filterProperties(latestProperties(partnerId), request);

    Map<String, List<PropertyObservation>> grouped = filtered.stream()
        .collect(Collectors.groupingBy(
            property -> property.property().province() + "|" + property.property().township(),
            LinkedHashMap::new,
            Collectors.toList()));

    List<DeedsModels.AreaBreakdownItem> areas = grouped.values().stream()
        .map(group -> {
          DeedsModels.PropertySnapshot first = group.getFirst().property();
          int properties = group.size();
          int owners = distinctOwnerCount(group);
          int activeBonds = group.stream()
              .mapToInt(item -> safeList(item.property().currentBonds()).size())
              .sum();
          int municipalFlags = (int) group.stream()
              .filter(item -> item.property().municipal() != null && item.property().municipal().ratesFlag())
              .count();
          int transfers = (int) group.stream()
              .filter(item -> hasTransferInRange(item.property().lastTransfer(), request))
              .count();
          return new DeedsModels.AreaBreakdownItem(
              first.province(),
              first.township(),
              properties,
              owners,
              activeBonds,
              municipalFlags,
              transfers);
        })
        .sorted(Comparator.comparing(DeedsModels.AreaBreakdownItem::properties).reversed())
        .toList();

    List<DeedsModels.TransferItem> transfers = filtered.stream()
        .filter(item -> hasTransferInRange(item.property().lastTransfer(), request))
        .sorted(Comparator.comparing(PropertyObservation::observedAt).reversed())
        .limit(25)
        .map(item -> new DeedsModels.TransferItem(
            item.property().propertyId(),
            item.property().titleDeed(),
            item.property().ownerName(),
            item.property().province(),
            item.property().township(),
            item.property().lastTransfer() != null ? item.property().lastTransfer().date() : null,
            item.property().lastTransfer() != null ? item.property().lastTransfer().amount() : null))
        .toList();

    DeedsModels.Summary summary = new DeedsModels.Summary(
        filtered.size(),
        distinctOwnerCount(filtered),
        filtered.stream().mapToInt(item -> safeList(item.property().currentBonds()).size()).sum(),
        (int) filtered.stream()
            .filter(item -> item.property().municipal() != null && item.property().municipal().ratesFlag())
            .count(),
        transfers.size());

    return new DeedsModels.AreaReportResponse(summary, areas, transfers, Instant.now().toString());
  }

  public DeedsModels.PropertyReportResponse getPropertyReport(String partnerId, String propertyId) {
    List<PropertyObservation> history = propertyHistory(partnerId, propertyId);
    if (history.isEmpty()) {
      throw new ResponseStatusException(NOT_FOUND, "Property not found in cached deeds history");
    }

    PropertyObservation latest = history.getFirst();
    DeedsModels.PropertySnapshot property = latest.property();
    List<DeedsModels.PropertyTimelineItem> timeline = history.stream()
        .map(item -> new DeedsModels.PropertyTimelineItem(
            item.observedAt(),
            item.property().ownerName(),
            item.property().ownerIdNumber(),
            item.property().titleDeed(),
            item.property().lastTransfer() != null ? item.property().lastTransfer().date() : null,
            item.property().lastTransfer() != null ? item.property().lastTransfer().amount() : null,
            safeList(item.property().currentBonds()).size(),
            item.property().municipal() != null && item.property().municipal().ratesFlag()))
        .toList();

    DeedsModels.Summary summary = new DeedsModels.Summary(
        1,
        isBlank(property.ownerName()) ? 0 : 1,
        safeList(property.currentBonds()).size(),
        property.municipal() != null && property.municipal().ratesFlag() ? 1 : 0,
        property.lastTransfer() != null && !isBlank(property.lastTransfer().date()) ? 1 : 0);

    return new DeedsModels.PropertyReportResponse(
        property,
        timeline,
        summary,
        buildDocumentDescriptors(property),
        DEFAULT_ALERT_TYPES,
        Instant.now().toString());
  }

  public DeedsModels.DocumentManifestResponse getDocumentManifest(String partnerId, String propertyId) {
    DeedsModels.PropertySnapshot property = latestProperty(partnerId, propertyId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Property not found in cached deeds history"))
        .property();
    return new DeedsModels.DocumentManifestResponse(
        property.propertyId(),
        property.titleDeed(),
        buildDocumentDescriptors(property),
        "PENDING_PROVIDER_CONFIGURATION");
  }

  public DeedsModels.ExportResponse exportAreaReport(
      String partnerId,
      DeedsModels.AreaReportRequest request,
      String format) {
    DeedsModels.AreaReportResponse report = generateAreaReport(partnerId, request);
    DeedsModels.ExportResponse export = buildExportResponse(report, format);
    recordExport(
        partnerId,
        null,
        "Ad hoc deeds area report",
        normalizeExportFormat(format),
        report.summary().totalProperties(),
        report.generatedAt());
    return export;
  }

  public DeedsModels.SavedReportResponse createSavedReport(
      String partnerId, DeedsModels.SavedReportRequest request) {
    DeedsModels.AreaReportResponse report = generateAreaReport(partnerId, request.filter());
    Map<String, Object> data = new LinkedHashMap<>();
    data.put("name", firstNonBlank(request.name(), defaultReportName(request.reportType())));
    data.put("reportType", firstNonBlank(request.reportType(), "AREA_ACTIVITY"));
    data.put("status", "ACTIVE");
    data.put("filter", request.filter());
    data.put("schedule", request.schedule());
    data.put("exportFormat", normalizeExportFormat(request.exportFormat()));
    data.put("currentReport", Boolean.TRUE.equals(request.currentReport()));
    data.put("autoRefresh", Boolean.TRUE.equals(request.autoRefresh()));
    applyLatestReportData(data, report);

    String id = partnerDataRepository.saveCustomEntity(
        partnerId, DEEDS_REPORT_PREFIX, "DEEDS_REPORT", null, data);
    logAudit(partnerId, "REPORT", "CREATE_SAVED_REPORT", "partner-user", id, "DEEDS_REPORT");
    return getSavedReport(partnerId, id);
  }

  public List<DeedsModels.SavedReportResponse> listSavedReports(String partnerId) {
    return partnerDataRepository.listCustomEntities(partnerId, DEEDS_REPORT_PREFIX).stream()
        .map(this::toSavedReportResponse)
        .sorted(Comparator.comparing(DeedsModels.SavedReportResponse::createdAt).reversed())
        .toList();
  }

  public DeedsModels.SavedReportResponse getSavedReport(String partnerId, String reportId) {
    Map<String, Object> data = partnerDataRepository.getCustomEntity(partnerId, DEEDS_REPORT_PREFIX, reportId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Saved deeds report not found"));
    return toSavedReportResponse(data);
  }

  public DeedsModels.SavedReportResponse updateSavedReport(
      String partnerId, String reportId, DeedsModels.SavedReportUpdateRequest request) {
    Map<String, Object> existing = partnerDataRepository.getCustomEntity(partnerId, DEEDS_REPORT_PREFIX, reportId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Saved deeds report not found"));
    Map<String, Object> updated = new LinkedHashMap<>(existing);

    if (!isBlank(request.name())) {
      updated.put("name", request.name());
    }
    if (request.filter() != null) {
      updated.put("filter", request.filter());
    }
    if (request.schedule() != null) {
      updated.put("schedule", request.schedule());
      updated.put("nextRunAt", computeNextRunAt(request.schedule()));
    }
    if (!isBlank(request.exportFormat())) {
      updated.put("exportFormat", normalizeExportFormat(request.exportFormat()));
    }
    if (!isBlank(request.status())) {
      updated.put("status", request.status());
    }
    if (request.currentReport() != null) {
      updated.put("currentReport", request.currentReport());
    }
    if (request.autoRefresh() != null) {
      updated.put("autoRefresh", request.autoRefresh());
    }

    partnerDataRepository.saveCustomEntity(
        partnerId, DEEDS_REPORT_PREFIX, "DEEDS_REPORT", reportId, updated);
    logAudit(partnerId, "REPORT", "UPDATE_SAVED_REPORT", "partner-user", reportId, "DEEDS_REPORT");
    return getSavedReport(partnerId, reportId);
  }

  public DeedsModels.SavedReportResponse refreshSavedReport(String partnerId, String reportId) {
    Map<String, Object> existing = partnerDataRepository.getCustomEntity(partnerId, DEEDS_REPORT_PREFIX, reportId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Saved deeds report not found"));
    DeedsModels.AreaReportRequest filter = objectMapper.convertValue(
        existing.get("filter"), DeedsModels.AreaReportRequest.class);
    DeedsModels.AreaReportResponse report = generateAreaReport(partnerId, filter);

    Map<String, Object> updated = new LinkedHashMap<>(existing);
    applyLatestReportData(updated, report);
    partnerDataRepository.saveCustomEntity(
        partnerId, DEEDS_REPORT_PREFIX, "DEEDS_REPORT", reportId, updated);
    reportsRefreshedCounter.increment();
    logAudit(partnerId, "REPORT", "REFRESH_SAVED_REPORT", "system", reportId, "DEEDS_REPORT");
    return getSavedReport(partnerId, reportId);
  }

  public void deleteSavedReport(String partnerId, String reportId) {
    partnerDataRepository.deleteCustomEntity(partnerId, DEEDS_REPORT_PREFIX, reportId);
    logAudit(partnerId, "REPORT", "DELETE_SAVED_REPORT", "partner-user", reportId, "DEEDS_REPORT");
  }

  public DeedsModels.ExportResponse exportSavedReport(String partnerId, String reportId, String format) {
    Map<String, Object> existing = partnerDataRepository.getCustomEntity(partnerId, DEEDS_REPORT_PREFIX, reportId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Saved deeds report not found"));
    DeedsModels.AreaReportRequest filter = objectMapper.convertValue(
        existing.get("filter"), DeedsModels.AreaReportRequest.class);
    DeedsModels.AreaReportResponse report = generateAreaReport(partnerId, filter);
    String normalizedFormat = !isBlank(format)
        ? normalizeExportFormat(format)
        : normalizeExportFormat((String) existing.get("exportFormat"));
    DeedsModels.ExportResponse export = buildExportResponse(report, normalizedFormat);

    Map<String, Object> updated = new LinkedHashMap<>(existing);
    applyLatestReportData(updated, report);
    partnerDataRepository.saveCustomEntity(
        partnerId, DEEDS_REPORT_PREFIX, "DEEDS_REPORT", reportId, updated);
    recordExport(
        partnerId,
        reportId,
        (String) existing.getOrDefault("name", "Saved deeds report"),
        normalizedFormat,
        report.summary().totalProperties(),
        report.generatedAt());
    logAudit(partnerId, "EXPORT", "EXPORT_SAVED_REPORT", "partner-user", reportId, "DEEDS_REPORT");
    return export;
  }

  public List<DeedsModels.ExportHistoryResponse> listExportHistory(String partnerId) {
    return partnerDataRepository.listCustomEntities(partnerId, DEEDS_EXPORT_PREFIX).stream()
        .map(this::toExportHistoryResponse)
        .sorted(Comparator.comparing(DeedsModels.ExportHistoryResponse::createdAt).reversed())
        .toList();
  }

  public DeedsModels.TeamMemberResponse createTeamMember(
      String partnerId, DeedsModels.TeamMemberRequest request) {
    Map<String, Object> data = new LinkedHashMap<>();
    data.put("name", request.name());
    data.put("email", request.email());
    data.put("role", firstNonBlank(request.role(), "deeds_operator"));
    data.put("permissions", request.permissions() == null ? List.of() : request.permissions());
    data.put("status", firstNonBlank(request.status(), "ACTIVE"));
    String id = partnerDataRepository.saveCustomEntity(
        partnerId, DEEDS_TEAM_PREFIX, "DEEDS_TEAM_MEMBER", null, data);
    logAudit(partnerId, "TEAM", "CREATE_MEMBER", request.email(), id, "TEAM_MEMBER");
    return getTeamMember(partnerId, id);
  }

  public List<DeedsModels.TeamMemberResponse> listTeamMembers(String partnerId) {
    return partnerDataRepository.listCustomEntities(partnerId, DEEDS_TEAM_PREFIX).stream()
        .map(this::toTeamMemberResponse)
        .sorted(Comparator.comparing(DeedsModels.TeamMemberResponse::createdAt).reversed())
        .toList();
  }

  public DeedsModels.TeamMemberResponse getTeamMember(String partnerId, String memberId) {
    Map<String, Object> data = partnerDataRepository.getCustomEntity(partnerId, DEEDS_TEAM_PREFIX, memberId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Deeds team member not found"));
    return toTeamMemberResponse(data);
  }

  public DeedsModels.TeamMemberResponse updateTeamMember(
      String partnerId, String memberId, DeedsModels.TeamMemberRequest request) {
    Map<String, Object> existing = partnerDataRepository.getCustomEntity(partnerId, DEEDS_TEAM_PREFIX, memberId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Deeds team member not found"));
    Map<String, Object> updated = new LinkedHashMap<>(existing);
    if (!isBlank(request.name())) updated.put("name", request.name());
    if (!isBlank(request.email())) updated.put("email", request.email());
    if (!isBlank(request.role())) updated.put("role", request.role());
    if (request.permissions() != null) updated.put("permissions", request.permissions());
    if (!isBlank(request.status())) updated.put("status", request.status());
    partnerDataRepository.saveCustomEntity(partnerId, DEEDS_TEAM_PREFIX, "DEEDS_TEAM_MEMBER", memberId, updated);
    logAudit(partnerId, "TEAM", "UPDATE_MEMBER", request.email(), memberId, "TEAM_MEMBER");
    return getTeamMember(partnerId, memberId);
  }

  public void deleteTeamMember(String partnerId, String memberId) {
    partnerDataRepository.deleteCustomEntity(partnerId, DEEDS_TEAM_PREFIX, memberId);
    logAudit(partnerId, "TEAM", "DELETE_MEMBER", "partner-admin", memberId, "TEAM_MEMBER");
  }

  public List<DeedsModels.AuditEventResponse> listAuditEvents(String partnerId) {
    return partnerDataRepository.listCustomEntities(partnerId, DEEDS_AUDIT_PREFIX).stream()
        .map(this::toAuditEventResponse)
        .sorted(Comparator.comparing(DeedsModels.AuditEventResponse::createdAt).reversed())
        .toList();
  }

  public DeedsModels.MapSearchResponse searchMap(
      String partnerId, DeedsModels.MapSearchRequest request) {
    List<PropertyObservation> filtered = latestProperties(partnerId).stream()
        .filter(item -> request == null || isBlank(request.province())
            || equalsIgnoreCase(item.property().province(), request.province()))
        .filter(item -> request == null || isBlank(request.township())
            || equalsIgnoreCase(item.property().township(), request.township()))
        .filter(item -> request == null || isBlank(request.streetName())
            || containsIgnoreCase(item.property().streetAddress(), request.streetName()))
        .filter(item -> request == null || isBlank(request.query())
            || containsIgnoreCase(item.property().ownerName(), request.query())
            || containsIgnoreCase(item.property().titleDeed(), request.query())
            || containsIgnoreCase(item.property().streetAddress(), request.query()))
        .toList();

    List<DeedsModels.BoundaryResponse> boundaries = filtered.stream()
        .map(PropertyObservation::property)
        .map(property -> new DeedsModels.BoundaryResponse(
            property.province() + "-" + property.township(),
            property.province(),
            firstNonBlank(property.township(), "Municipality"),
            property.township() + " boundary",
            boundaryPolygon(property)))
        .collect(Collectors.toMap(
            DeedsModels.BoundaryResponse::boundaryId,
            boundary -> boundary,
            (left, right) -> left,
            LinkedHashMap::new))
        .values()
        .stream()
        .toList();

    List<DeedsModels.MapPropertyResponse> properties = filtered.stream()
        .map(PropertyObservation::property)
        .map(property -> new DeedsModels.MapPropertyResponse(
            property.propertyId(),
            "ERF " + property.erfNumber() + "/" + property.portion(),
            property.province(),
            property.township(),
            property.streetAddress(),
            property.titleDeed(),
            centroid(property),
            parcelOutline(property),
            property.ownerName(),
            property.municipal() != null && property.municipal().ratesFlag()))
        .toList();

    return new DeedsModels.MapSearchResponse(boundaries, properties, Instant.now().toString());
  }

  public DeedsModels.ConversionResponse convertPropertyReference(
      String partnerId, DeedsModels.ConversionRequest request) {
    String direction = firstNonBlank(request.direction(), "street_to_erf").toLowerCase();
    List<DeedsModels.ConversionCandidate> candidates = latestProperties(partnerId).stream()
        .map(PropertyObservation::property)
        .filter(property -> isBlank(request.province()) || equalsIgnoreCase(property.province(), request.province()))
        .filter(property -> isBlank(request.township()) || equalsIgnoreCase(property.township(), request.township()))
        .map(property -> buildConversionCandidate(property, request, direction))
        .filter(Objects::nonNull)
        .sorted(Comparator.comparing(DeedsModels.ConversionCandidate::confidence).reversed())
        .limit(10)
        .toList();

    String normalizedInput = "street_to_erf".equals(direction)
        ? defaultString(request.streetName())
        : "ERF " + defaultString(request.erfNumber()) + "/" + defaultString(request.portion(), "0");

    return new DeedsModels.ConversionResponse(direction, normalizedInput, candidates, Instant.now().toString());
  }

  public DeedsModels.ValuationResponse estimateValue(
      String partnerId, DeedsModels.ValuationRequest request) {
    PropertyObservation observation = findValuationSubject(partnerId, request)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Property not found for valuation"));

    List<DeedsModels.ComparableSale> comparables = latestProperties(partnerId).stream()
        .map(PropertyObservation::property)
        .filter(property -> !property.propertyId().equals(observation.property().propertyId()))
        .filter(property -> equalsIgnoreCase(property.province(), observation.property().province()))
        .filter(property -> equalsIgnoreCase(property.township(), observation.property().township()))
        .filter(property -> property.lastTransfer() != null && property.lastTransfer().amount() != null)
        .map(property -> new DeedsModels.ComparableSale(
            property.propertyId(),
            property.titleDeed(),
            property.township(),
            property.lastTransfer().date(),
            property.lastTransfer().amount(),
            similarityScore(observation.property(), property)))
        .sorted(Comparator.comparing(DeedsModels.ComparableSale::similarityScore).reversed())
        .limit(5)
        .toList();

    double estimate = comparables.isEmpty()
        ? defaultNumber(observation.property().lastTransfer() != null
            ? observation.property().lastTransfer().amount()
            : null)
        : comparables.stream()
            .mapToDouble(item -> defaultNumber(item.transferAmount()) * item.similarityScore())
            .sum() / comparables.stream().mapToDouble(DeedsModels.ComparableSale::similarityScore).sum();
    if (estimate <= 0.0) {
      estimate = 750_000.0;
    }

    double confidenceSpread = comparables.size() >= 3 ? 0.15 : 0.25;
    String confidenceBand = comparables.size() >= 4 ? "MEDIUM" : "LOW";

    return new DeedsModels.ValuationResponse(
        observation.property().propertyId(),
        round2(estimate),
        round2(estimate * (1 - confidenceSpread)),
        round2(estimate * (1 + confidenceSpread)),
        confidenceBand,
        "Rules-based estimate from cached transfer activity and comparable sales.",
        "Internal estimate only. Replace with provider-backed or valuer-backed data before operational use.",
        comparables,
        Instant.now().toString());
  }

  public DeedsModels.OperationsRefreshResponse runRefreshCycle(String partnerId) {
    int refreshedReports = 0;
    for (DeedsModels.SavedReportResponse report : listSavedReports(partnerId)) {
      if (report.autoRefresh() && !"PAUSED".equalsIgnoreCase(report.status())) {
        refreshSavedReport(partnerId, report.id());
        refreshedReports++;
      }
    }

    int recalculatedWatches = listWatches(partnerId).size();
    reportsRefreshedCounter.increment(refreshedReports);
    watchesRecalculatedCounter.increment(recalculatedWatches);
    logAudit(partnerId, "OPERATIONS", "REFRESH_CYCLE", "system", null, "DEEDS");

    return new DeedsModels.OperationsRefreshResponse(
        refreshedReports,
        recalculatedWatches,
        Instant.now().plusSeconds(24L * 60 * 60).toString(),
        Instant.now().toString());
  }

  public DeedsModels.WatchResponse createWatch(String partnerId, DeedsModels.WatchRequest request) {
    PropertyObservation latest = latestProperty(partnerId, request.propertyId())
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Property not found in cached deeds history"));

    int knownPortionsCount = latestProperties(partnerId).stream()
        .map(PropertyObservation::property)
        .filter(property -> property.erfNumber() == latest.property().erfNumber())
        .filter(property -> equalsIgnoreCase(property.township(), latest.property().township()))
        .filter(property -> equalsIgnoreCase(property.province(), latest.property().province()))
        .map(DeedsModels.PropertySnapshot::propertyId)
        .collect(Collectors.toCollection(LinkedHashSet::new))
        .size();

    WatchMetadata metadata = new WatchMetadata(
        "deedsWatch",
        latest.property().propertyId(),
        latest.property().titleDeed(),
        latest.property(),
        request.alertTypes() == null || request.alertTypes().isEmpty()
            ? DEFAULT_ALERT_TYPES
            : request.alertTypes(),
        knownPortionsCount);

    MonitoredSubjectDataModel subject = monitoringService.createSubject(
        partnerId,
        null,
        "PROPERTY",
        propertyLabel(latest.property()),
        latest.property().propertyId(),
        writeJson(metadata),
        normalizeFrequency(request.monitoringFrequency()));

    logAudit(partnerId, "WATCH", "CREATE_WATCH", "partner-user", subject.getSubjectId(), "DEEDS_WATCH");
    return toWatchResponse(subject, metadata);
  }

  public DeedsModels.WatchResponse updateWatch(
      String partnerId, String subjectId, DeedsModels.WatchUpdateRequest request) {
    MonitoredSubjectDataModel subject = monitoringService.getSubject(subjectId, partnerId);
    WatchMetadata metadata = parseWatchMetadata(subject.getMetadataJson())
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Deeds watch not found"));

    List<String> alertTypes = request.alertTypes() == null || request.alertTypes().isEmpty()
        ? metadata.alertTypes()
        : request.alertTypes();
    WatchMetadata updatedMetadata = new WatchMetadata(
        metadata.kind(),
        metadata.propertyId(),
        metadata.titleDeed(),
        metadata.baseline(),
        alertTypes,
        metadata.knownPortionsCount());

    Map<String, Object> updates = new LinkedHashMap<>();
    if (!isBlank(request.status())) {
      updates.put("status", request.status().toUpperCase());
    }
    if (!isBlank(request.monitoringFrequency())) {
      updates.put("monitoringFrequency", normalizeFrequency(request.monitoringFrequency()));
    }
    updates.put("metadataJson", writeJson(updatedMetadata));

    MonitoredSubjectDataModel updated = monitoringService.updateSubject(subjectId, partnerId, updates);
    logAudit(partnerId, "WATCH", "UPDATE_WATCH", "partner-user", subjectId, "DEEDS_WATCH");
    return toWatchResponse(updated, updatedMetadata);
  }

  public void deleteWatch(String partnerId, String subjectId) {
    monitoringService.deleteSubject(subjectId, partnerId);
    logAudit(partnerId, "WATCH", "DELETE_WATCH", "partner-user", subjectId, "DEEDS_WATCH");
  }

  public List<DeedsModels.WatchResponse> listWatches(String partnerId) {
    return monitoringService.listSubjects(partnerId, null, 100).stream()
        .filter(subject -> "PROPERTY".equals(subject.getSubjectType()))
        .map(subject -> parseWatchMetadata(subject.getMetadataJson())
            .map(metadata -> toWatchResponse(subject, metadata))
            .orElse(null))
        .filter(Objects::nonNull)
        .toList();
  }

  public List<DeedsModels.WatchAlertResponse> listWatchAlerts(String partnerId) {
    Map<String, PropertyObservation> latest = latestPropertiesById(partnerId);
    List<DeedsModels.WatchAlertResponse> alerts = new ArrayList<>();

    for (MonitoredSubjectDataModel subject : monitoringService.listSubjects(partnerId, null, 100)) {
      if (!"PROPERTY".equals(subject.getSubjectType())) {
        continue;
      }

      Optional<WatchMetadata> metadataOptional = parseWatchMetadata(subject.getMetadataJson());
      if (metadataOptional.isEmpty()) {
        continue;
      }

      WatchMetadata metadata = metadataOptional.get();
      PropertyObservation currentObservation = latest.get(metadata.propertyId());
      if (currentObservation == null) {
        continue;
      }

      alerts.addAll(compareWatch(subject, metadata, currentObservation, latest.values()));
    }

    return alerts.stream()
        .sorted(Comparator.comparing(DeedsModels.WatchAlertResponse::createdAt).reversed())
        .toList();
  }

  private List<DeedsModels.WatchAlertResponse> compareWatch(
      MonitoredSubjectDataModel subject,
      WatchMetadata metadata,
      PropertyObservation currentObservation,
      Collection<PropertyObservation> latestProperties) {
    List<DeedsModels.WatchAlertResponse> alerts = new ArrayList<>();
    DeedsModels.PropertySnapshot baseline = metadata.baseline();
    DeedsModels.PropertySnapshot current = currentObservation.property();
    String createdAt = currentObservation.observedAt();

    if (metadata.alertTypes().contains("TRANSFER")
        && (!equalsIgnoreCase(baseline.ownerName(), current.ownerName())
            || !equalsIgnoreCase(baseline.ownerIdNumber(), current.ownerIdNumber())
            || !equalsIgnoreCase(transferDate(baseline), transferDate(current)))) {
      alerts.add(new DeedsModels.WatchAlertResponse(
          subject.getSubjectId() + "-transfer",
          subject.getSubjectId(),
          metadata.propertyId(),
          "HIGH",
          "TRANSFER",
          "Ownership or transfer change detected",
          "The latest cached deeds snapshot differs from the baseline ownership or transfer details.",
          createdAt));
    }

    if (metadata.alertTypes().contains("BOND_CHANGE") && bondStateChanged(baseline, current)) {
      alerts.add(new DeedsModels.WatchAlertResponse(
          subject.getSubjectId() + "-bond",
          subject.getSubjectId(),
          metadata.propertyId(),
          "MEDIUM",
          "BOND_CHANGE",
          "Bond position changed",
          "The number of active bonds or the registered bond amounts changed since the watch baseline.",
          createdAt));
    }

    if (metadata.alertTypes().contains("MUNICIPAL_CHANGE") && municipalStateChanged(baseline, current)) {
      alerts.add(new DeedsModels.WatchAlertResponse(
          subject.getSubjectId() + "-municipal",
          subject.getSubjectId(),
          metadata.propertyId(),
          "MEDIUM",
          "MUNICIPAL_CHANGE",
          "Municipal account status changed",
          "The municipal arrears or rates flag state changed since the watch baseline.",
          createdAt));
    }

    if (metadata.alertTypes().contains("PORTION_CHANGE")) {
      int currentPortionsCount = latestProperties.stream()
          .map(PropertyObservation::property)
          .filter(property -> property.erfNumber() == current.erfNumber())
          .filter(property -> equalsIgnoreCase(property.township(), current.township()))
          .filter(property -> equalsIgnoreCase(property.province(), current.province()))
          .map(DeedsModels.PropertySnapshot::propertyId)
          .collect(Collectors.toCollection(LinkedHashSet::new))
          .size();
      if (currentPortionsCount != metadata.knownPortionsCount()) {
        alerts.add(new DeedsModels.WatchAlertResponse(
            subject.getSubjectId() + "-portions",
            subject.getSubjectId(),
            metadata.propertyId(),
            "LOW",
            "PORTION_CHANGE",
            "Known portions changed",
            "The cached number of related portions for this erf changed since the watch baseline.",
            createdAt));
      }
    }

    return alerts;
  }

  private boolean bondStateChanged(
      DeedsModels.PropertySnapshot baseline, DeedsModels.PropertySnapshot current) {
    List<DeedsModels.BondSnapshot> baselineBonds = safeList(baseline.currentBonds());
    List<DeedsModels.BondSnapshot> currentBonds = safeList(current.currentBonds());
    if (baselineBonds.size() != currentBonds.size()) {
      return true;
    }

    List<String> baselineValues = baselineBonds.stream()
        .map(bond -> defaultString(bond.bondholder()) + "|" + defaultNumber(bond.amount()))
        .sorted()
        .toList();
    List<String> currentValues = currentBonds.stream()
        .map(bond -> defaultString(bond.bondholder()) + "|" + defaultNumber(bond.amount()))
        .sorted()
        .toList();
    return !baselineValues.equals(currentValues);
  }

  private boolean municipalStateChanged(
      DeedsModels.PropertySnapshot baseline, DeedsModels.PropertySnapshot current) {
    DeedsModels.MunicipalSnapshot baselineMunicipal = baseline.municipal();
    DeedsModels.MunicipalSnapshot currentMunicipal = current.municipal();
    if (baselineMunicipal == null || currentMunicipal == null) {
      return baselineMunicipal != currentMunicipal;
    }
    return baselineMunicipal.ratesFlag() != currentMunicipal.ratesFlag()
        || !Objects.equals(defaultNumber(baselineMunicipal.arrears()), defaultNumber(currentMunicipal.arrears()));
  }

  private Optional<PropertyObservation> latestProperty(String partnerId, String propertyId) {
    return Optional.ofNullable(latestPropertiesById(partnerId).get(propertyId));
  }

  private Map<String, PropertyObservation> latestPropertiesById(String partnerId) {
    Map<String, PropertyObservation> latest = new LinkedHashMap<>();
    for (PropertyObservation observation : propertyObservations(partnerId)) {
      latest.merge(
          observation.property().propertyId(),
          observation,
          (existing, candidate) -> existing.observedAt().compareTo(candidate.observedAt()) >= 0
              ? existing
              : candidate);
    }
    return latest;
  }

  private List<PropertyObservation> latestProperties(String partnerId) {
    return latestPropertiesById(partnerId).values().stream()
        .sorted(Comparator.comparing(PropertyObservation::observedAt).reversed())
        .toList();
  }

  private List<PropertyObservation> propertyHistory(String partnerId, String propertyId) {
    return propertyObservations(partnerId).stream()
        .filter(item -> propertyId.equals(item.property().propertyId()))
        .sorted(Comparator.comparing(PropertyObservation::observedAt).reversed())
        .toList();
  }

  private List<PropertyObservation> propertyObservations(String partnerId) {
    return commandStatusRepository.findByPartnerId(partnerId, CommandStatus.COMPLETED.name(), DEFAULT_HISTORY_LIMIT, null)
        .items()
        .stream()
        .filter(item -> item.getAuxiliaryData() != null)
        .filter(item -> item.getAuxiliaryData().containsKey("searchResultJson"))
        .map(this::parseSearchResult)
        .flatMap(Optional::stream)
        .flatMap(result -> safeList(result.items()).stream()
            .map(property -> new PropertyObservation(
                property,
                defaultString(result.observedAt(), Instant.now().toString()),
                result.commandId())))
        .toList();
  }

  private Optional<SearchResultPayload> parseSearchResult(VerificationCommandStoreItem item) {
    try {
      SearchResultPayload payload = objectMapper.readValue(
          item.getAuxiliaryData().get("searchResultJson"),
          SearchResultPayload.class);
      return Optional.of(new SearchResultPayload(
          payload.summary(),
          payload.items(),
          payload.criteria(),
          item.getCommandId(),
          item.getCreatedAt()));
    } catch (Exception e) {
      logger.warn("Skipping invalid deeds search payload for command {}: {}", item.getCommandId(), e.getMessage());
      return Optional.empty();
    }
  }

  private List<PropertyObservation> filterProperties(
      List<PropertyObservation> properties, DeedsModels.AreaReportRequest request) {
    return properties.stream()
        .filter(item -> request == null || isBlank(request.province())
            || equalsIgnoreCase(item.property().province(), request.province()))
        .filter(item -> request == null || isBlank(request.township())
            || equalsIgnoreCase(item.property().township(), request.township()))
        .filter(item -> request == null || !Boolean.TRUE.equals(request.municipalFlagsOnly())
            || (item.property().municipal() != null && item.property().municipal().ratesFlag()))
        .filter(item -> request == null || matchesTransferDateRange(item.property().lastTransfer(), request))
        .toList();
  }

  private boolean matchesTransferDateRange(
      DeedsModels.TransferSnapshot transfer,
      DeedsModels.AreaReportRequest request) {
    if (transfer == null || isBlank(transfer.date())) {
      return request == null || (isBlank(request.transferDateFrom()) && isBlank(request.transferDateTo()));
    }
    return hasTransferInRange(transfer, request);
  }

  private boolean hasTransferInRange(
      DeedsModels.TransferSnapshot transfer,
      DeedsModels.AreaReportRequest request) {
    if (transfer == null || isBlank(transfer.date())) {
      return false;
    }

    Optional<LocalDate> transferDate = parseLocalDate(transfer.date());
    if (transferDate.isEmpty()) {
      return false;
    }

    Optional<LocalDate> from = request == null ? Optional.empty() : parseLocalDate(request.transferDateFrom());
    Optional<LocalDate> to = request == null ? Optional.empty() : parseLocalDate(request.transferDateTo());
    if (from.isPresent() && transferDate.get().isBefore(from.get())) {
      return false;
    }
    if (to.isPresent() && transferDate.get().isAfter(to.get())) {
      return false;
    }
    return true;
  }

  private Optional<LocalDate> parseLocalDate(String value) {
    if (isBlank(value)) {
      return Optional.empty();
    }
    try {
      return Optional.of(LocalDate.parse(value));
    } catch (DateTimeParseException e) {
      return Optional.empty();
    }
  }

  private int distinctOwnerCount(List<PropertyObservation> properties) {
    return (int) properties.stream()
        .map(PropertyObservation::property)
        .map(property -> defaultString(property.ownerIdNumber(), property.ownerName()))
        .filter(value -> !isBlank(value))
        .distinct()
        .count();
  }

  private String writeJson(Object value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Failed to serialize deeds watch metadata", e);
    }
  }

  private Optional<WatchMetadata> parseWatchMetadata(String metadataJson) {
    if (isBlank(metadataJson)) {
      return Optional.empty();
    }
    try {
      WatchMetadata metadata = objectMapper.readValue(metadataJson, WatchMetadata.class);
      return "deedsWatch".equals(metadata.kind()) ? Optional.of(metadata) : Optional.empty();
    } catch (Exception e) {
      logger.warn("Skipping invalid deeds watch metadata: {}", e.getMessage());
      return Optional.empty();
    }
  }

  private DeedsModels.ExportResponse buildExportResponse(
      DeedsModels.AreaReportResponse report,
      String format) {
    String normalizedFormat = normalizeExportFormat(format);

    if ("json".equals(normalizedFormat)) {
      try {
        return new DeedsModels.ExportResponse(
            "deeds-area-report.json",
            "application/json",
            objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(report));
      } catch (JsonProcessingException e) {
        throw new IllegalStateException("Failed to serialize deeds report export", e);
      }
    }

    StringBuilder csv = new StringBuilder();
    csv.append("province,township,properties,owners,activeBonds,municipalFlags,transfers\n");
    for (DeedsModels.AreaBreakdownItem item : report.areas()) {
      csv.append(csvValue(item.province())).append(',')
          .append(csvValue(item.township())).append(',')
          .append(item.properties()).append(',')
          .append(item.owners()).append(',')
          .append(item.activeBonds()).append(',')
          .append(item.municipalFlags()).append(',')
          .append(item.transfers()).append('\n');
    }

    return new DeedsModels.ExportResponse("deeds-area-report.csv", "text/csv", csv.toString());
  }

  private void applyLatestReportData(
      Map<String, Object> data, DeedsModels.AreaReportResponse report) {
    data.put("latestSummary", report.summary());
    data.put("latestReportJson", writeJson(report));
    data.put("lastGeneratedAt", report.generatedAt());
    DeedsModels.ScheduleConfig schedule = objectMapper.convertValue(
        data.get("schedule"), DeedsModels.ScheduleConfig.class);
    data.put("nextRunAt", computeNextRunAt(schedule));
  }

  private void recordExport(
      String partnerId,
      String reportId,
      String reportName,
      String format,
      int recordCount,
      String generatedAt) {
    Map<String, Object> data = new LinkedHashMap<>();
    data.put("reportId", reportId);
    data.put("reportName", reportName);
    data.put("format", normalizeExportFormat(format));
    data.put("status", "READY");
    data.put("recordCount", recordCount);
    data.put("generatedAt", generatedAt);
    partnerDataRepository.saveCustomEntity(
        partnerId, DEEDS_EXPORT_PREFIX, "DEEDS_EXPORT", null, data);
    exportsCreatedCounter.increment();
  }

  private DeedsModels.SavedReportResponse toSavedReportResponse(Map<String, Object> data) {
    return new DeedsModels.SavedReportResponse(
        (String) data.get("id"),
        (String) data.getOrDefault("name", "Saved deeds report"),
        (String) data.getOrDefault("reportType", "AREA_ACTIVITY"),
        (String) data.getOrDefault("status", "ACTIVE"),
        objectMapper.convertValue(data.get("filter"), DeedsModels.AreaReportRequest.class),
        objectMapper.convertValue(data.get("schedule"), DeedsModels.ScheduleConfig.class),
        (String) data.getOrDefault("exportFormat", "csv"),
        Boolean.TRUE.equals(data.get("currentReport")),
        Boolean.TRUE.equals(data.get("autoRefresh")),
        objectMapper.convertValue(data.get("latestSummary"), DeedsModels.Summary.class),
        (String) data.get("lastGeneratedAt"),
        (String) data.get("nextRunAt"),
        (String) data.get("createdAt"),
        (String) data.get("updatedAt"));
  }

  private DeedsModels.ExportHistoryResponse toExportHistoryResponse(Map<String, Object> data) {
    return new DeedsModels.ExportHistoryResponse(
        (String) data.get("id"),
        (String) data.get("reportId"),
        (String) data.getOrDefault("reportName", "Saved deeds report"),
        (String) data.getOrDefault("format", "csv"),
        (String) data.getOrDefault("status", "READY"),
        data.get("recordCount") instanceof Number number ? number.intValue() : 0,
        (String) data.get("generatedAt"),
        (String) data.get("createdAt"));
  }

  private DeedsModels.TeamMemberResponse toTeamMemberResponse(Map<String, Object> data) {
    return new DeedsModels.TeamMemberResponse(
        (String) data.get("id"),
        (String) data.getOrDefault("name", ""),
        (String) data.getOrDefault("email", ""),
        (String) data.getOrDefault("role", "deeds_operator"),
        data.get("permissions") instanceof List<?> permissions
            ? permissions.stream().map(Object::toString).toList()
            : List.of(),
        (String) data.getOrDefault("status", "ACTIVE"),
        (String) data.get("createdAt"),
        (String) data.get("updatedAt"));
  }

  private DeedsModels.AuditEventResponse toAuditEventResponse(Map<String, Object> data) {
    return new DeedsModels.AuditEventResponse(
        (String) data.get("id"),
        (String) data.getOrDefault("category", "DEEDS"),
        (String) data.getOrDefault("action", ""),
        (String) data.getOrDefault("actor", "system"),
        (String) data.get("targetId"),
        (String) data.get("targetType"),
        (String) data.get("detail"),
        (String) data.get("createdAt"));
  }

  private void logAudit(
      String partnerId,
      String category,
      String action,
      String actor,
      String targetId,
      String targetType) {
    Map<String, Object> data = new LinkedHashMap<>();
    data.put("category", category);
    data.put("action", action);
    data.put("actor", actor);
    data.put("targetId", targetId);
    data.put("targetType", targetType);
    data.put("detail", action + " executed");
    partnerDataRepository.saveCustomEntity(partnerId, DEEDS_AUDIT_PREFIX, "DEEDS_AUDIT", null, data);
  }

  private String computeNextRunAt(DeedsModels.ScheduleConfig schedule) {
    if (schedule == null || isBlank(schedule.frequency())) {
      return null;
    }
    Instant now = Instant.now();
    return switch (schedule.frequency().toUpperCase()) {
      case "DAILY" -> now.plusSeconds(24 * 60 * 60).toString();
      case "WEEKLY" -> now.plusSeconds(7L * 24 * 60 * 60).toString();
      case "MONTHLY" -> now.plusSeconds(30L * 24 * 60 * 60).toString();
      default -> now.plusSeconds(7L * 24 * 60 * 60).toString();
    };
  }

  private String defaultReportName(String reportType) {
    return switch (defaultString(reportType).toUpperCase()) {
      case "MUNICIPAL_FLAGS" -> "Municipal flags current report";
      case "TRANSFER_ACTIVITY" -> "Transfer activity current report";
      case "PORTFOLIO_CURRENT" -> "Portfolio current report";
      default -> "Area activity current report";
    };
  }

  private String normalizeExportFormat(String format) {
    if (isBlank(format)) {
      return "csv";
    }
    return format.toLowerCase();
  }

  private List<DeedsModels.Coordinate> boundaryPolygon(DeedsModels.PropertySnapshot property) {
    DeedsModels.Coordinate centroid = centroid(property);
    return List.of(
        new DeedsModels.Coordinate(centroid.lat() + 0.01, centroid.lng() - 0.01),
        new DeedsModels.Coordinate(centroid.lat() + 0.01, centroid.lng() + 0.01),
        new DeedsModels.Coordinate(centroid.lat() - 0.01, centroid.lng() + 0.01),
        new DeedsModels.Coordinate(centroid.lat() - 0.01, centroid.lng() - 0.01));
  }

  private List<DeedsModels.Coordinate> parcelOutline(DeedsModels.PropertySnapshot property) {
    DeedsModels.Coordinate centroid = centroid(property);
    double offset = 0.001;
    return List.of(
        new DeedsModels.Coordinate(centroid.lat() + offset, centroid.lng() - offset),
        new DeedsModels.Coordinate(centroid.lat() + offset, centroid.lng() + offset),
        new DeedsModels.Coordinate(centroid.lat() - offset, centroid.lng() + offset),
        new DeedsModels.Coordinate(centroid.lat() - offset, centroid.lng() - offset));
  }

  private DeedsModels.Coordinate centroid(DeedsModels.PropertySnapshot property) {
    double provinceSeed = Math.abs(defaultString(property.province()).hashCode() % 1000) / 1000.0;
    double townshipSeed = Math.abs(defaultString(property.township()).hashCode() % 1000) / 1000.0;
    return new DeedsModels.Coordinate(-34.0 + provinceSeed * 10.0, 18.0 + townshipSeed * 14.0);
  }

  private DeedsModels.ConversionCandidate buildConversionCandidate(
      DeedsModels.PropertySnapshot property,
      DeedsModels.ConversionRequest request,
      String direction) {
    if ("street_to_erf".equals(direction)) {
      if (isBlank(request.streetName())) {
        return null;
      }
      double confidence = stringSimilarity(defaultString(request.streetName()), defaultString(property.streetAddress()));
      if (confidence < 0.2) {
        return null;
      }
      return new DeedsModels.ConversionCandidate(
          property.propertyId(),
          property.streetAddress(),
          property.township(),
          property.province(),
          property.erfNumber(),
          property.portion(),
          property.titleDeed(),
          round2(confidence),
          "Matched cached street address to deeds property register.");
    }

    if (isBlank(request.erfNumber())) {
      return null;
    }
    boolean erfMatch = String.valueOf(property.erfNumber()).equals(request.erfNumber());
    boolean portionMatch = isBlank(request.portion()) || String.valueOf(property.portion()).equals(request.portion());
    if (!erfMatch || !portionMatch) {
      return null;
    }
    return new DeedsModels.ConversionCandidate(
        property.propertyId(),
        property.streetAddress(),
        property.township(),
        property.province(),
        property.erfNumber(),
        property.portion(),
        property.titleDeed(),
        0.98,
        "Matched cached erf and portion reference.");
  }

  private Optional<PropertyObservation> findValuationSubject(
      String partnerId, DeedsModels.ValuationRequest request) {
    if (!isBlank(request.propertyId())) {
      return latestProperty(partnerId, request.propertyId());
    }
    return latestProperties(partnerId).stream()
        .filter(item -> isBlank(request.province()) || equalsIgnoreCase(item.property().province(), request.province()))
        .filter(item -> isBlank(request.township()) || equalsIgnoreCase(item.property().township(), request.township()))
        .filter(item -> request.erfNumber() == null || item.property().erfNumber() == request.erfNumber())
        .filter(item -> request.portion() == null || item.property().portion() == request.portion())
        .filter(item -> isBlank(request.titleDeed()) || equalsIgnoreCase(item.property().titleDeed(), request.titleDeed()))
        .findFirst();
  }

  private double similarityScore(
      DeedsModels.PropertySnapshot subject,
      DeedsModels.PropertySnapshot comparable) {
    double score = 0.4;
    if (equalsIgnoreCase(subject.township(), comparable.township())) score += 0.25;
    if (equalsIgnoreCase(subject.province(), comparable.province())) score += 0.15;
    if (Math.abs(subject.erfNumber() - comparable.erfNumber()) < 50) score += 0.10;
    if (subject.portion() == comparable.portion()) score += 0.10;
    return round2(Math.min(score, 0.99));
  }

  private boolean containsIgnoreCase(String left, String right) {
    return defaultString(left).toLowerCase().contains(defaultString(right).toLowerCase());
  }

  private double stringSimilarity(String left, String right) {
    String normalizedLeft = defaultString(left).toLowerCase();
    String normalizedRight = defaultString(right).toLowerCase();
    if (normalizedLeft.isBlank() || normalizedRight.isBlank()) {
      return 0.0;
    }
    int minLength = Math.min(normalizedLeft.length(), normalizedRight.length());
    int matches = 0;
    for (int i = 0; i < minLength; i++) {
      if (normalizedLeft.charAt(i) == normalizedRight.charAt(i)) {
        matches++;
      }
    }
    return round2((double) matches / Math.max(normalizedLeft.length(), normalizedRight.length()));
  }

  private double round2(double value) {
    return Math.round(value * 100.0) / 100.0;
  }

  private String normalizeFrequency(String frequency) {
    if (isBlank(frequency)) {
      return "MONTHLY";
    }
    return frequency.toUpperCase();
  }

  private DeedsModels.WatchResponse toWatchResponse(
      MonitoredSubjectDataModel subject,
      WatchMetadata metadata) {
    return new DeedsModels.WatchResponse(
        subject.getSubjectId(),
        metadata.propertyId(),
        subject.getSubjectName(),
        metadata.titleDeed(),
        subject.getMonitoringFrequency(),
        subject.getStatus(),
        metadata.alertTypes(),
        subject.getCreatedAt());
  }

  private String propertyLabel(DeedsModels.PropertySnapshot property) {
    return "ERF " + property.erfNumber() + "/" + property.portion() + " " + defaultString(property.township());
  }

  private List<DeedsModels.DocumentDescriptor> buildDocumentDescriptors(
      DeedsModels.PropertySnapshot property) {
    return List.of(
        new DeedsModels.DocumentDescriptor(
            "TITLE_DEED_COPY",
            "Title deed copy",
            firstNonBlank(property.titleDeed(), property.propertyId()),
            false,
            "PENDING_PROVIDER_CONFIGURATION",
            "Document retrieval is blocked until the live DeedsWeb contract is mapped."),
        new DeedsModels.DocumentDescriptor(
            "TITLE_DEED_ENDORSEMENT",
            "Title deed endorsement",
            firstNonBlank(property.titleDeed(), property.propertyId()),
            false,
            "PENDING_PROVIDER_CONFIGURATION",
            "Endorsement retrieval will be enabled after the provider field mapping is available."),
        new DeedsModels.DocumentDescriptor(
            "DOTS_PROPERTY_TRACE",
            "DOTS property trace",
            firstNonBlank(property.titleDeed(), property.propertyId()),
            false,
            "PENDING_PROVIDER_CONFIGURATION",
            "DOTS integration is scaffolded but not yet connected to the external provider."),
        new DeedsModels.DocumentDescriptor(
            "SG_DIAGRAM",
            "Surveyor-General diagram",
            property.propertyId(),
            false,
            "PENDING_PROVIDER_CONFIGURATION",
            "Diagram retrieval needs the provider document operation and reference mapping."),
        new DeedsModels.DocumentDescriptor(
            "JUDGMENT_COPY",
            "Judgment copy",
            firstNonBlank(property.ownerIdNumber(), property.ownerName()),
            false,
            "PENDING_PROVIDER_CONFIGURATION",
            "Judgment document retrieval depends on the external document service contract."));
  }

  private String csvValue(String value) {
    String safe = value == null ? "" : value.replace("\"", "\"\"");
    return "\"" + safe + "\"";
  }

  private String transferDate(DeedsModels.PropertySnapshot property) {
    return property.lastTransfer() != null ? property.lastTransfer().date() : null;
  }

  private boolean equalsIgnoreCase(String left, String right) {
    return defaultString(left).equalsIgnoreCase(defaultString(right));
  }

  private String defaultString(String value) {
    return value == null ? "" : value;
  }

  private String defaultString(String value, String fallback) {
    return value == null ? fallback : value;
  }

  private String firstNonBlank(String... values) {
    for (String value : values) {
      if (!isBlank(value)) {
        return value;
      }
    }
    return null;
  }

  private Double defaultNumber(Double value) {
    return value == null ? 0.0 : value;
  }

  private boolean isBlank(String value) {
    return value == null || value.isBlank();
  }

  private <T> List<T> safeList(List<T> items) {
    return items == null ? List.of() : items;
  }

  private record PropertyObservation(
      DeedsModels.PropertySnapshot property,
      String observedAt,
      String commandId) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  private record SearchResultPayload(
      SummaryPayload summary,
      List<DeedsModels.PropertySnapshot> items,
      CriteriaPayload criteria,
      String commandId,
      String observedAt) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  private record SummaryPayload(
      int totalProperties,
      int totalActiveBonds,
      int totalMunicipalFlags) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  private record CriteriaPayload(
      String searchType,
      String query,
      String province) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  private record WatchMetadata(
      String kind,
      String propertyId,
      String titleDeed,
      DeedsModels.PropertySnapshot baseline,
      List<String> alertTypes,
      int knownPortionsCount) {}
}
