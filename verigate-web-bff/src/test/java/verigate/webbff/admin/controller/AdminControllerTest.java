package verigate.webbff.admin.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import verigate.webbff.admin.model.PartnerResponse;
import verigate.webbff.admin.model.PartnerStatus;
import verigate.webbff.admin.repository.PartnerRepository;
import verigate.webbff.admin.service.PartnerService;
import verigate.webbff.auth.ApiKeyRecord;
import verigate.webbff.auth.ApiKeyService;
import verigate.webbff.auth.ApiKeyService.GeneratedApiKey;

@WebMvcTest(controllers = AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private ApiKeyService apiKeyService;
  @MockBean private PartnerService partnerService;
  @MockBean private PartnerRepository partnerRepository;

  @Test
  void createPartnerReturnsAccepted() throws Exception {
    UUID commandId = UUID.randomUUID();
    when(partnerService.submitCreatePartner(any()))
        .thenReturn(commandId);

    mockMvc.perform(post("/api/admin/partners")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Acme Corp\","
                + "\"contactEmail\":\"admin@acme.com\","
                + "\"billingPlan\":\"STANDARD\"}"))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.commandId")
            .value(commandId.toString()))
        .andExpect(jsonPath("$.status").value("ACCEPTED"));
  }

  @Test
  void listPartnersReturnsAll() throws Exception {
    when(partnerRepository.findAll()).thenReturn(List.of(
        new PartnerResponse("p-001", "Acme", "a@acme.com",
            "STANDARD", "ACTIVE", "2025-01-15T10:00:00"),
        new PartnerResponse("p-002", "Beta Inc", "b@beta.com",
            "PREMIUM", "PENDING", "2025-02-20T12:00:00")));

    mockMvc.perform(get("/api/admin/partners"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].partnerId").value("p-001"))
        .andExpect(jsonPath("$[0].status").value("ACTIVE"))
        .andExpect(jsonPath("$[1].partnerId").value("p-002"))
        .andExpect(jsonPath("$[1].status").value("PENDING"));
  }

  @Test
  void getPartnerReturnsDetails() throws Exception {
    when(partnerRepository.findById("p-001")).thenReturn(
        Optional.of(new PartnerResponse("p-001", "Acme",
            "a@acme.com", "STANDARD", "ACTIVE",
            "2025-01-15T10:00:00")));

    mockMvc.perform(get("/api/admin/partners/p-001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.partnerId").value("p-001"))
        .andExpect(jsonPath("$.name").value("Acme"))
        .andExpect(jsonPath("$.status").value("ACTIVE"));
  }

  @Test
  void getPartnerNotFoundReturns404() throws Exception {
    when(partnerRepository.findById("p-999"))
        .thenReturn(Optional.empty());

    mockMvc.perform(get("/api/admin/partners/p-999"))
        .andExpect(status().isNotFound());
  }

  @Test
  void updatePartnerStatusReturnsOk() throws Exception {
    when(partnerRepository.findById("p-001")).thenReturn(
        Optional.of(new PartnerResponse("p-001", "Acme",
            "a@acme.com", "STANDARD", "PENDING",
            "2025-01-15T10:00:00")));
    doNothing().when(partnerRepository)
        .updateStatus("p-001", PartnerStatus.ACTIVE);

    mockMvc.perform(put("/api/admin/partners/p-001/status")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"status\":\"ACTIVE\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.partnerId").value("p-001"))
        .andExpect(jsonPath("$.status").value("ACTIVE"));

    verify(partnerRepository)
        .updateStatus("p-001", PartnerStatus.ACTIVE);
  }

  @Test
  void updatePartnerStatusNotFoundReturns404() throws Exception {
    when(partnerRepository.findById("p-999"))
        .thenReturn(Optional.empty());

    mockMvc.perform(put("/api/admin/partners/p-999/status")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"status\":\"ACTIVE\"}"))
        .andExpect(status().isNotFound());
  }

  @Test
  void generateApiKeyReturnsCreated() throws Exception {
    LocalDateTime now = LocalDateTime.now();
    ApiKeyRecord record = new ApiKeyRecord(
        "abc123hash", "p-001", "ACTIVE",
        "vg_live_", now, null, "admin");
    when(apiKeyService.generateApiKey("p-001", "admin"))
        .thenReturn(new GeneratedApiKey(
            "vg_live_aabbccdd11223344", record));

    mockMvc.perform(
            post("/api/admin/partners/p-001/api-keys"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.apiKey")
            .value("vg_live_aabbccdd11223344"))
        .andExpect(jsonPath("$.partnerId").value("p-001"))
        .andExpect(jsonPath("$.status").value("ACTIVE"));
  }

  @Test
  void listApiKeysReturnsKeys() throws Exception {
    LocalDateTime now = LocalDateTime.now();
    when(apiKeyService.listApiKeys("p-001")).thenReturn(List.of(
        new ApiKeyRecord("hash1", "p-001", "ACTIVE",
            "vg_live_", now, null, "admin"),
        new ApiKeyRecord("hash2", "p-001", "REVOKED",
            "vg_live_", now, null, "admin")));

    mockMvc.perform(get("/api/admin/partners/p-001/api-keys"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.partnerId").value("p-001"))
        .andExpect(jsonPath("$.keys.length()").value(2))
        .andExpect(jsonPath("$.keys[0].status").value("ACTIVE"))
        .andExpect(jsonPath("$.keys[1].status").value("REVOKED"));
  }

  @Test
  void revokeApiKeyReturnsNoContent() throws Exception {
    LocalDateTime now = LocalDateTime.now();
    when(apiKeyService.listApiKeys("p-001")).thenReturn(List.of(
        new ApiKeyRecord("hash1", "p-001", "ACTIVE",
            "vg_live_", now, null, "admin")));

    mockMvc.perform(
            delete("/api/admin/partners/p-001/api-keys/vg_live_"))
        .andExpect(status().isNoContent());

    verify(apiKeyService).revokeApiKey("hash1");
  }

  @Test
  void revokeApiKeyNotFoundReturns404() throws Exception {
    when(apiKeyService.listApiKeys("p-001"))
        .thenReturn(List.of());

    mockMvc.perform(
            delete("/api/admin/partners/p-001/api-keys/no_match"))
        .andExpect(status().isNotFound());
  }
}
