/*
 * VeriGate (c) 2025. All rights reserved.
 */

package verigate.ai.risksignal.infrastructure.functions.lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import verigate.ai.common.domain.models.AiRequest;
import verigate.ai.common.domain.models.AiResponse;
import verigate.ai.common.domain.services.AiService;
import verigate.ai.common.infrastructure.bedrock.BedrockAiService;
import verigate.ai.common.infrastructure.bedrock.BedrockClientFactory;
import verigate.ai.common.infrastructure.prompts.PromptTemplates;

/**
 * Lambda handler that consumes Kinesis events after risk assessment completion and produces
 * AI-enhanced risk analysis. Runs as a post-processing step without modifying the existing risk
 * engine pipeline.
 */
public class AiRiskSignalLambdaHandler implements RequestHandler<KinesisEvent, Void> {

  private static final Logger logger = LoggerFactory.getLogger(AiRiskSignalLambdaHandler.class);

  private static final String PROMPT_TEMPLATE = "prompts/risk-analysis.txt";

  private final AiService aiService;
  private final DynamoDbClient dynamoDbClient;
  private final ObjectMapper objectMapper;
  private final String tableName;

  public AiRiskSignalLambdaHandler() {
    String region = System.getenv("BEDROCK_REGION");
    String modelId = System.getenv("BEDROCK_MODEL_ID");
    this.aiService = new BedrockAiService(
        BedrockClientFactory.create(region != null ? region : "us-east-1"), modelId);
    this.dynamoDbClient = DynamoDbClient.builder().build();
    this.objectMapper = new ObjectMapper();
    this.tableName = System.getenv("AI_RISK_ENHANCEMENTS_TABLE_NAME");
  }

  @Override
  public Void handleRequest(KinesisEvent event, Context context) {
    for (KinesisEvent.KinesisEventRecord record : event.getRecords()) {
      try {
        String payload = new String(
            record.getKinesis().getData().array(), StandardCharsets.UTF_8);
        JsonNode eventNode = objectMapper.readTree(payload);

        String eventType = eventNode.path("eventType").asText("");
        if (!"RiskAssessmentCompleted".equals(eventType)) {
          continue;
        }

        processRiskAssessment(eventNode);

      } catch (Exception e) {
        logger.error("Failed to process Kinesis record: {}", e.getMessage(), e);
      }
    }
    return null;
  }

  private void processRiskAssessment(JsonNode eventNode) {
    String workflowId = eventNode.path("workflowId").asText("");
    String subjectName = eventNode.path("subjectName").asText("Unknown");
    String adapterScoresJson = eventNode.path("adapterScores").toString();
    String compositeScore = eventNode.path("compositeScore").asText("0");
    String riskTier = eventNode.path("riskTier").asText("UNKNOWN");

    logger.info("Processing AI risk enhancement for workflow: {}", workflowId);

    String prompt = PromptTemplates.load(PROMPT_TEMPLATE, Map.of(
        "subjectName", subjectName,
        "adapterScoresJson", adapterScoresJson,
        "compositeScore", compositeScore,
        "riskTier", riskTier
    ));

    AiResponse response = aiService.invoke(new AiRequest(prompt, null));

    try {
      String json = response.content().trim();
      if (json.startsWith("```")) {
        json = json.replaceAll("```json\\s*", "").replaceAll("```\\s*$", "").trim();
      }

      JsonNode result = objectMapper.readTree(json);

      long ttl = Instant.now().plusSeconds(90 * 24 * 3600).getEpochSecond(); // 90 days

      dynamoDbClient.putItem(PutItemRequest.builder()
          .tableName(tableName)
          .item(Map.of(
              "workflowId", AttributeValue.fromS(workflowId),
              "confidenceAdjustment", AttributeValue.fromN(
                  String.valueOf(result.path("confidenceAdjustment").asInt(0))),
              "reasoning", AttributeValue.fromS(
                  result.path("reasoning").asText("")),
              "correlations", AttributeValue.fromS(
                  result.path("correlations").toString()),
              "anomalies", AttributeValue.fromS(
                  result.path("anomalies").toString()),
              "analyzedAt", AttributeValue.fromS(Instant.now().toString()),
              "ttl", AttributeValue.fromN(String.valueOf(ttl))
          ))
          .build());

      logger.info("AI risk enhancement saved for workflow: {}", workflowId);

    } catch (Exception e) {
      logger.error("Failed to save AI risk enhancement for {}: {}", workflowId, e.getMessage(), e);
    }
  }
}
