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
import verigate.adapter.opensanctions.domain.models.EntityMatches;
import verigate.adapter.opensanctions.domain.models.ScoredEntity;
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
import java.util.List;
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

    // ---- Phase 5.5: Added HARD_FAIL, SOFT_FAIL, and result details tests ----

    @Test
    void handle_highScoreMatch_returnsHardFail() throws Exception {
        // Arrange
        EntityMatchResponse mockResponse = createResponseWithMatch(0.95, "us_ofac_sdn");
        when(mockOpenSanctionsService.matchEntities(any())).thenReturn(mockResponse);

        // Act
        Map<String, String> result = handler.handle(testCommand);

        // Assert
        assertNotNull(result);
        assertEquals("HARD_FAIL", result.get("outcome"));
        assertEquals("OpenSanctions", result.get("provider"));
        assertNotNull(result.get("failureReason"));
        assertTrue(result.get("failureReason").contains("OpenSanctions match found"));

        verify(mockEventFactory).createEvent(eq(VerificationOutcome.HARD_FAIL), eq(testCommand), any());
    }

    @Test
    void handle_mediumScoreMatch_returnsSoftFail() throws Exception {
        // Arrange
        EntityMatchResponse mockResponse = createResponseWithMatch(0.78, "eu_sanctions");
        when(mockOpenSanctionsService.matchEntities(any())).thenReturn(mockResponse);

        // Act
        Map<String, String> result = handler.handle(testCommand);

        // Assert
        assertNotNull(result);
        assertEquals("SOFT_FAIL", result.get("outcome"));
        assertEquals("OpenSanctions", result.get("provider"));
        assertNotNull(result.get("failureReason"));

        verify(mockEventFactory).createEvent(eq(VerificationOutcome.SOFT_FAIL), eq(testCommand), any());
    }

    @Test
    void handle_lowScoreMatch_returnsSucceeded() throws Exception {
        // Arrange
        EntityMatchResponse mockResponse = createResponseWithMatch(0.55, "us_ofac_sdn");
        when(mockOpenSanctionsService.matchEntities(any())).thenReturn(mockResponse);

        // Act
        Map<String, String> result = handler.handle(testCommand);

        // Assert
        assertNotNull(result);
        assertEquals("SUCCEEDED", result.get("outcome"));
        assertNull(result.get("failureReason"));

        verify(mockEventFactory).createEvent(eq(VerificationOutcome.SUCCEEDED), eq(testCommand), any());
    }

    @Test
    void handle_resultContainsMatchDetails() throws Exception {
        // Arrange
        ScoredEntity entity = new ScoredEntity.Builder()
            .id("Q12345")
            .caption("Sanctioned Person")
            .datasets(List.of("us_ofac_sdn"))
            .score(0.92)
            .target(true)
            .build();

        EntityMatches matches = new EntityMatches(200, List.of(entity), null, null);
        EntityMatchResponse mockResponse =
            new EntityMatchResponse(Map.of("entity1", matches), Map.of(), 5);
        when(mockOpenSanctionsService.matchEntities(any())).thenReturn(mockResponse);

        // Act
        Map<String, String> result = handler.handle(testCommand);

        // Assert - verify match details are merged into result
        assertEquals("1", result.get("total_matches"));
        assertEquals("1", result.get("significant_matches_count"));
        assertEquals("Q12345", result.get("match_0_id"));
        assertEquals("Sanctioned Person", result.get("match_0_caption"));
        assertEquals("0.92", result.get("match_0_score"));
        assertEquals("us_ofac_sdn", result.get("match_0_datasets"));
        assertEquals("SANCTIONS", result.get("match_0_type"));
        assertEquals("true", result.get("match_0_target"));
        assertEquals("entity-matching", result.get("algorithm"));
    }

    @Test
    void handle_pepMatch_classifiedCorrectly() throws Exception {
        // Arrange
        ScoredEntity pepEntity = new ScoredEntity.Builder()
            .id("PEP-001")
            .caption("Politically Exposed Person")
            .datasets(List.of("za_pep_registry"))
            .score(0.85)
            .build();

        EntityMatches matches = new EntityMatches(200, List.of(pepEntity), null, null);
        EntityMatchResponse mockResponse =
            new EntityMatchResponse(Map.of("entity1", matches), Map.of(), 5);
        when(mockOpenSanctionsService.matchEntities(any())).thenReturn(mockResponse);

        // Act
        Map<String, String> result = handler.handle(testCommand);

        // Assert
        assertEquals("SOFT_FAIL", result.get("outcome"));
        assertEquals("PEP", result.get("match_0_type"));
        assertTrue(result.get("failureReason").contains("PEP"));
    }

    @Test
    void handle_unexpectedException_wrapsInPermanentException() throws Exception {
        // Arrange
        when(mockOpenSanctionsService.matchEntities(any()))
            .thenThrow(new RuntimeException("Unexpected failure"));

        // Act & Assert
        assertThrows(PermanentException.class, () -> handler.handle(testCommand));

        verify(mockEventPublisher).publish(any());
        verify(mockEventFactory).createEvent(eq(VerificationOutcome.HARD_FAIL), eq(testCommand), any());
    }

    @Test
    void handle_eventPublishFailure_doesNotFailVerification() throws Exception {
        // Arrange
        EntityMatchResponse mockResponse = new EntityMatchResponse(Map.of(), Map.of(), 5);
        when(mockOpenSanctionsService.matchEntities(any())).thenReturn(mockResponse);
        doThrow(new RuntimeException("Event bus down")).when(mockEventPublisher).publish(any());

        // Act - should not throw despite event publishing failure
        Map<String, String> result = handler.handle(testCommand);

        // Assert
        assertNotNull(result);
        assertEquals("SUCCEEDED", result.get("outcome"));
    }

    @Test
    void handleAsync_highScoreMatch_returnsHardFail() throws Exception {
        // Arrange
        EntityMatchResponse mockResponse = createResponseWithMatch(0.95, "us_ofac_sdn");
        when(mockOpenSanctionsService.matchEntities(any())).thenReturn(mockResponse);

        // Act
        var futureResult = handler.handleAsync(testCommand);
        VerificationResult result = futureResult.get();

        // Assert
        assertEquals(VerificationOutcome.HARD_FAIL, result.outcome());
        assertNotNull(result.failureReason());
    }

    @Test
    void handleAsync_mediumScoreMatch_returnsSoftFail() throws Exception {
        // Arrange
        EntityMatchResponse mockResponse = createResponseWithMatch(0.75, "eu_sanctions");
        when(mockOpenSanctionsService.matchEntities(any())).thenReturn(mockResponse);

        // Act
        var futureResult = handler.handleAsync(testCommand);
        VerificationResult result = futureResult.get();

        // Assert
        assertEquals(VerificationOutcome.SOFT_FAIL, result.outcome());
    }

    // ---- Helper methods ----

    private EntityMatchResponse createResponseWithMatch(double score, String dataset) {
        ScoredEntity entity = new ScoredEntity.Builder()
            .id("ent-" + UUID.randomUUID().toString().substring(0, 8))
            .caption("Test Entity")
            .datasets(List.of(dataset))
            .score(score)
            .build();

        EntityMatches matches = new EntityMatches(200, List.of(entity), null, null);
        return new EntityMatchResponse(Map.of("entity1", matches), Map.of(), 5);
    }
}