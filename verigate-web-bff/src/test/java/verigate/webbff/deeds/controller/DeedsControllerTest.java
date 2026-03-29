package verigate.webbff.deeds.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;
import verigate.webbff.auth.PartnerContextHolder;
import verigate.webbff.deeds.model.DeedsModels;
import verigate.webbff.deeds.service.DeedsPortfolioService;

@WebMvcTest(controllers = DeedsController.class)
@AutoConfigureMockMvc(addFilters = false)
class DeedsControllerTest {

  private static final String PARTNER_ID = "partner-123";

  @Autowired private MockMvc mockMvc;

  @MockBean private DeedsPortfolioService deedsPortfolioService;

  @AfterEach
  void tearDown() {
    PartnerContextHolder.clear();
  }

  @Test
  void generateAreaReportReturnsSummary() throws Exception {
    PartnerContextHolder.setPartnerId(PARTNER_ID);
    when(deedsPortfolioService.generateAreaReport(eq(PARTNER_ID), any()))
        .thenReturn(new DeedsModels.AreaReportResponse(
            new DeedsModels.Summary(3, 2, 1, 1, 1),
            List.of(new DeedsModels.AreaBreakdownItem("Gauteng", "Sandton", 3, 2, 1, 1, 1)),
            List.of(),
            "2026-03-29T20:00:00Z"));

    mockMvc.perform(post("/api/partner/deeds/reports/area")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"province\":\"Gauteng\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.summary.totalProperties").value(3))
        .andExpect(jsonPath("$.areas[0].township").value("Sandton"));
  }

  @Test
  void createWatchReturnsCreated() throws Exception {
    PartnerContextHolder.setPartnerId(PARTNER_ID);
    when(deedsPortfolioService.createWatch(eq(PARTNER_ID), any()))
        .thenReturn(new DeedsModels.WatchResponse(
            "subject-1",
            "property-1",
            "ERF 10/0 Sandton",
            "T12/12345",
            "MONTHLY",
            "ACTIVE",
            List.of("TRANSFER"),
            "2026-03-29T20:00:00Z"));

    mockMvc.perform(post("/api/partner/deeds/watches")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"propertyId\":\"property-1\",\"monitoringFrequency\":\"MONTHLY\"}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.propertyId").value("property-1"))
        .andExpect(jsonPath("$.status").value("ACTIVE"));
  }

  @Test
  void getDocumentManifestReturnsItems() throws Exception {
    PartnerContextHolder.setPartnerId(PARTNER_ID);
    when(deedsPortfolioService.getDocumentManifest(PARTNER_ID, "property-1"))
        .thenReturn(new DeedsModels.DocumentManifestResponse(
            "property-1",
            "T12/12345",
            List.of(new DeedsModels.DocumentDescriptor(
                "TITLE_DEED_COPY",
                "Title deed copy",
                "T12/12345",
                false,
                "PENDING_PROVIDER_CONFIGURATION",
                "pending")),
            "PENDING_PROVIDER_CONFIGURATION"));

    mockMvc.perform(get("/api/partner/deeds/documents/manifest")
            .param("propertyId", "property-1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.propertyId").value("property-1"))
        .andExpect(jsonPath("$.documents[0].type").value("TITLE_DEED_COPY"));
  }
}
