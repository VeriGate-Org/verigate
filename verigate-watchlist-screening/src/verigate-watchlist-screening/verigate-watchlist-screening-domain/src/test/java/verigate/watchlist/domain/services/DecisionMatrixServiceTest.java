/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import verigate.watchlist.domain.models.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

class DecisionMatrixServiceTest {

    private DecisionMatrixService service;
    private ScreeningConfiguration defaultConfig;
    private ScreeningConfiguration strictConfig;

    @BeforeEach
    void setUp() {
        service = new DefaultDecisionMatrixService();
        defaultConfig = ScreeningConfiguration.defaultConfiguration("TEST_PARTNER");
        strictConfig = ScreeningConfiguration.strictConfiguration("STRICT_PARTNER");
    }

    @Test
    void shouldReturnSystemOutageWhenNoResults() {
        ScreeningDecision decision = service.evaluateResults(List.of(), defaultConfig);
        
        assertEquals(ScreeningDecision.DecisionType.SYSTEM_OUTAGE, decision.getType());
        assertTrue(decision.getReason().contains("No provider results"));
    }

    @Test
    void shouldReturnSystemOutageWhenAllProvidersFail() {
        List<ProviderResult> failedResults = List.of(
            ProviderResult.failure("Provider1", "Connection failed"),
            ProviderResult.timeout("Provider2")
        );
        
        ScreeningDecision decision = service.evaluateResults(failedResults, defaultConfig);
        
        assertEquals(ScreeningDecision.DecisionType.SYSTEM_OUTAGE, decision.getType());
        assertTrue(decision.getReason().contains("All screening providers failed"));
    }

    @Test
    void shouldConfirmHighConfidenceMatches() {
        MatchedEntity highMatch = createMatchedEntity("High Risk Person", 0.95, List.of("Sanctions"));
        ProviderResult result = ProviderResult.success("OpenSanctions", List.of(highMatch));
        
        ScreeningDecision decision = service.evaluateResults(List.of(result), defaultConfig);
        
        assertEquals(ScreeningDecision.DecisionType.MATCH_CONFIRMED, decision.getType());
        assertEquals(0.95, decision.getConfidenceScore());
        assertEquals(highMatch, decision.getHighestMatch());
    }

    @Test
    void shouldRequireReviewForMediumConfidenceMatches() {
        MatchedEntity mediumMatch = createMatchedEntity("Medium Risk Person", 0.75, List.of("PEP"));
        ProviderResult result = ProviderResult.success("WorldCheck", List.of(mediumMatch));
        
        ScreeningDecision decision = service.evaluateResults(List.of(result), defaultConfig);
        
        assertEquals(ScreeningDecision.DecisionType.REVIEW_REQUIRED, decision.getType());
        assertEquals(0.75, decision.getConfidenceScore());
        assertTrue(decision.getReason().contains("requires manual review"));
    }

    @Test
    void shouldClearLowConfidenceMatches() {
        MatchedEntity lowMatch = createMatchedEntity("Low Risk Person", 0.40, List.of("Unknown"));
        ProviderResult result = ProviderResult.success("OpenSanctions", List.of(lowMatch));
        
        ScreeningDecision decision = service.evaluateResults(List.of(result), defaultConfig);
        
        assertEquals(ScreeningDecision.DecisionType.CLEARED, decision.getType());
        assertTrue(decision.getReason().contains("No significant watchlist matches"));
    }

    @Test
    void shouldApplyStrictConfigurationThresholds() {
        MatchedEntity mediumMatch = createMatchedEntity("Medium Risk", 0.75, List.of("PEP"));
        ProviderResult result = ProviderResult.success("OpenSanctions", List.of(mediumMatch));
        
        // With default config (0.90 threshold), this should be review required
        ScreeningDecision defaultDecision = service.evaluateResults(List.of(result), defaultConfig);
        assertEquals(ScreeningDecision.DecisionType.REVIEW_REQUIRED, defaultDecision.getType());
        
        // With strict config (0.80 threshold), this should be review required
        // as 0.75 < 0.80 (auto-reject threshold in strict config)
        ScreeningDecision strictDecision = service.evaluateResults(List.of(result), strictConfig);
        assertEquals(ScreeningDecision.DecisionType.REVIEW_REQUIRED, strictDecision.getType());
    }

    @Test
    void shouldSelectHighestScoreAcrossProviders() {
        MatchedEntity lowMatch = createMatchedEntity("Low Risk", 0.60, List.of("Unknown"));
        MatchedEntity highMatch = createMatchedEntity("High Risk", 0.85, List.of("Sanctions"));
        
        List<ProviderResult> results = List.of(
            ProviderResult.success("Provider1", List.of(lowMatch)),
            ProviderResult.success("Provider2", List.of(highMatch))
        );
        
        ScreeningDecision decision = service.evaluateResults(results, defaultConfig);
        
        assertEquals(ScreeningDecision.DecisionType.REVIEW_REQUIRED, decision.getType());
        assertEquals(0.85, decision.getConfidenceScore());
        assertEquals("High Risk", decision.getHighestMatch().getName());
    }

    @Test
    void shouldApplyEnhancedLogicForPEPs() {
        // Create PEP match just below auto-reject threshold
        MatchedEntity pepMatch = createMatchedEntity("PEP Person", 0.86, List.of("PEP"));
        ProviderResult result = ProviderResult.success("WorldCheck", List.of(pepMatch));
        
        ScreeningDecision decision = service.evaluateWithEnhancedLogic(List.of(result), defaultConfig);
        
        // Should be escalated due to PEP escalation threshold (0.85)
        assertEquals(ScreeningDecision.DecisionType.MATCH_CONFIRMED, decision.getType());
        assertTrue(decision.getReason().contains("PEP match above escalation threshold"));
    }

    @Test
    void shouldApplyEnhancedLogicForSanctions() {
        // Create sanctions match just below auto-reject threshold  
        MatchedEntity sanctionsMatch = createMatchedEntity("Sanctioned Person", 0.82, List.of("Sanctions"));
        ProviderResult result = ProviderResult.success("OpenSanctions", List.of(sanctionsMatch));
        
        ScreeningDecision decision = service.evaluateWithEnhancedLogic(List.of(result), defaultConfig);
        
        // Should be escalated due to sanctions escalation threshold (0.80)
        assertEquals(ScreeningDecision.DecisionType.MATCH_CONFIRMED, decision.getType());
        assertTrue(decision.getReason().contains("Watchlist match above escalation threshold"));
    }

    @Test
    void shouldHandleAutoApproveConfiguration() {
        MatchedEntity lowMatch = createMatchedEntity("Low Risk", 0.55, List.of("Unknown"));
        ProviderResult result = ProviderResult.success("OpenSanctions", List.of(lowMatch));
        
        // Default config has auto-approve enabled
        ScreeningDecision decision = service.evaluateResults(List.of(result), defaultConfig);
        assertEquals(ScreeningDecision.DecisionType.CLEARED, decision.getType());
        
        // Strict config has auto-approve disabled
        ScreeningDecision strictDecision = service.evaluateResults(List.of(result), strictConfig);
        assertEquals(ScreeningDecision.DecisionType.REVIEW_REQUIRED, strictDecision.getType());
    }

    private MatchedEntity createMatchedEntity(String name, double score, List<String> categories) {
        return new MatchedEntity(
            "entity_" + name.toLowerCase().replace(" ", "_"),
            name,
            score,
            categories,
            List.of("TestWatchlist"),
            "Test entity description",
            Map.of()
        );
    }
}