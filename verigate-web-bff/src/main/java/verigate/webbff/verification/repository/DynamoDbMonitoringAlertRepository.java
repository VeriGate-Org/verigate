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
import verigate.webbff.verification.repository.model.MonitoringAlertDataModel;

@Repository
public class DynamoDbMonitoringAlertRepository {

  private static final Logger logger =
      LoggerFactory.getLogger(DynamoDbMonitoringAlertRepository.class);
  private static final String PARTNER_SUBJECT_INDEX = "partner-subject-index";

  private final DynamoDbTable<MonitoringAlertDataModel> table;

  public DynamoDbMonitoringAlertRepository(
      DynamoDbEnhancedClient dynamoDbEnhancedClient, MonitoringProperties properties) {
    this.table = dynamoDbEnhancedClient.table(
        properties.getAlertsTableName(),
        TableSchema.fromBean(MonitoringAlertDataModel.class));
  }

  public void save(MonitoringAlertDataModel alert) {
    try {
      table.putItem(alert);
    } catch (DynamoDbException e) {
      logger.error("Failed to save monitoring alert {}: {}",
          alert.getAlertId(), e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
  }

  public Optional<MonitoringAlertDataModel> findById(String alertId) {
    try {
      MonitoringAlertDataModel item = table.getItem(
          Key.builder().partitionValue(alertId).build());
      return Optional.ofNullable(item);
    } catch (DynamoDbException e) {
      logger.error("Failed to get monitoring alert {}: {}", alertId, e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
  }

  public List<MonitoringAlertDataModel> findByPartnerId(
      String partnerId, String subjectIdPrefix, int limit) {
    try {
      DynamoDbIndex<MonitoringAlertDataModel> index = table.index(PARTNER_SUBJECT_INDEX);

      QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
          .limit(limit)
          .scanIndexForward(false);

      if (subjectIdPrefix != null && !subjectIdPrefix.isBlank()) {
        requestBuilder.queryConditional(
            QueryConditional.sortBeginsWith(
                Key.builder()
                    .partitionValue(partnerId)
                    .sortValue(subjectIdPrefix)
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
      logger.error("Failed to list monitoring alerts for partner {}: {}",
          partnerId, e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
  }
}
