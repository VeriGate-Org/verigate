/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.negativenews.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO for Negative News search API response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NegativeNewsSearchResponseDto(
    @JsonProperty("outcome") String outcome,
    @JsonProperty("articles") List<NegativeNewsArticleDto> articles,
    @JsonProperty("total_articles_found") int totalArticlesFound,
    @JsonProperty("adverse_articles_count") int adverseArticlesCount,
    @JsonProperty("highest_severity_score") double highestSeverityScore,
    @JsonProperty("screening_summary") String screeningSummary
) {

  /**
   * DTO for an individual news article in the response.
   */
  @JsonIgnoreProperties(ignoreUnknown = true)
  public record NegativeNewsArticleDto(
      @JsonProperty("title") String title,
      @JsonProperty("source") String source,
      @JsonProperty("published_date") String publishedDate,
      @JsonProperty("url") String url,
      @JsonProperty("snippet") String snippet,
      @JsonProperty("sentiment") String sentiment,
      @JsonProperty("relevance_score") double relevanceScore
  ) {
  }
}
