/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.negativenews.infrastructure.http.dto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * DTO for NewsAPI search request, built as query parameters.
 */
public record NegativeNewsSearchRequestDto(
    String query,
    String from,
    String to,
    String language,
    String sortBy,
    int pageSize
) {

  /**
   * Builds the query string for the NewsAPI request, including the API key.
   *
   * @param apiKey the NewsAPI API key
   * @return the URL-encoded query string
   */
  public String toQueryString(String apiKey) {
    return String.format(
        "q=%s&from=%s&to=%s&language=%s&sortBy=%s&pageSize=%d&apiKey=%s",
        URLEncoder.encode(query, StandardCharsets.UTF_8),
        from, to, language, sortBy, pageSize,
        URLEncoder.encode(apiKey, StandardCharsets.UTF_8));
  }
}
