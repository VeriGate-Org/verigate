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
import verigate.webbff.config.properties.PolicyProperties;
import verigate.webbff.verification.repository.model.PolicyDataModel;

@Repository
public class DynamoDbPolicyRepository {

  private static final Logger logger = LoggerFactory.getLogger(DynamoDbPolicyRepository.class);
  private static final String PARTNER_STATUS_INDEX = "partner-status-index";

  private final DynamoDbTable<PolicyDataModel> table;

  public DynamoDbPolicyRepository(
      DynamoDbEnhancedClient dynamoDbEnhancedClient, PolicyProperties properties) {
    this.table = dynamoDbEnhancedClient.table(
        properties.getTableName(), TableSchema.fromBean(PolicyDataModel.class));
  }

  public void save(PolicyDataModel policy) {
    try {
      table.putItem(policy);
    } catch (DynamoDbException e) {
      logger.error("Failed to save policy {}: {}", policy.getPartnerPolicyId(), e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
  }

  public Optional<PolicyDataModel> findById(String partnerPolicyId) {
    try {
      PolicyDataModel item = table.getItem(
          Key.builder().partitionValue(partnerPolicyId).build());
      return Optional.ofNullable(item);
    } catch (DynamoDbException e) {
      logger.error("Failed to get policy {}: {}", partnerPolicyId, e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
  }

  public List<PolicyDataModel> findByPartnerId(String partnerId) {
    try {
      DynamoDbIndex<PolicyDataModel> index = table.index(PARTNER_STATUS_INDEX);
      return index.query(QueryEnhancedRequest.builder()
              .queryConditional(QueryConditional.keyEqualTo(
                  Key.builder().partitionValue(partnerId).build()))
              .build())
          .stream()
          .flatMap(page -> page.items().stream())
          .toList();
    } catch (DynamoDbException e) {
      logger.error("Failed to list policies for partner {}: {}", partnerId, e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
  }

  public void delete(String partnerPolicyId) {
    try {
      table.deleteItem(Key.builder().partitionValue(partnerPolicyId).build());
    } catch (DynamoDbException e) {
      logger.error("Failed to delete policy {}: {}", partnerPolicyId, e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
  }
}
