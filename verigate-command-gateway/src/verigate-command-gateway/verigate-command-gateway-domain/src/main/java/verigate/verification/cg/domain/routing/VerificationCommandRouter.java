/*
 * Arthmatic + Karisani(c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 *
 */

package verigate.verification.cg.domain.routing;

import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.common.CommandInvoker;

/**
 * An interface for routing {@link VerifyPartyCommand} commands to the appropriate provider adapter.
 * Commands are routed based on the specified provider and command type.
 */
public interface VerificationCommandRouter extends CommandInvoker<VerifyPartyCommand> {}
