/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.models;

import java.util.List;
import java.util.Map;

/**
 * Represents a matched entity from sanctions screening.
 * Standardized across all providers (OpenSanctions, WorldCheck, etc.).
 */
public class MatchedEntity {
    
    private final String entityId;
    private final String name;
    private final double confidenceScore;
    private final List<String> categories; // PEP, Sanctions, etc.
    private final List<String> datasets;   // Source watchlists
    private final String description;
    private final Map<String, String> additionalProperties;
    
    public MatchedEntity(String entityId, String name, double confidenceScore, 
                        List<String> categories, List<String> datasets, 
                        String description, Map<String, String> additionalProperties) {
        this.entityId = entityId;
        this.name = name;
        this.confidenceScore = confidenceScore;
        this.categories = categories;
        this.datasets = datasets;
        this.description = description;
        this.additionalProperties = additionalProperties;
    }
    
    // Getters
    public String getEntityId() { return entityId; }
    public String getName() { return name; }
    public double getConfidenceScore() { return confidenceScore; }
    public List<String> getCategories() { return categories; }
    public List<String> getDatasets() { return datasets; }
    public String getDescription() { return description; }
    public Map<String, String> getAdditionalProperties() { return additionalProperties; }
    
    /**
     * Checks if this entity is a PEP (Politically Exposed Person).
     */
    public boolean isPEP() {
        return categories.stream().anyMatch(cat -> cat.toLowerCase().contains("pep"));
    }
    
    /**
     * Checks if this entity is on a sanctions list.
     */
    public boolean isSanctioned() {
        return categories.stream().anyMatch(cat -> 
            cat.toLowerCase().contains("sanction") || 
            cat.toLowerCase().contains("embargo"));
    }
    
    /**
     * Gets a summary description of the match.
     */
    public String getMatchSummary() {
        return String.format("%s (Score: %.1f%%, Categories: %s)", 
            name, confidenceScore * 100, String.join(",", categories));
    }
}