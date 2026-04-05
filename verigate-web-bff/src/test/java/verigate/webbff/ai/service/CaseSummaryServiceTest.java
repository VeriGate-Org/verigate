/*
 * VeriGate (c) 2025. All rights reserved.
 */

package verigate.webbff.ai.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import verigate.webbff.ai.dto.CaseSummaryResponse;

@ExtendWith(MockitoExtension.class)
class CaseSummaryServiceTest {

    @Mock
    private BedrockChatService chatService;

    private CaseSummaryService caseSummaryService;

    @BeforeEach
    void setUp() {
        caseSummaryService = new CaseSummaryService(chatService, new ObjectMapper());
    }

    @Test
    void shouldParseSummaryResponse() {
        String aiResponse = """
            {
              "summary": "Low risk individual with clean verification history",
              "suggestedDisposition": "APPROVE",
              "reasoning": "All checks passed with high confidence",
              "keySignals": ["Identity confirmed", "No sanctions matches", "Clean credit"]
            }
            """;

        when(chatService.chat(anyString(), anyString())).thenReturn(aiResponse);

        CaseSummaryResponse result = caseSummaryService.summarize("case-123", "{}");

        assertNotNull(result);
        assertEquals("APPROVE", result.suggestedDisposition());
        assertEquals("Low risk individual with clean verification history", result.summary());
        assertEquals("All checks passed with high confidence", result.reasoning());
        assertEquals(3, result.keySignals().size());
    }

    @Test
    void shouldHandleMarkdownWrappedResponse() {
        String aiResponse = """
            ```json
            {
              "summary": "Subject flagged for review",
              "suggestedDisposition": "ESCALATE",
              "reasoning": "Conflicting signals detected",
              "keySignals": ["Sanctions near-match"]
            }
            ```
            """;

        when(chatService.chat(anyString(), anyString())).thenReturn(aiResponse);

        CaseSummaryResponse result = caseSummaryService.summarize("case-456", "{}");

        assertNotNull(result);
        assertEquals("ESCALATE", result.suggestedDisposition());
        assertEquals(1, result.keySignals().size());
    }

    @Test
    void shouldReturnFallbackOnServiceFailure() {
        when(chatService.chat(anyString(), anyString()))
            .thenThrow(new RuntimeException("Bedrock unavailable"));

        CaseSummaryResponse result = caseSummaryService.summarize("case-789", "{}");

        assertNotNull(result);
        assertEquals("ESCALATE", result.suggestedDisposition());
        assertEquals("AI analysis unavailable", result.reasoning());
        assertEquals(List.of(), result.keySignals());
    }

    @Test
    void shouldHandleNonJsonResponse() {
        when(chatService.chat(anyString(), anyString()))
            .thenReturn("This is a plain text response that is not JSON");

        CaseSummaryResponse result = caseSummaryService.summarize("case-000", "{}");

        assertNotNull(result);
        // Falls back to using the raw content as summary
        assertEquals("This is a plain text response that is not JSON", result.summary());
    }
}
