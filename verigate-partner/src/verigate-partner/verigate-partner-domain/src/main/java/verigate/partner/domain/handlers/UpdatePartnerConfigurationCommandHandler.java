/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package verigate.partner.domain.handlers;

import domain.commands.CommandHandler;
import verigate.partner.domain.commands.UpdatePartnerConfigurationCommand;
import verigate.partner.domain.models.PartnerConfiguration;

public interface UpdatePartnerConfigurationCommandHandler
        extends CommandHandler<UpdatePartnerConfigurationCommand, PartnerConfiguration> {
}
