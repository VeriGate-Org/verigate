package verigate.adapter.dharesponse.infrastructure.persistence;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import verigate.adapter.dharesponse.application.handlers.DefaultProcessDhaResponseCommandHandler;

/** Adapts DHA response command store operations to DynamoDB. */
public class DhaResponseCommandStoreAdapter
    implements DefaultProcessDhaResponseCommandHandler.CommandStoreAdapter {

  private static final Logger logger =
      LoggerFactory.getLogger(DhaResponseCommandStoreAdapter.class);

  private final DynamoDbClient dynamoDbClient;
  private final String tableName;

  public DhaResponseCommandStoreAdapter(
      DynamoDbClient dynamoDbClient, String tableName) {
    this.dynamoDbClient = dynamoDbClient;
    this.tableName = tableName;
  }

  @Override
  public DefaultProcessDhaResponseCommandHandler.CommandRecord findById(
      String commandId) {
    GetItemResponse response = dynamoDbClient.getItem(
        GetItemRequest.builder()
            .tableName(tableName)
            .key(Map.of("commandId",
                AttributeValue.builder().s(commandId).build()))
            .build());

    if (!response.hasItem() || response.item().isEmpty()) {
      return null;
    }

    Map<String, AttributeValue> item = response.item();
    String partnerId = item.containsKey("partnerId")
        ? item.get("partnerId").s() : "";
    String status = item.containsKey("status")
        ? item.get("status").s() : "";

    Map<String, String> auxiliaryData = new HashMap<>();
    if (item.containsKey("auxiliaryData")
        && item.get("auxiliaryData").m() != null) {
      item.get("auxiliaryData").m().forEach((k, v) -> {
        if (v.s() != null) {
          auxiliaryData.put(k, v.s());
        }
      });
    }

    return new DefaultProcessDhaResponseCommandHandler.CommandRecord(
        commandId, partnerId, status, auxiliaryData);
  }

  @Override
  public void updateStatus(
      String commandId, String status,
      Map<String, String> auxiliaryData) {
    Map<String, AttributeValue> auxMap = new HashMap<>();
    auxiliaryData.forEach((k, v) ->
        auxMap.put(k, AttributeValue.builder().s(v).build()));

    Map<String, AttributeValue> expressionValues = new HashMap<>();
    expressionValues.put(":status",
        AttributeValue.builder().s(status).build());
    expressionValues.put(":auxData",
        AttributeValue.builder().m(auxMap).build());

    dynamoDbClient.updateItem(UpdateItemRequest.builder()
        .tableName(tableName)
        .key(Map.of("commandId",
            AttributeValue.builder().s(commandId).build()))
        .updateExpression(
            "SET #s = :status, auxiliaryData = :auxData")
        .expressionAttributeNames(Map.of("#s", "status"))
        .expressionAttributeValues(expressionValues)
        .build());

    logger.info("Updated command store: commandId={}, status={}",
        commandId, status);
  }
}
