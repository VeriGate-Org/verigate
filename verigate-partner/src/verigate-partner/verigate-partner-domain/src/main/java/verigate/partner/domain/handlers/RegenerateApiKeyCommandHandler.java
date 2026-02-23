/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package verigate.partner.domain.handlers;

import domain.commands.CommandHandler;
import verigate.partner.domain.commands.RegenerateApiKeyCommand;
import verigate.partner.domain.models.ApiConfiguration;

public interface RegenerateApiKeyCommandHandler
        extends CommandHandler<RegenerateApiKeyCommand, ApiConfiguration> {
}
