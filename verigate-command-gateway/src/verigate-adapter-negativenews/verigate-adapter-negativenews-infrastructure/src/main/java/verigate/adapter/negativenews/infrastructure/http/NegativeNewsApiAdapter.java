/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.negativenews.infrastructure.http;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.negativenews.domain.constants.DomainConstants;
import verigate.adapter.negativenews.infrastructure.http.dto.NegativeNewsSearchRequestDto;
import verigate.adapter.negativenews.infrastructure.http.dto.NegativeNewsSearchResponseDto;

/**
 * API adapter for calling the Negative News screening endpoints.
 */
public class NegativeNewsApiAdapter {

  private static final Logger logger = LoggerFactory.getLogger(NegativeNewsApiAdapter.class);

  private final NegativeNewsHttpAdapter httpAdapter;

  public NegativeNewsApiAdapter(NegativeNewsHttpAdapter httpAdapter) {
    this.httpAdapter = httpAdapter;
  }

  /**
   * Searches for negative news articles for the supplied subject.
   *
   * @param requestDto the search request containing subject details and search parameters
   * @return the search response containing articles and analysis
   * @throws TransientException if a temporary error occurs
   * @throws PermanentException if a permanent error occurs
   */
  public NegativeNewsSearchResponseDto searchNegativeNews(NegativeNewsSearchRequestDto requestDto)
      throws TransientException, PermanentException {

    validateRequest(requestDto);

    logger.debug(
        "Requesting negative news screening for subject: [MASKED]");

    try {
      NegativeNewsSearchResponseDto response =
          httpAdapter.post(
              DomainConstants.ENDPOINT_SEARCH_NEWS,
              requestDto,
              NegativeNewsSearchResponseDto.class);

      logger.debug("Successfully retrieved negative news screening results");
      return response;
    } catch (TransientException | PermanentException e) {
      logger.error(
          "Failed to retrieve negative news screening results: {}",
          e.getMessage());
      throw e;
    }
  }

  private void validateRequest(NegativeNewsSearchRequestDto requestDto) {
    if (requestDto == null) {
      throw new IllegalArgumentException("Search request cannot be null");
    }

    if (requestDto.subjectName() == null || requestDto.subjectName().trim().isEmpty()) {
      throw new IllegalArgumentException("Subject name cannot be null or empty");
    }
  }
}
