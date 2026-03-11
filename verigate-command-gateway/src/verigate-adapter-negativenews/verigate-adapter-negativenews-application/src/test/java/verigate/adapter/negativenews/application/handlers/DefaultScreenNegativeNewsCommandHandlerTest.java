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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import verigate.adapter.negativenews.domain.models.NegativeNewsScreeningRequest;
import verigate.adapter.negativenews.domain.models.NegativeNewsScreeningResponse;
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
            Map.of(
                "firstName", "John",
                "lastName", "Doe",
                "idNumber", "8501015009087"
            )
        );

        NegativeNewsScreeningResponse clearResponse = NegativeNewsScreeningResponse.clear(
            15, "No adverse news found");

        when(screeningService.screenForNegativeNews(any(NegativeNewsScreeningRequest.class)))
            .thenReturn(clearResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("No adverse news"));
        assertEquals(ScreeningOutcome.CLEAR.toString(), result.get("screeningOutcome"));
        assertEquals("15", result.get("totalArticles"));
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
            Map.of("firstName", "Bad", "lastName", "Actor", "idNumber", "8501015009087")
        );

        NegativeNewsScreeningResponse adverseHighResponse =
            NegativeNewsScreeningResponse.adverseFound(
                List.of(), 50, 10, 0.95, "Serious adverse news detected");

        when(screeningService.screenForNegativeNews(any(NegativeNewsScreeningRequest.class)))
            .thenReturn(adverseHighResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("Adverse news found"));
        assertEquals(ScreeningOutcome.ADVERSE_FOUND.toString(), result.get("screeningOutcome"));
        assertEquals("10", result.get("adverseCount"));

        verify(screeningService).screenForNegativeNews(any(NegativeNewsScreeningRequest.class));
    }

    @Test
    void testHandleAdverseFoundMediumSeverity() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("firstName", "Medium", "lastName", "Risk", "idNumber", "8501015009087")
        );

        NegativeNewsScreeningResponse adverseMediumResponse =
            NegativeNewsScreeningResponse.adverseFound(
                List.of(), 30, 5, 0.75, "Moderate adverse news detected");

        when(screeningService.screenForNegativeNews(any(NegativeNewsScreeningRequest.class)))
            .thenReturn(adverseMediumResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertEquals(ScreeningOutcome.ADVERSE_FOUND.toString(), result.get("screeningOutcome"));

        verify(screeningService).screenForNegativeNews(any(NegativeNewsScreeningRequest.class));
    }

    @Test
    void testHandleAdverseFoundLowSeverity() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("firstName", "Minor", "lastName", "Issue", "idNumber", "8501015009087")
        );

        NegativeNewsScreeningResponse adverseLowResponse =
            NegativeNewsScreeningResponse.adverseFound(
                List.of(), 20, 2, 0.4, "Minor adverse news found");

        when(screeningService.screenForNegativeNews(any(NegativeNewsScreeningRequest.class)))
            .thenReturn(adverseLowResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("low severity"));
        assertEquals(ScreeningOutcome.ADVERSE_FOUND.toString(), result.get("screeningOutcome"));

        verify(screeningService).screenForNegativeNews(any(NegativeNewsScreeningRequest.class));
    }

    @Test
    void testHandleInconclusive() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("firstName", "Unclear", "lastName", "Person", "idNumber", "8501015009087")
        );

        NegativeNewsScreeningResponse inconclusiveResponse =
            NegativeNewsScreeningResponse.inconclusive("Screening results were ambiguous");

        when(screeningService.screenForNegativeNews(any(NegativeNewsScreeningRequest.class)))
            .thenReturn(inconclusiveResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("inconclusive"));
        assertEquals(ScreeningOutcome.INCONCLUSIVE.toString(), result.get("screeningOutcome"));

        verify(screeningService).screenForNegativeNews(any(NegativeNewsScreeningRequest.class));
    }

    @Test
    void testHandleScreeningError() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("firstName", "Test", "lastName", "User", "idNumber", "8501015009087")
        );

        NegativeNewsScreeningResponse errorResponse =
            NegativeNewsScreeningResponse.error("API timeout");

        when(screeningService.screenForNegativeNews(any(NegativeNewsScreeningRequest.class)))
            .thenReturn(errorResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SYSTEM_OUTAGE.toString(), result.get("outcome"));
        assertEquals(ScreeningOutcome.ERROR.toString(), result.get("screeningOutcome"));

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
            Map.of("idNumber", "8501015009087")
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));

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
            Map.of("firstName", "John", "lastName", "Doe", "idNumber", "8501015009087")
        );

        when(screeningService.screenForNegativeNews(any(NegativeNewsScreeningRequest.class)))
            .thenThrow(new PermanentException("Negative news API error"));

        // Act & Assert
        assertThrows(PermanentException.class, () -> handler.handle(command));

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
            Map.of("firstName", "John", "lastName", "Doe", "idNumber", "8501015009087")
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
            Map.of("firstName", "John", "lastName", "Doe", "idNumber", "8501015009087")
        );

        NegativeNewsScreeningResponse clearResponse = NegativeNewsScreeningResponse.clear(
            10, "No adverse news found");

        when(screeningService.screenForNegativeNews(any(NegativeNewsScreeningRequest.class)))
            .thenReturn(clearResponse);

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED, result.outcome());

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
            Map.of("firstName", "John", "lastName", "Doe", "idNumber", "8501015009087")
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
