/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.partner.application.handlers;

import verigate.partner.domain.commands.CreatePartnerCommand;
import verigate.partner.domain.factories.CreatePartnerFactory;
import verigate.partner.domain.handlers.CreatePartnerCommandHandler;
import verigate.partner.domain.models.PartnerAggregateRoot;
import verigate.partner.domain.repositories.PartnerRepository;
import verigate.partner.domain.services.PartnerService;

/**
 * Default implementation of the CreatePartnerCommandHandler interface.
 * Orchestrates partner creation by delegating to domain services and factories.
 */
public class DefaultCreatePartnerCommandHandler implements CreatePartnerCommandHandler {

    private final PartnerService partnerService;
    private final PartnerRepository partnerRepository;
    private final CreatePartnerFactory partnerFactory;

    public DefaultCreatePartnerCommandHandler(PartnerService partnerService, 
                                            PartnerRepository partnerRepository, 
                                            CreatePartnerFactory partnerFactory) {
        this.partnerService = partnerService;
        this.partnerRepository = partnerRepository;
        this.partnerFactory = partnerFactory;
    }

    @Override
    public PartnerAggregateRoot handle(CreatePartnerCommand command) {
        // Create the partner using the factory
        PartnerAggregateRoot partner = partnerFactory.create(command);

        // Persist the partner
        return partnerRepository.addOrUpdate(partner);
    }
}