/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.partner.application.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.partner.domain.commands.SuspendPartnerCommand;
import verigate.partner.domain.handlers.SuspendPartnerCommandHandler;
import verigate.partner.domain.models.PartnerAggregateRoot;
import verigate.partner.domain.repositories.PartnerRepository;

public class DefaultSuspendPartnerCommandHandler implements SuspendPartnerCommandHandler {

    private static final Logger logger = LoggerFactory.getLogger(
        DefaultSuspendPartnerCommandHandler.class);

    private final PartnerRepository partnerRepository;

    public DefaultSuspendPartnerCommandHandler(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }

    @Override
    public PartnerAggregateRoot handle(SuspendPartnerCommand command) {
        logger.info("Suspending partner: {} reason: {}", command.getPartnerId(), command.getReason());

        PartnerAggregateRoot partner = partnerRepository.get(command.getPartnerId())
            .orElseThrow(() -> new IllegalArgumentException(
                "Partner not found: " + command.getPartnerId()));

        partner.suspend();
        PartnerAggregateRoot savedPartner = partnerRepository.addOrUpdate(partner);

        logger.info("Partner suspended: {}", command.getPartnerId());
        return savedPartner;
    }
}
