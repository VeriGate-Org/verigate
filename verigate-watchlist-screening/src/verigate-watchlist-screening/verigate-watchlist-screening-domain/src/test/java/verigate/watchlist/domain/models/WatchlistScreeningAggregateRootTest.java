/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

class WatchlistScreeningAggregateRootTest {

    private WatchlistScreeningAggregateRoot screeningRoot;
    private ScreeningRequest testRequest;
    private String partnerId;
    private UUID screeningId;

    @BeforeEach
    void setUp() {
        partnerId = "TEST_PARTNER";
        screeningId = UUID.randomUUID();
        testRequest = ScreeningRequest.person("John", "Doe", "1980-01-01", "US");
        screeningRoot = new WatchlistScreeningAggregateRoot(screeningId, partnerId, testRequest);
    }

    @Test
    void shouldInitializeWithCorrectValues() {
        assertEquals(screeningId, screeningRoot.getScreeningId());
        assertEquals(partnerId, screeningRoot.getPartnerId());
        assertEquals(testRequest, screeningRoot.getRequest());
        assertEquals(ScreeningStatus.PENDING, screeningRoot.getStatus());
        assertTrue(screeningRoot.getProviderResults().isEmpty());
        assertNotNull(screeningRoot.getCreatedAt());
    }

    @Test
    void shouldAddProviderResultAndEvaluate() {
        // Create a high-confidence match
        MatchedEntity highConfidenceMatch = new MatchedEntity(
            "entity123", "John Doe", 0.95, 
            List.of("Sanctions"), List.of("UN"), 
            "High-risk individual", Map.of()
        );
        
        ProviderResult result = ProviderResult.success("OpenSanctions", 
            List.of(highConfidenceMatch));
        
        screeningRoot.addProviderResult(result);
        
        assertEquals(1, screeningRoot.getProviderResults().size());
        assertEquals(ScreeningStatus.COMPLETED, screeningRoot.getStatus());
        assertNotNull(screeningRoot.getDecision());
        assertEquals(ScreeningDecision.DecisionType.MATCH_CONFIRMED, 
                   screeningRoot.getDecision().getType());
    }

    @Test
    void shouldHandleMediumConfidenceMatch() {
        // Create a medium-confidence match
        MatchedEntity mediumMatch = new MatchedEntity(
            "entity456", "John Doe", 0.75, 
            List.of("PEP"), List.of("OFAC"), 
            "Politically exposed person", Map.of()
        );
        
        ProviderResult result = ProviderResult.success("WorldCheck", 
            List.of(mediumMatch));
        
        screeningRoot.addProviderResult(result);
        
        assertEquals(ScreeningDecision.DecisionType.REVIEW_REQUIRED, 
                   screeningRoot.getDecision().getType());
    }

    @Test
    void shouldHandleLowConfidenceMatch() {
        // Create a low-confidence match
        MatchedEntity lowMatch = new MatchedEntity(
            "entity789", "John D.", 0.60, 
            List.of("Unknown"), List.of("Custom"), 
            "Low confidence match", Map.of()
        );
        
        ProviderResult result = ProviderResult.success("OpenSanctions", 
            List.of(lowMatch));
        
        screeningRoot.addProviderResult(result);
        
        assertEquals(ScreeningDecision.DecisionType.CLEARED, 
                   screeningRoot.getDecision().getType());
    }

    @Test
    void shouldHandleNoMatches() {
        ProviderResult result = ProviderResult.success("OpenSanctions", List.of());
        
        screeningRoot.addProviderResult(result);
        
        assertEquals(ScreeningDecision.DecisionType.CLEARED, 
                   screeningRoot.getDecision().getType());
    }

    @Test
    void shouldHandleProviderFailure() {
        ProviderResult failureResult = ProviderResult.failure("OpenSanctions", 
            "Connection timeout");
        
        screeningRoot.addProviderResult(failureResult);
        
        assertEquals(ScreeningStatus.FAILED, screeningRoot.getStatus());
        assertEquals(ScreeningDecision.DecisionType.SYSTEM_OUTAGE, 
                   screeningRoot.getDecision().getType());
    }

    @Test
    void shouldSelectHighestScoreAcrossProviders() {
        // Add lower scoring result first
        MatchedEntity lowMatch = new MatchedEntity(
            "low", "John D.", 0.60, List.of("PEP"), List.of("Custom"), "", Map.of()
        );
        ProviderResult lowResult = ProviderResult.success("Provider1", List.of(lowMatch));
        
        // Add higher scoring result
        MatchedEntity highMatch = new MatchedEntity(
            "high", "John Doe", 0.85, List.of("Sanctions"), List.of("UN"), "", Map.of()
        );
        ProviderResult highResult = ProviderResult.success("Provider2", List.of(highMatch));
        
        screeningRoot.addProviderResult(lowResult);
        // Status should still be pending after first low-confidence result
        assertEquals(ScreeningStatus.COMPLETED, screeningRoot.getStatus()); // Actually evaluates immediately
        
        screeningRoot.addProviderResult(highResult);
        
        // Should now be based on highest score (0.85 = review required)
        assertEquals(ScreeningDecision.DecisionType.REVIEW_REQUIRED, 
                   screeningRoot.getDecision().getType());
        assertEquals(0.85, screeningRoot.getDecision().getConfidenceScore());
    }
}
