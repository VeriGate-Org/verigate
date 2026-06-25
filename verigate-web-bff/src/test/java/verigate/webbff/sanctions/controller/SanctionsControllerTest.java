package verigate.webbff.sanctions.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import verigate.webbff.auth.PartnerContextHolder;
import verigate.webbff.sanctions.service.SanctionsProxyService;

@WebMvcTest(controllers = SanctionsController.class)
@AutoConfigureMockMvc(addFilters = false)
class SanctionsControllerTest {

  private static final String PARTNER_ID = "partner-abc";

  @Autowired private MockMvc mockMvc;

  @MockBean private SanctionsProxyService sanctionsProxyService;

  @BeforeEach
  void setUpPartnerContext() {
    PartnerContextHolder.setPartnerId(PARTNER_ID);
  }

  @AfterEach
  void clearPartnerContext() {
    PartnerContextHolder.clear();
  }

  @Test
  void getReport_returns200WithPresignedUrlForValidCommand() throws Exception {
    var commandId = UUID.randomUUID();
    var expectedUrl = "https://s3.amazonaws.com/verigate-documents/sanctions-screening/"
        + commandId + ".txt?X-Amz-Signature=abc";

    when(sanctionsProxyService.getReportPresignedUrl(eq(commandId), eq(PARTNER_ID)))
        .thenReturn(Map.of(
            "downloadUrl", expectedUrl,
            "documentId", "sanctions-screening/" + commandId + ".txt",
            "expiresIn", 900));

    mockMvc
        .perform(get("/api/partner/sanctions/report/" + commandId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.downloadUrl").value(expectedUrl))
        .andExpect(jsonPath("$.documentId").value("sanctions-screening/" + commandId + ".txt"))
        .andExpect(jsonPath("$.expiresIn").value(900));
  }

  @Test
  void getReport_returns500WhenCommandNotFound() throws Exception {
    var commandId = UUID.randomUUID();
    when(sanctionsProxyService.getReportPresignedUrl(eq(commandId), eq(PARTNER_ID)))
        .thenThrow(new RuntimeException("Screening not found: " + commandId));

    mockMvc
        .perform(get("/api/partner/sanctions/report/" + commandId))
        .andExpect(status().isInternalServerError());
  }

  @Test
  void getReport_returns500WhenReportNotYetGenerated() throws Exception {
    var commandId = UUID.randomUUID();
    when(sanctionsProxyService.getReportPresignedUrl(eq(commandId), eq(PARTNER_ID)))
        .thenThrow(new RuntimeException("No report available for screening: " + commandId));

    mockMvc
        .perform(get("/api/partner/sanctions/report/" + commandId))
        .andExpect(status().isInternalServerError());
  }

  @Test
  void getReport_returns500WhenPartnerDoesNotOwnScreening() throws Exception {
    var commandId = UUID.randomUUID();
    when(sanctionsProxyService.getReportPresignedUrl(eq(commandId), eq(PARTNER_ID)))
        .thenThrow(new RuntimeException("Screening not found: " + commandId));

    mockMvc
        .perform(get("/api/partner/sanctions/report/" + commandId))
        .andExpect(status().isInternalServerError());
  }

  @Test
  void getReport_returns400ForMalformedUuid() throws Exception {
    mockMvc
        .perform(get("/api/partner/sanctions/report/not-a-uuid"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getHistory_returns200WithItemsForAuthenticatedPartner() throws Exception {
    when(sanctionsProxyService.getScreeningHistory(eq(PARTNER_ID)))
        .thenReturn(Map.of("items", List.of(), "total", 0));

    mockMvc
        .perform(get("/api/partner/sanctions/history"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.total").value(0));
  }

  @Test
  void submitDisposition_returns200WithDispositionId() throws Exception {
    when(sanctionsProxyService.submitDisposition(any(), eq(PARTNER_ID)))
        .thenReturn(Map.of(
            "dispositionId", "disp-123",
            "entityId", "NK-456",
            "action", "FALSE_POSITIVE",
            "createdAt", "2026-06-25T12:00:00Z"));

    mockMvc
        .perform(post("/api/partner/sanctions/dispositions")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .content("""
                {"entityId":"NK-456","screeningId":"550e8400-e29b-41d4-a716-446655440000",
                 "action":"FALSE_POSITIVE","reason":"Not the same person"}
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.dispositionId").value("disp-123"))
        .andExpect(jsonPath("$.action").value("FALSE_POSITIVE"));
  }
}
