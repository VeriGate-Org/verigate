package verigate.webbff.verification.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import verigate.webbff.auth.PartnerContextHolder;
import verigate.webbff.config.properties.DocumentProperties;
import verigate.webbff.verification.model.CommandStatus;
import verigate.webbff.verification.repository.CommandStatusRepository;
import verigate.webbff.verification.repository.model.PageResult;
import verigate.webbff.verification.repository.model.VerificationCommandStoreItem;
import verigate.webbff.verification.service.DhaPermitNotificationService;
import verigate.webbff.verification.service.VerificationService;

@WebMvcTest(controllers = DocumentController.class)
@AutoConfigureMockMvc(addFilters = false)
class DocumentControllerTest {

  private static final String PARTNER_ID = "partner-test";

  @Autowired private MockMvc mockMvc;

  @MockBean private S3Presigner s3Presigner;
  @MockBean private DocumentProperties documentProperties;
  @MockBean private CommandStatusRepository commandStatusRepository;
  @MockBean private VerificationService verificationService;
  @MockBean private DhaPermitNotificationService dhaPermitNotificationService;

  @AfterEach
  void tearDown() {
    PartnerContextHolder.clear();
  }

  @Test
  void historyReturnsDocumentVerificationsWithCorrectFields() throws Exception {
    PartnerContextHolder.setPartnerId(PARTNER_ID);

    var docItem = buildItem("cmd-1", CommandStatus.COMPLETED, Map.of(
        "documentType", "id_card",
        "documentNumber", "9001015009087",
        "outcome", "SUCCEEDED",
        "confidenceScore", "0.95"));

    when(commandStatusRepository.findByPartnerId(
        eq(PARTNER_ID), isNull(), eq("VerifyPartyCommand"), eq(50), isNull()))
        .thenReturn(new PageResult<>(List.of(docItem), Map.of()));

    mockMvc
        .perform(get("/api/partner/documents/history"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items.length()").value(1))
        .andExpect(jsonPath("$.items[0].verificationId").value("cmd-1"))
        .andExpect(jsonPath("$.items[0].documentType").value("id_card"))
        .andExpect(jsonPath("$.items[0].documentTypeLabel").value("SA ID Card"))
        .andExpect(jsonPath("$.items[0].documentNumber").value("9001015009087"))
        .andExpect(jsonPath("$.items[0].outcome").value("VERIFIED"));
  }

  @Test
  void historyFiltersOutNonDocumentVerifications() throws Exception {
    PartnerContextHolder.setPartnerId(PARTNER_ID);

    // Document verification — has documentType in auxiliaryData
    var docItem = buildItem("cmd-doc", CommandStatus.COMPLETED, Map.of(
        "documentType", "passport",
        "documentNumber", "A12345678",
        "outcome", "SUCCEEDED"));

    // Identity verification — no documentType in auxiliaryData
    var identityItem = buildItem("cmd-id", CommandStatus.COMPLETED, Map.of(
        "idNumber", "9001015009087",
        "fullName", "Thabo Mokwena",
        "outcome", "SUCCEEDED"));

    // Sanctions screening — no documentType
    var sanctionsItem = buildItem("cmd-sanc", CommandStatus.COMPLETED, Map.of(
        "subjectName", "John Doe",
        "outcome", "SUCCEEDED"));

    when(commandStatusRepository.findByPartnerId(
        eq(PARTNER_ID), isNull(), eq("VerifyPartyCommand"), eq(50), isNull()))
        .thenReturn(new PageResult<>(List.of(docItem, identityItem, sanctionsItem), Map.of()));

    mockMvc
        .perform(get("/api/partner/documents/history"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items.length()").value(1))
        .andExpect(jsonPath("$.items[0].verificationId").value("cmd-doc"))
        .andExpect(jsonPath("$.items[0].documentType").value("passport"))
        .andExpect(jsonPath("$.items[0].documentTypeLabel").value("Passport"))
        .andExpect(jsonPath("$.items[0].documentNumber").value("A12345678"));
  }

  @Test
  void historyFallsBackToDocumentReferenceWhenDocumentNumberMissing() throws Exception {
    PartnerContextHolder.setPartnerId(PARTNER_ID);

    var item = buildItem("cmd-ref", CommandStatus.COMPLETED, Map.of(
        "documentType", "drivers_license",
        "documentReference", "DL-9876543",
        "outcome", "SUCCEEDED"));

    when(commandStatusRepository.findByPartnerId(
        eq(PARTNER_ID), isNull(), eq("VerifyPartyCommand"), eq(50), isNull()))
        .thenReturn(new PageResult<>(List.of(item), Map.of()));

    mockMvc
        .perform(get("/api/partner/documents/history"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items.length()").value(1))
        .andExpect(jsonPath("$.items[0].documentNumber").value("DL-9876543"))
        .andExpect(jsonPath("$.items[0].documentTypeLabel").value("Driver's License"));
  }

  @Test
  void historyExcludesItemsWithNullAuxiliaryData() throws Exception {
    PartnerContextHolder.setPartnerId(PARTNER_ID);

    var nullAuxItem = buildItem("cmd-null", CommandStatus.COMPLETED, null);

    var docItem = buildItem("cmd-ok", CommandStatus.COMPLETED, Map.of(
        "documentType", "id_card",
        "documentNumber", "8501015009087",
        "outcome", "SUCCEEDED"));

    when(commandStatusRepository.findByPartnerId(
        eq(PARTNER_ID), isNull(), eq("VerifyPartyCommand"), eq(50), isNull()))
        .thenReturn(new PageResult<>(List.of(nullAuxItem, docItem), Map.of()));

    mockMvc
        .perform(get("/api/partner/documents/history"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items.length()").value(1))
        .andExpect(jsonPath("$.items[0].verificationId").value("cmd-ok"));
  }

  @Test
  void historyMapsOutcomeStatusesCorrectly() throws Exception {
    PartnerContextHolder.setPartnerId(PARTNER_ID);

    var verified = buildItem("cmd-v", CommandStatus.COMPLETED, Map.of(
        "documentType", "id_card", "outcome", "SUCCEEDED"));
    var notVerified = buildItem("cmd-nv", CommandStatus.COMPLETED, Map.of(
        "documentType", "passport", "outcome", "SOFT_FAIL"));
    var failed = buildItem("cmd-f", CommandStatus.PERMANENT_FAILURE, Map.of(
        "documentType", "drivers_license"));

    when(commandStatusRepository.findByPartnerId(
        eq(PARTNER_ID), isNull(), eq("VerifyPartyCommand"), eq(50), isNull()))
        .thenReturn(new PageResult<>(List.of(verified, notVerified, failed), Map.of()));

    mockMvc
        .perform(get("/api/partner/documents/history"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items.length()").value(3))
        .andExpect(jsonPath("$.items[0].outcome").value("VERIFIED"))
        .andExpect(jsonPath("$.items[1].outcome").value("NOT_VERIFIED"))
        .andExpect(jsonPath("$.items[2].outcome").value("FAILED"));
  }

  @Test
  void historyReturnsEmptyListWhenNoDocumentVerifications() throws Exception {
    PartnerContextHolder.setPartnerId(PARTNER_ID);

    when(commandStatusRepository.findByPartnerId(
        eq(PARTNER_ID), isNull(), eq("VerifyPartyCommand"), eq(50), isNull()))
        .thenReturn(new PageResult<>(List.of(), Map.of()));

    mockMvc
        .perform(get("/api/partner/documents/history"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items.length()").value(0))
        .andExpect(jsonPath("$.hasMore").value(false));
  }

  private VerificationCommandStoreItem buildItem(
      String commandId, CommandStatus status, Map<String, String> auxiliaryData) {
    var item = new VerificationCommandStoreItem();
    item.setCommandId(commandId);
    item.setCommandName("VerifyPartyCommand");
    item.setStatus(status);
    item.setPartnerId(PARTNER_ID);
    item.setCreatedAt("2025-06-15T10:00:00Z");
    item.setAuxiliaryData(auxiliaryData != null ? new HashMap<>(auxiliaryData) : null);
    return item;
  }
}
