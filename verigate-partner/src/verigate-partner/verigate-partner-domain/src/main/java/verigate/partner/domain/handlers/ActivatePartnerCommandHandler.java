/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package verigate.partner.domain.handlers;

import domain.commands.CommandHandler;
import verigate.partner.domain.commands.ActivatePartnerCommand;
import verigate.partner.domain.models.PartnerAggregateRoot;

public interface ActivatePartnerCommandHandler
        extends CommandHandler<ActivatePartnerCommand, PartnerAggregateRoot> {
}
