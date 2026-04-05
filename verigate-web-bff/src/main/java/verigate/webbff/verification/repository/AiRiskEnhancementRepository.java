package verigate.webbff.verification.repository;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import verigate.webbff.config.properties.AiRiskEnhancementProperties;
import verigate.webbff.verification.repository.model.AiRiskEnhancementItem;

@Repository
public class AiRiskEnhancementRepository {

  private static final Logger logger = LoggerFactory.getLogger(AiRiskEnhancementRepository.class);

  private final DynamoDbTable<AiRiskEnhancementItem> table;

  public AiRiskEnhancementRepository(
      DynamoDbEnhancedClient dynamoDbEnhancedClient, AiRiskEnhancementProperties properties) {
    this.table =
        dynamoDbEnhancedClient.table(
            properties.getTableName(), TableSchema.fromBean(AiRiskEnhancementItem.class));
  }

  public Optional<AiRiskEnhancementItem> findByWorkflowId(String workflowId) {
    try {
      AiRiskEnhancementItem item =
          table.getItem(Key.builder().partitionValue(workflowId).build());
      return Optional.ofNullable(item);
    } catch (DynamoDbException e) {
      logger.warn("Failed to fetch AI risk enhancement for workflowId {}: {}",
          workflowId, e.getMessage());
      return Optional.empty();
    }
  }
}
