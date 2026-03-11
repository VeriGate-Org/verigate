/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.negativenews.application.handlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import verigate.adapter.negativenews.domain.models.ArticleSentiment;
import verigate.adapter.negativenews.domain.models.NegativeNewsScreeningRequest;
import verigate.adapter.negativenews.domain.models.NegativeNewsScreeningResponse;
import verigate.adapter.negativenews.domain.models.NewsArticle;
import verigate.adapter.negativenews.domain.models.ScreeningOutcome;
import verigate.adapter.negativenews.domain.services.NegativeNewsScreeningService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationOutcome;

class DefaultScreenNegativeNewsCommandHandlerTest {

    @Mock
    private NegativeNewsScreeningService screeningService;

    private DefaultScreenNegativeNewsCommandHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new DefaultScreenNegativeNewsCommandHandler(screeningService);
    }

    @Test
    void testHandleClearScreening() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("firstName", "John", "lastName", "Doe")
        );

        NegativeNewsScreeningResponse clearResponse =
            NegativeNewsScreeningResponse.clear(10, "No adverse news found");

        when(screeningService.screenForNegativeNews(any(NegativeNewsScreeningRequest.class)))
            .thenReturn(clearResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("No adverse news found"));
        assertEquals("CLEAR", result.get("screeningOutcome"));
        assertEquals("10", result.get("totalArticles"));
        assertEquals("0", result.get("adverseCount"));

        verify(screeningService).screenForNegativeNews(any(NegativeNewsScreeningRequest.class));
    }

    @Test
    void testHandleAdverseFoundHighSeverity() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("firstName", "John", "lastName", "Doe")
        );

        List<NewsArticle> articles = List.of(
            new NewsArticle("Fraud allegations", "Daily News", LocalDate.now(),
                "https://example.com/article", "Subject linked to fraud",
                ArticleSentiment.HIGHLY_NEGATIVE, 0.95)
        );

        NegativeNewsScreeningResponse adverseResponse =
            NegativeNewsScreeningResponse.adverseFound(articles, 5, 2, 0.95, "Adverse news found");

        when(screeningService.screenForNegativeNews(any(NegativeNewsScreeningRequest.class)))
            .thenReturn(adverseResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL.toString(), result.get("outcome"));
        assertEquals("ADVERSE_FOUND", result.get("screeningOutcome"));
        assertEquals("0.95", result.get("severity"));
        assertEquals("2", result.get("adverseCount"));

        verify(screeningService).screenForNegativeNews(any(NegativeNewsScreeningRequest.class));
    }

    @Test
    void testHandleInconclusiveScreening() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("firstName", "John", "lastName", "Doe")
        );

        NegativeNewsScreeningResponse inconclusiveResponse =
            NegativeNewsScreeningResponse.inconclusive("Inconclusive screening results");

        when(screeningService.screenForNegativeNews(any(NegativeNewsScreeningRequest.class)))
            .thenReturn(inconclusiveResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("inconclusive"));
        assertEquals("INCONCLUSIVE", result.get("screeningOutcome"));

        verify(screeningService).screenForNegativeNews(any(NegativeNewsScreeningRequest.class));
    }

    @Test
    void testHandleMissingSubjectName() {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("someOtherKey", "value")
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, () -> handler.handle(command));
        assertTrue(exception.getMessage().contains("Subject name is required for negative news screening"));

        verifyNoInteractions(screeningService);
    }

    @Test
    void testHandleServiceError() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("firstName", "John", "lastName", "Doe")
        );

        NegativeNewsScreeningResponse errorResponse =
            NegativeNewsScreeningResponse.error("Screening service unavailable");

        when(screeningService.screenForNegativeNews(any(NegativeNewsScreeningRequest.class)))
            .thenReturn(errorResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SYSTEM_OUTAGE.toString(), result.get("outcome"));
        assertEquals("ERROR", result.get("screeningOutcome"));

        verify(screeningService).screenForNegativeNews(any(NegativeNewsScreeningRequest.class));
    }

    @Test
    void testHandleTransientException() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("firstName", "John", "lastName", "Doe")
        );

        when(screeningService.screenForNegativeNews(any(NegativeNewsScreeningRequest.class)))
            .thenThrow(new TransientException("Network timeout"));

        // Act & Assert
        assertThrows(TransientException.class, () -> handler.handle(command));

        verify(screeningService).screenForNegativeNews(any(NegativeNewsScreeningRequest.class));
    }

    @Test
    void testHandleAsyncSuccess() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("firstName", "John", "lastName", "Doe")
        );

        NegativeNewsScreeningResponse clearResponse =
            NegativeNewsScreeningResponse.clear(5, "No adverse news found");

        when(screeningService.screenForNegativeNews(any(NegativeNewsScreeningRequest.class)))
            .thenReturn(clearResponse);

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED, result.outcome());
        assertTrue(result.failureReason().contains("No adverse news found"));

        verify(screeningService).screenForNegativeNews(any(NegativeNewsScreeningRequest.class));
    }

    @Test
    void testHandleAsyncFailure() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("firstName", "John", "lastName", "Doe")
        );

        when(screeningService.screenForNegativeNews(any(NegativeNewsScreeningRequest.class)))
            .thenThrow(new PermanentException("Service error"));

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL, result.outcome());
        assertTrue(result.failureReason().contains("Negative news screening failed"));

        verify(screeningService).screenForNegativeNews(any(NegativeNewsScreeningRequest.class));
    }
}
