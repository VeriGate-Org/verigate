/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.fraudwatchlist.infrastructure.services;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.fraudwatchlist.domain.models.FraudCheckRequest;
import verigate.adapter.fraudwatchlist.domain.models.FraudCheckResponse;
import verigate.adapter.fraudwatchlist.domain.services.FraudWatchlistScreeningService;
import verigate.adapter.fraudwatchlist.infrastructure.http.FraudWatchlistApiAdapter;
import verigate.adapter.fraudwatchlist.infrastructure.http.dto.FraudCheckRequestDto;
import verigate.adapter.fraudwatchlist.infrastructure.http.dto.FraudCheckResponseDto;
import verigate.adapter.fraudwatchlist.infrastructure.mappers.FraudWatchlistDtoMapper;

/**
 * Default implementation of the {@link FraudWatchlistScreeningService} using the infrastructure
 * HTTP adapter.
 */
public class DefaultFraudWatchlistScreeningService implements FraudWatchlistScreeningService {

  private static final Logger logger =
      LoggerFactory.getLogger(DefaultFraudWatchlistScreeningService.class);

  private final FraudWatchlistApiAdapter apiAdapter;
  private final FraudWatchlistDtoMapper dtoMapper;

  public DefaultFraudWatchlistScreeningService(
      FraudWatchlistApiAdapter apiAdapter, FraudWatchlistDtoMapper dtoMapper) {
    this.apiAdapter = apiAdapter;
    this.dtoMapper = dtoMapper;
  }

  @Override
  public FraudCheckResponse checkFraudWatchlist(FraudCheckRequest request) {
    logger.info(
        "Performing fraud watchlist check for ID: {}",
        maskIdNumber(request.idNumber()));

    try {
      FraudCheckRequestDto requestDto = dtoMapper.mapToRequestDto(request);

      FraudCheckResponseDto responseDto = apiAdapter.checkFraudWatchlist(requestDto);

      FraudCheckResponse response = dtoMapper.mapToFraudCheckResponse(responseDto);

      logger.info(
          "Fraud watchlist check completed for ID: {} - status: {}, risk score: {}",
          maskIdNumber(request.idNumber()),
          response.status(),
          response.overallRiskScore());

      return response;

    } catch (PermanentException e) {
      logger.error(
          "Permanent error during fraud watchlist check for ID {}: {}",
          maskIdNumber(request.idNumber()),
          e.getMessage());
      return FraudCheckResponse.error("Fraud watchlist check failed: " + e.getMessage());

    } catch (TransientException e) {
      logger.warn(
          "Transient error during fraud watchlist check for ID {}: {}",
          maskIdNumber(request.idNumber()),
          e.getMessage());
      throw e;

    } catch (Exception e) {
      logger.error(
          "Unexpected error during fraud watchlist check for ID {}: {}",
          maskIdNumber(request.idNumber()),
          e.getMessage(),
          e);
      return FraudCheckResponse.error("Unexpected error: " + e.getMessage());
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
