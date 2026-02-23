/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.models;

/**
 * Final decision from watchlist screening evaluation.
 */
public class ScreeningDecision {
    
    private final DecisionType type;
    private final String reason;
    private final Double confidenceScore;
    private final MatchedEntity highestMatch;
    
    private ScreeningDecision(DecisionType type, String reason, Double confidenceScore, MatchedEntity highestMatch) {
        this.type = type;
        this.reason = reason;
        this.confidenceScore = confidenceScore;
        this.highestMatch = highestMatch;
    }
    
    public static ScreeningDecision cleared(String reason) {
        return new ScreeningDecision(DecisionType.CLEARED, reason, null, null);
    }
    
    public static ScreeningDecision confirmed(String reason, double score, MatchedEntity match) {
        return new ScreeningDecision(DecisionType.MATCH_CONFIRMED, reason, score, match);
    }
    
    public static ScreeningDecision reviewRequired(String reason, double score, MatchedEntity match) {
        return new ScreeningDecision(DecisionType.REVIEW_REQUIRED, reason, score, match);
    }
    
    public static ScreeningDecision softFailed(String reason) {
        return new ScreeningDecision(DecisionType.SOFT_FAILED, reason, null, null);
    }
    
    public static ScreeningDecision systemOutage(String reason) {
        return new ScreeningDecision(DecisionType.SYSTEM_OUTAGE, reason, null, null);
    }
    
    // Getters
    public DecisionType getType() { return type; }
    public String getReason() { return reason; }
    public Double getConfidenceScore() { return confidenceScore; }
    public MatchedEntity getHighestMatch() { return highestMatch; }
    
    public enum DecisionType {
        CLEARED,           // No watchlist match - proceed
        MATCH_CONFIRMED,   // High-confidence match - auto-reject
        REVIEW_REQUIRED,   // Medium-confidence match - manual review needed
        SOFT_FAILED,       // Provider returned ambiguous result
        SYSTEM_OUTAGE      // Technical failure
    }
}