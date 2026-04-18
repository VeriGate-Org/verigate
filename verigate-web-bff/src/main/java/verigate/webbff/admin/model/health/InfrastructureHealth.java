package verigate.webbff.admin.model.health;

import java.util.List;

public record InfrastructureHealth(
    List<DynamoDbTableHealth> dynamoDbTables,
    List<SqsQueueHealth> sqsQueues,
    KinesisStreamHealth kinesisStream,
    BedrockHealth bedrock
) {

  public record DynamoDbTableHealth(
      String tableName,
      String status,
      Long itemCount,
      Long tableSizeBytes,
      long latencyMs,
      String error
  ) {}

  public record SqsQueueHealth(
      String queueName,
      String status,
      Long approximateMessageCount,
      Long dlqMessageCount,
      long latencyMs,
      String error
  ) {}

  public record KinesisStreamHealth(
      String streamName,
      String status,
      Integer shardCount,
      long latencyMs,
      String error
  ) {}

  public record BedrockHealth(
      String region,
      String status,
      long latencyMs,
      String error
  ) {}
}
