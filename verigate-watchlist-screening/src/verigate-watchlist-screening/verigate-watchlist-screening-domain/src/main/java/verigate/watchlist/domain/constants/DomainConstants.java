/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.constants;

/**
 * Domain constants for watchlist screening service.
 */
public class DomainConstants {
    
    // Default threshold values
    public static final double DEFAULT_AUTO_REJECT_THRESHOLD = 0.90;
    public static final double DEFAULT_MANUAL_REVIEW_THRESHOLD = 0.70;
    public static final double DEFAULT_MINIMUM_THRESHOLD = 0.50;
    public static final double DEFAULT_PEP_ESCALATION_THRESHOLD = 0.85;
    public static final double DEFAULT_SANCTIONS_ESCALATION_THRESHOLD = 0.80;
    
    // Event types
    public static final String SCREENING_CLEARED_EVENT = "WatchlistScreeningCleared";
    public static final String MATCH_CONFIRMED_EVENT = "WatchlistMatchConfirmed";
    public static final String REVIEW_REQUIRED_EVENT = "WatchlistMatchReviewRequired";
    public static final String SOFT_FAILED_EVENT = "WatchlistScreeningSoftFailed";
    public static final String HARD_FAILED_EVENT = "WatchlistScreeningHardFailed";
    public static final String RAW_RESULT_RECEIVED_EVENT = "WatchlistScreeningRawResultReceived";
    
    // Provider names
    public static final String OPENSANCTIONS_PROVIDER = "OpenSanctions";
    public static final String WORLDCHECK_PROVIDER = "WorldCheck";
    public static final String LSEG_PROVIDER = "LSEG";
    
    // Common watchlist names
    public static final String UN_WATCHLIST = "UN";
    public static final String OFAC_WATCHLIST = "OFAC";
    public static final String EU_WATCHLIST = "EU";
    public static final String INTERPOL_WATCHLIST = "Interpol";
    public static final String UK_WATCHLIST = "UK";
    public static final String AUSTRAC_WATCHLIST = "AUSTRAC";
    
    // Entity categories
    public static final String PEP_CATEGORY = "PEP";
    public static final String SANCTIONS_CATEGORY = "Sanctions";
    public static final String EMBARGO_CATEGORY = "Embargo";
    public static final String CRIMINAL_CATEGORY = "Criminal";
    
    private DomainConstants() {
        // Utility class
    }
}