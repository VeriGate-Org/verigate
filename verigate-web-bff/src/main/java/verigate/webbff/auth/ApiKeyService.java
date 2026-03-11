/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.webbff.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

/**
 * Service for managing partner API keys.
 * <p>
 * API keys are generated as cryptographically secure random strings,
 * but only the SHA-256 hash is stored in DynamoDB. The raw key value
 * is returned exactly once at creation time — it cannot be retrieved
 * afterwards.
 * <p>
 * DynamoDB table schema expectations:
 * <ul>
 *   <li>Partition key: {@code apiKeyHash} (String)</li>
 *   <li>GSI {@code partnerId-index} on attribute {@code partnerId} (String)
 *       — required by {@link #listApiKeys(String)}</li>
 * </ul>
 */
@Service
public class ApiKeyService {

  private static final Logger logger = LoggerFactory.getLogger(ApiKeyService.class);

  /** Length of the raw API key in bytes (before Base64 encoding). */
  private static final int KEY_BYTE_LENGTH = 32;

  /** Length of the salt in bytes (before Base64 encoding). */
  private static final int SALT_BYTE_LENGTH = 16;

  /** Number of prefix characters stored for human-readable identification. */
  private static final int KEY_PREFIX_LENGTH = 8;

  /** Name of the GSI used to query keys by partnerId. */
  private static final String PARTNER_ID_INDEX = "partnerId-index";

  private final DynamoDbClient dynamoDbClient;
  private final String tableName;
  private final SecureRandom secureRandom;

  public ApiKeyService(
      DynamoDbClient dynamoDbClient,
      @Value("${verigate.auth.api-keys-table:verigate-api-keys}") String tableName) {
    this.dynamoDbClient = dynamoDbClient;
    this.tableName = tableName;
    this.secureRandom = new SecureRandom();
  }

  /**
   * Generates a new API key for the given partner.
   * <p>
   * The returned {@link GeneratedApiKey} contains the raw key value
   * (returned to the caller exactly once) and the persisted record.
   *
   * @param partnerId the partner to create the key for
   * @param createdBy identifier of the user/system creating the key
   * @return the generated key (raw value + record metadata)
   */
  public GeneratedApiKey generateApiKey(String partnerId, String createdBy) {
    // Generate a cryptographically secure random key
    byte[] keyBytes = new byte[KEY_BYTE_LENGTH];
    secureRandom.nextBytes(keyBytes);
    StringBuilder hex = new StringBuilder(KEY_BYTE_LENGTH * 2);
    for (byte b : keyBytes) {
      hex.append(String.format("%02x", b));
    }
    String rawApiKey = "vg_live_" + hex;

    // Generate a cryptographically secure salt
    String salt = generateSalt();

    // Generate both hashes
    String lookupHash = hashApiKeyForLookup(rawApiKey);
    String verificationHash = hashApiKeyForVerification(rawApiKey, salt);
    String keyPrefix = rawApiKey.substring(0, Math.min(KEY_PREFIX_LENGTH, rawApiKey.length()));
    LocalDateTime now = LocalDateTime.now();

    // Build DynamoDB item
    Map<String, AttributeValue> item = new HashMap<>();
    item.put("lookupHash", AttributeValue.builder().s(lookupHash).build());
    item.put("verificationHash", AttributeValue.builder().s(verificationHash).build());
    item.put("salt", AttributeValue.builder().s(salt).build());
    item.put("partnerId", AttributeValue.builder().s(partnerId).build());
    item.put("status", AttributeValue.builder().s(ApiKeyRecord.STATUS_ACTIVE).build());
    item.put("keyPrefix", AttributeValue.builder().s(keyPrefix).build());
    item.put("createdAt", AttributeValue.builder().s(now.toString()).build());
    item.put("createdBy", AttributeValue.builder().s(createdBy).build());

    try {
      dynamoDbClient.putItem(PutItemRequest.builder()
          .tableName(tableName)
          .item(item)
          .build());
    } catch (DynamoDbException e) {
      logger.error("DynamoDB putItem failed for API key generation (partner: {}): {}", partnerId, e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }

    logger.info("Generated new API key for partner {} (prefix: {})", partnerId, keyPrefix);

    ApiKeyRecord record = new ApiKeyRecord(
        lookupHash, verificationHash, salt, partnerId, ApiKeyRecord.STATUS_ACTIVE,
        keyPrefix, now, null, createdBy);

    return new GeneratedApiKey(rawApiKey, record);
  }

  /**
   * Revokes an API key by setting its status to REVOKED.
   *
   * @param lookupHash unsalted SHA-256 hash of the key to revoke (partition key)
   * @throws IllegalArgumentException if the key is not found
   */
  public void revokeApiKey(String lookupHash) {
    GetItemResponse existing;
    try {
      // Verify the key exists
      existing = dynamoDbClient.getItem(GetItemRequest.builder()
          .tableName(tableName)
          .key(Map.of("lookupHash", AttributeValue.builder().s(lookupHash).build()))
          .build());
    } catch (DynamoDbException e) {
      logger.error("DynamoDB getItem failed during key revocation: {}", e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }

    if (!existing.hasItem() || existing.item().isEmpty()) {
      throw new IllegalArgumentException("API key not found: " + lookupHash);
    }

    try {
      dynamoDbClient.updateItem(UpdateItemRequest.builder()
          .tableName(tableName)
          .key(Map.of("lookupHash", AttributeValue.builder().s(lookupHash).build()))
          .updateExpression("SET #status = :revoked, revokedAt = :revokedAt")
          .expressionAttributeNames(Map.of("#status", "status"))
          .expressionAttributeValues(Map.of(
              ":revoked", AttributeValue.builder().s(ApiKeyRecord.STATUS_REVOKED).build(),
              ":revokedAt", AttributeValue.builder().s(LocalDateTime.now().toString()).build()))
          .build());
    } catch (DynamoDbException e) {
      logger.error("DynamoDB updateItem failed during key revocation: {}", e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }

    String partnerId = existing.item().containsKey("partnerId")
        ? existing.item().get("partnerId").s() : "unknown";
    logger.info("Revoked API key for partner {} (hash: {}...)",
        partnerId, lookupHash.substring(0, Math.min(12, lookupHash.length())));
  }

  /**
   * Rotates an API key: revokes the old key and generates a new one atomically.
   *
   * @param partnerId      the partner whose key is being rotated
   * @param oldLookupHash  unsalted SHA-256 hash of the key to revoke
   * @param createdBy      identifier of the user/system performing the rotation
   * @return the newly generated key
   */
  public GeneratedApiKey rotateApiKey(String partnerId, String oldLookupHash, String createdBy) {
    // Revoke the old key first
    revokeApiKey(oldLookupHash);

    // Generate and store a new key
    GeneratedApiKey newKey = generateApiKey(partnerId, createdBy);

    logger.info("Rotated API key for partner {} (old hash: {}..., new prefix: {})",
        partnerId,
        oldLookupHash.substring(0, Math.min(12, oldLookupHash.length())),
        newKey.record().keyPrefix());

    return newKey;
  }

  /**
   * Lists all API keys for a partner. Raw key values are never returned.
   * <p>
   * Requires a GSI named {@value #PARTNER_ID_INDEX} on the
   * {@code partnerId} attribute.
   *
   * @param partnerId the partner to list keys for
   * @return list of key records (without raw values)
   */
  public List<ApiKeyRecord> listApiKeys(String partnerId) {
    QueryResponse queryResponse;
    try {
      queryResponse = dynamoDbClient.query(QueryRequest.builder()
          .tableName(tableName)
          .indexName(PARTNER_ID_INDEX)
          .keyConditionExpression("partnerId = :pid")
          .expressionAttributeValues(Map.of(
              ":pid", AttributeValue.builder().s(partnerId).build()))
          .build());
    } catch (DynamoDbException e) {
      logger.error("DynamoDB query failed for listApiKeys (partner: {}): {}", partnerId, e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }

    List<ApiKeyRecord> records = new ArrayList<>();
    for (Map<String, AttributeValue> item : queryResponse.items()) {
      records.add(toApiKeyRecord(item));
    }

    logger.debug("Listed {} API keys for partner {}", records.size(), partnerId);
    return records;
  }

  /**
   * Converts a DynamoDB item map to an {@link ApiKeyRecord}.
   */
  private ApiKeyRecord toApiKeyRecord(Map<String, AttributeValue> item) {
    return new ApiKeyRecord(
        getStringAttribute(item, "lookupHash"),
        getStringAttribute(item, "verificationHash"),
        getStringAttribute(item, "salt"),
        getStringAttribute(item, "partnerId"),
        getStringAttribute(item, "status"),
        getStringAttribute(item, "keyPrefix"),
        parseLocalDateTime(getStringAttribute(item, "createdAt")),
        parseLocalDateTime(getStringAttribute(item, "expiresAt")),
        getStringAttribute(item, "createdBy"));
  }

  /**
   * Safely extracts a String attribute from a DynamoDB item map.
   */
  private String getStringAttribute(Map<String, AttributeValue> item, String key) {
    AttributeValue value = item.get(key);
    return (value != null && value.s() != null) ? value.s() : null;
  }

  /**
   * Parses an ISO LocalDateTime string, returning {@code null} for
   * null or blank input.
   */
  private LocalDateTime parseLocalDateTime(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    try {
      return LocalDateTime.parse(value);
    } catch (Exception e) {
      logger.warn("Failed to parse LocalDateTime: {}", value);
      return null;
    }
  }

  /**
   * Computes an unsalted SHA-256 hash of a raw API key.
   * Used for lookup (partition key) only, not for secure verification.
   *
   * @param apiKey the raw API key
   * @return hex-encoded SHA-256 hash
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
   * Computes the salted SHA-256 hash of a raw API key for secure verification.
   * The salt is prepended to the API key before hashing to prevent rainbow table attacks.
   *
   * @param apiKey the raw API key
   * @param salt   the base64-encoded salt
   * @return hex-encoded SHA-256 hash of (salt + apiKey)
   */
  private String hashApiKeyForVerification(String apiKey, String salt) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      // Combine salt and API key before hashing
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
   * Converts a byte array to a hex string.
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
   * Generates a cryptographically secure random salt.
   *
   * @return base64-encoded salt
   */
  private String generateSalt() {
    byte[] saltBytes = new byte[SALT_BYTE_LENGTH];
    secureRandom.nextBytes(saltBytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(saltBytes);
  }

  /**
   * Holds the raw API key value together with the persisted record.
   * The raw key is only available immediately after generation.
   *
   * @param rawApiKey the raw API key value (shown once, never stored)
   * @param record    the persisted key metadata
   */
  public record GeneratedApiKey(String rawApiKey, ApiKeyRecord record) {}
}
