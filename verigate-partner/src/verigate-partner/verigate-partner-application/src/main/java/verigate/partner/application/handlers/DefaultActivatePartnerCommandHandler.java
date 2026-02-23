/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.partner.application.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.partner.domain.commands.ActivatePartnerCommand;
import verigate.partner.domain.handlers.ActivatePartnerCommandHandler;
import verigate.partner.domain.models.PartnerAggregateRoot;
import verigate.partner.domain.repositories.PartnerRepository;

public class DefaultActivatePartnerCommandHandler implements ActivatePartnerCommandHandler {

    private static final Logger logger = LoggerFactory.getLogger(
        DefaultActivatePartnerCommandHandler.class);

    private final PartnerRepository partnerRepository;

    public DefaultActivatePartnerCommandHandler(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }

    @Override
    public PartnerAggregateRoot handle(ActivatePartnerCommand command) {
        logger.info("Activating partner: {}", command.getPartnerId());

        PartnerAggregateRoot partner = partnerRepository.get(command.getPartnerId())
            .orElseThrow(() -> new IllegalArgumentException(
                "Partner not found: " + command.getPartnerId()));

        partner.activate();
        PartnerAggregateRoot savedPartner = partnerRepository.addOrUpdate(partner);

        logger.info("Partner activated: {}", command.getPartnerId());
        return savedPartner;
    }
}
