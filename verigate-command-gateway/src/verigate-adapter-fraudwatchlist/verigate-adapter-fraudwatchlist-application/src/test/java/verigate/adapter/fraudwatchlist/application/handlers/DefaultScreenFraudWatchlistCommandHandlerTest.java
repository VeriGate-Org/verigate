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
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import verigate.adapter.fraudwatchlist.domain.models.FraudAlert;
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
            Map.of("idNumber", "9001015009087")
        );

        FraudCheckResponse clearResponse = FraudCheckResponse.clear("No fraud alerts found");

        when(screeningService.checkFraudWatchlist(any(FraudCheckRequest.class)))
            .thenReturn(clearResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("clear"));
        assertEquals("CLEAR", result.get("fraudStatus"));
        assertEquals("0.0", result.get("riskScore"));
        assertEquals("0", result.get("alertCount"));

        verify(screeningService).checkFraudWatchlist(any(FraudCheckRequest.class));
    }

    @Test
    void testHandleFlaggedScreening() throws Exception {
        // Arrange
        VerifyPartyCommand command = new VerifyPartyCommand(
            UUID.randomUUID(),
            Instant.now(),
            "test-user",
            null,
            null,
            Map.of("idNumber", "9001015009087")
        );

        List<FraudAlert> alerts = List.of(
            new FraudAlert(FraudSource.SABRIC, "IDENTITY_FRAUD", LocalDate.now(), 0.6,
                "Possible identity fraud", "REF-001")
        );
        List<FraudSource> sources = List.of(FraudSource.SABRIC);

        FraudCheckResponse flaggedResponse = FraudCheckResponse.flagged(
            alerts, 0.6, sources, "Flagged for review");

        when(screeningService.checkFraudWatchlist(any(FraudCheckRequest.class)))
            .thenReturn(flaggedResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL.toString(), result.get("outcome"));
        assertEquals("FLAGGED", result.get("fraudStatus"));
        assertEquals("0.6", result.get("riskScore"));
        assertEquals("1", result.get("alertCount"));

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
            Map.of("idNumber", "9001015009087")
        );

        List<FraudAlert> alerts = List.of(
            new FraudAlert(FraudSource.SAFPS, "CONFIRMED_FRAUD", LocalDate.now(), 0.95,
                "Confirmed fraud match", "REF-002")
        );
        List<FraudSource> sources = List.of(FraudSource.SAFPS);

        FraudCheckResponse confirmedResponse = FraudCheckResponse.confirmedFraud(
            alerts, 0.95, sources, "Confirmed fraud detected");

        when(screeningService.checkFraudWatchlist(any(FraudCheckRequest.class)))
            .thenReturn(confirmedResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL.toString(), result.get("outcome"));
        assertTrue(result.get("details").contains("Confirmed fraud"));
        assertEquals("CONFIRMED_FRAUD", result.get("fraudStatus"));
        assertEquals("0.95", result.get("riskScore"));

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
            Map.of("someOtherKey", "value")
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, () -> handler.handle(command));
        assertTrue(exception.getMessage().contains("ID number is required for fraud watchlist screening"));

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
            Map.of("idNumber", "9001015009087")
        );

        FraudCheckResponse errorResponse = FraudCheckResponse.error("Service unavailable");

        when(screeningService.checkFraudWatchlist(any(FraudCheckRequest.class)))
            .thenReturn(errorResponse);

        // Act
        Map<String, String> result = handler.handle(command);

        // Assert
        assertEquals(VerificationOutcome.SYSTEM_OUTAGE.toString(), result.get("outcome"));
        assertEquals("ERROR", result.get("fraudStatus"));

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
            Map.of("idNumber", "9001015009087")
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
            Map.of("idNumber", "9001015009087")
        );

        FraudCheckResponse clearResponse = FraudCheckResponse.clear("No fraud alerts found");

        when(screeningService.checkFraudWatchlist(any(FraudCheckRequest.class)))
            .thenReturn(clearResponse);

        // Act
        var future = handler.handleAsync(command);
        var result = future.get();

        // Assert
        assertEquals(VerificationOutcome.SUCCEEDED, result.outcome());
        assertTrue(result.failureReason().contains("clear"));

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
            Map.of("idNumber", "9001015009087")
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
