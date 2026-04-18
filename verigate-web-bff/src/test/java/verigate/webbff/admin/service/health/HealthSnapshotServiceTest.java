package verigate.webbff.admin.service.health;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import verigate.webbff.admin.model.health.ExternalServiceHealth;
import verigate.webbff.admin.model.health.HealthHistoryResponse.IncidentResponse;
import verigate.webbff.admin.model.health.HealthHistoryResponse.UptimeResponse;
import verigate.webbff.admin.model.health.HealthSnapshotItem;
import verigate.webbff.admin.model.health.InfrastructureHealth;
import verigate.webbff.admin.model.health.SystemHealthResponse;
import verigate.webbff.admin.repository.HealthSnapshotRepository;
import verigate.webbff.admin.service.SystemHealthService;

@ExtendWith(MockitoExtension.class)
class HealthSnapshotServiceTest {

  @Mock private SystemHealthService systemHealthService;
  @Mock private HealthSnapshotRepository repository;
  @Captor private ArgumentCaptor<List<HealthSnapshotItem>> itemsCaptor;

  private HealthSnapshotService service;

  @BeforeEach
  void setUp() {
    service = new HealthSnapshotService(systemHealthService, repository, new ObjectMapper());
  }

  @Test
  void captureSnapshotConvertsExternalServicesToItems() {
    ExternalServiceHealth ext = new ExternalServiceHealth(
        "deedsweb", "DeedsWeb SOAP", "SOAP", "https://deedsweb.example.com",
        new ExternalServiceHealth.ProbeResult(true, 50, "OK"),
        new ExternalServiceHealth.ProbeResult(true, 30, "OK"),
        new ExternalServiceHealth.ProbeResult(true, 100, "OK"),
        "HEALTHY", 180);

    SystemHealthResponse response = new SystemHealthResponse(
        "HEALTHY",
        new SystemHealthResponse.Summary(1, 1, 0, 0, 0),
        List.of(ext),
        new InfrastructureHealth(List.of(), List.of(), null, null),
        Instant.now());

    when(systemHealthService.checkHealth()).thenReturn(response);

    int count = service.captureSnapshot();

    verify(repository).saveAll(itemsCaptor.capture());
    List<HealthSnapshotItem> items = itemsCaptor.getValue();
    assertThat(count).isEqualTo(1);
    assertThat(items).hasSize(1);
    assertThat(items.get(0).getServiceId()).isEqualTo("ext:deedsweb");
    assertThat(items.get(0).getStatus()).isEqualTo("HEALTHY");
    assertThat(items.get(0).getLatencyMs()).isEqualTo(180);
    assertThat(items.get(0).getTtl()).isGreaterThan(0);
  }

  @Test
  void captureSnapshotIncludesInfrastructureComponents() {
    var dynamoTable = new InfrastructureHealth.DynamoDbTableHealth(
        "commands-table", "ACTIVE", 100L, 5000L, 25, null);
    var sqsQueue = new InfrastructureHealth.SqsQueueHealth(
        "verify-party", "ACTIVE", 5L, 0L, 10, null);

    SystemHealthResponse response = new SystemHealthResponse(
        "HEALTHY",
        new SystemHealthResponse.Summary(2, 2, 0, 0, 0),
        List.of(),
        new InfrastructureHealth(List.of(dynamoTable), List.of(sqsQueue), null, null),
        Instant.now());

    when(systemHealthService.checkHealth()).thenReturn(response);

    int count = service.captureSnapshot();

    verify(repository).saveAll(itemsCaptor.capture());
    List<HealthSnapshotItem> items = itemsCaptor.getValue();
    assertThat(count).isEqualTo(2);
    assertThat(items.get(0).getServiceId()).isEqualTo("infra:dynamodb:commands-table");
    assertThat(items.get(0).getStatus()).isEqualTo("HEALTHY");
    assertThat(items.get(1).getServiceId()).isEqualTo("infra:sqs:verify-party");
  }

  @Test
  void getUptimeCalculatesPercentageAccurately() {
    String from = "2025-01-01T00:00:00Z";
    String to = "2025-01-02T00:00:00Z";
    String serviceId = "ext:deedsweb";

    List<HealthSnapshotItem> history = new ArrayList<>();
    for (int i = 0; i < 96; i++) { // 96 checks in 24 hours at 15-min intervals
      HealthSnapshotItem item = new HealthSnapshotItem();
      item.setServiceId(serviceId);
      item.setCheckedAt(Instant.parse(from).plusSeconds(i * 900L).toString());
      item.setStatus(i < 90 ? "HEALTHY" : "DOWN"); // 90 healthy, 6 down
      item.setLatencyMs(100);
      history.add(item);
    }

    when(repository.findByServiceId(serviceId, from, to)).thenReturn(history);

    UptimeResponse uptime = service.getUptimeForService(serviceId, from, to);

    assertThat(uptime.serviceId()).isEqualTo(serviceId);
    assertThat(uptime.totalChecks()).isEqualTo(96);
    assertThat(uptime.healthyChecks()).isEqualTo(90);
    assertThat(uptime.uptimePercentage()).isEqualTo(93.75);
  }

  @Test
  void getIncidentsMergesConsecutiveDownSnapshots() {
    String from = "2025-01-15T00:00:00Z";
    String to = "2025-01-16T00:00:00Z";

    // Three consecutive DOWN snapshots 15 min apart → should merge into one incident
    List<HealthSnapshotItem> downItems = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      HealthSnapshotItem item = new HealthSnapshotItem();
      item.setServiceId("ext:deedsweb");
      item.setCheckedAt(Instant.parse("2025-01-15T10:00:00Z").plusSeconds(i * 900L).toString());
      item.setStatus("DOWN");
      item.setLatencyMs(0);
      downItems.add(item);
    }

    when(repository.findByStatus("DOWN", from, to)).thenReturn(downItems);
    when(repository.findByStatus("DEGRADED", from, to)).thenReturn(List.of());

    List<IncidentResponse> incidents = service.getIncidents(from, to);

    assertThat(incidents).hasSize(1);
    assertThat(incidents.get(0).serviceId()).isEqualTo("ext:deedsweb");
    assertThat(incidents.get(0).startedAt()).isEqualTo("2025-01-15T10:00:00Z");
    assertThat(incidents.get(0).endedAt()).isEqualTo("2025-01-15T10:30:00Z");
    assertThat(incidents.get(0).durationMinutes()).isEqualTo(30);
    assertThat(incidents.get(0).status()).isEqualTo("DOWN");
  }

  @Test
  void getIncidentsSplitsNonConsecutiveDownSnapshots() {
    String from = "2025-01-15T00:00:00Z";
    String to = "2025-01-16T00:00:00Z";

    List<HealthSnapshotItem> downItems = new ArrayList<>();

    // First incident
    HealthSnapshotItem item1 = new HealthSnapshotItem();
    item1.setServiceId("ext:deedsweb");
    item1.setCheckedAt("2025-01-15T08:00:00Z");
    item1.setStatus("DOWN");
    downItems.add(item1);

    // Second incident (2 hours later — beyond merge threshold)
    HealthSnapshotItem item2 = new HealthSnapshotItem();
    item2.setServiceId("ext:deedsweb");
    item2.setCheckedAt("2025-01-15T10:00:00Z");
    item2.setStatus("DOWN");
    downItems.add(item2);

    when(repository.findByStatus("DOWN", from, to)).thenReturn(downItems);
    when(repository.findByStatus("DEGRADED", from, to)).thenReturn(List.of());

    List<IncidentResponse> incidents = service.getIncidents(from, to);

    assertThat(incidents).hasSize(2);
  }
}
