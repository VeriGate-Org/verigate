/*
 * Arthmatic + Karisani(c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.cg.domain.handlers;

import domain.commands.CommandHandler;
import java.util.Map;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Represents a handler for the {@link VerifyPartyCommand} command.
 */
public interface VerifyPartyCommandHandler
    extends CommandHandler<VerifyPartyCommand, Map<String, String>> {}
