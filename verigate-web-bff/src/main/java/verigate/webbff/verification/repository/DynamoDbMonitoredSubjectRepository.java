package verigate.webbff.verification.repository;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import verigate.webbff.config.properties.MonitoringProperties;
import verigate.webbff.verification.repository.model.MonitoredSubjectDataModel;

@Repository
public class DynamoDbMonitoredSubjectRepository {

  private static final Logger logger =
      LoggerFactory.getLogger(DynamoDbMonitoredSubjectRepository.class);
  private static final String PARTNER_STATUS_INDEX = "partner-status-index";

  private final DynamoDbTable<MonitoredSubjectDataModel> table;

  public DynamoDbMonitoredSubjectRepository(
      DynamoDbEnhancedClient dynamoDbEnhancedClient, MonitoringProperties properties) {
    this.table = dynamoDbEnhancedClient.table(
        properties.getSubjectsTableName(),
        TableSchema.fromBean(MonitoredSubjectDataModel.class));
  }

  public void save(MonitoredSubjectDataModel subject) {
    try {
      table.putItem(subject);
    } catch (DynamoDbException e) {
      logger.error("Failed to save monitored subject {}: {}",
          subject.getSubjectId(), e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
  }

  public Optional<MonitoredSubjectDataModel> findById(String subjectId) {
    try {
      MonitoredSubjectDataModel item = table.getItem(
          Key.builder().partitionValue(subjectId).build());
      return Optional.ofNullable(item);
    } catch (DynamoDbException e) {
      logger.error("Failed to get monitored subject {}: {}", subjectId, e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
  }

  public List<MonitoredSubjectDataModel> findByPartnerId(
      String partnerId, String statusPrefix, int limit) {
    try {
      DynamoDbIndex<MonitoredSubjectDataModel> index = table.index(PARTNER_STATUS_INDEX);

      QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
          .limit(limit)
          .scanIndexForward(false);

      if (statusPrefix != null && !statusPrefix.isBlank()) {
        requestBuilder.queryConditional(
            QueryConditional.sortBeginsWith(
                Key.builder()
                    .partitionValue(partnerId)
                    .sortValue(statusPrefix)
                    .build()));
      } else {
        requestBuilder.queryConditional(
            QueryConditional.keyEqualTo(
                Key.builder()
                    .partitionValue(partnerId)
                    .build()));
      }

      return index.query(requestBuilder.build())
          .stream()
          .flatMap(page -> page.items().stream())
          .limit(limit)
          .toList();
    } catch (DynamoDbException e) {
      logger.error("Failed to list monitored subjects for partner {}: {}",
          partnerId, e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
  }

  public void delete(String subjectId) {
    try {
      table.deleteItem(Key.builder().partitionValue(subjectId).build());
    } catch (DynamoDbException e) {
      logger.error("Failed to delete monitored subject {}: {}", subjectId, e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
  }
}
