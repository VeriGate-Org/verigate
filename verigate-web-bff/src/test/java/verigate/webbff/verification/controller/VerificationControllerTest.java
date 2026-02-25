package verigate.webbff.verification.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import verigate.webbff.verification.model.CommandStatus;
import verigate.webbff.verification.model.VerificationResponse;
import verigate.webbff.verification.repository.model.VerificationCommandStoreItem;
import verigate.webbff.verification.service.VerificationService;

@WebMvcTest(controllers = VerificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class VerificationControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private VerificationService verificationService;

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
}
