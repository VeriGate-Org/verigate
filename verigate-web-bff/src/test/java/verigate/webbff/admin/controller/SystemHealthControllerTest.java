package verigate.webbff.admin.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import verigate.webbff.admin.model.health.HealthHistoryResponse.IncidentResponse;
import verigate.webbff.admin.model.health.HealthHistoryResponse.ServiceHistoryResponse;
import verigate.webbff.admin.model.health.HealthHistoryResponse.UptimeResponse;
import verigate.webbff.admin.model.health.InfrastructureHealth;
import verigate.webbff.admin.model.health.SystemHealthResponse;
import verigate.webbff.admin.service.SystemHealthService;
import verigate.webbff.admin.service.health.HealthSnapshotService;

@WebMvcTest(controllers = SystemHealthController.class)
@AutoConfigureMockMvc(addFilters = false)
class SystemHealthControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private SystemHealthService systemHealthService;
  @MockBean private HealthSnapshotService healthSnapshotService;

  @Test
  void healthySystemReturns200() throws Exception {
    SystemHealthResponse response = new SystemHealthResponse(
        "HEALTHY",
        new SystemHealthResponse.Summary(5, 5, 0, 0, 0),
        List.of(),
        new InfrastructureHealth(List.of(), List.of(), null, null),
        Instant.now()
    );
    when(systemHealthService.checkHealth()).thenReturn(response);

    mockMvc.perform(get("/api/admin/system-health"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.overallStatus").value("HEALTHY"))
        .andExpect(jsonPath("$.summary.healthy").value(5))
        .andExpect(jsonPath("$.summary.down").value(0));
  }

  @Test
  void degradedSystemReturns200() throws Exception {
    SystemHealthResponse response = new SystemHealthResponse(
        "DEGRADED",
        new SystemHealthResponse.Summary(5, 3, 1, 1, 0),
        List.of(),
        new InfrastructureHealth(List.of(), List.of(), null, null),
        Instant.now()
    );
    when(systemHealthService.checkHealth()).thenReturn(response);

    mockMvc.perform(get("/api/admin/system-health"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.overallStatus").value("DEGRADED"))
        .andExpect(jsonPath("$.summary.degraded").value(1));
  }

  @Test
  void downSystemReturns503() throws Exception {
    SystemHealthResponse response = new SystemHealthResponse(
        "DOWN",
        new SystemHealthResponse.Summary(5, 0, 0, 5, 0),
        List.of(),
        new InfrastructureHealth(List.of(), List.of(), null, null),
        Instant.now()
    );
    when(systemHealthService.checkHealth()).thenReturn(response);

    mockMvc.perform(get("/api/admin/system-health"))
        .andExpect(status().isServiceUnavailable())
        .andExpect(jsonPath("$.overallStatus").value("DOWN"))
        .andExpect(jsonPath("$.summary.down").value(5));
  }

  @Test
  void snapshotReturns201() throws Exception {
    when(healthSnapshotService.captureSnapshot()).thenReturn(5);

    mockMvc.perform(post("/api/admin/system-health/snapshot"))
        .andExpect(status().isCreated());
  }

  @Test
  void historyReturnsDataPoints() throws Exception {
    var response = new ServiceHistoryResponse("ext:deedsweb", List.of(
        new ServiceHistoryResponse.DataPoint("2025-01-15T10:00:00Z", "HEALTHY", 150)));
    when(healthSnapshotService.getServiceHistory(anyString(), anyString(), anyString()))
        .thenReturn(response);

    mockMvc.perform(get("/api/admin/system-health/history")
            .param("serviceId", "ext:deedsweb")
            .param("range", "24h"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.serviceId").value("ext:deedsweb"))
        .andExpect(jsonPath("$.dataPoints[0].status").value("HEALTHY"));
  }

  @Test
  void uptimeReturnsPercentages() throws Exception {
    when(healthSnapshotService.getUptime(anyString(), anyString()))
        .thenReturn(List.of(
            new UptimeResponse("ext:deedsweb", 99.5, 672, 668, "24h")));

    mockMvc.perform(get("/api/admin/system-health/uptime").param("range", "24h"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].serviceId").value("ext:deedsweb"))
        .andExpect(jsonPath("$[0].uptimePercentage").value(99.5));
  }

  @Test
  void incidentsReturnsTimeline() throws Exception {
    when(healthSnapshotService.getIncidents(anyString(), anyString()))
        .thenReturn(List.of(new IncidentResponse(
            "ext:deedsweb", "DeedsWeb",
            "2025-01-15T10:00:00Z", "2025-01-15T11:00:00Z",
            60, "DOWN")));

    mockMvc.perform(get("/api/admin/system-health/incidents").param("range", "7d"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].serviceId").value("ext:deedsweb"))
        .andExpect(jsonPath("$[0].durationMinutes").value(60));
  }
}
