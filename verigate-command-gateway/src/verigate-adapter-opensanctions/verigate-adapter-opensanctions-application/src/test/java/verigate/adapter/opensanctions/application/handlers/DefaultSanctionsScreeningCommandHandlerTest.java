/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.opensanctions.application.handlers;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import verigate.adapter.opensanctions.domain.models.EntityMatchResponse;
import verigate.adapter.opensanctions.domain.services.OpenSanctionsMatchingService;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.events.VerificationEventPublisher;
import verigate.verification.cg.domain.factories.EventFactory;
import verigate.verification.cg.domain.models.VerificationResult;
import verigate.verification.cg.domain.models.VerificationOutcome;
import verigate.verification.cg.domain.models.VerificationType;
import verigate.verification.cg.domain.events.VerificationSucceededEvent;
import verigate.verification.cg.domain.events.VerificationHardFailEvent;

import java.time.Instant;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DefaultSanctionsScreeningCommandHandlerTest {

    @Mock
    private OpenSanctionsMatchingService mockOpenSanctionsService;

    @Mock
    private VerificationEventPublisher mockEventPublisher;

    @Mock
    private EventFactory mockEventFactory;

    private DefaultSanctionsScreeningCommandHandler handler;
    private VerifyPartyCommand testCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new DefaultSanctionsScreeningCommandHandler(
            mockOpenSanctionsService, 
            mockEventPublisher, 
            mockEventFactory
        );

        // Create test command instance
        Map<String, Object> testMetadata = new HashMap<>();
        testMetadata.put("firstName", "John");
        testMetadata.put("lastName", "Doe");
        testMetadata.put("dateOfBirth", "1980-01-01");
        
        testCommand = new VerifyPartyCommand(
            UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
            Instant.now(),
            "test-user",
            VerificationType.SANCTIONS_SCREENING,
            null,
            testMetadata
        );
        
        // Mock the event factory to return concrete event instances
        VerificationSucceededEvent successEvent = new VerificationSucceededEvent(
            UUID.randomUUID(), Instant.now(), Instant.now(), null, VerificationType.SANCTIONS_SCREENING, null
        );
        VerificationHardFailEvent failEvent = new VerificationHardFailEvent(
            UUID.randomUUID(), Instant.now(), Instant.now(), null, VerificationType.SANCTIONS_SCREENING, null
        );
        
        // Set up different mocks for different outcomes
        when(mockEventFactory.createEvent(eq(VerificationOutcome.SUCCEEDED), any(), any())).thenReturn(successEvent);
        when(mockEventFactory.createEvent(eq(VerificationOutcome.HARD_FAIL), any(), any())).thenReturn(failEvent);
        when(mockEventFactory.createEvent(eq(VerificationOutcome.SYSTEM_OUTAGE), any(), any())).thenReturn(failEvent);
        when(mockEventFactory.createEvent(eq(VerificationOutcome.SOFT_FAIL), any(), any())).thenReturn(failEvent);
    }

    @Test
    void handle_noMatches_returnsSuccessfulVerification() throws Exception {
        // Arrange
        EntityMatchResponse mockResponse = new EntityMatchResponse(Map.of(), Map.of(), 5);
        when(mockOpenSanctionsService.matchEntities(any())).thenReturn(mockResponse);

        // Act
        Map<String, String> result = handler.handle(testCommand);

        // Assert
        assertNotNull(result);
        assertEquals("OpenSanctions", result.get("provider"));
        assertEquals("SUCCEEDED", result.get("outcome"));

        verify(mockOpenSanctionsService).matchEntities(any());
        verify(mockEventPublisher).publish(any());
        verify(mockEventFactory).createEvent(eq(VerificationOutcome.SUCCEEDED), eq(testCommand), any());
    }

    @Test
    void handle_transientException_throwsAndPublishesEvent() throws Exception {
        // Arrange
        TransientException transientError = new TransientException("Service temporarily unavailable");
        when(mockOpenSanctionsService.matchEntities(any())).thenThrow(transientError);

        // Act & Assert
        assertThrows(TransientException.class, () -> handler.handle(testCommand));
        
        verify(mockOpenSanctionsService).matchEntities(any());
        verify(mockEventPublisher).publish(any());
        verify(mockEventFactory).createEvent(eq(VerificationOutcome.SYSTEM_OUTAGE), eq(testCommand), any());
    }

    @Test
    void handle_permanentException_throwsAndPublishesEvent() throws Exception {
        // Arrange
        PermanentException permanentError = new PermanentException("Invalid request data");
        when(mockOpenSanctionsService.matchEntities(any())).thenThrow(permanentError);

        // Act & Assert
        assertThrows(PermanentException.class, () -> handler.handle(testCommand));
        
        verify(mockOpenSanctionsService).matchEntities(any());
        verify(mockEventPublisher).publish(any());
        verify(mockEventFactory).createEvent(eq(VerificationOutcome.HARD_FAIL), eq(testCommand), any());
    }

    @Test
    void handleAsync_validCommand_returnsCompletableFuture() throws Exception {
        // Arrange
        EntityMatchResponse mockResponse = new EntityMatchResponse(Map.of(), Map.of(), 5);
        when(mockOpenSanctionsService.matchEntities(any())).thenReturn(mockResponse);

        // Act
        var futureResult = handler.handleAsync(testCommand);

        // Assert
        assertNotNull(futureResult);
        VerificationResult result = futureResult.get();
        assertNotNull(result);
        assertEquals(VerificationOutcome.SUCCEEDED, result.outcome());

        verify(mockOpenSanctionsService).matchEntities(any());
        verify(mockEventPublisher).publish(any());
    }
}