/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.negativenews.infrastructure.http.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * DTO for NewsAPI search response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NegativeNewsSearchResponseDto(
    String status,
    int totalResults,
    List<NewsApiArticleDto> articles
) {

  /**
   * DTO for an individual news article from the NewsAPI response.
   */
  @JsonIgnoreProperties(ignoreUnknown = true)
  public record NewsApiArticleDto(
      NewsApiSourceDto source,
      String author,
      String title,
      String description,
      String url,
      String urlToImage,
      String publishedAt,
      String content
  ) {
  }

  /**
   * DTO for the source of a news article.
   */
  @JsonIgnoreProperties(ignoreUnknown = true)
  public record NewsApiSourceDto(
      String id,
      String name
  ) {
  }
}
