/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.negativenews.infrastructure.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.adapter.negativenews.domain.models.ArticleSentiment;
import verigate.ai.common.domain.models.AiRequest;
import verigate.ai.common.domain.models.AiResponse;
import verigate.ai.common.domain.services.AiService;
import verigate.ai.common.infrastructure.prompts.PromptTemplates;

/**
 * AI-powered sentiment classifier for negative news articles. Uses AWS Bedrock Claude to analyze
 * article sentiment and relevance to a subject.
 */
public class AiSentimentClassifier {

  private static final Logger logger = LoggerFactory.getLogger(AiSentimentClassifier.class);

  private static final String PROMPT_TEMPLATE = "prompts/negative-news-sentiment.txt";

  private final AiService aiService;
  private final ObjectMapper objectMapper;

  public AiSentimentClassifier(AiService aiService) {
    this.aiService = aiService;
    this.objectMapper = new ObjectMapper();
  }

  /**
   * Classifies the sentiment of a news article using AI.
   *
   * @param title the article title
   * @param description the article description
   * @param subjectName the name of the subject being screened
   * @return the classification result
   */
  public SentimentResult classify(String title, String description, String subjectName) {
    try {
      String prompt = PromptTemplates.load(PROMPT_TEMPLATE, Map.of(
          "subjectName", subjectName != null ? subjectName : "",
          "title", title != null ? title : "",
          "description", description != null ? description : ""
      ));

      AiResponse response = aiService.invoke(new AiRequest(prompt, null));
      return parseResult(response.content());

    } catch (Exception e) {
      logger.warn("AI sentiment classification failed, will fall back to keywords: {}",
          e.getMessage());
      throw e;
    }
  }

  private SentimentResult parseResult(String content) {
    try {
      // Extract JSON from response (handle potential markdown wrapping)
      String json = content.trim();
      if (json.startsWith("```")) {
        json = json.replaceAll("```json\\s*", "").replaceAll("```\\s*$", "").trim();
      }

      JsonNode root = objectMapper.readTree(json);

      ArticleSentiment sentiment = parseSentiment(root.path("sentiment").asText("NEUTRAL"));
      double relevanceScore = root.path("relevanceScore").asDouble(0.3);
      String reasoning = root.path("reasoning").asText("");
      String subjectRole = root.path("subjectRole").asText("UNRELATED");

      return new SentimentResult(sentiment, relevanceScore, reasoning, subjectRole);

    } catch (Exception e) {
      logger.warn("Failed to parse AI sentiment response: {}", e.getMessage());
      throw new RuntimeException("Failed to parse AI sentiment response", e);
    }
  }

  private ArticleSentiment parseSentiment(String value) {
    return switch (value.toUpperCase()) {
      case "HIGHLY_NEGATIVE" -> ArticleSentiment.HIGHLY_NEGATIVE;
      case "NEGATIVE" -> ArticleSentiment.NEGATIVE;
      case "POSITIVE" -> ArticleSentiment.POSITIVE;
      default -> ArticleSentiment.NEUTRAL;
    };
  }

  /**
   * Result of AI sentiment classification.
   *
   * @param sentiment the classified sentiment
   * @param relevanceScore relevance score (0.0-1.0) of the article to the subject
   * @param reasoning one-sentence explanation of the classification
   * @param subjectRole the role of the subject in the article
   */
  public record SentimentResult(
      ArticleSentiment sentiment,
      double relevanceScore,
      String reasoning,
      String subjectRole
  ) {
  }
}
