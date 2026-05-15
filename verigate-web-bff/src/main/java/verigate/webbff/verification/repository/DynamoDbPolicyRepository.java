package verigate.webbff.verification.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import verigate.webbff.config.properties.PartnerHubProperties;
import verigate.webbff.verification.repository.model.PolicyDataModel;

@Repository
public class DynamoDbPolicyRepository {

  private static final Logger logger = LoggerFactory.getLogger(DynamoDbPolicyRepository.class);
  private static final String POLICY_ID_INDEX = "policy-id-index";

  private final DynamoDbClient dynamoDbClient;
  private final String tableName;

  public DynamoDbPolicyRepository(
      DynamoDbClient dynamoDbClient, PartnerHubProperties partnerHubProperties) {
    this.dynamoDbClient = dynamoDbClient;
    this.tableName = partnerHubProperties.getTableName();
  }

  @CacheEvict(value = "policies", key = "#policy.partnerPolicyId")
  public void save(PolicyDataModel policy) {
    try {
      dynamoDbClient.putItem(PutItemRequest.builder()
          .tableName(tableName)
          .item(toItem(policy))
          .build());
    } catch (DynamoDbException e) {
      logger.error("Failed to save policy {}: {}", policy.getPartnerPolicyId(), e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
  }

  @Cacheable(value = "policies", key = "#partnerPolicyId", unless = "#result == null")
  public Optional<PolicyDataModel> findById(String partnerPolicyId) {
    try {
      QueryResponse response = dynamoDbClient.query(QueryRequest.builder()
          .tableName(tableName)
          .indexName(POLICY_ID_INDEX)
          .keyConditionExpression("partnerPolicyId = :pid")
          .expressionAttributeValues(Map.of(
              ":pid", AttributeValue.builder().s(partnerPolicyId).build()))
          .limit(1)
          .build());

      if (response.items().isEmpty()) {
        return Optional.empty();
      }
      return Optional.of(toPolicyDataModel(response.items().get(0)));
    } catch (DynamoDbException e) {
      logger.error("Failed to get policy {}: {}", partnerPolicyId, e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
  }

  public List<PolicyDataModel> findByPartnerId(String partnerId) {
    try {
      QueryResponse response = dynamoDbClient.query(QueryRequest.builder()
          .tableName(tableName)
          .keyConditionExpression("partnerId = :pid AND begins_with(entityType, :prefix)")
          .expressionAttributeValues(Map.of(
              ":pid", AttributeValue.builder().s(partnerId).build(),
              ":prefix", AttributeValue.builder().s("POLICY#").build()))
          .build());

      List<PolicyDataModel> results = new ArrayList<>();
      for (var item : response.items()) {
        results.add(toPolicyDataModel(item));
      }
      return results;
    } catch (DynamoDbException e) {
      logger.error("Failed to list policies for partner {}: {}", partnerId, e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
  }

  @CacheEvict(value = "policies", key = "#partnerPolicyId")
  public void delete(String partnerPolicyId) {
    try {
      // Query GSI to resolve the composite key
      QueryResponse response = dynamoDbClient.query(QueryRequest.builder()
          .tableName(tableName)
          .indexName(POLICY_ID_INDEX)
          .keyConditionExpression("partnerPolicyId = :pid")
          .expressionAttributeValues(Map.of(
              ":pid", AttributeValue.builder().s(partnerPolicyId).build()))
          .limit(1)
          .build());

      if (response.items().isEmpty()) {
        return;
      }

      Map<String, AttributeValue> item = response.items().get(0);
      dynamoDbClient.deleteItem(DeleteItemRequest.builder()
          .tableName(tableName)
          .key(Map.of(
              "partnerId", item.get("partnerId"),
              "entityType", item.get("entityType")))
          .build());
    } catch (DynamoDbException e) {
      logger.error("Failed to delete policy {}: {}", partnerPolicyId, e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
  }

  private PolicyDataModel toPolicyDataModel(Map<String, AttributeValue> item) {
    PolicyDataModel model = new PolicyDataModel();
    model.setPartnerPolicyId(getStr(item, "partnerPolicyId"));
    model.setPartnerId(getStr(item, "partnerId"));
    model.setName(getStr(item, "name"));
    model.setDescription(getStr(item, "description"));
    model.setStepsJson(getStr(item, "stepsJson"));
    model.setScoringConfigJson(getStr(item, "scoringConfigJson"));
    model.setStatus(getStr(item, "status"));
    model.setVersion(getInt(item, "version"));
    model.setCreatedAt(getStr(item, "createdAt"));
    model.setUpdatedAt(getStr(item, "updatedAt"));
    return model;
  }

  private Map<String, AttributeValue> toItem(PolicyDataModel policy) {
    Map<String, AttributeValue> item = new HashMap<>();
    item.put("partnerId", AttributeValue.builder().s(policy.getPartnerId()).build());
    item.put("entityType",
        AttributeValue.builder().s("POLICY#" + policy.getPartnerPolicyId()).build());
    item.put("partnerPolicyId",
        AttributeValue.builder().s(policy.getPartnerPolicyId()).build());
    putIfNotNull(item, "name", policy.getName());
    putIfNotNull(item, "description", policy.getDescription());
    putIfNotNull(item, "stepsJson", policy.getStepsJson());
    putIfNotNull(item, "scoringConfigJson", policy.getScoringConfigJson());
    putIfNotNull(item, "status", policy.getStatus());
    item.put("version", AttributeValue.builder().n(String.valueOf(policy.getVersion())).build());
    putIfNotNull(item, "createdAt", policy.getCreatedAt());
    putIfNotNull(item, "updatedAt", policy.getUpdatedAt());
    return item;
  }

  private static String getStr(Map<String, AttributeValue> item, String key) {
    AttributeValue val = item.get(key);
    return (val != null && val.s() != null) ? val.s() : null;
  }

  private static int getInt(Map<String, AttributeValue> item, String key) {
    AttributeValue val = item.get(key);
    return (val != null && val.n() != null) ? Integer.parseInt(val.n()) : 0;
  }

  private static void putIfNotNull(Map<String, AttributeValue> item, String key, String value) {
    if (value != null) {
      item.put(key, AttributeValue.builder().s(value).build());
    }
  }
}
