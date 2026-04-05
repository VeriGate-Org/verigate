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
import verigate.adapter.negativenews.infrastructure.http.dto.NegativeNewsSearchResponseDto.NewsApiArticleDto;
import verigate.adapter.negativenews.infrastructure.services.AiSentimentClassifier;
import verigate.adapter.negativenews.infrastructure.services.AiSentimentClassifier.SentimentResult;

/**
 * Mapper for converting between NewsAPI DTOs and domain models. Uses AI-powered sentiment
 * classification with fallback to keyword-based classification on failure.
 */
public class NegativeNewsDtoMapper {

  private static final Logger logger = LoggerFactory.getLogger(NegativeNewsDtoMapper.class);

  private static final double HIGHLY_NEGATIVE_RELEVANCE = 0.95;
  private static final double NEGATIVE_RELEVANCE = 0.75;
  private static final double NEUTRAL_RELEVANCE = 0.3;

  private static final String STANDARD_ADVERSE_TERMS =
      "fraud OR scandal OR lawsuit OR corruption OR criminal OR sanctions";

  private final AiSentimentClassifier aiSentimentClassifier;

  private String currentSubjectName;

  // Date formatters for parsing NewsAPI dates and generating query dates
  private static final DateTimeFormatter[] DATE_FORMATTERS = {
      DateTimeFormatter.ofPattern("yyyy-MM-dd"),
      DateTimeFormatter.ofPattern("dd-MM-yyyy"),
      DateTimeFormatter.ofPattern("dd/MM/yyyy"),
      DateTimeFormatter.ofPattern("yyyy/MM/dd"),
      DateTimeFormatter.ISO_LOCAL_DATE,
      DateTimeFormatter.ISO_OFFSET_DATE_TIME,
      DateTimeFormatter.ISO_INSTANT
  };

  public NegativeNewsDtoMapper(AiSentimentClassifier aiSentimentClassifier) {
    this.aiSentimentClassifier = aiSentimentClassifier;
  }

  public NegativeNewsDtoMapper() {
    this.aiSentimentClassifier = null;
  }

  /**
   * Maps a domain screening request to a NewsAPI request DTO.
   *
   * @param request the domain screening request
   * @return the API request DTO with query parameters
   */
  public NegativeNewsSearchRequestDto mapToRequestDto(NegativeNewsScreeningRequest request) {
    if (request == null) {
      return null;
    }

    this.currentSubjectName = request.subjectName();
    String query = buildSearchQuery(request);
    String from = LocalDate.now()
        .minusMonths(request.dateRangeMonths())
        .format(DateTimeFormatter.ISO_LOCAL_DATE);
    String to = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

    return new NegativeNewsSearchRequestDto(
        query, from, to, "en", "relevancy", DomainConstants.MAX_ARTICLES);
  }

  /**
   * Maps a NewsAPI response DTO to a domain screening response. Performs keyword-based sentiment
   * classification on each article and computes aggregate screening metrics.
   *
   * @param responseDto the NewsAPI response DTO
   * @return the domain screening response
   */
  public NegativeNewsScreeningResponse mapToScreeningResponse(
      NegativeNewsSearchResponseDto responseDto) {
    if (responseDto == null) {
      return NegativeNewsScreeningResponse.error("No response received from screening API");
    }

    List<NewsArticle> articles = mapToNewsArticles(responseDto.articles());
    int totalArticles = responseDto.totalResults();

    long adverseArticlesCount = articles.stream()
        .filter(a -> a.sentiment() == ArticleSentiment.NEGATIVE
            || a.sentiment() == ArticleSentiment.HIGHLY_NEGATIVE)
        .count();

    double highestSeverityScore = articles.stream()
        .filter(a -> a.sentiment() == ArticleSentiment.NEGATIVE
            || a.sentiment() == ArticleSentiment.HIGHLY_NEGATIVE)
        .mapToDouble(NewsArticle::relevanceScore)
        .max()
        .orElse(0.0);

    ScreeningOutcome outcome = adverseArticlesCount > 0
        ? ScreeningOutcome.ADVERSE_FOUND
        : ScreeningOutcome.CLEAR;

    String summary = buildSummary(totalArticles, (int) adverseArticlesCount,
        highestSeverityScore, outcome);

    if (outcome == ScreeningOutcome.CLEAR) {
      return NegativeNewsScreeningResponse.clear(totalArticles, summary);
    }

    return NegativeNewsScreeningResponse.adverseFound(
        articles, totalArticles, (int) adverseArticlesCount, highestSeverityScore, summary);
  }

  private String buildSearchQuery(NegativeNewsScreeningRequest request) {
    StringBuilder query = new StringBuilder();
    query.append("\"").append(request.subjectName()).append("\"");

    StringBuilder keywords = new StringBuilder();

    if (request.idNumber() != null && !request.idNumber().trim().isEmpty()) {
      keywords.append(request.idNumber().trim());
    }

    if (request.additionalKeywords() != null && !request.additionalKeywords().isEmpty()) {
      for (String keyword : request.additionalKeywords()) {
        if (keyword != null && !keyword.trim().isEmpty()) {
          if (keywords.length() > 0) {
            keywords.append(" OR ");
          }
          keywords.append(keyword.trim());
        }
      }
    }

    if (keywords.length() > 0) {
      keywords.append(" OR ");
    }
    keywords.append(STANDARD_ADVERSE_TERMS);

    query.append(" AND (").append(keywords).append(")");
    return query.toString();
  }

  private List<NewsArticle> mapToNewsArticles(List<NewsApiArticleDto> articleDtos) {
    if (articleDtos == null) {
      return List.of();
    }

    return articleDtos.stream()
        .map(this::mapToNewsArticle)
        .filter(article -> article != null)
        .collect(Collectors.toList());
  }

  private NewsArticle mapToNewsArticle(NewsApiArticleDto dto) {
    if (dto == null) {
      return null;
    }

    ArticleSentiment sentiment;
    double relevanceScore;

    // Try AI classification first, fall back to keyword-based
    if (aiSentimentClassifier != null) {
      try {
        SentimentResult aiResult = aiSentimentClassifier.classify(
            dto.title(), dto.description(), currentSubjectName);

        // Filter out articles where subject is unrelated
        if ("UNRELATED".equals(aiResult.subjectRole())) {
          logger.debug("AI classified article as UNRELATED, filtering: {}", dto.title());
          return null;
        }

        sentiment = aiResult.sentiment();
        relevanceScore = aiResult.relevanceScore();
        logger.debug("AI sentiment for '{}': {} (score={}, role={})",
            dto.title(), sentiment, relevanceScore, aiResult.subjectRole());

      } catch (Exception e) {
        logger.warn("AI classification failed for '{}', using keyword fallback: {}",
            dto.title(), e.getMessage());
        String combinedText = buildCombinedText(dto);
        sentiment = classifySentiment(combinedText);
        relevanceScore = computeRelevanceScore(sentiment);
      }
    } else {
      String combinedText = buildCombinedText(dto);
      sentiment = classifySentiment(combinedText);
      relevanceScore = computeRelevanceScore(sentiment);
    }

    String sourceName = dto.source() != null ? dto.source().name() : null;
    String snippet = dto.description() != null ? dto.description() : dto.content();

    return new NewsArticle(
        dto.title(),
        sourceName,
        parsePublishedDate(dto.publishedAt()),
        dto.url(),
        snippet,
        sentiment,
        relevanceScore);
  }

  private String buildCombinedText(NewsApiArticleDto dto) {
    StringBuilder text = new StringBuilder();
    if (dto.title() != null) {
      text.append(dto.title()).append(" ");
    }
    if (dto.description() != null) {
      text.append(dto.description());
    }
    return text.toString().toLowerCase();
  }

  /**
   * Classifies the sentiment of an article based on keyword matching against title and description.
   */
  private ArticleSentiment classifySentiment(String text) {
    if (text == null || text.isEmpty()) {
      return ArticleSentiment.NEUTRAL;
    }

    for (String keyword : DomainConstants.HIGHLY_NEGATIVE_KEYWORDS) {
      if (text.contains(keyword)) {
        return ArticleSentiment.HIGHLY_NEGATIVE;
      }
    }

    for (String keyword : DomainConstants.NEGATIVE_KEYWORDS) {
      if (text.contains(keyword)) {
        return ArticleSentiment.NEGATIVE;
      }
    }

    return ArticleSentiment.NEUTRAL;
  }

  private double computeRelevanceScore(ArticleSentiment sentiment) {
    return switch (sentiment) {
      case HIGHLY_NEGATIVE -> HIGHLY_NEGATIVE_RELEVANCE;
      case NEGATIVE -> NEGATIVE_RELEVANCE;
      default -> NEUTRAL_RELEVANCE;
    };
  }

  /**
   * Parses date string using multiple format attempts, including ISO 8601 datetime strings from
   * NewsAPI (e.g., "2024-01-15T10:30:00Z").
   */
  LocalDate parsePublishedDate(String dateString) {
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

  private String buildSummary(int totalArticles, int adverseCount,
      double highestSeverity, ScreeningOutcome outcome) {
    if (outcome == ScreeningOutcome.CLEAR) {
      return String.format("Screened %d articles. No adverse news found.", totalArticles);
    }
    return String.format(
        "Screened %d articles. Found %d adverse article(s) with highest severity %.2f.",
        totalArticles, adverseCount, highestSeverity);
  }
}
