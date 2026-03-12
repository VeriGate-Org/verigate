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
import verigate.webbff.config.properties.CaseProperties;
import verigate.webbff.verification.repository.model.CaseDataModel;

@Repository
public class DynamoDbCaseRepository {

  private static final Logger logger = LoggerFactory.getLogger(DynamoDbCaseRepository.class);
  private static final String PARTNER_STATUS_INDEX = "partner-status-index";

  private final DynamoDbTable<CaseDataModel> table;

  public DynamoDbCaseRepository(
      DynamoDbEnhancedClient dynamoDbEnhancedClient, CaseProperties properties) {
    this.table = dynamoDbEnhancedClient.table(
        properties.getTableName(), TableSchema.fromBean(CaseDataModel.class));
  }

  public void save(CaseDataModel caseItem) {
    try {
      table.putItem(caseItem);
    } catch (DynamoDbException e) {
      logger.error("Failed to save case {}: {}", caseItem.getCaseId(), e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
  }

  public Optional<CaseDataModel> findById(String caseId) {
    try {
      CaseDataModel item = table.getItem(
          Key.builder().partitionValue(caseId).build());
      return Optional.ofNullable(item);
    } catch (DynamoDbException e) {
      logger.error("Failed to get case {}: {}", caseId, e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
  }

  public List<CaseDataModel> findByPartnerId(String partnerId, String statusPrefix, int limit) {
    try {
      DynamoDbIndex<CaseDataModel> index = table.index(PARTNER_STATUS_INDEX);

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
      logger.error("Failed to list cases for partner {}: {}", partnerId, e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
  }
}
