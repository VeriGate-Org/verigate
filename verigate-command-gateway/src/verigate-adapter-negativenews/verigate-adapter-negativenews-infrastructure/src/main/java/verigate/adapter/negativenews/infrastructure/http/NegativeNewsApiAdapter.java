/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.negativenews.infrastructure.http;

import crosscutting.environment.Environment;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.negativenews.domain.constants.DomainConstants;
import verigate.adapter.negativenews.infrastructure.constants.EnvironmentConstants;
import verigate.adapter.negativenews.infrastructure.http.dto.NegativeNewsSearchRequestDto;
import verigate.adapter.negativenews.infrastructure.http.dto.NegativeNewsSearchResponseDto;

/**
 * API adapter for calling the NewsAPI screening endpoints.
 */
public class NegativeNewsApiAdapter {

  private static final Logger logger = LoggerFactory.getLogger(NegativeNewsApiAdapter.class);

  private final NegativeNewsHttpAdapter httpAdapter;
  private final Environment environment;

  public NegativeNewsApiAdapter(NegativeNewsHttpAdapter httpAdapter, Environment environment) {
    this.httpAdapter = httpAdapter;
    this.environment = environment;
  }

  /**
   * Searches for negative news articles for the supplied subject.
   *
   * @param requestDto the search request containing subject details and search parameters
   * @return the search response containing articles
   * @throws TransientException if a temporary error occurs
   * @throws PermanentException if a permanent error occurs
   */
  public NegativeNewsSearchResponseDto searchNegativeNews(NegativeNewsSearchRequestDto requestDto)
      throws TransientException, PermanentException {

    validateRequest(requestDto);

    logger.debug("Requesting negative news screening for subject: [MASKED]");

    try {
      String apiUrl = getRequiredEnvValue(
          EnvironmentConstants.NEGATIVENEWS_API_URL, "NEGATIVENEWS_API_URL");
      String apiKey = getRequiredEnvValue(
          EnvironmentConstants.NEGATIVENEWS_API_KEY, "NEGATIVENEWS_API_KEY");

      String fullUrl = apiUrl + DomainConstants.ENDPOINT_SEARCH_NEWS
          + "?" + requestDto.toQueryString(apiKey);

      NegativeNewsSearchResponseDto response =
          httpAdapter.get(fullUrl, NegativeNewsSearchResponseDto.class);

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

    if (requestDto.query() == null || requestDto.query().trim().isEmpty()) {
      throw new IllegalArgumentException("Subject name cannot be null or empty");
    }
  }

  private String getRequiredEnvValue(String envKey, String name) {
    String value = environment.get(envKey);
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalStateException(name + " environment variable is required");
    }
    return value.trim();
  }
}
