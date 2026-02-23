/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.fraudwatchlist.infrastructure.http;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.fraudwatchlist.domain.constants.DomainConstants;
import verigate.adapter.fraudwatchlist.infrastructure.http.dto.FraudCheckRequestDto;
import verigate.adapter.fraudwatchlist.infrastructure.http.dto.FraudCheckResponseDto;

/**
 * API adapter for calling the Fraud Watchlist screening endpoints.
 */
public class FraudWatchlistApiAdapter {

  private static final Logger logger = LoggerFactory.getLogger(FraudWatchlistApiAdapter.class);

  private final FraudWatchlistHttpAdapter httpAdapter;

  public FraudWatchlistApiAdapter(FraudWatchlistHttpAdapter httpAdapter) {
    this.httpAdapter = httpAdapter;
  }

  /**
   * Performs a fraud watchlist check for the supplied party details.
   */
  public FraudCheckResponseDto checkFraudWatchlist(FraudCheckRequestDto request)
      throws TransientException, PermanentException {
    validateRequest(request);

    logger.debug(
        "Requesting fraud watchlist check for ID: {}",
        maskIdNumber(request.idNumber()));

    try {
      FraudCheckResponseDto response =
          httpAdapter.post(
              DomainConstants.ENDPOINT_FRAUD_CHECK,
              request,
              FraudCheckResponseDto.class);

      logger.debug(
          "Successfully completed fraud watchlist check for ID: {}",
          maskIdNumber(request.idNumber()));
      return response;
    } catch (TransientException | PermanentException e) {
      logger.error(
          "Failed to perform fraud watchlist check for ID {}: {}",
          maskIdNumber(request.idNumber()),
          e.getMessage());
      throw e;
    }
  }

  private void validateRequest(FraudCheckRequestDto request) {
    if (request == null) {
      throw new IllegalArgumentException("Fraud check request cannot be null");
    }
    if (request.idNumber() == null || request.idNumber().trim().isEmpty()) {
      throw new IllegalArgumentException("ID number cannot be null or empty");
    }
  }

  /**
   * Masks the ID number for safe logging.
   */
  private String maskIdNumber(String idNumber) {
    if (idNumber == null || idNumber.trim().isEmpty()) {
      return "***";
    }
    String trimmed = idNumber.trim();
    if (trimmed.length() < 6) {
      return "***";
    }
    return trimmed.substring(0, 3) + "***" + trimmed.substring(trimmed.length() - 2);
  }
}
