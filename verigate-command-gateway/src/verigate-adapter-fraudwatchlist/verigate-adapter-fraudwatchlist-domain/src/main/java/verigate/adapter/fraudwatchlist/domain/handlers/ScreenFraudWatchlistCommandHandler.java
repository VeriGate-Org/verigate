/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.fraudwatchlist.domain.handlers;

import domain.commands.CommandHandler;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.models.VerificationResult;

/**
 * Command handler interface for processing fraud watchlist screening commands.
 */
public interface ScreenFraudWatchlistCommandHandler
    extends CommandHandler<VerifyPartyCommand, Map<String, String>> {

  /**
   * Handles the command asynchronously for non-blocking screening.
   *
   * @param command the verification command containing party details to screen
   * @return CompletableFuture containing the verification result
   */
  CompletableFuture<VerificationResult> handleAsync(VerifyPartyCommand command);
}
