/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.webbff.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
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
 * Uses constant-time comparison to prevent timing attacks.
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
      @Value("${verigate.partner-hub.table-name:verigate-partner-hub}")
      String partnerTable) {
    this.dynamoDbClient = dynamoDbClient;
    this.apiKeysTable = apiKeysTable;
    this.partnerTable = partnerTable;
  }

  @Override
  public String resolvePartnerId(String rawApiKey) {
    try {
      // Compute unsalted hash for lookup
      String lookupHash = hashApiKeyForLookup(rawApiKey);

      GetItemResponse response = dynamoDbClient.getItem(GetItemRequest.builder()
          .tableName(apiKeysTable)
          .key(Map.of("lookupHash", AttributeValue.builder().s(lookupHash).build()))
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

      // Retrieve salt and verification hash
      String salt = item.containsKey("salt") ? item.get("salt").s() : null;
      String storedVerificationHash = item.containsKey("verificationHash")
          ? item.get("verificationHash").s() : null;

      if (salt == null || storedVerificationHash == null) {
        logger.error("API key record missing salt or verificationHash");
        return null;
      }

      // Compute verification hash with salt
      String computedVerificationHash = hashApiKeyForVerification(rawApiKey, salt);

      // Use constant-time comparison to prevent timing attacks
      if (!constantTimeEquals(computedVerificationHash, storedVerificationHash)) {
        logger.warn("API key verification hash mismatch");
        return null;
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
              .key(Map.of(
                  "partnerId", AttributeValue.builder().s(partnerId).build(),
                  "entityType", AttributeValue.builder().s("METADATA").build()))
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

  /**
   * Computes an unsalted SHA-256 hash of a raw API key for lookup.
   */
  private String hashApiKeyForLookup(String apiKey) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(apiKey.getBytes(StandardCharsets.UTF_8));
      return bytesToHex(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 not available", e);
    }
  }

  /**
   * Computes a salted SHA-256 hash of a raw API key for verification.
   */
  private String hashApiKeyForVerification(String apiKey, String salt) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] saltBytes = Base64.getUrlDecoder().decode(salt);
      byte[] apiKeyBytes = apiKey.getBytes(StandardCharsets.UTF_8);
      byte[] combined = new byte[saltBytes.length + apiKeyBytes.length];
      System.arraycopy(saltBytes, 0, combined, 0, saltBytes.length);
      System.arraycopy(apiKeyBytes, 0, combined, saltBytes.length, apiKeyBytes.length);

      byte[] hash = digest.digest(combined);
      return bytesToHex(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 not available", e);
    }
  }

  /**
   * Converts byte array to hex string.
   */
  private String bytesToHex(byte[] bytes) {
    StringBuilder hexString = new StringBuilder();
    for (byte b : bytes) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }

  /**
   * Constant-time string comparison to prevent timing attacks.
   * Converts hex strings to bytes and uses MessageDigest.isEqual().
   */
  private boolean constantTimeEquals(String a, String b) {
    if (a == null || b == null || a.length() != b.length()) {
      return false;
    }

    byte[] aBytes = hexToBytes(a);
    byte[] bBytes = hexToBytes(b);
    return MessageDigest.isEqual(aBytes, bBytes);
  }

  /**
   * Converts hex string to byte array.
   */
  private byte[] hexToBytes(String hex) {
    int len = hex.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
          + Character.digit(hex.charAt(i + 1), 16));
    }
    return data;
  }
}
