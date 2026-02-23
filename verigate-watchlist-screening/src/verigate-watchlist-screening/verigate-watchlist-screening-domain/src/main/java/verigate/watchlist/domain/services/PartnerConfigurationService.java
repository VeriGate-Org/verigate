/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.services;

import verigate.watchlist.domain.models.ScreeningConfiguration;

/**
 * Service for retrieving partner-specific screening configurations.
 */
public interface PartnerConfigurationService {
    
    /**
     * Gets the screening configuration for a specific partner.
     */
    ScreeningConfiguration getConfiguration(String partnerId);
    
    /**
     * Checks if a partner has custom screening configuration.
     */
    boolean hasCustomConfiguration(String partnerId);
    
    /**
     * Gets the default configuration used when no partner-specific config exists.
     */
    ScreeningConfiguration getDefaultConfiguration();
}