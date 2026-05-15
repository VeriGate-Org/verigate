package verigate.webbff.verification.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import verigate.webbff.config.properties.PartnerHubProperties;
import verigate.webbff.verification.repository.model.RiskScoringConfigDataModel;

@Repository
public class DynamoDbRiskConfigRepository {

  private static final Logger logger = LoggerFactory.getLogger(DynamoDbRiskConfigRepository.class);

  private final DynamoDbClient dynamoDbClient;
  private final String tableName;

  public DynamoDbRiskConfigRepository(
      DynamoDbClient dynamoDbClient, PartnerHubProperties partnerHubProperties) {
    this.dynamoDbClient = dynamoDbClient;
    this.tableName = partnerHubProperties.getTableName();
  }

  public Optional<RiskScoringConfigDataModel> findByPartnerId(String partnerId) {
    try {
      GetItemResponse response = dynamoDbClient.getItem(GetItemRequest.builder()
          .tableName(tableName)
          .key(Map.of(
              "partnerId", AttributeValue.builder().s(partnerId).build(),
              "entityType", AttributeValue.builder().s("RISK_SCORING").build()))
          .build());

      if (!response.hasItem() || response.item().isEmpty()) {
        return Optional.empty();
      }
      return Optional.of(toModel(response.item()));
    } catch (DynamoDbException e) {
      logger.error("Failed to get risk config for partner {}: {}", partnerId, e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
  }

  public void save(RiskScoringConfigDataModel config) {
    try {
      dynamoDbClient.putItem(PutItemRequest.builder()
          .tableName(tableName)
          .item(toItem(config))
          .build());
    } catch (DynamoDbException e) {
      logger.error("Failed to save risk config for partner {}: {}",
          config.getPartnerId(), e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
  }

  private RiskScoringConfigDataModel toModel(Map<String, AttributeValue> item) {
    RiskScoringConfigDataModel model = new RiskScoringConfigDataModel();
    model.setPartnerId(getStr(item, "partnerId"));
    model.setStrategy(getStr(item, "strategy"));
    model.setWeightsJson(getStr(item, "weightsJson"));
    model.setTiersJson(getStr(item, "tiersJson"));
    model.setOverrideRulesJson(getStr(item, "overrideRulesJson"));
    model.setUpdatedAt(getStr(item, "updatedAt"));
    return model;
  }

  private Map<String, AttributeValue> toItem(RiskScoringConfigDataModel config) {
    Map<String, AttributeValue> item = new HashMap<>();
    item.put("partnerId", AttributeValue.builder().s(config.getPartnerId()).build());
    item.put("entityType", AttributeValue.builder().s("RISK_SCORING").build());
    putIfNotNull(item, "strategy", config.getStrategy());
    putIfNotNull(item, "weightsJson", config.getWeightsJson());
    putIfNotNull(item, "tiersJson", config.getTiersJson());
    putIfNotNull(item, "overrideRulesJson", config.getOverrideRulesJson());
    putIfNotNull(item, "updatedAt", config.getUpdatedAt());
    return item;
  }

  private static String getStr(Map<String, AttributeValue> item, String key) {
    AttributeValue val = item.get(key);
    return (val != null && val.s() != null) ? val.s() : null;
  }

  private static void putIfNotNull(Map<String, AttributeValue> item, String key, String value) {
    if (value != null) {
      item.put(key, AttributeValue.builder().s(value).build());
    }
  }
}
