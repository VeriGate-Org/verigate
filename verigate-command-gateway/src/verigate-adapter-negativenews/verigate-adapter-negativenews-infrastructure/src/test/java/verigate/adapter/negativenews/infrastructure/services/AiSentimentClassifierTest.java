/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.negativenews.infrastructure.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import verigate.adapter.negativenews.domain.models.ArticleSentiment;
import verigate.ai.common.domain.models.AiRequest;
import verigate.ai.common.domain.models.AiResponse;
import verigate.ai.common.domain.services.AiService;

@ExtendWith(MockitoExtension.class)
class AiSentimentClassifierTest {

  @Mock
  private AiService aiService;

  private AiSentimentClassifier classifier;

  @BeforeEach
  void setUp() {
    classifier = new AiSentimentClassifier(aiService);
  }

  @Test
  void shouldClassifyHighlyNegativeSentiment() {
    String aiResponseJson = """
        {
          "sentiment": "HIGHLY_NEGATIVE",
          "relevanceScore": 0.95,
          "reasoning": "Subject is accused of fraud",
          "subjectRole": "PERPETRATOR"
        }
        """;

    when(aiService.invoke(any(AiRequest.class)))
        .thenReturn(new AiResponse(aiResponseJson, "end_turn", 100, 50));

    AiSentimentClassifier.SentimentResult result =
        classifier.classify("CEO arrested for fraud", "Major fraud scandal", "John Doe");

    assertEquals(ArticleSentiment.HIGHLY_NEGATIVE, result.sentiment());
    assertEquals(0.95, result.relevanceScore(), 0.01);
    assertEquals("PERPETRATOR", result.subjectRole());
  }

  @Test
  void shouldClassifyNeutralSentiment() {
    String aiResponseJson = """
        {
          "sentiment": "NEUTRAL",
          "relevanceScore": 0.3,
          "reasoning": "Subject mentioned in passing",
          "subjectRole": "BYSTANDER"
        }
        """;

    when(aiService.invoke(any(AiRequest.class)))
        .thenReturn(new AiResponse(aiResponseJson, "end_turn", 100, 50));

    AiSentimentClassifier.SentimentResult result =
        classifier.classify("Industry report", "Market analysis", "John Doe");

    assertEquals(ArticleSentiment.NEUTRAL, result.sentiment());
    assertEquals("BYSTANDER", result.subjectRole());
  }

  @Test
  void shouldHandleMarkdownWrappedJson() {
    String aiResponseJson = """
        ```json
        {
          "sentiment": "NEGATIVE",
          "relevanceScore": 0.75,
          "reasoning": "Subject under investigation",
          "subjectRole": "PERPETRATOR"
        }
        ```
        """;

    when(aiService.invoke(any(AiRequest.class)))
        .thenReturn(new AiResponse(aiResponseJson, "end_turn", 100, 50));

    AiSentimentClassifier.SentimentResult result =
        classifier.classify("Investigation launched", "Regulatory probe", "John Doe");

    assertNotNull(result);
    assertEquals(ArticleSentiment.NEGATIVE, result.sentiment());
  }

  @Test
  void shouldThrowOnAiServiceFailure() {
    when(aiService.invoke(any(AiRequest.class)))
        .thenThrow(new RuntimeException("Bedrock unavailable"));

    assertThrows(RuntimeException.class,
        () -> classifier.classify("Title", "Description", "John Doe"));
  }
}
