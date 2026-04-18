package verigate.webbff.admin.controller;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import verigate.webbff.admin.model.health.HealthHistoryResponse.IncidentResponse;
import verigate.webbff.admin.model.health.HealthHistoryResponse.ServiceHistoryResponse;
import verigate.webbff.admin.model.health.HealthHistoryResponse.UptimeResponse;
import verigate.webbff.admin.model.health.SystemHealthResponse;
import verigate.webbff.admin.service.SystemHealthService;
import verigate.webbff.admin.service.health.HealthSnapshotService;

@RestController
@RequestMapping("/api/admin")
public class SystemHealthController {

  private static final Logger logger = LoggerFactory.getLogger(SystemHealthController.class);

  private final SystemHealthService systemHealthService;
  private final HealthSnapshotService healthSnapshotService;

  public SystemHealthController(
      SystemHealthService systemHealthService,
      HealthSnapshotService healthSnapshotService) {
    this.systemHealthService = systemHealthService;
    this.healthSnapshotService = healthSnapshotService;
  }

  @GetMapping("/system-health")
  public ResponseEntity<SystemHealthResponse> getSystemHealth() {
    logger.info("System health check requested");
    SystemHealthResponse response = systemHealthService.checkHealth();

    if ("DOWN".equals(response.overallStatus())) {
      return ResponseEntity.status(503).body(response);
    }
    return ResponseEntity.ok(response);
  }

  @PostMapping("/system-health/snapshot")
  public ResponseEntity<Void> captureSnapshot() {
    logger.info("Health snapshot capture triggered");
    int count = healthSnapshotService.captureSnapshot();
    logger.info("Captured {} snapshot items", count);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping("/system-health/history")
  public ResponseEntity<ServiceHistoryResponse> getServiceHistory(
      @RequestParam String serviceId,
      @RequestParam(defaultValue = "24h") String range) {
    var timeRange = resolveTimeRange(range);
    ServiceHistoryResponse response = healthSnapshotService.getServiceHistory(
        serviceId, timeRange.from, timeRange.to);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/system-health/uptime")
  public ResponseEntity<List<UptimeResponse>> getUptime(
      @RequestParam(defaultValue = "24h") String range) {
    var timeRange = resolveTimeRange(range);
    List<UptimeResponse> response = healthSnapshotService.getUptime(timeRange.from, timeRange.to);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/system-health/incidents")
  public ResponseEntity<List<IncidentResponse>> getIncidents(
      @RequestParam(defaultValue = "24h") String range) {
    var timeRange = resolveTimeRange(range);
    List<IncidentResponse> response = healthSnapshotService.getIncidents(timeRange.from, timeRange.to);
    return ResponseEntity.ok(response);
  }

  private record TimeRange(String from, String to) {}

  private TimeRange resolveTimeRange(String range) {
    Instant now = Instant.now();
    Duration duration = switch (range) {
      case "7d" -> Duration.ofDays(7);
      case "30d" -> Duration.ofDays(30);
      default -> Duration.ofHours(24);
    };
    return new TimeRange(now.minus(duration).toString(), now.toString());
  }
}
