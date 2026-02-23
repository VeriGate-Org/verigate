/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package verigate.partner.domain.services;

import verigate.partner.domain.models.PartnerConfiguration;

/**
 * Central configuration resolution service for partner settings.
 * Replaces the local PartnerConfigurationService in watchlist-screening.
 */
public interface PartnerConfigurationService {

    /**
     * Gets the full resolved configuration for a partner.
     * Returns default config if no partner-specific config exists.
     */
    PartnerConfiguration getConfiguration(String partnerId);

    /**
     * Checks if a partner has custom configuration.
     */
    boolean hasCustomConfiguration(String partnerId);

    /**
     * Gets the default configuration used as fallback.
     */
    PartnerConfiguration getDefaultConfiguration();

    /**
     * Updates the configuration for a partner.
     */
    void updateConfiguration(PartnerConfiguration configuration);
}
