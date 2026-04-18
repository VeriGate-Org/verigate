package verigate.webbff.admin.service.health;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamRequest;
import software.amazon.awssdk.services.kinesis.model.StreamDescription;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import verigate.webbff.admin.model.health.InfrastructureHealth;

@Component
public class AwsInfrastructureProber {

  private static final Logger logger = LoggerFactory.getLogger(AwsInfrastructureProber.class);

  private final DynamoDbClient dynamoDbClient;
  private final SqsClient sqsClient;
  private final KinesisClient kinesisClient;

  private final List<String> tableNames;
  private final List<QueuePair> queuePairs;
  private final String kinesisStreamName;

  public AwsInfrastructureProber(
      DynamoDbClient dynamoDbClient,
      SqsClient sqsClient,
      KinesisClient kinesisClient,
      @Value("${verigate.verification.command-store.table-name:verification-command-store}") String commandStoreTable,
      @Value("${verigate.auth.api-keys-table:verigate-api-keys}") String apiKeysTable,
      @Value("${verigate.partner.table-name:verigate-partner-table}") String partnerTable,
      @Value("${verigate.cases.table-name:cases}") String casesTable,
      @Value("${verigate.policy.table-name:policies}") String policiesTable,
      @Value("${verigate.monitoring.subjects-table-name:monitored-subjects}") String monitoringSubjectsTable,
      @Value("${verigate.verification.queue-name:verify-party}") String verificationQueueName,
      @Value("${verigate.partner.create-queue-name:partner-create}") String partnerCreateQueueName,
      @Value("${verigate.healthcheck.kinesis-stream-name:}") String kinesisStreamName) {
    this.dynamoDbClient = dynamoDbClient;
    this.sqsClient = sqsClient;
    this.kinesisClient = kinesisClient;
    this.tableNames = List.of(
        commandStoreTable, apiKeysTable, partnerTable,
        casesTable, policiesTable, monitoringSubjectsTable
    );
    this.queuePairs = List.of(
        new QueuePair(verificationQueueName, verificationQueueName + "-dlq"),
        new QueuePair(partnerCreateQueueName, partnerCreateQueueName + "-dlq")
    );
    this.kinesisStreamName = kinesisStreamName;
  }

  public List<InfrastructureHealth.DynamoDbTableHealth> probeDynamoDb() {
    List<InfrastructureHealth.DynamoDbTableHealth> results = new ArrayList<>();
    for (String tableName : tableNames) {
      results.add(probeTable(tableName));
    }
    return results;
  }

  private InfrastructureHealth.DynamoDbTableHealth probeTable(String tableName) {
    long start = System.currentTimeMillis();
    try {
      DescribeTableResponse response = dynamoDbClient.describeTable(
          DescribeTableRequest.builder().tableName(tableName).build());
      long latency = System.currentTimeMillis() - start;
      TableDescription table = response.table();
      return new InfrastructureHealth.DynamoDbTableHealth(
          tableName,
          table.tableStatusAsString(),
          table.itemCount(),
          table.tableSizeBytes(),
          latency,
          null
      );
    } catch (Exception e) {
      long latency = System.currentTimeMillis() - start;
      logger.warn("DynamoDB probe failed for table {}: {}", tableName, e.getMessage());
      return new InfrastructureHealth.DynamoDbTableHealth(
          tableName, "ERROR", null, null, latency, e.getMessage()
      );
    }
  }

  public List<InfrastructureHealth.SqsQueueHealth> probeSqs() {
    List<InfrastructureHealth.SqsQueueHealth> results = new ArrayList<>();
    for (QueuePair pair : queuePairs) {
      results.add(probeQueue(pair));
    }
    return results;
  }

  private InfrastructureHealth.SqsQueueHealth probeQueue(QueuePair pair) {
    long start = System.currentTimeMillis();
    try {
      String queueUrl = sqsClient.getQueueUrl(
          GetQueueUrlRequest.builder().queueName(pair.mainQueue).build()).queueUrl();

      var attrs = sqsClient.getQueueAttributes(
          GetQueueAttributesRequest.builder()
              .queueUrl(queueUrl)
              .attributeNames(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES)
              .build()
      ).attributes();

      Long messageCount = Long.parseLong(
          attrs.getOrDefault(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES, "0"));

      Long dlqCount = null;
      try {
        String dlqUrl = sqsClient.getQueueUrl(
            GetQueueUrlRequest.builder().queueName(pair.dlqQueue).build()).queueUrl();
        var dlqAttrs = sqsClient.getQueueAttributes(
            GetQueueAttributesRequest.builder()
                .queueUrl(dlqUrl)
                .attributeNames(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES)
                .build()
        ).attributes();
        dlqCount = Long.parseLong(
            dlqAttrs.getOrDefault(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES, "0"));
      } catch (Exception e) {
        logger.debug("DLQ probe skipped for {}: {}", pair.dlqQueue, e.getMessage());
      }

      long latency = System.currentTimeMillis() - start;
      return new InfrastructureHealth.SqsQueueHealth(
          pair.mainQueue, "HEALTHY", messageCount, dlqCount, latency, null
      );
    } catch (Exception e) {
      long latency = System.currentTimeMillis() - start;
      logger.warn("SQS probe failed for queue {}: {}", pair.mainQueue, e.getMessage());
      return new InfrastructureHealth.SqsQueueHealth(
          pair.mainQueue, "ERROR", null, null, latency, e.getMessage()
      );
    }
  }

  public InfrastructureHealth.KinesisStreamHealth probeKinesis() {
    if (kinesisStreamName == null || kinesisStreamName.isBlank()) {
      return new InfrastructureHealth.KinesisStreamHealth(
          "", "UNCONFIGURED", null, 0, null
      );
    }
    long start = System.currentTimeMillis();
    try {
      StreamDescription stream = kinesisClient.describeStream(
          DescribeStreamRequest.builder().streamName(kinesisStreamName).build()
      ).streamDescription();
      long latency = System.currentTimeMillis() - start;
      return new InfrastructureHealth.KinesisStreamHealth(
          kinesisStreamName,
          stream.streamStatusAsString(),
          stream.shards().size(),
          latency,
          null
      );
    } catch (Exception e) {
      long latency = System.currentTimeMillis() - start;
      logger.warn("Kinesis probe failed for stream {}: {}", kinesisStreamName, e.getMessage());
      return new InfrastructureHealth.KinesisStreamHealth(
          kinesisStreamName, "ERROR", null, latency, e.getMessage()
      );
    }
  }

  private record QueuePair(String mainQueue, String dlqQueue) {}
}
