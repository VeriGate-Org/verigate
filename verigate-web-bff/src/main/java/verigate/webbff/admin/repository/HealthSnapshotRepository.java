package verigate.webbff.admin.repository;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import verigate.webbff.admin.model.health.HealthSnapshotItem;

@Repository
public class HealthSnapshotRepository {

  private static final Logger logger = LoggerFactory.getLogger(HealthSnapshotRepository.class);
  private static final int MAX_BATCH_SIZE = 25;

  private final DynamoDbEnhancedClient enhancedClient;
  private final DynamoDbTable<HealthSnapshotItem> table;

  public HealthSnapshotRepository(
      DynamoDbEnhancedClient enhancedClient,
      @Value("${verigate.healthcheck.snapshot-table-name:health-snapshots}") String tableName) {
    this.enhancedClient = enhancedClient;
    this.table = enhancedClient.table(tableName, TableSchema.fromBean(HealthSnapshotItem.class));
  }

  public void saveAll(List<HealthSnapshotItem> items) {
    try {
      // DynamoDB batch write limit is 25 items
      for (int i = 0; i < items.size(); i += MAX_BATCH_SIZE) {
        List<HealthSnapshotItem> batch = items.subList(i, Math.min(i + MAX_BATCH_SIZE, items.size()));
        WriteBatch.Builder<HealthSnapshotItem> writeBatchBuilder =
            WriteBatch.builder(HealthSnapshotItem.class).mappedTableResource(table);
        batch.forEach(writeBatchBuilder::addPutItem);

        enhancedClient.batchWriteItem(
            BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBatchBuilder.build())
                .build());
      }
      logger.info("Saved {} health snapshot items", items.size());
    } catch (DynamoDbException e) {
      logger.error("Failed to save health snapshots: {}", e.getMessage(), e);
      throw e;
    }
  }

  public List<HealthSnapshotItem> findByServiceId(String serviceId, String from, String to) {
    try {
      QueryConditional queryConditional = QueryConditional.sortBetween(
          Key.builder().partitionValue(serviceId).sortValue(from).build(),
          Key.builder().partitionValue(serviceId).sortValue(to).build());

      QueryEnhancedRequest request = QueryEnhancedRequest.builder()
          .queryConditional(queryConditional)
          .build();

      List<HealthSnapshotItem> results = new ArrayList<>();
      table.query(request).items().forEach(results::add);
      return results;
    } catch (DynamoDbException e) {
      logger.warn("Failed to query snapshots for serviceId {}: {}", serviceId, e.getMessage());
      return List.of();
    }
  }

  public List<HealthSnapshotItem> findByStatus(String status, String from, String to) {
    try {
      DynamoDbIndex<HealthSnapshotItem> index = table.index("status-checkedAt-index");

      QueryConditional queryConditional = QueryConditional.sortBetween(
          Key.builder().partitionValue(status).sortValue(from).build(),
          Key.builder().partitionValue(status).sortValue(to).build());

      QueryEnhancedRequest request = QueryEnhancedRequest.builder()
          .queryConditional(queryConditional)
          .build();

      List<HealthSnapshotItem> results = new ArrayList<>();
      index.query(request).forEach(page -> page.items().forEach(results::add));
      return results;
    } catch (DynamoDbException e) {
      logger.warn("Failed to query snapshots by status {}: {}", status, e.getMessage());
      return List.of();
    }
  }
}
