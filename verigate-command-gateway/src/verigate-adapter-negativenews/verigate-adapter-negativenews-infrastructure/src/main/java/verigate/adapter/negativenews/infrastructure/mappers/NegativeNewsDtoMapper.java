/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.negativenews.infrastructure.mappers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.negativenews.domain.constants.DomainConstants;
import verigate.adapter.negativenews.domain.models.ArticleSentiment;
import verigate.adapter.negativenews.domain.models.NegativeNewsScreeningRequest;
import verigate.adapter.negativenews.domain.models.NegativeNewsScreeningResponse;
import verigate.adapter.negativenews.domain.models.NewsArticle;
import verigate.adapter.negativenews.domain.models.ScreeningOutcome;
import verigate.adapter.negativenews.infrastructure.http.dto.NegativeNewsSearchRequestDto;
import verigate.adapter.negativenews.infrastructure.http.dto.NegativeNewsSearchResponseDto;
import verigate.adapter.negativenews.infrastructure.http.dto.NegativeNewsSearchResponseDto.NegativeNewsArticleDto;

/**
 * Mapper for converting Negative News DTOs to domain models and vice versa.
 */
public class NegativeNewsDtoMapper {

  private static final Logger logger = LoggerFactory.getLogger(NegativeNewsDtoMapper.class);

  // Common date formats used by Negative News APIs
  private static final DateTimeFormatter[] DATE_FORMATTERS = {
      DateTimeFormatter.ofPattern("yyyy-MM-dd"),
      DateTimeFormatter.ofPattern("dd-MM-yyyy"),
      DateTimeFormatter.ofPattern("dd/MM/yyyy"),
      DateTimeFormatter.ofPattern("yyyy/MM/dd"),
      DateTimeFormatter.ISO_LOCAL_DATE
  };

  /**
   * Maps a domain screening request to an API request DTO.
   *
   * @param request the domain screening request
   * @return the API request DTO
   */
  public NegativeNewsSearchRequestDto mapToRequestDto(NegativeNewsScreeningRequest request) {
    if (request == null) {
      return null;
    }

    return new NegativeNewsSearchRequestDto(
        request.subjectName(),
        request.idNumber(),
        request.additionalKeywords(),
        request.dateRangeMonths(),
        DomainConstants.MAX_ARTICLES);
  }

  /**
   * Maps an API response DTO to a domain screening response.
   *
   * @param responseDto the API response DTO
   * @return the domain screening response
   */
  public NegativeNewsScreeningResponse mapToScreeningResponse(
      NegativeNewsSearchResponseDto responseDto) {
    if (responseDto == null) {
      return NegativeNewsScreeningResponse.error("No response received from screening API");
    }

    ScreeningOutcome outcome = ScreeningOutcome.fromDescription(responseDto.outcome());
    List<NewsArticle> articles = mapToNewsArticles(responseDto.articles());

    return new NegativeNewsScreeningResponse(
        outcome,
        articles,
        responseDto.totalArticlesFound(),
        responseDto.adverseArticlesCount(),
        responseDto.highestSeverityScore(),
        responseDto.screeningSummary());
  }

  /**
   * Maps a list of article DTOs to domain article models.
   */
  private List<NewsArticle> mapToNewsArticles(List<NegativeNewsArticleDto> articleDtos) {
    if (articleDtos == null) {
      return List.of();
    }

    return articleDtos.stream()
        .map(this::mapToNewsArticle)
        .collect(Collectors.toList());
  }

  /**
   * Maps an individual article DTO to a domain article model.
   */
  private NewsArticle mapToNewsArticle(NegativeNewsArticleDto dto) {
    if (dto == null) {
      return null;
    }

    return new NewsArticle(
        dto.title(),
        dto.source(),
        parseDate(dto.publishedDate()),
        dto.url(),
        dto.snippet(),
        ArticleSentiment.fromDescription(dto.sentiment()),
        dto.relevanceScore());
  }

  /**
   * Parses date string using multiple format attempts.
   */
  private LocalDate parseDate(String dateString) {
    if (dateString == null || dateString.trim().isEmpty()) {
      return null;
    }

    String trimmed = dateString.trim();

    for (DateTimeFormatter formatter : DATE_FORMATTERS) {
      try {
        return LocalDate.parse(trimmed, formatter);
      } catch (DateTimeParseException e) {
        // Try next formatter
      }
    }

    logger.warn("Failed to parse date: {}", dateString);
    return null;
  }
}
