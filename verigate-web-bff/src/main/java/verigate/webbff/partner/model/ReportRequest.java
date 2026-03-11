package verigate.webbff.partner.model;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record ReportRequest(
    @NotBlank String name,
    @NotBlank String type,
    String description,
    ReportFilter filter,
    ScheduleConfig schedule) {

  public record ReportFilter(
      String dateRange,
      String startDate,
      String endDate,
      List<String> status,
      List<String> verificationTypes) {}

  public record ScheduleConfig(
      String frequency,
      String time,
      List<String> recipients) {}
}
