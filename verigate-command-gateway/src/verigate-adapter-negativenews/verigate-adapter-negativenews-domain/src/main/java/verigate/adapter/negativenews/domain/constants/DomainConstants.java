/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.negativenews.domain.constants;

/**
 * Constants for the Negative News Screening adapter domain.
 */
public class DomainConstants {

  // Provider name
  public static final String NEGATIVE_NEWS_PROVIDER = "NEGATIVE_NEWS_PROVIDER";

  // API endpoints
  public static final String ENDPOINT_SEARCH_NEWS = "/search/news";

  // Default date range in months for screening lookback period
  public static final int DEFAULT_DATE_RANGE_MONTHS = 24;

  // Sentiment thresholds
  public static final double SENTIMENT_THRESHOLD_NEGATIVE = 0.7;
  public static final double SENTIMENT_THRESHOLD_HIGHLY_NEGATIVE = 0.9;

  // Relevance threshold
  public static final double RELEVANCE_THRESHOLD = 0.5;

  // Maximum articles to return
  public static final int MAX_ARTICLES = 100;

  // Default values
  public static final int DEFAULT_HTTP_TIMEOUT_SECONDS = 30;
  public static final int DEFAULT_RETRY_ATTEMPTS = 3;
  public static final int DEFAULT_RETRY_DELAY_MS = 1000;

  // Rate limiting defaults
  public static final int DEFAULT_RATE_LIMIT_RPS = 5;
  public static final int DEFAULT_RATE_LIMIT_BURST = 10;

  // Monitoring defaults
  public static final boolean DEFAULT_ENABLE_REQUEST_LOGGING = false;
  public static final boolean DEFAULT_ENABLE_RESPONSE_LOGGING = false;
  public static final String DEFAULT_LOG_LEVEL = "INFO";

  private DomainConstants() {
  }
}
