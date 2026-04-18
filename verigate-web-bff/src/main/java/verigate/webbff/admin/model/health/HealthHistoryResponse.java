package verigate.webbff.admin.model.health;

import java.util.List;

public final class HealthHistoryResponse {

  private HealthHistoryResponse() {}

  public record ServiceHistoryResponse(
      String serviceId,
      List<DataPoint> dataPoints
  ) {
    public record DataPoint(
        String checkedAt,
        String status,
        long latencyMs
    ) {}
  }

  public record UptimeResponse(
      String serviceId,
      double uptimePercentage,
      int totalChecks,
      int healthyChecks,
      String timeRange
  ) {}

  public record IncidentResponse(
      String serviceId,
      String serviceName,
      String startedAt,
      String endedAt,
      long durationMinutes,
      String status
  ) {}
}
