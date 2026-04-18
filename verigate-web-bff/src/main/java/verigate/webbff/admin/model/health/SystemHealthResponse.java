package verigate.webbff.admin.model.health;

import java.time.Instant;
import java.util.List;

public record SystemHealthResponse(
    String overallStatus,
    Summary summary,
    List<ExternalServiceHealth> externalIntegrations,
    InfrastructureHealth infrastructure,
    Instant checkedAt
) {

  public record Summary(
      int total,
      int healthy,
      int degraded,
      int down,
      int unconfigured
  ) {}
}
