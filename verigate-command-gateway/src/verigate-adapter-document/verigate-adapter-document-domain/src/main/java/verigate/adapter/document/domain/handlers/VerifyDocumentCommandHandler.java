/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.document.domain.handlers;

import domain.commands.CommandHandler;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationResult;

/**
 * Command handler interface for processing document verification commands.
 */
public interface VerifyDocumentCommandHandler
    extends CommandHandler<VerifyPartyCommand, Map<String, String>> {

  /**
   * Handles the command asynchronously for non-blocking verification.
   *
   * @param command the verification command containing document details to verify
   * @return CompletableFuture containing the verification result
   */
  CompletableFuture<VerificationResult> handleAsync(VerifyPartyCommand command);
}
