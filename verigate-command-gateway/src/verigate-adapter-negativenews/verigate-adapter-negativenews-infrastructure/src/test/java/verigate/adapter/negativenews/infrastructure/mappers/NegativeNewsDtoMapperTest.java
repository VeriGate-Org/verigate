/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.negativenews.infrastructure.mappers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import verigate.adapter.negativenews.domain.models.ArticleSentiment;
import verigate.adapter.negativenews.domain.models.NegativeNewsScreeningRequest;
import verigate.adapter.negativenews.domain.models.NegativeNewsScreeningResponse;
import verigate.adapter.negativenews.domain.models.ScreeningOutcome;
import verigate.adapter.negativenews.infrastructure.http.dto.NegativeNewsSearchRequestDto;
import verigate.adapter.negativenews.infrastructure.http.dto.NegativeNewsSearchResponseDto;
import verigate.adapter.negativenews.infrastructure.http.dto.NegativeNewsSearchResponseDto.NewsApiArticleDto;
import verigate.adapter.negativenews.infrastructure.http.dto.NegativeNewsSearchResponseDto.NewsApiSourceDto;
import verigate.adapter.negativenews.infrastructure.services.AiSentimentClassifier;
import verigate.adapter.negativenews.infrastructure.services.AiSentimentClassifier.SentimentResult;

@ExtendWith(MockitoExtension.class)
class NegativeNewsDtoMapperTest {

  private NegativeNewsDtoMapper mapper;

  @Mock
  private AiSentimentClassifier aiSentimentClassifier;

  @BeforeEach
  void setUp() {
    mapper = new NegativeNewsDtoMapper();
  }

  @Test
  void mapToRequestDto_shouldBuildQueryWithSubjectNameAndKeywords() {
    NegativeNewsScreeningRequest request = NegativeNewsScreeningRequest.builder()
        .subjectName("John Doe")
        .idNumber("8501015009087")
        .additionalKeywords(List.of("politics", "finance"))
        .dateRangeMonths(12)
        .build();

    NegativeNewsSearchRequestDto dto = mapper.mapToRequestDto(request);

    assertNotNull(dto);
    assertTrue(dto.query().contains("\"John Doe\""));
    assertTrue(dto.query().contains("8501015009087"));
    assertTrue(dto.query().contains("politics"));
    assertTrue(dto.query().contains("finance"));
    assertTrue(dto.query().contains("fraud OR scandal"));
    assertEquals("en", dto.language());
    assertEquals("relevancy", dto.sortBy());
    assertEquals(100, dto.pageSize());

    LocalDate expectedFrom = LocalDate.now().minusMonths(12);
    assertEquals(expectedFrom.toString(), dto.from());
    assertEquals(LocalDate.now().toString(), dto.to());
  }

  @Test
  void mapToRequestDto_shouldHandleNullRequest() {
    assertNull(mapper.mapToRequestDto(null));
  }

  @Test
  void mapToRequestDto_shouldHandleEmptyKeywordsAndIdNumber() {
    NegativeNewsScreeningRequest request = NegativeNewsScreeningRequest.builder()
        .subjectName("Jane Smith")
        .dateRangeMonths(24)
        .build();

    NegativeNewsSearchRequestDto dto = mapper.mapToRequestDto(request);

    assertNotNull(dto);
    assertTrue(dto.query().contains("\"Jane Smith\""));
    assertTrue(dto.query().contains("fraud OR scandal"));
  }

  @Test
  void mapToScreeningResponse_shouldClassifyHighlyNegativeArticle() {
    NewsApiArticleDto article = new NewsApiArticleDto(
        new NewsApiSourceDto("bbc", "BBC News"),
        "Reporter",
        "CEO arrested for fraud and corruption charges",
        "The CEO was arrested on multiple fraud charges related to embezzlement.",
        "https://example.com/article",
        null,
        "2024-01-15T10:30:00Z",
        "Full content here"
    );

    NegativeNewsSearchResponseDto responseDto = new NegativeNewsSearchResponseDto(
        "ok", 1, List.of(article));

    NegativeNewsScreeningResponse response = mapper.mapToScreeningResponse(responseDto);

    assertEquals(ScreeningOutcome.ADVERSE_FOUND, response.outcome());
    assertEquals(1, response.totalArticlesFound());
    assertEquals(1, response.adverseArticlesCount());
    assertEquals(0.95, response.highestSeverityScore(), 0.01);
    assertFalse(response.articles().isEmpty());
    assertEquals(ArticleSentiment.HIGHLY_NEGATIVE, response.articles().get(0).sentiment());
  }

  @Test
  void mapToScreeningResponse_shouldClassifyNegativeArticle() {
    NewsApiArticleDto article = new NewsApiArticleDto(
        new NewsApiSourceDto("cnn", "CNN"),
        "Reporter",
        "Company faces major lawsuit over contract violation",
        "The company is being sued for breach of contract and alleged misconduct.",
        "https://example.com/article2",
        null,
        "2024-02-20T14:00:00Z",
        "Full content here"
    );

    NegativeNewsSearchResponseDto responseDto = new NegativeNewsSearchResponseDto(
        "ok", 1, List.of(article));

    NegativeNewsScreeningResponse response = mapper.mapToScreeningResponse(responseDto);

    assertEquals(ScreeningOutcome.ADVERSE_FOUND, response.outcome());
    assertEquals(1, response.adverseArticlesCount());
    assertEquals(0.75, response.highestSeverityScore(), 0.01);
    assertEquals(ArticleSentiment.NEGATIVE, response.articles().get(0).sentiment());
  }

  @Test
  void mapToScreeningResponse_shouldClassifyNeutralArticle() {
    NewsApiArticleDto article = new NewsApiArticleDto(
        new NewsApiSourceDto("reuters", "Reuters"),
        "Reporter",
        "Company announces new product launch",
        "The company revealed its latest innovation at the annual tech conference.",
        "https://example.com/article3",
        null,
        "2024-03-10T08:00:00Z",
        "Full content here"
    );

    NegativeNewsSearchResponseDto responseDto = new NegativeNewsSearchResponseDto(
        "ok", 1, List.of(article));

    NegativeNewsScreeningResponse response = mapper.mapToScreeningResponse(responseDto);

    assertEquals(ScreeningOutcome.CLEAR, response.outcome());
    assertEquals(0, response.adverseArticlesCount());
    assertEquals(0.0, response.highestSeverityScore(), 0.01);
  }

  @Test
  void mapToScreeningResponse_shouldComputeCorrectAggregatesForMixedArticles() {
    NewsApiArticleDto highlyNegative = new NewsApiArticleDto(
        new NewsApiSourceDto("bbc", "BBC News"), "Reporter",
        "Politician convicted of bribery", "Sentenced to prison.",
        "https://example.com/1", null, "2024-01-15T10:30:00Z", null);

    NewsApiArticleDto negative = new NewsApiArticleDto(
        new NewsApiSourceDto("cnn", "CNN"), "Reporter",
        "Investigation launched into company scandal", "Regulatory probe.",
        "https://example.com/2", null, "2024-02-20T14:00:00Z", null);

    NewsApiArticleDto neutral = new NewsApiArticleDto(
        new NewsApiSourceDto("reuters", "Reuters"), "Reporter",
        "Company hosts charity event", "Annual fundraiser successful.",
        "https://example.com/3", null, "2024-03-10T08:00:00Z", null);

    NegativeNewsSearchResponseDto responseDto = new NegativeNewsSearchResponseDto(
        "ok", 3, List.of(highlyNegative, negative, neutral));

    NegativeNewsScreeningResponse response = mapper.mapToScreeningResponse(responseDto);

    assertEquals(ScreeningOutcome.ADVERSE_FOUND, response.outcome());
    assertEquals(3, response.totalArticlesFound());
    assertEquals(2, response.adverseArticlesCount());
    assertEquals(0.95, response.highestSeverityScore(), 0.01);
    assertEquals(3, response.articles().size());
  }

  @Test
  void mapToScreeningResponse_shouldReturnClearForNoArticles() {
    NegativeNewsSearchResponseDto responseDto = new NegativeNewsSearchResponseDto(
        "ok", 0, List.of());

    NegativeNewsScreeningResponse response = mapper.mapToScreeningResponse(responseDto);

    assertEquals(ScreeningOutcome.CLEAR, response.outcome());
    assertEquals(0, response.totalArticlesFound());
    assertEquals(0, response.adverseArticlesCount());
    assertEquals(0.0, response.highestSeverityScore(), 0.01);
  }

  @Test
  void mapToScreeningResponse_shouldReturnErrorForNullResponse() {
    NegativeNewsScreeningResponse response = mapper.mapToScreeningResponse(null);

    assertEquals(ScreeningOutcome.ERROR, response.outcome());
  }

  @Test
  void parsePublishedDate_shouldParseIso8601DateTime() {
    LocalDate result = mapper.parsePublishedDate("2024-01-15T10:30:00Z");
    assertEquals(LocalDate.of(2024, 1, 15), result);
  }

  @Test
  void parsePublishedDate_shouldParseSimpleDateFormat() {
    LocalDate result = mapper.parsePublishedDate("2024-01-15");
    assertEquals(LocalDate.of(2024, 1, 15), result);
  }

  @Test
  void parsePublishedDate_shouldReturnNullForInvalidDate() {
    assertNull(mapper.parsePublishedDate("not-a-date"));
  }

  @Test
  void parsePublishedDate_shouldReturnNullForNullInput() {
    assertNull(mapper.parsePublishedDate(null));
  }

  @Test
  void parsePublishedDate_shouldReturnNullForEmptyInput() {
    assertNull(mapper.parsePublishedDate(""));
  }

  @Test
  void mapToScreeningResponse_shouldHandleNullArticlesList() {
    NegativeNewsSearchResponseDto responseDto = new NegativeNewsSearchResponseDto(
        "ok", 0, null);

    NegativeNewsScreeningResponse response = mapper.mapToScreeningResponse(responseDto);

    assertEquals(ScreeningOutcome.CLEAR, response.outcome());
    assertTrue(response.articles().isEmpty());
  }

  @Test
  void mapToScreeningResponse_shouldUseDescriptionAsSnippet() {
    NewsApiArticleDto article = new NewsApiArticleDto(
        new NewsApiSourceDto("bbc", "BBC News"),
        "Reporter",
        "CEO arrested for fraud",
        "This is the description of the fraud case",
        "https://example.com/article",
        null,
        "2024-01-15T10:30:00Z",
        "This is the full content"
    );

    NegativeNewsSearchResponseDto responseDto = new NegativeNewsSearchResponseDto(
        "ok", 1, List.of(article));

    NegativeNewsScreeningResponse response = mapper.mapToScreeningResponse(responseDto);

    assertEquals("This is the description of the fraud case",
        response.articles().get(0).snippet());
  }

  @Test
  void mapToScreeningResponse_shouldFallbackToContentWhenDescriptionNull() {
    NewsApiArticleDto article = new NewsApiArticleDto(
        new NewsApiSourceDto("bbc", "BBC News"),
        "Reporter",
        "CEO arrested for fraud",
        null,
        "https://example.com/article",
        null,
        "2024-01-15T10:30:00Z",
        "This is the full content"
    );

    NegativeNewsSearchResponseDto responseDto = new NegativeNewsSearchResponseDto(
        "ok", 1, List.of(article));

    NegativeNewsScreeningResponse response = mapper.mapToScreeningResponse(responseDto);

    assertEquals("This is the full content", response.articles().get(0).snippet());
  }

  @Nested
  class AiClassificationTests {

    @BeforeEach
    void setUp() {
      mapper = new NegativeNewsDtoMapper(aiSentimentClassifier);
    }

    @Test
    void shouldUseAiClassificationWhenAvailable() {
      when(aiSentimentClassifier.classify(anyString(), anyString(), anyString()))
          .thenReturn(new SentimentResult(
              ArticleSentiment.HIGHLY_NEGATIVE, 0.98, "Subject accused of fraud", "PERPETRATOR"));

      NegativeNewsScreeningRequest request = NegativeNewsScreeningRequest.builder()
          .subjectName("John Doe")
          .dateRangeMonths(12)
          .build();
      mapper.mapToRequestDto(request);

      NewsApiArticleDto article = new NewsApiArticleDto(
          new NewsApiSourceDto("bbc", "BBC News"), "Reporter",
          "CEO arrested for financial crimes", "Major fraud case uncovered.",
          "https://example.com/article", null, "2024-01-15T10:30:00Z", null);

      NegativeNewsSearchResponseDto responseDto = new NegativeNewsSearchResponseDto(
          "ok", 1, List.of(article));

      NegativeNewsScreeningResponse response = mapper.mapToScreeningResponse(responseDto);

      assertEquals(ScreeningOutcome.ADVERSE_FOUND, response.outcome());
      assertEquals(1, response.adverseArticlesCount());
      assertEquals(0.98, response.highestSeverityScore(), 0.01);
      assertEquals(ArticleSentiment.HIGHLY_NEGATIVE, response.articles().get(0).sentiment());
    }

    @Test
    void shouldFilterUnrelatedArticlesFromAiClassification() {
      when(aiSentimentClassifier.classify(anyString(), anyString(), anyString()))
          .thenReturn(new SentimentResult(
              ArticleSentiment.NEUTRAL, 0.1, "Subject not related", "UNRELATED"));

      NegativeNewsScreeningRequest request = NegativeNewsScreeningRequest.builder()
          .subjectName("John Doe")
          .dateRangeMonths(12)
          .build();
      mapper.mapToRequestDto(request);

      NewsApiArticleDto article = new NewsApiArticleDto(
          new NewsApiSourceDto("bbc", "BBC News"), "Reporter",
          "Different John Doe wins award", "Local charity event.",
          "https://example.com/article", null, "2024-01-15T10:30:00Z", null);

      NegativeNewsSearchResponseDto responseDto = new NegativeNewsSearchResponseDto(
          "ok", 1, List.of(article));

      NegativeNewsScreeningResponse response = mapper.mapToScreeningResponse(responseDto);

      assertEquals(ScreeningOutcome.CLEAR, response.outcome());
      assertEquals(0, response.articles().size());
    }

    @Test
    void shouldFallbackToKeywordsWhenAiClassificationFails() {
      when(aiSentimentClassifier.classify(anyString(), anyString(), anyString()))
          .thenThrow(new RuntimeException("Bedrock unavailable"));

      NegativeNewsScreeningRequest request = NegativeNewsScreeningRequest.builder()
          .subjectName("John Doe")
          .dateRangeMonths(12)
          .build();
      mapper.mapToRequestDto(request);

      NewsApiArticleDto article = new NewsApiArticleDto(
          new NewsApiSourceDto("bbc", "BBC News"), "Reporter",
          "CEO arrested for fraud and corruption charges",
          "Major fraud scandal uncovered in banking sector.",
          "https://example.com/article", null, "2024-01-15T10:30:00Z", null);

      NegativeNewsSearchResponseDto responseDto = new NegativeNewsSearchResponseDto(
          "ok", 1, List.of(article));

      NegativeNewsScreeningResponse response = mapper.mapToScreeningResponse(responseDto);

      assertEquals(ScreeningOutcome.ADVERSE_FOUND, response.outcome());
      assertEquals(1, response.adverseArticlesCount());
      // Should use keyword-based classification as fallback
      assertEquals(ArticleSentiment.HIGHLY_NEGATIVE, response.articles().get(0).sentiment());
    }
  }
}
