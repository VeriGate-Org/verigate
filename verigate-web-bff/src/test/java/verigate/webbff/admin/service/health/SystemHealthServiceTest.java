package verigate.webbff.admin.service.health;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import verigate.webbff.admin.model.health.ExternalServiceHealth;
import verigate.webbff.admin.model.health.ExternalServiceHealth.ProbeResult;
import verigate.webbff.admin.model.health.InfrastructureHealth;
import verigate.webbff.admin.model.health.SystemHealthResponse;
import verigate.webbff.admin.service.SystemHealthService;
import verigate.webbff.config.properties.HealthCheckProperties;

@ExtendWith(MockitoExtension.class)
class SystemHealthServiceTest {

  @Mock private ExternalServiceProber externalServiceProber;
  @Mock private AwsInfrastructureProber awsInfrastructureProber;
  @Mock private BedrockProber bedrockProber;

  private SystemHealthService service;

  @BeforeEach
  void setUp() {
    HealthCheckProperties properties = new HealthCheckProperties();
    properties.setOverallTimeoutSeconds(10);
    service = new SystemHealthService(
        externalServiceProber, awsInfrastructureProber, bedrockProber, properties
    );
  }

  @Test
  void allHealthyReturnsHealthyStatus() {
    when(externalServiceProber.probeAll()).thenReturn(List.of(
        healthyService("svc1", "Service 1"),
        healthyService("svc2", "Service 2")
    ));
    when(awsInfrastructureProber.probeDynamoDb()).thenReturn(List.of(
        new InfrastructureHealth.DynamoDbTableHealth("table1", "ACTIVE", 100L, 5000L, 10, null)
    ));
    when(awsInfrastructureProber.probeSqs()).thenReturn(List.of(
        new InfrastructureHealth.SqsQueueHealth("queue1", "HEALTHY", 0L, 0L, 8, null)
    ));
    when(awsInfrastructureProber.probeKinesis()).thenReturn(
        new InfrastructureHealth.KinesisStreamHealth("stream1", "ACTIVE", 2, 5, null)
    );
    when(bedrockProber.probe()).thenReturn(
        new InfrastructureHealth.BedrockHealth("us-east-1", "HEALTHY", 50, null)
    );

    SystemHealthResponse response = service.checkHealth();

    assertEquals("HEALTHY", response.overallStatus());
    assertNotNull(response.checkedAt());
    assertEquals(6, response.summary().total());
    assertEquals(6, response.summary().healthy());
    assertEquals(0, response.summary().down());
  }

  @Test
  void someDownReturnsDegradedStatus() {
    when(externalServiceProber.probeAll()).thenReturn(List.of(
        healthyService("svc1", "Service 1"),
        downService("svc2", "Service 2")
    ));
    when(awsInfrastructureProber.probeDynamoDb()).thenReturn(List.of());
    when(awsInfrastructureProber.probeSqs()).thenReturn(List.of());
    when(awsInfrastructureProber.probeKinesis()).thenReturn(
        new InfrastructureHealth.KinesisStreamHealth("", "UNCONFIGURED", null, 0, null)
    );
    when(bedrockProber.probe()).thenReturn(
        new InfrastructureHealth.BedrockHealth("us-east-1", "HEALTHY", 50, null)
    );

    SystemHealthResponse response = service.checkHealth();

    assertEquals("DEGRADED", response.overallStatus());
  }

  @Test
  void allDownReturnsDownStatus() {
    when(externalServiceProber.probeAll()).thenReturn(List.of(
        downService("svc1", "Service 1"),
        downService("svc2", "Service 2")
    ));
    when(awsInfrastructureProber.probeDynamoDb()).thenReturn(List.of());
    when(awsInfrastructureProber.probeSqs()).thenReturn(List.of());
    when(awsInfrastructureProber.probeKinesis()).thenReturn(
        new InfrastructureHealth.KinesisStreamHealth("", "UNCONFIGURED", null, 0, null)
    );
    when(bedrockProber.probe()).thenReturn(
        new InfrastructureHealth.BedrockHealth("us-east-1", "ERROR", 0, "Connection refused")
    );

    SystemHealthResponse response = service.checkHealth();

    assertEquals("DOWN", response.overallStatus());
  }

  private ExternalServiceHealth healthyService(String id, String name) {
    return new ExternalServiceHealth(
        id, name, "REST", "https://example.com",
        new ProbeResult(true, 10, "1.2.3.4"),
        new ProbeResult(true, 15, "Connected"),
        new ProbeResult(true, 50, "HTTP 200"),
        "HEALTHY", 75
    );
  }

  private ExternalServiceHealth downService(String id, String name) {
    return new ExternalServiceHealth(
        id, name, "REST", "https://example.com",
        new ProbeResult(false, 3000, "DNS lookup failed"),
        new ProbeResult(false, 0, "Skipped"),
        new ProbeResult(false, 0, "Skipped"),
        "DOWN", 3000
    );
  }
}
