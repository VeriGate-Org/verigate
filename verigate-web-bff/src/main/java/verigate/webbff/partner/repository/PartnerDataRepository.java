package verigate.webbff.partner.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

/**
 * Repository for partner-scoped data stored in DynamoDB.
 * Uses a single-table design with composite keys:
 * PK = PARTNER#{partnerId}, SK = POLICY#{id} | REPORT#{id} | PROFILE | NOTIFICATIONS
 */
@Repository
public class PartnerDataRepository {

  private static final Logger logger = LoggerFactory.getLogger(PartnerDataRepository.class);

  private final DynamoDbClient dynamoDbClient;
  private final ObjectMapper objectMapper;
  private final String tableName;

  public PartnerDataRepository(
      DynamoDbClient dynamoDbClient,
      ObjectMapper objectMapper,
      @Value("${verigate.partner.data-table:verigate-partner-data}") String tableName) {
    this.dynamoDbClient = dynamoDbClient;
    this.objectMapper = objectMapper;
    this.tableName = tableName;
  }

  private Map<String, AttributeValue> key(String partnerId, String sk) {
    return Map.of(
        "PK", AttributeValue.builder().s("PARTNER#" + partnerId).build(),
        "SK", AttributeValue.builder().s(sk).build());
  }

  // ── Policies ──────────────────────────────────────────────────────

  public String savePolicy(String partnerId, String policyId, Map<String, Object> data) {
    String id = policyId != null ? policyId : UUID.randomUUID().toString();
    String now = Instant.now().toString();

    Map<String, AttributeValue> item = new HashMap<>(key(partnerId, "POLICY#" + id));
    item.put("id", AttributeValue.builder().s(id).build());
    item.put("partnerId", AttributeValue.builder().s(partnerId).build());
    item.put("entityType", AttributeValue.builder().s("POLICY").build());
    item.put("data", AttributeValue.builder().s(toJson(data)).build());
    item.put("updatedAt", AttributeValue.builder().s(now).build());
    if (policyId == null) {
      item.put("createdAt", AttributeValue.builder().s(now).build());
    }

    putItem(item);
    return id;
  }

  public Optional<Map<String, Object>> getPolicy(String partnerId, String policyId) {
    return getItem(partnerId, "POLICY#" + policyId);
  }

  public List<Map<String, Object>> listPolicies(String partnerId) {
    return queryByPrefix(partnerId, "POLICY#");
  }

  public void deletePolicy(String partnerId, String policyId) {
    deleteItem(partnerId, "POLICY#" + policyId);
  }

  // ── Reports ───────────────────────────────────────────────────────

  public String saveReport(String partnerId, String reportId, Map<String, Object> data) {
    String id = reportId != null ? reportId : UUID.randomUUID().toString();
    String now = Instant.now().toString();

    Map<String, AttributeValue> item = new HashMap<>(key(partnerId, "REPORT#" + id));
    item.put("id", AttributeValue.builder().s(id).build());
    item.put("partnerId", AttributeValue.builder().s(partnerId).build());
    item.put("entityType", AttributeValue.builder().s("REPORT").build());
    item.put("data", AttributeValue.builder().s(toJson(data)).build());
    item.put("createdAt", AttributeValue.builder().s(now).build());

    putItem(item);
    return id;
  }

  public List<Map<String, Object>> listReports(String partnerId) {
    return queryByPrefix(partnerId, "REPORT#");
  }

  public void deleteReport(String partnerId, String reportId) {
    deleteItem(partnerId, "REPORT#" + reportId);
  }

  // ── Profile ───────────────────────────────────────────────────────

  public void saveProfile(String partnerId, Map<String, Object> data) {
    Map<String, AttributeValue> item = new HashMap<>(key(partnerId, "PROFILE"));
    item.put("partnerId", AttributeValue.builder().s(partnerId).build());
    item.put("entityType", AttributeValue.builder().s("PROFILE").build());
    item.put("data", AttributeValue.builder().s(toJson(data)).build());
    item.put("updatedAt", AttributeValue.builder().s(Instant.now().toString()).build());
    putItem(item);
  }

  public Optional<Map<String, Object>> getProfile(String partnerId) {
    return getItem(partnerId, "PROFILE");
  }

  // ── Notifications ─────────────────────────────────────────────────

  public void saveNotifications(String partnerId, Map<String, Object> data) {
    Map<String, AttributeValue> item = new HashMap<>(key(partnerId, "NOTIFICATIONS"));
    item.put("partnerId", AttributeValue.builder().s(partnerId).build());
    item.put("entityType", AttributeValue.builder().s("NOTIFICATIONS").build());
    item.put("data", AttributeValue.builder().s(toJson(data)).build());
    item.put("updatedAt", AttributeValue.builder().s(Instant.now().toString()).build());
    putItem(item);
  }

  public Optional<Map<String, Object>> getNotifications(String partnerId) {
    return getItem(partnerId, "NOTIFICATIONS");
  }

  // ── Custom Partner Entities ───────────────────────────────────────

  public String saveCustomEntity(
      String partnerId,
      String entityPrefix,
      String entityType,
      String entityId,
      Map<String, Object> data) {
    String id = entityId != null ? entityId : UUID.randomUUID().toString();
    String now = Instant.now().toString();
    Optional<Map<String, Object>> existing = entityId != null
        ? getItem(partnerId, entityPrefix + id)
        : Optional.empty();
    String createdAt = existing.map(item -> (String) item.get("createdAt")).orElse(now);

    Map<String, AttributeValue> item = new HashMap<>(key(partnerId, entityPrefix + id));
    item.put("id", AttributeValue.builder().s(id).build());
    item.put("partnerId", AttributeValue.builder().s(partnerId).build());
    item.put("entityType", AttributeValue.builder().s(entityType).build());
    item.put("data", AttributeValue.builder().s(toJson(data)).build());
    item.put("createdAt", AttributeValue.builder().s(createdAt).build());
    item.put("updatedAt", AttributeValue.builder().s(now).build());

    putItem(item);
    return id;
  }

  public Optional<Map<String, Object>> getCustomEntity(
      String partnerId, String entityPrefix, String entityId) {
    return getItem(partnerId, entityPrefix + entityId);
  }

  public List<Map<String, Object>> listCustomEntities(String partnerId, String entityPrefix) {
    return queryByPrefix(partnerId, entityPrefix);
  }

  public void deleteCustomEntity(String partnerId, String entityPrefix, String entityId) {
    deleteItem(partnerId, entityPrefix + entityId);
  }

  // ── Internal helpers ──────────────────────────────────────────────

  private void putItem(Map<String, AttributeValue> item) {
    try {
      dynamoDbClient.putItem(PutItemRequest.builder()
          .tableName(tableName)
          .item(item)
          .build());
    } catch (DynamoDbException e) {
      logger.error("DynamoDB putItem failed: {}", e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
  }

  private Optional<Map<String, Object>> getItem(String partnerId, String sk) {
    try {
      var response = dynamoDbClient.getItem(GetItemRequest.builder()
          .tableName(tableName)
          .key(key(partnerId, sk))
          .build());
      if (!response.hasItem() || response.item().isEmpty()) {
        return Optional.empty();
      }
      return Optional.of(parseData(response.item()));
    } catch (DynamoDbException e) {
      logger.error("DynamoDB getItem failed: {}", e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
  }

  private void deleteItem(String partnerId, String sk) {
    try {
      dynamoDbClient.deleteItem(DeleteItemRequest.builder()
          .tableName(tableName)
          .key(key(partnerId, sk))
          .build());
    } catch (DynamoDbException e) {
      logger.error("DynamoDB deleteItem failed: {}", e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
  }

  private List<Map<String, Object>> queryByPrefix(String partnerId, String skPrefix) {
    try {
      var response = dynamoDbClient.query(QueryRequest.builder()
          .tableName(tableName)
          .keyConditionExpression("PK = :pk AND begins_with(SK, :skPrefix)")
          .expressionAttributeValues(Map.of(
              ":pk", AttributeValue.builder().s("PARTNER#" + partnerId).build(),
              ":skPrefix", AttributeValue.builder().s(skPrefix).build()))
          .scanIndexForward(false)
          .build());

      List<Map<String, Object>> results = new ArrayList<>();
      for (var item : response.items()) {
        results.add(parseData(item));
      }
      return results;
    } catch (DynamoDbException e) {
      logger.error("DynamoDB query failed: {}", e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> parseData(Map<String, AttributeValue> item) {
    Map<String, Object> result = new HashMap<>();
    // Copy scalar fields
    for (var entry : item.entrySet()) {
      if ("data".equals(entry.getKey())) {
        try {
          Map<String, Object> data = objectMapper.readValue(entry.getValue().s(), Map.class);
          result.putAll(data);
        } catch (JsonProcessingException e) {
          logger.warn("Failed to parse JSON data field: {}", e.getMessage());
        }
      } else if (entry.getValue().s() != null) {
        result.put(entry.getKey(), entry.getValue().s());
      }
    }
    return result;
  }

  private String toJson(Map<String, Object> data) {
    try {
      return objectMapper.writeValueAsString(data);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to serialize data to JSON", e);
    }
  }
}
