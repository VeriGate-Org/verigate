/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.partner.domain.handlers;

import verigate.partner.domain.commands.CreatePartnerCommand;
import verigate.partner.domain.models.PartnerAggregateRoot;
import domain.commands.CommandHandler;

/**
 * Command handler interface for handling partner creation commands.
 */
public interface CreatePartnerCommandHandler 
        extends CommandHandler<CreatePartnerCommand, PartnerAggregateRoot> {
}