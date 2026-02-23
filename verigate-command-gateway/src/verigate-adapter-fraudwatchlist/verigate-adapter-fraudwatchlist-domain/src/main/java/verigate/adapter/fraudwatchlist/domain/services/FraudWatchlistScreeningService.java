/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.fraudwatchlist.domain.services;

import verigate.adapter.fraudwatchlist.domain.models.FraudCheckRequest;
import verigate.adapter.fraudwatchlist.domain.models.FraudCheckResponse;

/**
 * Service interface for fraud watchlist screening operations.
 * Provides methods to check parties against fraud watchlists.
 */
public interface FraudWatchlistScreeningService {

  /**
   * Checks a party against fraud watchlists and returns the screening result.
   *
   * @param request the fraud check request containing party details to screen
   * @return the fraud check response with screening results
   * @throws TransientException if a temporary error occurs that may be retried
   * @throws PermanentException if a permanent error occurs that should not be retried
   */
  FraudCheckResponse checkFraudWatchlist(FraudCheckRequest request);
}
