package verigate.webbff.partner.model;

public record ReportResponse(
    String id,
    String partnerId,
    String name,
    String type,
    String description,
    String status,
    ReportRequest.ReportFilter filter,
    ReportRequest.ScheduleConfig schedule,
    String createdAt) {}
