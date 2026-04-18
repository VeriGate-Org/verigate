package verigate.webbff.admin.model.health;

public record ExternalServiceHealth(
    String id,
    String name,
    String protocol,
    String url,
    ProbeResult dns,
    ProbeResult tcp,
    ProbeResult http,
    String overallStatus,
    long totalLatencyMs
) {

  public record ProbeResult(
      boolean success,
      long latencyMs,
      String detail
  ) {}
}
