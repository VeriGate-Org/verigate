/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.partner.application.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.partner.domain.commands.UpdatePartnerConfigurationCommand;
import verigate.partner.domain.events.PartnerConfigurationUpdatedEvent;
import verigate.partner.domain.handlers.UpdatePartnerConfigurationCommandHandler;
import verigate.partner.domain.models.PartnerAggregateRoot;
import verigate.partner.domain.models.PartnerConfiguration;
import verigate.partner.domain.repositories.PartnerConfigurationRepository;
import verigate.partner.domain.repositories.PartnerRepository;

public class DefaultUpdatePartnerConfigurationCommandHandler
        implements UpdatePartnerConfigurationCommandHandler {

    private static final Logger logger = LoggerFactory.getLogger(
        DefaultUpdatePartnerConfigurationCommandHandler.class);

    private final PartnerRepository partnerRepository;
    private final PartnerConfigurationRepository configurationRepository;

    public DefaultUpdatePartnerConfigurationCommandHandler(
            PartnerRepository partnerRepository,
            PartnerConfigurationRepository configurationRepository) {
        this.partnerRepository = partnerRepository;
        this.configurationRepository = configurationRepository;
    }

    @Override
    public PartnerConfiguration handle(UpdatePartnerConfigurationCommand command) {
        logger.info("Updating configuration for partner: {}", command.getPartnerId());

        PartnerAggregateRoot partner = partnerRepository.get(command.getPartnerId())
            .orElseThrow(() -> new IllegalArgumentException(
                "Partner not found: " + command.getPartnerId()));

        PartnerConfiguration newConfig = new PartnerConfiguration(
            command.getPartnerId().toString(),
            command.getVerificationFlowConfiguration(),
            command.getApiConfiguration(),
            command.getBillingConfiguration()
        );

        partner.updateConfiguration(newConfig);
        partnerRepository.addOrUpdate(partner);
        PartnerConfiguration savedConfig = configurationRepository.save(newConfig);

        logger.info("Configuration updated for partner: {}", command.getPartnerId());
        return savedConfig;
    }
}
