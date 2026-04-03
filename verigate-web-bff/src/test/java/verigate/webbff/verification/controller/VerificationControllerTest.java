package verigate.webbff.verification.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import verigate.webbff.auth.PartnerContextHolder;
import verigate.webbff.verification.model.CommandStatus;
import verigate.webbff.verification.model.VerificationListItem;
import verigate.webbff.verification.model.VerificationListResponse;
import verigate.webbff.verification.model.VerificationResponse;
import verigate.webbff.verification.repository.model.VerificationCommandStoreItem;
import verigate.webbff.verification.mapper.IdentityVerificationMapper;
import verigate.webbff.verification.service.VerificationService;

@WebMvcTest(controllers = VerificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class VerificationControllerTest {

  private static final String PARTNER_ID = "partner-123";

  @Autowired private MockMvc mockMvc;

  @MockBean private VerificationService verificationService;
  @MockBean private IdentityVerificationMapper identityVerificationMapper;

  @AfterEach
  void tearDown() {
    PartnerContextHolder.clear();
  }

  @Test
  void submitVerificationReturnsAccepted() throws Exception {
    var commandId = UUID.randomUUID();
    when(verificationService.submitVerification(any()))
        .thenReturn(new VerificationResponse(commandId, CommandStatus.PENDING));

    var payload =
        "{" +
            "\"verificationType\":\"VERIFICATION_OF_PERSONAL_DETAILS\"," +
            "\"originationType\":\"POLICY\"," +
            "\"originationId\":\"" + commandId + "\"," +
            "\"requestedBy\":\"tester\"," +
            "\"metadata\":{\"firstName\":\"John\",\"lastName\":\"Doe\",\"idNumber\":\"123\"}" +
            "}";

    mockMvc
        .perform(post("/api/verifications").contentType(MediaType.APPLICATION_JSON).content(payload))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.commandId").value(commandId.toString()))
        .andExpect(jsonPath("$.status").value("PENDING"));
  }

  @Test
  void getVerificationReturnsStatus() throws Exception {
    var commandId = UUID.randomUUID();
    var item = new VerificationCommandStoreItem();
    item.setCommandId(commandId.toString());
    item.setCommandName("VerifyPartyCommand");
    item.setStatus(CommandStatus.COMPLETED);
    item.setErrorDetails(List.of());
    item.setAuxiliaryData(Map.of("provider", "test"));

    when(verificationService.findVerification(commandId))
        .thenReturn(Optional.of(item));

    mockMvc
        .perform(get("/api/verifications/" + commandId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.commandId").value(commandId.toString()))
        .andExpect(jsonPath("$.status").value("COMPLETED"));
  }

  @Test
  void listVerificationsReturnsPagedResults() throws Exception {
    PartnerContextHolder.setPartnerId(PARTNER_ID);

    var id1 = UUID.randomUUID();
    var id2 = UUID.randomUUID();
    var items = List.of(
        new VerificationListItem(id1, CommandStatus.COMPLETED, "2025-06-01T10:00:00Z", "VerifyPartyCommand"),
        new VerificationListItem(id2, CommandStatus.PENDING, "2025-06-01T09:00:00Z", "VerifyPartyCommand"));

    when(verificationService.listVerifications(eq(PARTNER_ID), isNull(), eq(50), isNull()))
        .thenReturn(new VerificationListResponse(items, null, false));

    mockMvc
        .perform(get("/api/verifications"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items.length()").value(2))
        .andExpect(jsonPath("$.items[0].commandId").value(id1.toString()))
        .andExpect(jsonPath("$.items[0].status").value("COMPLETED"))
        .andExpect(jsonPath("$.items[1].commandId").value(id2.toString()))
        .andExpect(jsonPath("$.hasMore").value(false))
        .andExpect(jsonPath("$.cursor").doesNotExist());
  }

  @Test
  void listVerificationsWithStatusFilter() throws Exception {
    PartnerContextHolder.setPartnerId(PARTNER_ID);

    var id1 = UUID.randomUUID();
    var items = List.of(
        new VerificationListItem(id1, CommandStatus.COMPLETED, "2025-06-01T10:00:00Z", "VerifyPartyCommand"));

    when(verificationService.listVerifications(eq(PARTNER_ID), eq("COMPLETED"), eq(50), isNull()))
        .thenReturn(new VerificationListResponse(items, null, false));

    mockMvc
        .perform(get("/api/verifications").param("status", "COMPLETED"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items.length()").value(1))
        .andExpect(jsonPath("$.items[0].status").value("COMPLETED"));
  }

  @Test
  void listVerificationsWithCursorPagination() throws Exception {
    PartnerContextHolder.setPartnerId(PARTNER_ID);

    var id1 = UUID.randomUUID();
    var nextCursor = UUID.randomUUID().toString();
    var items = List.of(
        new VerificationListItem(id1, CommandStatus.PENDING, "2025-06-01T08:00:00Z", "VerifyPartyCommand"));

    when(verificationService.listVerifications(eq(PARTNER_ID), isNull(), eq(10), any()))
        .thenReturn(new VerificationListResponse(items, nextCursor, true));

    mockMvc
        .perform(get("/api/verifications")
            .param("limit", "10")
            .param("cursor", UUID.randomUUID().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items.length()").value(1))
        .andExpect(jsonPath("$.hasMore").value(true))
        .andExpect(jsonPath("$.cursor").value(nextCursor));
  }

  @Test
  void listVerificationsReturnsEmptyForNoResults() throws Exception {
    PartnerContextHolder.setPartnerId(PARTNER_ID);

    when(verificationService.listVerifications(eq(PARTNER_ID), isNull(), eq(50), isNull()))
        .thenReturn(new VerificationListResponse(List.of(), null, false));

    mockMvc
        .perform(get("/api/verifications"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items.length()").value(0))
        .andExpect(jsonPath("$.hasMore").value(false));
  }

  @Test
  void listVerificationsLimitIsClamped() throws Exception {
    PartnerContextHolder.setPartnerId(PARTNER_ID);

    when(verificationService.listVerifications(eq(PARTNER_ID), isNull(), eq(100), isNull()))
        .thenReturn(new VerificationListResponse(List.of(), null, false));

    mockMvc
        .perform(get("/api/verifications").param("limit", "500"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items").isArray());
  }
}
