/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.fraudwatchlist.application.handlers;

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
import verigate.adapter.fraudwatchlist.domain.models.FraudCheckRequest;
import verigate.adapter.fraudwatchlist.domain.models.FraudCheckResponse;
import verigate.adapter.fraudwatchlist.domain.models.FraudCheckStatus;
import verigate.adapter.fraudwatchlist.domain.models.FraudSource;
import verigate.adapter.fraudwatchlist.domain.services.FraudWatchlistScreeningService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationOutcome;

class DefaultScreenFraudWatchlistCommandHandlerTest {

    @Mock
    private FraudWatchlistScreeningService screeningService;

    private DefaultScreenFraudWatchlistCommandHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new DefaultScreenFraudWatchlistCommandHandler(screeningService);
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
                "idNumber", "8501015009087",
                "firstName", "John",
                "lastName", "Doe"
            )
        );

        FraudCheckResponse clearResponse = FraudCheckResponse.clear("No fraud alerts found");

        when(screeningService.checkFraudWatchlist(any(FraudCheckRequest.class)))
            .thenReturn(clearResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("clear"));
        assertEquals(FraudCheckStatus.CLEAR.toString(), result.get("fraudStatus"));

        verify(screeningService).checkFraudWatchlist(any(FraudCheckRequest.class));
    }

    @Test
    void testHandleConfirmedFraud() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "8501015009087", "firstName", "Bad", "lastName", "Actor")
        );

        FraudCheckResponse confirmedFraudResponse = FraudCheckResponse.confirmedFraud(
            List.of(), 0.95, List.of(FraudSource.SABRIC, FraudSource.SAFPS),
            "Confirmed fraud detected");

        when(screeningService.checkFraudWatchlist(any(FraudCheckRequest.class)))
            .thenReturn(confirmedFraudResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("Confirmed fraud"));
        assertEquals(FraudCheckStatus.CONFIRMED_FRAUD.toString(), result.get("fraudStatus"));

        verify(screeningService).checkFraudWatchlist(any(FraudCheckRequest.class));
    }

    @Test
    void testHandleSuspectedFraud() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "8501015009087", "firstName", "Suspect", "lastName", "Person")
        );

        FraudCheckResponse suspectedResponse = new FraudCheckResponse(
            FraudCheckStatus.SUSPECTED_FRAUD,
            List.of(),
            0.7,
            List.of(FraudSource.COMPUSCAN),
            "Suspected fraudulent activity detected"
        );

        when(screeningService.checkFraudWatchlist(any(FraudCheckRequest.class)))
            .thenReturn(suspectedResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("Suspected fraud"));
        assertEquals(FraudCheckStatus.SUSPECTED_FRAUD.toString(), result.get("fraudStatus"));

        verify(screeningService).checkFraudWatchlist(any(FraudCheckRequest.class));
    }

    @Test
    void testHandleFlaggedHighRisk() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "8501015009087", "firstName", "Risky", "lastName", "Person")
        );

        FraudCheckResponse flaggedHighResponse = FraudCheckResponse.flagged(
            List.of(), 0.85, List.of(FraudSource.TRANSUNION), "High risk flagged");

        when(screeningService.checkFraudWatchlist(any(FraudCheckRequest.class)))
            .thenReturn(flaggedHighResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL.toString(), result.get("outcome"));
        assertEquals(FraudCheckStatus.FLAGGED.toString(), result.get("fraudStatus"));

        verify(screeningService).checkFraudWatchlist(any(FraudCheckRequest.class));
    }

    @Test
    void testHandleFlaggedMediumRisk() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "8501015009087", "firstName", "Medium", "lastName", "Risk")
        );

        FraudCheckResponse flaggedMediumResponse = FraudCheckResponse.flagged(
            List.of(), 0.6, List.of(FraudSource.INTERNAL_BLACKLIST), "Medium risk flagged");

        when(screeningService.checkFraudWatchlist(any(FraudCheckRequest.class)))
            .thenReturn(flaggedMediumResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertEquals(FraudCheckStatus.FLAGGED.toString(), result.get("fraudStatus"));

        verify(screeningService).checkFraudWatchlist(any(FraudCheckRequest.class));
    }

    @Test
    void testHandleFlaggedLowRisk() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "8501015009087", "firstName", "Low", "lastName", "Risk")
        );

        FraudCheckResponse flaggedLowResponse = FraudCheckResponse.flagged(
            List.of(), 0.3, List.of(FraudSource.COMPUSCAN), "Low risk flagged");

        when(screeningService.checkFraudWatchlist(any(FraudCheckRequest.class)))
            .thenReturn(flaggedLowResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED.toString(), result.get("outcome"));
        assertEquals(FraudCheckStatus.FLAGGED.toString(), result.get("fraudStatus"));
        assertTrue(result.get("details").contains("Low risk"));

        verify(screeningService).checkFraudWatchlist(any(FraudCheckRequest.class));
    }

    @Test
    void testHandleNotFound() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "9999999999999", "firstName", "Clean", "lastName", "Person")
        );

        FraudCheckResponse notFoundResponse = new FraudCheckResponse(
            FraudCheckStatus.NOT_FOUND, List.of(), 0.0, List.of(),
            "No records found on fraud watchlists");

        when(screeningService.checkFraudWatchlist(any(FraudCheckRequest.class)))
            .thenReturn(notFoundResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED.toString(), result.get("outcome"));
        assertEquals(FraudCheckStatus.NOT_FOUND.toString(), result.get("fraudStatus"));

        verify(screeningService).checkFraudWatchlist(any(FraudCheckRequest.class));
    }

    @Test
    void testHandleSystemError() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "8501015009087", "firstName", "Test", "lastName", "User")
        );

        FraudCheckResponse errorResponse = FraudCheckResponse.error("System error");

        when(screeningService.checkFraudWatchlist(any(FraudCheckRequest.class)))
            .thenReturn(errorResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SYSTEM_OUTAGE.toString(), result.get("outcome"));
        assertEquals(FraudCheckStatus.ERROR.toString(), result.get("fraudStatus"));

        verify(screeningService).checkFraudWatchlist(any(FraudCheckRequest.class));
    }

    @Test
    void testHandleMissingIdNumber() {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("firstName", "John", "lastName", "Doe")
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
            Map.of("idNumber", "8501015009087", "firstName", "John", "lastName", "Doe")
        );

        when(screeningService.checkFraudWatchlist(any(FraudCheckRequest.class)))
            .thenThrow(new PermanentException("Fraud watchlist API error"));

        // Act & Assert
        assertThrows(PermanentException.class, () -> handler.handle(command));

        verify(screeningService).checkFraudWatchlist(any(FraudCheckRequest.class));
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
            Map.of("idNumber", "8501015009087", "firstName", "John", "lastName", "Doe")
        );

        when(screeningService.checkFraudWatchlist(any(FraudCheckRequest.class)))
            .thenThrow(new TransientException("Network timeout"));

        // Act & Assert
        assertThrows(TransientException.class, () -> handler.handle(command));

        verify(screeningService).checkFraudWatchlist(any(FraudCheckRequest.class));
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
            Map.of("idNumber", "8501015009087", "firstName", "John", "lastName", "Doe")
        );

        FraudCheckResponse clearResponse = FraudCheckResponse.clear("No fraud alerts found");

        when(screeningService.checkFraudWatchlist(any(FraudCheckRequest.class)))
            .thenReturn(clearResponse);

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED, result.outcome());

        verify(screeningService).checkFraudWatchlist(any(FraudCheckRequest.class));
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
            Map.of("idNumber", "8501015009087", "firstName", "John", "lastName", "Doe")
        );

        when(screeningService.checkFraudWatchlist(any(FraudCheckRequest.class)))
            .thenThrow(new PermanentException("Service error"));

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL, result.outcome());
        assertTrue(result.failureReason().contains("Fraud watchlist screening failed"));

        verify(screeningService).checkFraudWatchlist(any(FraudCheckRequest.class));
    }
}
