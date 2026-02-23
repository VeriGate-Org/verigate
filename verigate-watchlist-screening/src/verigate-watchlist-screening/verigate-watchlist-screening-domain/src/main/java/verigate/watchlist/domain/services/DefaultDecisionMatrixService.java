/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.services;

import verigate.watchlist.domain.models.MatchedEntity;
import verigate.watchlist.domain.models.ProviderResult;
import verigate.watchlist.domain.models.ScreeningDecision;
import verigate.watchlist.domain.models.ScreeningConfiguration;

import java.util.List;

/**
 * Implementation of decision matrix service for watchlist screening results.
 * Evaluates match scores against partner-defined thresholds.
 */
public class DefaultDecisionMatrixService implements DecisionMatrixService {
    
    /**
     * Applies decision matrix based on provider results and partner configuration.
     */
    @Override
    public ScreeningDecision evaluateResults(List<ProviderResult> results, ScreeningConfiguration config) {
        if (results.isEmpty()) {
            return ScreeningDecision.systemOutage("No provider results available");
        }
        
        // Check for provider failures
        boolean allProvidersFailed = results.stream().allMatch(r -> !r.isSuccessful());
        if (allProvidersFailed) {
            return ScreeningDecision.systemOutage("All screening providers failed");
        }
        
        // Find highest scoring match across all successful providers
        ProviderResult highestResult = findHighestScoringResult(results);
        if (highestResult == null) {
            return ScreeningDecision.cleared("No matches found across all providers");
        }
        
        MatchedEntity highestMatch = highestResult.getHighestMatch();
        double score = highestMatch.getConfidenceScore();
        
        // Apply configured thresholds
        if (score >= config.getAutoRejectThreshold()) {
            return ScreeningDecision.confirmed(
                buildConfirmedMessage(highestResult, highestMatch),
                score,
                highestMatch
            );
        }
        
        if (score >= config.getManualReviewThreshold()) {
            return ScreeningDecision.reviewRequired(
                buildReviewMessage(highestResult, highestMatch),
                score,
                highestMatch
            );
        }
        
        if (score >= config.getMinimumThreshold()) {
            // Low confidence match - depends on partner rules
            if (config.isAutoApproveEnabled()) {
                return ScreeningDecision.cleared("Low confidence matches auto-approved per partner configuration");
            } else {
                return ScreeningDecision.reviewRequired(
                    "Low confidence match requires review per partner policy",
                    score,
                    highestMatch
                );
            }
        }
        
        return ScreeningDecision.cleared("No significant watchlist matches found");
    }
    
    /**
     * Finds the provider result with the highest scoring match.
     */
    private ProviderResult findHighestScoringResult(List<ProviderResult> results) {
        return results.stream()
            .filter(ProviderResult::isSuccessful)
            .filter(r -> !r.getMatches().isEmpty())
            .max((a, b) -> Double.compare(a.getHighestScore(), b.getHighestScore()))
            .orElse(null);
    }
    
    /**
     * Builds failure message for confirmed watchlist matches.
     */
    private String buildConfirmedMessage(ProviderResult result, MatchedEntity match) {
        return String.format("High confidence watchlist match confirmed via %s: %s", 
            result.getProvider(), match.getMatchSummary());
    }
    
    /**
     * Builds message for matches requiring manual review.
     */
    private String buildReviewMessage(ProviderResult result, MatchedEntity match) {
        return String.format("Potential watchlist match via %s requires manual review: %s", 
            result.getProvider(), match.getMatchSummary());
    }
    
    /**
     * Applies enhanced decision logic considering multiple factors.
     */
    @Override
    public ScreeningDecision evaluateWithEnhancedLogic(List<ProviderResult> results, ScreeningConfiguration config) {
        ScreeningDecision basicDecision = evaluateResults(results, config);
        
        // If basic decision is not conclusive, apply enhanced logic
        if (basicDecision.getType() == ScreeningDecision.DecisionType.REVIEW_REQUIRED) {
            return applyEnhancedReviewLogic(results, config, basicDecision);
        }
        
        return basicDecision;
    }
    
    /**
     * Applies enhanced logic for review cases, considering additional factors.
     */
    private ScreeningDecision applyEnhancedReviewLogic(List<ProviderResult> results, 
                                                      ScreeningConfiguration config, 
                                                      ScreeningDecision basicDecision) {
        
        MatchedEntity match = basicDecision.getHighestMatch();
        if (match == null) return basicDecision;
        
        // Auto-escalate PEPs above certain threshold
        if (match.isPEP() && basicDecision.getConfidenceScore() >= config.getPepEscalationThreshold()) {
            return ScreeningDecision.confirmed(
                "PEP match above escalation threshold: " + match.getMatchSummary(),
                basicDecision.getConfidenceScore(),
                match
            );
        }
        
        // Auto-escalate watchlist matches above certain threshold  
        if (match.isSanctioned() && basicDecision.getConfidenceScore() >= config.getSanctionsEscalationThreshold()) {
            return ScreeningDecision.confirmed(
                "Watchlist match above escalation threshold: " + match.getMatchSummary(),
                basicDecision.getConfidenceScore(),
                match
            );
        }
        
        return basicDecision;
    }
}