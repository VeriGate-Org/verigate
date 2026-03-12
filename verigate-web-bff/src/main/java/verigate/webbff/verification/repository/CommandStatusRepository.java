package verigate.webbff.verification.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import verigate.webbff.config.properties.CommandStoreProperties;
import verigate.webbff.verification.repository.model.PageResult;
import verigate.webbff.verification.repository.model.VerificationCommandStoreItem;

@Repository
public class CommandStatusRepository {

  private static final Logger logger = LoggerFactory.getLogger(CommandStatusRepository.class);
  private static final String PARTNER_INDEX = "partner-index";

  private final DynamoDbTable<VerificationCommandStoreItem> table;
  private final DynamoDbClient dynamoDbClient;
  private final String tableName;

  public CommandStatusRepository(
      DynamoDbEnhancedClient dynamoDbEnhancedClient,
      DynamoDbClient dynamoDbClient,
      CommandStoreProperties properties) {
    this.tableName = properties.getTableName();
    this.dynamoDbClient = dynamoDbClient;
    this.table =
        dynamoDbEnhancedClient.table(
            tableName, TableSchema.fromBean(VerificationCommandStoreItem.class));
  }

  @Cacheable(value = "command-status", key = "#commandId")
  public Optional<VerificationCommandStoreItem> findById(UUID commandId) {
    VerificationCommandStoreItem item;
    try {
      item = table.getItem(Key.builder().partitionValue(commandId.toString()).build());
    } catch (DynamoDbException e) {
      logger.error("DynamoDB getItem failed for commandId {}: {}", commandId, e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
    return Optional.ofNullable(item);
  }

  /**
   * Queries verifications by partnerId using the partner-index GSI.
   * Supports optional status filtering via begins_with on the statusCreatedAt sort key,
   * cursor-based pagination, and descending createdAt ordering.
   *
   * @param partnerId the partner to query for
   * @param status    optional status filter (e.g. "COMPLETED")
   * @param limit     max items to return
   * @param cursor    exclusive start key for pagination (commandId from last page)
   * @return page of results with last evaluated key for cursor-based pagination
   */
  public PageResult<VerificationCommandStoreItem> findByPartnerId(
      String partnerId, String status, int limit, Map<String, AttributeValue> cursor) {

    Map<String, AttributeValue> expressionValues = new HashMap<>();
    expressionValues.put(":pid", AttributeValue.builder().s(partnerId).build());

    String keyCondition = "partnerId = :pid";

    if (status != null && !status.isBlank()) {
      keyCondition += " AND begins_with(statusCreatedAt, :statusPrefix)";
      expressionValues.put(":statusPrefix", AttributeValue.builder().s(status + "#").build());
    }

    QueryRequest.Builder queryBuilder = QueryRequest.builder()
        .tableName(tableName)
        .indexName(PARTNER_INDEX)
        .keyConditionExpression(keyCondition)
        .expressionAttributeValues(expressionValues)
        .scanIndexForward(false)
        .limit(limit);

    if (cursor != null && !cursor.isEmpty()) {
      queryBuilder.exclusiveStartKey(cursor);
    }

    try {
      var response = dynamoDbClient.query(queryBuilder.build());
      var schema = TableSchema.fromBean(VerificationCommandStoreItem.class);

      List<VerificationCommandStoreItem> items = new ArrayList<>();
      for (var dynamoItem : response.items()) {
        items.add(schema.mapToItem(dynamoItem));
      }

      return new PageResult<>(items, response.lastEvaluatedKey());
    } catch (DynamoDbException e) {
      logger.error("DynamoDB query failed for partnerId {}: {}", partnerId, e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
  }
}
