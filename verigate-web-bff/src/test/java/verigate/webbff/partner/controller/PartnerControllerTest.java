package verigate.webbff.partner.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import verigate.webbff.admin.model.ApiKeyListResponse;
import verigate.webbff.auth.ApiKeyRecord;
import verigate.webbff.auth.ApiKeyService;
import verigate.webbff.auth.ApiKeyService.GeneratedApiKey;
import verigate.webbff.auth.PartnerContextHolder;
import verigate.webbff.partner.model.BulkActionResponse;
import verigate.webbff.partner.model.NotificationPreferences;
import verigate.webbff.partner.model.PartnerProfileResponse;
import verigate.webbff.partner.model.PolicyListResponse;
import verigate.webbff.partner.model.PolicyResponse;
import verigate.webbff.partner.model.ReportListResponse;
import verigate.webbff.partner.model.ReportResponse;
import verigate.webbff.partner.service.PartnerFeatureService;

@WebMvcTest(controllers = PartnerController.class)
@AutoConfigureMockMvc(addFilters = false)
class PartnerControllerTest {

  private static final String PARTNER_ID = "partner-123";

  @Autowired private MockMvc mockMvc;

  @MockBean private PartnerFeatureService featureService;
  @MockBean private ApiKeyService apiKeyService;

  @AfterEach
  void tearDown() {
    PartnerContextHolder.clear();
  }

  // ── Policy Tests ──────────────────────────────────────────────────

  @Test
  void listPoliciesReturnsItems() throws Exception {
    PartnerContextHolder.setPartnerId(PARTNER_ID);

    var policy = new PolicyResponse("p1", PARTNER_ID, "ID Check Policy", "desc", 1, "draft", List.of(), "2025-06-01T10:00:00Z", "2025-06-01T10:00:00Z");
    when(featureService.listPolicies(PARTNER_ID))
        .thenReturn(new PolicyListResponse(List.of(policy)));

    mockMvc.perform(get("/api/partner/policies"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items.length()").value(1))
        .andExpect(jsonPath("$.items[0].name").value("ID Check Policy"))
        .andExpect(jsonPath("$.items[0].status").value("draft"));
  }

  @Test
  void createPolicyReturnsCreated() throws Exception {
    PartnerContextHolder.setPartnerId(PARTNER_ID);

    var response = new PolicyResponse("p1", PARTNER_ID, "New Policy", null, 1, "draft", List.of(), "2025-06-01T10:00:00Z", "2025-06-01T10:00:00Z");
    when(featureService.savePolicy(eq(PARTNER_ID), any(), isNull()))
        .thenReturn(response);

    mockMvc.perform(post("/api/partner/policies")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"New Policy\",\"steps\":[]}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("p1"))
        .andExpect(jsonPath("$.status").value("draft"));
  }

  @Test
  void publishPolicyUpdatesStatus() throws Exception {
    PartnerContextHolder.setPartnerId(PARTNER_ID);

    var response = new PolicyResponse("p1", PARTNER_ID, "Policy", null, 2, "published", List.of(), "2025-06-01T10:00:00Z", "2025-06-01T11:00:00Z");
    when(featureService.publishPolicy(PARTNER_ID, "p1"))
        .thenReturn(response);

    mockMvc.perform(post("/api/partner/policies/p1/publish"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("published"))
        .andExpect(jsonPath("$.version").value(2));
  }

  @Test
  void deletePolicyReturnsNoContent() throws Exception {
    PartnerContextHolder.setPartnerId(PARTNER_ID);
    doNothing().when(featureService).deletePolicy(PARTNER_ID, "p1");

    mockMvc.perform(delete("/api/partner/policies/p1"))
        .andExpect(status().isNoContent());
  }

  // ── Report Tests ──────────────────────────────────────────────────

  @Test
  void generateReportReturnsCreated() throws Exception {
    PartnerContextHolder.setPartnerId(PARTNER_ID);

    var response = new ReportResponse("r1", PARTNER_ID, "Monthly Report", "verification_summary", "desc", "generated", null, null, "2025-06-01T10:00:00Z");
    when(featureService.generateReport(eq(PARTNER_ID), any()))
        .thenReturn(response);

    mockMvc.perform(post("/api/partner/reports/generate")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Monthly Report\",\"type\":\"verification_summary\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("r1"))
        .andExpect(jsonPath("$.status").value("generated"));
  }

  @Test
  void listReportsReturnsItems() throws Exception {
    PartnerContextHolder.setPartnerId(PARTNER_ID);

    when(featureService.listReports(PARTNER_ID))
        .thenReturn(new ReportListResponse(List.of()));

    mockMvc.perform(get("/api/partner/reports"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items").isArray());
  }

  // ── Profile Tests ─────────────────────────────────────────────────

  @Test
  void getProfileReturnsPartnerData() throws Exception {
    PartnerContextHolder.setPartnerId(PARTNER_ID);

    when(featureService.getProfile(PARTNER_ID))
        .thenReturn(new PartnerProfileResponse(PARTNER_ID, "Acme Corp", "admin@acme.co", "enterprise",
            List.of(), List.of(), Map.of(), "ACTIVE", "2025-01-01T00:00:00Z",
            null, null, null, null, null, null));

    mockMvc.perform(get("/api/partner/profile"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.partnerId").value(PARTNER_ID))
        .andExpect(jsonPath("$.name").value("Acme Corp"))
        .andExpect(jsonPath("$.billingPlan").value("enterprise"));
  }

  @Test
  void updateProfileReturnsUpdated() throws Exception {
    PartnerContextHolder.setPartnerId(PARTNER_ID);

    when(featureService.updateProfile(eq(PARTNER_ID), any()))
        .thenReturn(new PartnerProfileResponse(PARTNER_ID, "Updated Corp", "new@acme.co", "enterprise",
            List.of(), List.of(), Map.of(), "ACTIVE", null,
            null, null, null, null, null, null));

    mockMvc.perform(put("/api/partner/profile")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Updated Corp\",\"contactEmail\":\"new@acme.co\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Corp"));
  }

  // ── API Key Tests ─────────────────────────────────────────────────

  @Test
  void listApiKeysReturnsPartnerKeys() throws Exception {
    PartnerContextHolder.setPartnerId(PARTNER_ID);

    var record = new ApiKeyRecord(
        "hash1",
        "verification-hash-1",
        "salt-1",
        PARTNER_ID,
        "ACTIVE",
        "vg_abc1",
        LocalDateTime.now(),
        null,
        "admin");
    when(apiKeyService.listApiKeys(PARTNER_ID))
        .thenReturn(List.of(record));

    mockMvc.perform(get("/api/partner/api-keys"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.partnerId").value(PARTNER_ID))
        .andExpect(jsonPath("$.keys.length()").value(1))
        .andExpect(jsonPath("$.keys[0].keyPrefix").value("vg_abc1"));
  }

  @Test
  void generateApiKeyReturnsCreated() throws Exception {
    PartnerContextHolder.setPartnerId(PARTNER_ID);

    var record = new ApiKeyRecord(
        "hash1",
        "verification-hash-1",
        "salt-1",
        PARTNER_ID,
        "ACTIVE",
        "vg_new1",
        LocalDateTime.now(),
        null,
        "partner-portal");
    when(apiKeyService.generateApiKey(PARTNER_ID, "partner-portal"))
        .thenReturn(new GeneratedApiKey("vg_new1234567890", record));

    mockMvc.perform(post("/api/partner/api-keys"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.apiKey").value("vg_new1234567890"))
        .andExpect(jsonPath("$.keyPrefix").value("vg_new1"));
  }

  // ── Notification Tests ────────────────────────────────────────────

  @Test
  void getNotificationsReturnsDefaults() throws Exception {
    PartnerContextHolder.setPartnerId(PARTNER_ID);

    when(featureService.getNotifications(PARTNER_ID))
        .thenReturn(new NotificationPreferences(true, true, false, true));

    mockMvc.perform(get("/api/partner/notifications"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.verificationComplete").value(true))
        .andExpect(jsonPath("$.weeklySummary").value(false));
  }

  @Test
  void updateNotificationsReturnsSaved() throws Exception {
    PartnerContextHolder.setPartnerId(PARTNER_ID);

    when(featureService.updateNotifications(eq(PARTNER_ID), any()))
        .thenReturn(new NotificationPreferences(true, false, true, true));

    mockMvc.perform(put("/api/partner/notifications")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"verificationComplete\":true,\"verificationFailure\":false,\"weeklySummary\":true,\"securityAlerts\":true}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.verificationFailure").value(false))
        .andExpect(jsonPath("$.weeklySummary").value(true));
  }

  // ── Bulk Operation Tests ──────────────────────────────────────────

  @Test
  void exportVerificationsReturnsResult() throws Exception {
    PartnerContextHolder.setPartnerId(PARTNER_ID);

    when(featureService.exportVerifications(eq(PARTNER_ID), any(), eq("csv")))
        .thenReturn(new BulkActionResponse("export", 5, 0, "Exported 5 verifications as csv"));

    mockMvc.perform(post("/api/partner/verifications/export")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"commandIds\":[\"id1\",\"id2\",\"id3\",\"id4\",\"id5\"],\"format\":\"csv\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.action").value("export"))
        .andExpect(jsonPath("$.processed").value(5));
  }

  @Test
  void retryVerificationsReturnsResult() throws Exception {
    PartnerContextHolder.setPartnerId(PARTNER_ID);

    when(featureService.retryVerifications(eq(PARTNER_ID), any()))
        .thenReturn(new BulkActionResponse("retry", 3, 0, "Retried 3 verifications"));

    mockMvc.perform(post("/api/partner/verifications/retry")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"commandIds\":[\"id1\",\"id2\",\"id3\"]}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.action").value("retry"))
        .andExpect(jsonPath("$.processed").value(3));
  }
}
