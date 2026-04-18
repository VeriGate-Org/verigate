package verigate.webbff.admin.service.health;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import verigate.webbff.admin.model.health.ExternalServiceHealth;
import verigate.webbff.admin.model.health.HealthHistoryResponse.IncidentResponse;
import verigate.webbff.admin.model.health.HealthHistoryResponse.ServiceHistoryResponse;
import verigate.webbff.admin.model.health.HealthHistoryResponse.UptimeResponse;
import verigate.webbff.admin.model.health.HealthSnapshotItem;
import verigate.webbff.admin.model.health.InfrastructureHealth;
import verigate.webbff.admin.model.health.SystemHealthResponse;
import verigate.webbff.admin.repository.HealthSnapshotRepository;
import verigate.webbff.admin.service.SystemHealthService;

@Service
public class HealthSnapshotService {

  private static final Logger logger = LoggerFactory.getLogger(HealthSnapshotService.class);
  private static final long TTL_DAYS = 365;
  private static final long INCIDENT_MERGE_THRESHOLD_MINUTES = 30;

  private final SystemHealthService systemHealthService;
  private final HealthSnapshotRepository repository;
  private final ObjectMapper objectMapper;

  public HealthSnapshotService(
      SystemHealthService systemHealthService,
      HealthSnapshotRepository repository,
      ObjectMapper objectMapper) {
    this.systemHealthService = systemHealthService;
    this.repository = repository;
    this.objectMapper = objectMapper;
  }

  public int captureSnapshot() {
    logger.info("Capturing health snapshot");
    SystemHealthResponse health = systemHealthService.checkHealth();
    Instant now = Instant.now();
    String checkedAt = now.toString();
    long ttl = now.plus(Duration.ofDays(TTL_DAYS)).getEpochSecond();

    List<HealthSnapshotItem> items = new ArrayList<>();

    // External services
    for (ExternalServiceHealth ext : health.externalIntegrations()) {
      HealthSnapshotItem item = new HealthSnapshotItem();
      item.setServiceId("ext:" + ext.id());
      item.setCheckedAt(checkedAt);
      item.setStatus(ext.overallStatus());
      item.setLatencyMs(ext.totalLatencyMs());
      item.setDetail(serializeDetail(Map.of(
          "name", ext.name(),
          "protocol", ext.protocol(),
          "dnsOk", ext.dns().success(),
          "tcpOk", ext.tcp().success(),
          "httpOk", ext.http().success())));
      item.setTtl(ttl);
      items.add(item);
    }

    // DynamoDB tables
    InfrastructureHealth infra = health.infrastructure();
    if (infra.dynamoDbTables() != null) {
      for (var table : infra.dynamoDbTables()) {
        HealthSnapshotItem item = new HealthSnapshotItem();
        item.setServiceId("infra:dynamodb:" + table.tableName());
        item.setCheckedAt(checkedAt);
        item.setStatus("ERROR".equals(table.status()) ? "DOWN" : "HEALTHY");
        item.setLatencyMs(table.latencyMs());
        item.setDetail(serializeDetail(Map.of("tableName", table.tableName())));
        item.setTtl(ttl);
        items.add(item);
      }
    }

    // SQS queues
    if (infra.sqsQueues() != null) {
      for (var queue : infra.sqsQueues()) {
        HealthSnapshotItem item = new HealthSnapshotItem();
        item.setServiceId("infra:sqs:" + queue.queueName());
        item.setCheckedAt(checkedAt);
        item.setStatus("ERROR".equals(queue.status()) ? "DOWN" : "HEALTHY");
        item.setLatencyMs(queue.latencyMs());
        item.setDetail(serializeDetail(Map.of("queueName", queue.queueName())));
        item.setTtl(ttl);
        items.add(item);
      }
    }

    // Kinesis
    if (infra.kinesisStream() != null && !"UNCONFIGURED".equals(infra.kinesisStream().status())) {
      HealthSnapshotItem item = new HealthSnapshotItem();
      item.setServiceId("infra:kinesis:" + infra.kinesisStream().streamName());
      item.setCheckedAt(checkedAt);
      item.setStatus("ERROR".equals(infra.kinesisStream().status()) ? "DOWN" : "HEALTHY");
      item.setLatencyMs(infra.kinesisStream().latencyMs());
      item.setDetail(serializeDetail(Map.of("streamName", infra.kinesisStream().streamName())));
      item.setTtl(ttl);
      items.add(item);
    }

    // Bedrock
    if (infra.bedrock() != null) {
      HealthSnapshotItem item = new HealthSnapshotItem();
      item.setServiceId("infra:bedrock");
      item.setCheckedAt(checkedAt);
      item.setStatus("ERROR".equals(infra.bedrock().status()) ? "DEGRADED" : "HEALTHY");
      item.setLatencyMs(infra.bedrock().latencyMs());
      item.setDetail(serializeDetail(Map.of("region", infra.bedrock().region())));
      item.setTtl(ttl);
      items.add(item);
    }

    if (!items.isEmpty()) {
      repository.saveAll(items);
    }
    logger.info("Captured {} health snapshot items", items.size());
    return items.size();
  }

  public ServiceHistoryResponse getServiceHistory(String serviceId, String from, String to) {
    List<HealthSnapshotItem> snapshots = repository.findByServiceId(serviceId, from, to);
    List<ServiceHistoryResponse.DataPoint> dataPoints = snapshots.stream()
        .map(s -> new ServiceHistoryResponse.DataPoint(
            s.getCheckedAt(), s.getStatus(), s.getLatencyMs()))
        .toList();
    return new ServiceHistoryResponse(serviceId, dataPoints);
  }

  public List<UptimeResponse> getUptime(String from, String to) {
    // Query all DOWN and DEGRADED to find problematic services, then query each unique service
    List<HealthSnapshotItem> downItems = repository.findByStatus("DOWN", from, to);
    List<HealthSnapshotItem> degradedItems = repository.findByStatus("DEGRADED", from, to);

    // Collect all unique service IDs from degraded/down items
    java.util.Set<String> allServiceIds = new java.util.LinkedHashSet<>();
    downItems.forEach(i -> allServiceIds.add(i.getServiceId()));
    degradedItems.forEach(i -> allServiceIds.add(i.getServiceId()));

    // For each known service, query its full history to compute uptime
    List<UptimeResponse> results = new ArrayList<>();
    for (String serviceId : allServiceIds) {
      List<HealthSnapshotItem> history = repository.findByServiceId(serviceId, from, to);
      results.add(computeUptime(serviceId, history, from, to));
    }

    return results;
  }

  public UptimeResponse getUptimeForService(String serviceId, String from, String to) {
    List<HealthSnapshotItem> history = repository.findByServiceId(serviceId, from, to);
    return computeUptime(serviceId, history, from, to);
  }

  public List<IncidentResponse> getIncidents(String from, String to) {
    List<HealthSnapshotItem> downItems = repository.findByStatus("DOWN", from, to);
    List<HealthSnapshotItem> degradedItems = repository.findByStatus("DEGRADED", from, to);

    List<HealthSnapshotItem> allIncidentItems = new ArrayList<>();
    allIncidentItems.addAll(downItems);
    allIncidentItems.addAll(degradedItems);
    allIncidentItems.sort(Comparator.comparing(HealthSnapshotItem::getCheckedAt));

    // Group by service and merge consecutive incident windows
    Map<String, List<HealthSnapshotItem>> byService = new TreeMap<>();
    for (HealthSnapshotItem item : allIncidentItems) {
      byService.computeIfAbsent(item.getServiceId(), k -> new ArrayList<>()).add(item);
    }

    List<IncidentResponse> incidents = new ArrayList<>();
    for (var entry : byService.entrySet()) {
      String serviceId = entry.getKey();
      List<HealthSnapshotItem> serviceItems = entry.getValue();

      HealthSnapshotItem currentStart = serviceItems.get(0);
      HealthSnapshotItem currentEnd = serviceItems.get(0);

      for (int i = 1; i < serviceItems.size(); i++) {
        HealthSnapshotItem next = serviceItems.get(i);
        Instant currentEndTime = Instant.parse(currentEnd.getCheckedAt());
        Instant nextTime = Instant.parse(next.getCheckedAt());
        long gapMinutes = Duration.between(currentEndTime, nextTime).toMinutes();

        if (gapMinutes <= INCIDENT_MERGE_THRESHOLD_MINUTES) {
          currentEnd = next;
        } else {
          incidents.add(buildIncident(serviceId, currentStart, currentEnd));
          currentStart = next;
          currentEnd = next;
        }
      }
      incidents.add(buildIncident(serviceId, currentStart, currentEnd));
    }

    incidents.sort(Comparator.comparing(IncidentResponse::startedAt).reversed());
    return incidents;
  }

  private UptimeResponse computeUptime(
      String serviceId, List<HealthSnapshotItem> history, String from, String to) {
    int totalChecks = history.size();
    long healthyChecks = history.stream().filter(s -> "HEALTHY".equals(s.getStatus())).count();
    double uptimePercentage = totalChecks > 0 ? (healthyChecks * 100.0) / totalChecks : 100.0;

    return new UptimeResponse(
        serviceId,
        Math.round(uptimePercentage * 100.0) / 100.0,
        totalChecks,
        (int) healthyChecks,
        from + " to " + to);
  }

  private IncidentResponse buildIncident(
      String serviceId, HealthSnapshotItem start, HealthSnapshotItem end) {
    Instant startTime = Instant.parse(start.getCheckedAt());
    Instant endTime = Instant.parse(end.getCheckedAt());
    long durationMinutes = Duration.between(startTime, endTime).toMinutes();
    if (durationMinutes == 0) {
      durationMinutes = 15; // single snapshot = at least one interval
    }
    return new IncidentResponse(
        serviceId,
        serviceId,
        start.getCheckedAt(),
        end.getCheckedAt(),
        durationMinutes,
        start.getStatus());
  }

  private String serializeDetail(Map<String, Object> detail) {
    try {
      return objectMapper.writeValueAsString(detail);
    } catch (JsonProcessingException e) {
      logger.warn("Failed to serialize detail: {}", e.getMessage());
      return "{}";
    }
  }
}
