package verigate.webbff.admin.service.health;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import verigate.webbff.admin.model.health.ExternalServiceHealth;
import verigate.webbff.admin.model.health.ExternalServiceHealth.ProbeResult;
import verigate.webbff.config.properties.HealthCheckProperties;

class ExternalServiceProberTest {

  private ExternalServiceProber prober;

  @BeforeEach
  void setUp() {
    HealthCheckProperties properties = new HealthCheckProperties();
    properties.setConnectTimeoutMs(2000);
    properties.setReadTimeoutMs(3000);
    prober = new ExternalServiceProber(properties);
  }

  @Test
  void dnsProbeSucceedsForValidHostname() {
    ProbeResult result = prober.probeDns("google.com");
    assertTrue(result.success());
    assertTrue(result.latencyMs() >= 0);
    assertFalse(result.detail().isEmpty());
  }

  @Test
  void dnsProbeFailsForInvalidHostname() {
    ProbeResult result = prober.probeDns("this-host-does-not-exist-xyz123.invalid");
    assertFalse(result.success());
  }

  @Test
  void unconfiguredServiceReportsUnconfigured() {
    HealthCheckProperties.ServiceEntry entry = new HealthCheckProperties.ServiceEntry();
    entry.setName("Test Service");
    entry.setUrl("");

    ExternalServiceHealth result = prober.probe("test", entry);
    assertEquals("UNCONFIGURED", result.overallStatus());
  }

  @Test
  void invalidUrlReportsDown() {
    HealthCheckProperties.ServiceEntry entry = new HealthCheckProperties.ServiceEntry();
    entry.setName("Bad Service");
    entry.setUrl("not-a-valid-url");

    ExternalServiceHealth result = prober.probe("bad", entry);
    assertEquals("DOWN", result.overallStatus());
  }

  @Test
  void tcpProbeFailsForUnreachablePort() {
    ProbeResult result = prober.probeTcp("127.0.0.1", 19999);
    assertFalse(result.success());
  }
}
