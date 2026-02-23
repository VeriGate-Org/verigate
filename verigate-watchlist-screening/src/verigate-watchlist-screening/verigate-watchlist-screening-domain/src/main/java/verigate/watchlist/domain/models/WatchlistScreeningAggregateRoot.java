/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.models;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate root representing a watchlist screening.
 * Handles the evaluation of watchlist screening results from multiple providers
 * against partner-defined thresholds and match criteria.
 */
public class WatchlistScreeningAggregateRoot {
    
    private final UUID screeningId;
    private final String partnerId;
    private final ScreeningRequest request;
    private final List<ProviderResult> providerResults;
    private final Instant createdAt;
    private final Instant updatedAt;
    private ScreeningStatus status;
    private ScreeningDecision decision;
    
    public WatchlistScreeningAggregateRoot(UUID screeningId, String partnerId, ScreeningRequest request) {
        this.screeningId = screeningId;
        this.partnerId = partnerId;
        this.request = request;
        this.providerResults = new ArrayList<>();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.status = ScreeningStatus.PENDING;
    }
    
    /**
     * Adds a raw result from a screening provider.
     */
    public void addProviderResult(ProviderResult result) {
        this.providerResults.add(result);
        updateTimestamp();
        
        // Handle failures immediately
        if (!result.isSuccessful()) {
            this.status = ScreeningStatus.FAILED;
            this.decision = ScreeningDecision.systemOutage(result.getErrorMessage());
            return;
        }
        
        // Automatically evaluate if we have sufficient results
        if (canMakeDecision()) {
            evaluateResults();
        }
    }
    
    /**
     * Evaluates all provider results against thresholds to make a screening decision.
     */
    public void evaluateResults() {
        if (providerResults.isEmpty()) {
            this.decision = ScreeningDecision.systemOutage("No provider results available");
            this.status = ScreeningStatus.FAILED;
            return;
        }
        
        // Find the highest risk result across all providers
        ProviderResult highestRiskResult = findHighestRiskResult();
        
        // Apply decision matrix based on highest score
        this.decision = applyDecisionMatrix(highestRiskResult);
        this.status = ScreeningStatus.COMPLETED;
        updateTimestamp();
    }
    
    /**
     * Determines if we have sufficient results to make a screening decision.
     */
    private boolean canMakeDecision() {
        // For now, we make decisions as soon as we get any result
        // This can be enhanced to wait for multiple providers
        return !providerResults.isEmpty();
    }
    
    /**
     * Finds the provider result with the highest risk/confidence score.
     */
    private ProviderResult findHighestRiskResult() {
        return providerResults.stream()
            .max((a, b) -> Double.compare(a.getHighestScore(), b.getHighestScore()))
            .orElse(null);
    }
    
    /**
     * Applies the decision matrix based on match scores and provider confidence.
     */
    private ScreeningDecision applyDecisionMatrix(ProviderResult result) {
        double score = result.getHighestScore();
        String provider = result.getProvider();
        
        // High confidence match (≥90%) - Confirmed risk
        if (score >= 0.90) {
            return ScreeningDecision.confirmed(
                String.format("High confidence match found via %s (Score: %.2f)", provider, score),
                score,
                result.getHighestMatch()
            );
        }
        
        // Medium confidence match (70-89%) - Requires review
        if (score >= 0.70) {
            return ScreeningDecision.reviewRequired(
                String.format("Medium confidence match via %s requires review (Score: %.2f)", provider, score),
                score,
                result.getHighestMatch()
            );
        }
        
        // Low confidence or no match (<70%) - Clean result
        return ScreeningDecision.cleared("No significant watchlist matches found");
    }
    
    private void updateTimestamp() {
        // Note: In a real implementation, this would be handled by the infrastructure layer
        // For now, we'll keep it simple
    }
    
    // Getters
    public UUID getScreeningId() { return screeningId; }
    public String getPartnerId() { return partnerId; }
    public ScreeningRequest getRequest() { return request; }
    public List<ProviderResult> getProviderResults() { return new ArrayList<>(providerResults); }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public ScreeningStatus getStatus() { return status; }
    public ScreeningDecision getDecision() { return decision; }
    
    /**
     * Checks if this screening has been completed.
     */
    public boolean isCompleted() {
        return status == ScreeningStatus.COMPLETED;
    }
    
    /**
     * Checks if this screening failed due to system issues.
     */
    public boolean isFailed() {
        return status == ScreeningStatus.FAILED;
    }
}
