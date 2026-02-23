/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.webbff.auth;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

/**
 * Resolves partner IDs from API key hashes using DynamoDB.
 */
@Component
public class DynamoDbApiKeyResolver implements ApiKeyResolver {

  private static final Logger logger = LoggerFactory.getLogger(DynamoDbApiKeyResolver.class);

  private final DynamoDbClient dynamoDbClient;
  private final String tableName;

  public DynamoDbApiKeyResolver(
      DynamoDbClient dynamoDbClient,
      @Value("${verigate.auth.api-keys-table:verigate-api-keys}") String tableName) {
    this.dynamoDbClient = dynamoDbClient;
    this.tableName = tableName;
  }

  @Override
  public String resolvePartnerId(String apiKeyHash) {
    try {
      GetItemResponse response = dynamoDbClient.getItem(GetItemRequest.builder()
          .tableName(tableName)
          .key(Map.of("apiKeyHash", AttributeValue.builder().s(apiKeyHash).build()))
          .build());

      if (!response.hasItem() || response.item().isEmpty()) {
        return null;
      }

      Map<String, AttributeValue> item = response.item();

      // Check if the key is active
      if (item.containsKey("status")) {
        String status = item.get("status").s();
        if (!"ACTIVE".equals(status)) {
          logger.warn("API key is not active, status: {}", status);
          return null;
        }
      }

      return item.containsKey("partnerId") ? item.get("partnerId").s() : null;

    } catch (Exception e) {
      logger.error("Failed to resolve API key", e);
      return null;
    }
  }
}
