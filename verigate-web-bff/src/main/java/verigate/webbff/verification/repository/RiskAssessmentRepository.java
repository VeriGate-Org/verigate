package verigate.webbff.verification.repository;

import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import verigate.webbff.config.properties.RiskAssessmentProperties;
import verigate.webbff.verification.repository.model.RiskAssessmentItem;

@Repository
public class RiskAssessmentRepository {

  private static final Logger logger = LoggerFactory.getLogger(RiskAssessmentRepository.class);

  private final DynamoDbTable<RiskAssessmentItem> table;

  public RiskAssessmentRepository(
      DynamoDbEnhancedClient dynamoDbEnhancedClient, RiskAssessmentProperties properties) {
    this.table =
        dynamoDbEnhancedClient.table(
            properties.getTableName(), TableSchema.fromBean(RiskAssessmentItem.class));
  }

  @Cacheable(value = "risk-assessments", key = "#verificationId")
  public Optional<RiskAssessmentItem> findByVerificationId(UUID verificationId) {
    RiskAssessmentItem item;
    try {
      item = table.getItem(Key.builder().partitionValue(verificationId.toString()).build());
    } catch (DynamoDbException e) {
      logger.error(
          "DynamoDB getItem failed for risk assessment verificationId {}: {}",
          verificationId,
          e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
    return Optional.ofNullable(item);
  }
}
