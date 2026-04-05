/*
 * VeriGate (c) 2025. All rights reserved.
 */

package verigate.webbff.ai.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import verigate.webbff.ai.dto.CaseSummaryResponse;
import verigate.webbff.ai.dto.VerificationExplanationResponse;
import verigate.webbff.ai.service.BedrockChatService;
import verigate.webbff.ai.service.CaseSummaryService;
import verigate.webbff.ai.service.ChatContextBuilder;
import verigate.webbff.ai.service.VerificationExplainerService;

@WebMvcTest(AiController.class)
@AutoConfigureMockMvc(addFilters = false)
class AiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CaseSummaryService caseSummaryService;

    @MockBean
    private VerificationExplainerService verificationExplainerService;

    @MockBean
    private BedrockChatService chatService;

    @MockBean
    private ChatContextBuilder chatContextBuilder;

    @Test
    void shouldReturnCaseSummary() throws Exception {
        CaseSummaryResponse summary = new CaseSummaryResponse(
                "Clean verification with low risk",
                "APPROVE",
                "All checks passed successfully",
                List.of("Identity verified", "No sanctions hits")
        );

        when(caseSummaryService.summarize(anyString(), anyString())).thenReturn(summary);

        mockMvc.perform(post("/api/partner/ai/cases/case-123/summary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.suggestedDisposition").value("APPROVE"))
                .andExpect(jsonPath("$.summary").value("Clean verification with low risk"));
    }

    @Test
    void shouldReturnVerificationExplanation() throws Exception {
        VerificationExplanationResponse explanation = new VerificationExplanationResponse(
                "The identity check confirmed the person's details.",
                "South African ID number validation via DHA",
                "The person's identity has been confirmed",
                "No action needed - verification successful"
        );

        when(verificationExplainerService.explain(anyString(), anyString()))
                .thenReturn(explanation);

        mockMvc.perform(post("/api/partner/ai/verifications/cmd-456/explain")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.whatWasChecked").exists())
                .andExpect(jsonPath("$.recommendedAction").exists());
    }

    @Test
    void shouldReturnAlertExplanation() throws Exception {
        when(chatService.chat(anyString(), anyString()))
                .thenReturn("The risk score increased due to new sanctions screening results.");

        mockMvc.perform(post("/api/partner/ai/monitoring/alerts/alert-789/explain")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alertId").value("alert-789"))
                .andExpect(jsonPath("$.explanation").exists());
    }
}
