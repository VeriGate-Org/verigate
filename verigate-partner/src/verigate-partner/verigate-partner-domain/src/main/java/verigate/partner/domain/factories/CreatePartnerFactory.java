/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.partner.domain.factories;

import verigate.partner.domain.commands.CreatePartnerCommand;
import verigate.partner.domain.models.PartnerAggregateRoot;
import verigate.partner.domain.models.PartnerType;

import java.util.UUID;

/**
 * Factory interface for creating Partner aggregates.
 */
public interface CreatePartnerFactory {
    
    /**
     * Creates a partner aggregate from a command.
     * 
     * @param command the create partner command
     * @return the created partner aggregate
     */
    PartnerAggregateRoot create(CreatePartnerCommand command);
    
    /**
     * Creates a partner aggregate with specific parameters.
     * 
     * @param partnerId the unique identifier for the partner
     * @param partnerName the name of the partner
     * @param contactEmail the contact email for the partner
     * @param partnerType the type of partner
     * @return the created partner aggregate
     */
    PartnerAggregateRoot createPartner(UUID partnerId, String partnerName, String contactEmail, PartnerType partnerType);
}