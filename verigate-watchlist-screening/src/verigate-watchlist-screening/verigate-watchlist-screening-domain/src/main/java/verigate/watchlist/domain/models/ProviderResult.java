/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.models;

import java.time.Instant;
import java.util.List;

/**
 * Raw result from a sanctions screening provider (e.g., OpenSanctions, WorldCheck).
 */
public class ProviderResult {
    
    private final String provider;
    private final List<MatchedEntity> matches;
    private final Instant receivedAt;
    private final ProviderStatus status;
    private final String errorMessage;
    
    public ProviderResult(String provider, List<MatchedEntity> matches, ProviderStatus status, String errorMessage) {
        this.provider = provider;
        this.matches = matches;
        this.receivedAt = Instant.now();
        this.status = status;
        this.errorMessage = errorMessage;
    }
    
    public static ProviderResult success(String provider, List<MatchedEntity> matches) {
        return new ProviderResult(provider, matches, ProviderStatus.SUCCESS, null);
    }
    
    public static ProviderResult failure(String provider, String errorMessage) {
        return new ProviderResult(provider, List.of(), ProviderStatus.FAILED, errorMessage);
    }
    
    public static ProviderResult timeout(String provider) {
        return new ProviderResult(provider, List.of(), ProviderStatus.TIMEOUT, "Provider response timeout");
    }
    
    /**
     * Gets the highest confidence score from all matches.
     */
    public double getHighestScore() {
        return matches.stream()
            .mapToDouble(MatchedEntity::getConfidenceScore)
            .max()
            .orElse(0.0);
    }
    
    /**
     * Gets the match with the highest confidence score.
     */
    public MatchedEntity getHighestMatch() {
        return matches.stream()
            .max((a, b) -> Double.compare(a.getConfidenceScore(), b.getConfidenceScore()))
            .orElse(null);
    }
    
    // Getters
    public String getProvider() { return provider; }
    public List<MatchedEntity> getMatches() { return matches; }
    public Instant getReceivedAt() { return receivedAt; }
    public ProviderStatus getStatus() { return status; }
    public String getErrorMessage() { return errorMessage; }
    
    public boolean isSuccessful() {
        return status == ProviderStatus.SUCCESS;
    }
    
    public enum ProviderStatus {
        SUCCESS,
        FAILED,
        TIMEOUT
    }
}