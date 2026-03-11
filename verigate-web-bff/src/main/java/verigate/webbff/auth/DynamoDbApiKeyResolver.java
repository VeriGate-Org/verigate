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
 * Validates both key status (ACTIVE) and partner status (ACTIVE).
 */
@Component
public class DynamoDbApiKeyResolver implements ApiKeyResolver {

  private static final Logger logger =
      LoggerFactory.getLogger(DynamoDbApiKeyResolver.class);

  private final DynamoDbClient dynamoDbClient;
  private final String apiKeysTable;
  private final String partnerTable;

  public DynamoDbApiKeyResolver(
      DynamoDbClient dynamoDbClient,
      @Value("${verigate.auth.api-keys-table:verigate-api-keys}")
      String apiKeysTable,
      @Value("${verigate.partner.table-name:verigate-partner-table}")
      String partnerTable) {
    this.dynamoDbClient = dynamoDbClient;
    this.apiKeysTable = apiKeysTable;
    this.partnerTable = partnerTable;
  }

  @Override
  public String resolvePartnerId(String apiKeyHash) {
    try {
      GetItemResponse response = dynamoDbClient.getItem(
          GetItemRequest.builder()
              .tableName(apiKeysTable)
              .key(Map.of("apiKeyHash",
                  AttributeValue.builder().s(apiKeyHash).build()))
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

      String partnerId = item.containsKey("partnerId")
          ? item.get("partnerId").s() : null;
      if (partnerId == null) {
        return null;
      }

      // Check partner status — only ACTIVE partners may authenticate
      if (!isPartnerActive(partnerId)) {
        logger.warn("Partner {} is not active, rejecting API key",
            partnerId);
        return null;
      }

      return partnerId;

    } catch (Exception e) {
      logger.error("Failed to resolve API key", e);
      return null;
    }
  }

  private boolean isPartnerActive(String partnerId) {
    try {
      GetItemResponse response = dynamoDbClient.getItem(
          GetItemRequest.builder()
              .tableName(partnerTable)
              .key(Map.of("partnerId",
                  AttributeValue.builder().s(partnerId).build()))
              .projectionExpression("partnerStatus")
              .build());

      if (!response.hasItem() || response.item().isEmpty()) {
        // Partner not found in table — allow by default
        // (partner may have been created before table was populated)
        logger.debug("Partner {} not found in partner table, "
            + "allowing by default", partnerId);
        return true;
      }

      AttributeValue statusAttr =
          response.item().get("partnerStatus");
      if (statusAttr == null || statusAttr.s() == null) {
        return true;
      }

      return "ACTIVE".equals(statusAttr.s());
    } catch (Exception e) {
      logger.error("Failed to check partner status for {}",
          partnerId, e);
      // Fail open to avoid blocking all traffic on table issues
      return true;
    }
  }
}
