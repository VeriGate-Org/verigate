package verigate.webbff.admin.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import verigate.webbff.admin.model.PartnerResponse;
import verigate.webbff.admin.model.PartnerStatus;

@Repository
public class PartnerRepository {

  private static final Logger logger =
      LoggerFactory.getLogger(PartnerRepository.class);

  private final DynamoDbClient dynamoDbClient;
  private final String tableName;

  public PartnerRepository(
      DynamoDbClient dynamoDbClient,
      @Value("${verigate.partner.table-name}") String tableName) {
    this.dynamoDbClient = dynamoDbClient;
    this.tableName = tableName;
  }

  public Optional<PartnerResponse> findById(String partnerId) {
    GetItemResponse response = dynamoDbClient.getItem(
        GetItemRequest.builder()
            .tableName(tableName)
            .key(Map.of("partnerId",
                AttributeValue.builder().s(partnerId).build()))
            .build());

    if (!response.hasItem() || response.item().isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(toPartnerResponse(response.item()));
  }

  public List<PartnerResponse> findAll() {
    List<PartnerResponse> partners = new ArrayList<>();
    Map<String, AttributeValue> lastKey = null;

    do {
      ScanRequest.Builder scanBuilder = ScanRequest.builder()
          .tableName(tableName);
      if (lastKey != null) {
        scanBuilder.exclusiveStartKey(lastKey);
      }
      ScanResponse response = dynamoDbClient.scan(scanBuilder.build());
      for (Map<String, AttributeValue> item : response.items()) {
        partners.add(toPartnerResponse(item));
      }
      lastKey = response.lastEvaluatedKey();
    } while (lastKey != null && !lastKey.isEmpty());

    logger.debug("Listed {} partners", partners.size());
    return partners;
  }

  public void updateStatus(String partnerId, PartnerStatus status) {
    dynamoDbClient.updateItem(UpdateItemRequest.builder()
        .tableName(tableName)
        .key(Map.of("partnerId",
            AttributeValue.builder().s(partnerId).build()))
        .updateExpression("SET partnerStatus = :status")
        .expressionAttributeValues(Map.of(
            ":status",
            AttributeValue.builder().s(status.name()).build()))
        .conditionExpression("attribute_exists(partnerId)")
        .build());
    logger.info("Updated partner {} status to {}", partnerId, status);
  }

  public Optional<String> getPartnerStatus(String partnerId) {
    GetItemResponse response = dynamoDbClient.getItem(
        GetItemRequest.builder()
            .tableName(tableName)
            .key(Map.of("partnerId",
                AttributeValue.builder().s(partnerId).build()))
            .projectionExpression("partnerStatus")
            .build());

    if (!response.hasItem() || response.item().isEmpty()) {
      return Optional.empty();
    }
    AttributeValue statusAttr = response.item().get("partnerStatus");
    if (statusAttr == null || statusAttr.s() == null) {
      return Optional.empty();
    }
    return Optional.of(statusAttr.s());
  }

  private PartnerResponse toPartnerResponse(
      Map<String, AttributeValue> item) {
    return new PartnerResponse(
        getStr(item, "partnerId"),
        getStr(item, "name"),
        getStr(item, "contactEmail"),
        getStr(item, "billingPlan"),
        getStr(item, "partnerStatus"),
        getStr(item, "createdAt"));
  }

  private String getStr(
      Map<String, AttributeValue> item, String key) {
    AttributeValue val = item.get(key);
    return (val != null && val.s() != null) ? val.s() : null;
  }
}
