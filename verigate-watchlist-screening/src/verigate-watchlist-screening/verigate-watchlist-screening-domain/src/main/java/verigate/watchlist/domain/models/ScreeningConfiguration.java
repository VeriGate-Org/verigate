/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.models;

import java.util.Set;

/**
 * Configuration for sanctions screening rules specific to each partner.
 */
public class ScreeningConfiguration {
    
    private final String partnerId;
    private final double autoRejectThreshold;        // e.g., 0.90 - Auto-reject above this
    private final double manualReviewThreshold;     // e.g., 0.70 - Manual review between this and auto-reject
    private final double minimumThreshold;          // e.g., 0.50 - Ignore matches below this
    private final double pepEscalationThreshold;    // e.g., 0.85 - PEPs escalated above this
    private final double sanctionsEscalationThreshold; // e.g., 0.80 - Sanctions escalated above this
    private final boolean autoApproveEnabled;       // Auto-approve low-confidence matches
    private final Set<String> enabledProviders;     // Which providers to use
    private final Set<String> watchlistsToScreen;   // UN, OFAC, EU, etc.
    
    public ScreeningConfiguration(String partnerId, double autoRejectThreshold, 
                                double manualReviewThreshold, double minimumThreshold,
                                double pepEscalationThreshold, double sanctionsEscalationThreshold,
                                boolean autoApproveEnabled, Set<String> enabledProviders, 
                                Set<String> watchlistsToScreen) {
        this.partnerId = partnerId;
        this.autoRejectThreshold = autoRejectThreshold;
        this.manualReviewThreshold = manualReviewThreshold;
        this.minimumThreshold = minimumThreshold;
        this.pepEscalationThreshold = pepEscalationThreshold;
        this.sanctionsEscalationThreshold = sanctionsEscalationThreshold;
        this.autoApproveEnabled = autoApproveEnabled;
        this.enabledProviders = enabledProviders;
        this.watchlistsToScreen = watchlistsToScreen;
    }
    
    /**
     * Creates default configuration suitable for most compliance scenarios.
     */
    public static ScreeningConfiguration defaultConfiguration(String partnerId) {
        return new ScreeningConfiguration(
            partnerId,
            0.90,  // Auto-reject at 90%+
            0.70,  // Manual review at 70-89%
            0.50,  // Minimum threshold 50%
            0.85,  // PEP escalation at 85%
            0.80,  // Sanctions escalation at 80%
            true,  // Auto-approve low matches
            Set.of("OpenSanctions", "WorldCheck"), // Default providers
            Set.of("UN", "OFAC", "EU", "Interpol")  // Default watchlists
        );
    }
    
    /**
     * Creates strict configuration for high-risk scenarios.
     */
    public static ScreeningConfiguration strictConfiguration(String partnerId) {
        return new ScreeningConfiguration(
            partnerId,
            0.80,  // Lower auto-reject threshold
            0.60,  // Lower manual review threshold
            0.40,  // Lower minimum threshold
            0.75,  // Lower PEP escalation
            0.70,  // Lower sanctions escalation
            false, // Never auto-approve
            Set.of("OpenSanctions", "WorldCheck"),
            Set.of("UN", "OFAC", "EU", "Interpol", "UK", "AUSTRAC")
        );
    }
    
    // Getters
    public String getPartnerId() { return partnerId; }
    public double getAutoRejectThreshold() { return autoRejectThreshold; }
    public double getManualReviewThreshold() { return manualReviewThreshold; }
    public double getMinimumThreshold() { return minimumThreshold; }
    public double getPepEscalationThreshold() { return pepEscalationThreshold; }
    public double getSanctionsEscalationThreshold() { return sanctionsEscalationThreshold; }
    public boolean isAutoApproveEnabled() { return autoApproveEnabled; }
    public Set<String> getEnabledProviders() { return enabledProviders; }
    public Set<String> getWatchlistsToScreen() { return watchlistsToScreen; }
}