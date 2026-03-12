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
import verigate.webbff.config.properties.RiskScoringConfigProperties;
import verigate.webbff.verification.repository.model.RiskScoringConfigDataModel;

@Repository
public class DynamoDbRiskConfigRepository {

  private static final Logger logger = LoggerFactory.getLogger(DynamoDbRiskConfigRepository.class);

  private final DynamoDbTable<RiskScoringConfigDataModel> table;

  public DynamoDbRiskConfigRepository(
      DynamoDbEnhancedClient dynamoDbEnhancedClient, RiskScoringConfigProperties properties) {
    this.table = dynamoDbEnhancedClient.table(
        properties.getTableName(), TableSchema.fromBean(RiskScoringConfigDataModel.class));
  }

  public Optional<RiskScoringConfigDataModel> findByPartnerId(String partnerId) {
    try {
      RiskScoringConfigDataModel item = table.getItem(
          Key.builder().partitionValue(partnerId).build());
      return Optional.ofNullable(item);
    } catch (DynamoDbException e) {
      logger.error("Failed to get risk config for partner {}: {}", partnerId, e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
  }

  public void save(RiskScoringConfigDataModel config) {
    try {
      table.putItem(config);
    } catch (DynamoDbException e) {
      logger.error("Failed to save risk config for partner {}: {}",
          config.getPartnerId(), e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
  }
}
