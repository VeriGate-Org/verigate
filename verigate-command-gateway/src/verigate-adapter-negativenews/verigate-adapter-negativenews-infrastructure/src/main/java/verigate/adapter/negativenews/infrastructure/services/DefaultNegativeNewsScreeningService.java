/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.negativenews.infrastructure.services;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.negativenews.domain.models.NegativeNewsScreeningRequest;
import verigate.adapter.negativenews.domain.models.NegativeNewsScreeningResponse;
import verigate.adapter.negativenews.domain.services.NegativeNewsScreeningService;
import verigate.adapter.negativenews.infrastructure.http.NegativeNewsApiAdapter;
import verigate.adapter.negativenews.infrastructure.http.dto.NegativeNewsSearchRequestDto;
import verigate.adapter.negativenews.infrastructure.http.dto.NegativeNewsSearchResponseDto;
import verigate.adapter.negativenews.infrastructure.mappers.NegativeNewsDtoMapper;

/**
 * Default implementation of the {@link NegativeNewsScreeningService} using the infrastructure
 * HTTP adapter.
 */
public class DefaultNegativeNewsScreeningService implements NegativeNewsScreeningService {

  private static final Logger logger =
      LoggerFactory.getLogger(DefaultNegativeNewsScreeningService.class);

  private final NegativeNewsApiAdapter apiAdapter;
  private final NegativeNewsDtoMapper dtoMapper;

  public DefaultNegativeNewsScreeningService(
      NegativeNewsApiAdapter apiAdapter, NegativeNewsDtoMapper dtoMapper) {
    this.apiAdapter = apiAdapter;
    this.dtoMapper = dtoMapper;
  }

  @Override
  public NegativeNewsScreeningResponse screenForNegativeNews(
      NegativeNewsScreeningRequest request) {

    logger.info(
        "Performing negative news screening for subject: [MASKED], "
            + "date range: {} months",
        request.dateRangeMonths());

    try {
      NegativeNewsSearchRequestDto requestDto = dtoMapper.mapToRequestDto(request);

      NegativeNewsSearchResponseDto responseDto = apiAdapter.searchNegativeNews(requestDto);

      NegativeNewsScreeningResponse screeningResponse =
          dtoMapper.mapToScreeningResponse(responseDto);

      logger.info(
          "Negative news screening completed. Outcome: {}, Total articles: {}, "
              + "Adverse articles: {}",
          screeningResponse.outcome(),
          screeningResponse.totalArticlesFound(),
          screeningResponse.adverseArticlesCount());

      return screeningResponse;

    } catch (PermanentException e) {
      logger.error(
          "Permanent error during negative news screening: {}", e.getMessage());
      return NegativeNewsScreeningResponse.error(
          "Failed to perform screening: " + e.getMessage());

    } catch (TransientException e) {
      logger.warn(
          "Transient error during negative news screening: {}", e.getMessage());
      throw e;

    } catch (Exception e) {
      logger.error(
          "Unexpected error during negative news screening: {}",
          e.getMessage(),
          e);
      return NegativeNewsScreeningResponse.error("Unexpected error: " + e.getMessage());
    }
  }
}
