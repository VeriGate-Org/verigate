package verigate.webbff.deeds.model;

import java.util.List;

public final class DeedsModels {

  private DeedsModels() {}

  public record AreaReportRequest(
      String province,
      String township,
      String transferDateFrom,
      String transferDateTo,
      Boolean municipalFlagsOnly) {}

  public record AreaReportResponse(
      Summary summary,
      List<AreaBreakdownItem> areas,
      List<TransferItem> recentTransfers,
      String generatedAt) {}

  public record Summary(
      int totalProperties,
      int totalOwners,
      int totalActiveBonds,
      int totalMunicipalFlags,
      int totalTransfers) {}

  public record AreaBreakdownItem(
      String province,
      String township,
      int properties,
      int owners,
      int activeBonds,
      int municipalFlags,
      int transfers) {}

  public record TransferItem(
      String propertyId,
      String titleDeed,
      String ownerName,
      String province,
      String township,
      String transferDate,
      Double transferAmount) {}

  public record PropertyReportResponse(
      PropertySnapshot property,
      List<PropertyTimelineItem> timeline,
      Summary summary,
      List<DocumentDescriptor> documents,
      List<String> recommendedWatchAlerts,
      String generatedAt) {}

  public record PropertySnapshot(
      String propertyId,
      int erfNumber,
      int portion,
      String township,
      String province,
      String titleDeed,
      String deedNumber,
      String registrationDate,
      String ownerName,
      String ownerIdNumber,
      String streetAddress,
      List<BondSnapshot> currentBonds,
      TransferSnapshot lastTransfer,
      MunicipalSnapshot municipal) {}

  public record BondSnapshot(
      String bondholder,
      Double amount,
      String registered) {}

  public record TransferSnapshot(
      String date,
      Double amount) {}

  public record MunicipalSnapshot(
      String accountNumber,
      Double arrears,
      boolean ratesFlag) {}

  public record PropertyTimelineItem(
      String observedAt,
      String ownerName,
      String ownerIdNumber,
      String titleDeed,
      String transferDate,
      Double transferAmount,
      int bondCount,
      boolean municipalFlag) {}

  public record DocumentManifestResponse(
      String propertyId,
      String titleDeed,
      List<DocumentDescriptor> documents,
      String providerStatus) {}

  public record DocumentDescriptor(
      String type,
      String label,
      String reference,
      boolean downloadable,
      String status,
      String note) {}

  public record WatchRequest(
      String propertyId,
      String monitoringFrequency,
      List<String> alertTypes) {}

  public record WatchUpdateRequest(
      String status,
      String monitoringFrequency,
      List<String> alertTypes) {}

  public record WatchResponse(
      String subjectId,
      String propertyId,
      String subjectName,
      String titleDeed,
      String monitoringFrequency,
      String status,
      List<String> alertTypes,
      String createdAt) {}

  public record WatchAlertResponse(
      String alertId,
      String subjectId,
      String propertyId,
      String severity,
      String alertType,
      String title,
      String description,
      String createdAt) {}

  public record SavedReportRequest(
      String name,
      String reportType,
      AreaReportRequest filter,
      ScheduleConfig schedule,
      String exportFormat,
      Boolean currentReport,
      Boolean autoRefresh) {}

  public record SavedReportUpdateRequest(
      String name,
      AreaReportRequest filter,
      ScheduleConfig schedule,
      String exportFormat,
      String status,
      Boolean currentReport,
      Boolean autoRefresh) {}

  public record ScheduleConfig(
      String frequency,
      String time,
      List<String> recipients) {}

  public record SavedReportResponse(
      String id,
      String name,
      String reportType,
      String status,
      AreaReportRequest filter,
      ScheduleConfig schedule,
      String exportFormat,
      boolean currentReport,
      boolean autoRefresh,
      Summary latestSummary,
      String lastGeneratedAt,
      String nextRunAt,
      String createdAt,
      String updatedAt) {}

  public record ExportHistoryResponse(
      String id,
      String reportId,
      String reportName,
      String format,
      String status,
      int recordCount,
      String generatedAt,
      String createdAt) {}

  public record TeamMemberRequest(
      String name,
      String email,
      String role,
      List<String> permissions,
      String status) {}

  public record TeamMemberResponse(
      String id,
      String name,
      String email,
      String role,
      List<String> permissions,
      String status,
      String createdAt,
      String updatedAt) {}

  public record AuditEventResponse(
      String id,
      String category,
      String action,
      String actor,
      String targetId,
      String targetType,
      String detail,
      String createdAt) {}

  public record MapSearchRequest(
      String province,
      String township,
      String streetName,
      String query) {}

  public record BoundaryResponse(
      String boundaryId,
      String province,
      String municipality,
      String label,
      List<Coordinate> polygon) {}

  public record Coordinate(
      double lat,
      double lng) {}

  public record MapPropertyResponse(
      String propertyId,
      String label,
      String province,
      String township,
      String streetAddress,
      String titleDeed,
      Coordinate centroid,
      List<Coordinate> outline,
      String ownerName,
      boolean municipalFlag) {}

  public record MapSearchResponse(
      List<BoundaryResponse> boundaries,
      List<MapPropertyResponse> properties,
      String generatedAt) {}

  public record ConversionRequest(
      String province,
      String township,
      String streetName,
      String erfNumber,
      String portion,
      String direction) {}

  public record ConversionCandidate(
      String propertyId,
      String streetAddress,
      String township,
      String province,
      Integer erfNumber,
      Integer portion,
      String titleDeed,
      double confidence,
      String reason) {}

  public record ConversionResponse(
      String direction,
      String normalizedInput,
      List<ConversionCandidate> candidates,
      String generatedAt) {}

  public record ComparableSale(
      String propertyId,
      String titleDeed,
      String township,
      String transferDate,
      Double transferAmount,
      Double similarityScore) {}

  public record ValuationRequest(
      String propertyId,
      String province,
      String township,
      String titleDeed,
      Integer erfNumber,
      Integer portion) {}

  public record ValuationResponse(
      String propertyId,
      Double estimatedValue,
      Double lowerBound,
      Double upperBound,
      String confidenceBand,
      String methodology,
      String disclaimer,
      List<ComparableSale> comparableSales,
      String generatedAt) {}

  public record OperationsRefreshResponse(
      int refreshedReports,
      int recalculatedWatches,
      String nextScheduledRefreshAt,
      String generatedAt) {}

  public record ExportResponse(
      String fileName,
      String contentType,
      String content) {}
}
