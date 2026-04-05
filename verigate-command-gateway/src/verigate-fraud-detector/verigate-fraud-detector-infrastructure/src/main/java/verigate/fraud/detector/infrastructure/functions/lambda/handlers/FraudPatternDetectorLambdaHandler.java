/*
 * VeriGate (c) 2025. All rights reserved.
 */

package verigate.fraud.detector.infrastructure.functions.lambda.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import verigate.ai.common.domain.models.AiRequest;
import verigate.ai.common.domain.models.AiResponse;
import verigate.ai.common.domain.services.AiService;
import verigate.ai.common.infrastructure.bedrock.BedrockAiService;
import verigate.ai.common.infrastructure.bedrock.BedrockClientFactory;
import verigate.ai.common.infrastructure.prompts.PromptTemplates;

/**
 * Lambda handler for fraud pattern detection. Consumes verification events from Kinesis, tracks
 * submission velocity, and uses AI to analyze suspicious patterns.
 */
public class FraudPatternDetectorLambdaHandler implements RequestHandler<KinesisEvent, Void> {

  private static final Logger logger =
      LoggerFactory.getLogger(FraudPatternDetectorLambdaHandler.class);

  private static final String PROMPT_TEMPLATE = "prompts/fraud-pattern-analysis.txt";

  private static final int VELOCITY_THRESHOLD_HIGH = 10;
  private static final int MULTI_PARTNER_THRESHOLD = 3;

  private final AiService aiService;
  private final DynamoDbClient dynamoDbClient;
  private final ObjectMapper objectMapper;
  private final String velocityTableName;
  private final String patternsTableName;
  private final String alertsTableName;

  public FraudPatternDetectorLambdaHandler() {
    String region = System.getenv("BEDROCK_REGION");
    String modelId = System.getenv("BEDROCK_MODEL_ID");
    this.aiService = new BedrockAiService(
        BedrockClientFactory.create(region != null ? region : "us-east-1"), modelId);
    this.dynamoDbClient = DynamoDbClient.builder().build();
    this.objectMapper = new ObjectMapper();
    this.velocityTableName = System.getenv("FRAUD_VELOCITY_TABLE_NAME");
    this.patternsTableName = System.getenv("FRAUD_PATTERNS_TABLE_NAME");
    this.alertsTableName = System.getenv("MONITORING_ALERTS_TABLE_NAME");
  }

  @Override
  public Void handleRequest(KinesisEvent event, Context context) {
    for (KinesisEvent.KinesisEventRecord record : event.getRecords()) {
      try {
        String payload = new String(
            record.getKinesis().getData().array(), StandardCharsets.UTF_8);
        JsonNode eventNode = objectMapper.readTree(payload);

        String eventType = eventNode.path("eventType").asText("");
        if (!eventType.startsWith("Verification")) {
          continue;
        }

        processVerificationEvent(eventNode);

      } catch (Exception e) {
        logger.error("Failed to process fraud detection record: {}", e.getMessage(), e);
      }
    }
    return null;
  }

  private void processVerificationEvent(JsonNode eventNode) {
    String idNumber = eventNode.path("idNumber").asText("");
    String partnerId = eventNode.path("partnerId").asText("");

    if (idNumber.isEmpty()) {
      return;
    }

    String identifierHash = hashIdentifier(idNumber);
    String windowKey = LocalDate.now(ZoneOffset.UTC).toString();

    // Update velocity counter
    int currentCount = incrementVelocity(identifierHash, windowKey, partnerId);

    // Check thresholds
    boolean velocityAlert = currentCount >= VELOCITY_THRESHOLD_HIGH;
    boolean multiPartnerAlert = getUniquePartnerCount(identifierHash, windowKey)
        >= MULTI_PARTNER_THRESHOLD;

    if (velocityAlert || multiPartnerAlert) {
      logger.warn("Fraud threshold crossed: identifierHash={}, count={}, multiPartner={}",
          identifierHash, currentCount, multiPartnerAlert);

      analyzeWithAi(identifierHash, currentCount,
          getUniquePartnerCount(identifierHash, windowKey));
    }
  }

  private int incrementVelocity(String identifierHash, String windowKey, String partnerId) {
    try {
      long ttl = Instant.now().plusSeconds(7 * 24 * 3600).getEpochSecond(); // 7 days

      dynamoDbClient.updateItem(UpdateItemRequest.builder()
          .tableName(velocityTableName)
          .key(Map.of(
              "identifierHash", AttributeValue.fromS(identifierHash),
              "windowKey", AttributeValue.fromS(windowKey)
          ))
          .updateExpression(
              "SET #count = if_not_exists(#count, :zero) + :one, "
              + "#ttl = :ttl "
              + "ADD #partners :partnerSet")
          .expressionAttributeNames(Map.of(
              "#count", "submissionCount",
              "#ttl", "ttl",
              "#partners", "uniquePartners"
          ))
          .expressionAttributeValues(Map.of(
              ":zero", AttributeValue.fromN("0"),
              ":one", AttributeValue.fromN("1"),
              ":ttl", AttributeValue.fromN(String.valueOf(ttl)),
              ":partnerSet", AttributeValue.fromSs(List.of(partnerId))
          ))
          .build());

      // Get current count
      GetItemResponse response = dynamoDbClient.getItem(GetItemRequest.builder()
          .tableName(velocityTableName)
          .key(Map.of(
              "identifierHash", AttributeValue.fromS(identifierHash),
              "windowKey", AttributeValue.fromS(windowKey)
          ))
          .build());

      if (response.hasItem()) {
        return Integer.parseInt(
            response.item().getOrDefault("submissionCount",
                AttributeValue.fromN("0")).n());
      }
    } catch (Exception e) {
      logger.error("Failed to update velocity: {}", e.getMessage());
    }
    return 0;
  }

  private int getUniquePartnerCount(String identifierHash, String windowKey) {
    try {
      GetItemResponse response = dynamoDbClient.getItem(GetItemRequest.builder()
          .tableName(velocityTableName)
          .key(Map.of(
              "identifierHash", AttributeValue.fromS(identifierHash),
              "windowKey", AttributeValue.fromS(windowKey)
          ))
          .build());

      if (response.hasItem() && response.item().containsKey("uniquePartners")) {
        return response.item().get("uniquePartners").ss().size();
      }
    } catch (Exception e) {
      logger.error("Failed to get partner count: {}", e.getMessage());
    }
    return 0;
  }

  private void analyzeWithAi(String identifierHash, int submissionCount, int uniquePartnerCount) {
    try {
      String prompt = PromptTemplates.load(PROMPT_TEMPLATE, Map.of(
          "identifierHash", identifierHash,
          "submissionCount", String.valueOf(submissionCount),
          "uniquePartnerCount", String.valueOf(uniquePartnerCount),
          "submissionsJson", "{}"
      ));

      AiResponse response = aiService.invoke(new AiRequest(prompt, null));

      String json = response.content().trim();
      if (json.startsWith("```")) {
        json = json.replaceAll("```json\\s*", "").replaceAll("```\\s*$", "").trim();
      }

      JsonNode result = objectMapper.readTree(json);
      String patternType = result.path("patternType").asText("NORMAL");

      if (!"NORMAL".equals(patternType)) {
        String patternId = UUID.randomUUID().toString();
        String severity = result.path("severity").asText("MEDIUM");
        String description = result.path("description").asText("");
        long ttl = Instant.now().plusSeconds(90 * 24 * 3600).getEpochSecond();

        // Save fraud pattern
        dynamoDbClient.putItem(PutItemRequest.builder()
            .tableName(patternsTableName)
            .item(Map.of(
                "patternId", AttributeValue.fromS(patternId),
                "identifierHash", AttributeValue.fromS(identifierHash),
                "patternType", AttributeValue.fromS(patternType),
                "severity", AttributeValue.fromS(severity),
                "description", AttributeValue.fromS(description),
                "recommendedAction", AttributeValue.fromS(
                    result.path("recommendedAction").asText("FLAG_FOR_REVIEW")),
                "detectedAt", AttributeValue.fromS(Instant.now().toString()),
                "ttl", AttributeValue.fromN(String.valueOf(ttl))
            ))
            .build());

        // Create monitoring alert
        String alertId = UUID.randomUUID().toString();
        dynamoDbClient.putItem(PutItemRequest.builder()
            .tableName(alertsTableName)
            .item(Map.of(
                "alertId", AttributeValue.fromS(alertId),
                "partnerId", AttributeValue.fromS("SYSTEM"),
                "subjectIdCreatedAt", AttributeValue.fromS(
                    identifierHash + "#" + Instant.now().toString()),
                "title", AttributeValue.fromS("Fraud Pattern Detected: " + patternType),
                "description", AttributeValue.fromS(description),
                "severity", AttributeValue.fromS(severity),
                "acknowledged", AttributeValue.fromBool(false),
                "createdAt", AttributeValue.fromS(Instant.now().toString())
            ))
            .build());

        logger.info("Fraud pattern detected and alert created: type={}, severity={}",
            patternType, severity);
      }

    } catch (Exception e) {
      logger.error("AI fraud analysis failed: {}", e.getMessage(), e);
    }
  }

  private String hashIdentifier(String identifier) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(identifier.getBytes(StandardCharsets.UTF_8));
      StringBuilder hexString = new StringBuilder();
      for (byte b : hash) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (Exception e) {
      throw new RuntimeException("Failed to hash identifier", e);
    }
  }
}
