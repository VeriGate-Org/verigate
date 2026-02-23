/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.partner.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.partner.domain.models.PartnerConfiguration;
import verigate.partner.domain.repositories.PartnerConfigurationRepository;
import verigate.partner.domain.services.PartnerConfigurationService;

/**
 * Default implementation of PartnerConfigurationService.
 * Resolves partner configuration with fallback to defaults.
 */
public class DefaultPartnerConfigurationService implements PartnerConfigurationService {

    private static final Logger logger = LoggerFactory.getLogger(
        DefaultPartnerConfigurationService.class);

    private final PartnerConfigurationRepository configurationRepository;

    public DefaultPartnerConfigurationService(
            PartnerConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    @Override
    public PartnerConfiguration getConfiguration(String partnerId) {
        return configurationRepository.findByPartnerId(partnerId)
            .orElseGet(() -> {
                logger.debug("No custom config for partner {}, using defaults", partnerId);
                return PartnerConfiguration.withDefaults(partnerId);
            });
    }

    @Override
    public boolean hasCustomConfiguration(String partnerId) {
        return configurationRepository.existsByPartnerId(partnerId);
    }

    @Override
    public PartnerConfiguration getDefaultConfiguration() {
        return PartnerConfiguration.withDefaults("default");
    }

    @Override
    public void updateConfiguration(PartnerConfiguration configuration) {
        logger.info("Saving configuration for partner: {}", configuration.getPartnerId());
        configurationRepository.save(configuration);
    }
}
