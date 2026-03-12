/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.dha.infrastructure.vault;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import verigate.adapter.dha.domain.models.IdentityVerificationRequest;
import verigate.adapter.dha.domain.models.IdentityVerificationResponse;
import verigate.adapter.dha.domain.models.VerifiedIdentity;
import verigate.adapter.dha.domain.services.IdentityVaultService;

/**
 * DynamoDB-backed identity vault adapter. Stores and retrieves cached DHA
 * verification results using SHA-256 hashed ID numbers as the partition key.
 */
public class DynamoDbIdentityVaultAdapter implements IdentityVaultService {

  private static final Logger logger = LoggerFactory.getLogger(DynamoDbIdentityVaultAdapter.class);

  private final DynamoDbClient dynamoDbClient;
  private final String tableName;
  private final Duration freshnessDuration;

  public DynamoDbIdentityVaultAdapter(
      DynamoDbClient dynamoDbClient, String tableName, int freshnessDays) {
    this.dynamoDbClient = dynamoDbClient;
    this.tableName = tableName;
    this.freshnessDuration = Duration.ofDays(freshnessDays);
  }

  @Override
  public Optional<VerifiedIdentity> findVerifiedIdentity(String idNumber, String partnerId) {
    String hash = sha256(idNumber);

    try {
      Map<String, AttributeValue> key = Map.of(
          "identityHash", AttributeValue.fromS(hash),
          "partnerId", AttributeValue.fromS(partnerId));

      var response = dynamoDbClient.getItem(
          GetItemRequest.builder().tableName(tableName).key(key).build());

      if (!response.hasItem() || response.item().isEmpty()) {
        return Optional.empty();
      }

      var item = response.item();
      var verified = new VerifiedIdentity(
          item.get("identityHash").s(),
          item.get("partnerId").s(),
          getStringOrNull(item, "verificationStatus"),
          getStringOrNull(item, "citizenshipStatus"),
          getStringOrNull(item, "vitalStatus"),
          getStringOrNull(item, "matchDetails"),
          item.containsKey("verifiedAt") ? Instant.parse(item.get("verifiedAt").s()) : null,
          item.containsKey("expiresAt") ? Long.parseLong(item.get("expiresAt").n()) : 0);

      if (!verified.isFreshEnough(freshnessDuration)) {
        logger.info("Vault entry expired for identity hash: ...{}", hash.substring(hash.length() - 8));
        return Optional.empty();
      }

      return Optional.of(verified);
    } catch (Exception e) {
      logger.warn("Failed to read from identity vault: {}", e.getMessage());
      return Optional.empty();
    }
  }

  @Override
  public void storeVerifiedIdentity(
      IdentityVerificationRequest request,
      IdentityVerificationResponse response,
      String partnerId,
      String verificationId) {

    String hash = sha256(request.idNumber());
    Instant now = Instant.now();
    long expiresAt = now.plus(freshnessDuration).getEpochSecond();

    Map<String, AttributeValue> item = new HashMap<>();
    item.put("identityHash", AttributeValue.fromS(hash));
    item.put("partnerId", AttributeValue.fromS(partnerId));
    item.put("verificationStatus", AttributeValue.fromS(response.status().toString()));
    item.put("citizenshipStatus", AttributeValue.fromS(response.citizenshipStatus().toString()));
    item.put("vitalStatus", AttributeValue.fromS(response.vitalStatus().toString()));
    if (response.matchDetails() != null) {
      item.put("matchDetails", AttributeValue.fromS(response.matchDetails()));
    }
    item.put("verifiedAt", AttributeValue.fromS(now.toString()));
    item.put("expiresAt", AttributeValue.fromN(String.valueOf(expiresAt)));
    item.put("verificationId", AttributeValue.fromS(verificationId));

    try {
      dynamoDbClient.putItem(
          PutItemRequest.builder().tableName(tableName).item(item).build());
      logger.info("Stored identity vault entry for hash: ...{}", hash.substring(hash.length() - 8));
    } catch (Exception e) {
      logger.warn("Failed to store in identity vault: {}", e.getMessage());
    }
  }

  private static String sha256(String input) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 not available", e);
    }
  }

  private static String getStringOrNull(Map<String, AttributeValue> item, String key) {
    return item.containsKey(key) ? item.get(key).s() : null;
  }
}
