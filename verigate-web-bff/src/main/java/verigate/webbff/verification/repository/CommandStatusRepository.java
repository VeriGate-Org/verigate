package verigate.webbff.verification.repository;

import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import verigate.webbff.config.properties.CommandStoreProperties;
import verigate.webbff.verification.repository.model.VerificationCommandStoreItem;

@Repository
public class CommandStatusRepository {

  private static final Logger logger = LoggerFactory.getLogger(CommandStatusRepository.class);

  private final DynamoDbTable<VerificationCommandStoreItem> table;

  public CommandStatusRepository(
      DynamoDbEnhancedClient dynamoDbEnhancedClient, CommandStoreProperties properties) {
    this.table =
        dynamoDbEnhancedClient.table(
            properties.getTableName(), TableSchema.fromBean(VerificationCommandStoreItem.class));
  }

  public Optional<VerificationCommandStoreItem> findById(UUID commandId) {
    VerificationCommandStoreItem item;
    try {
      item = table.getItem(Key.builder().partitionValue(commandId.toString()).build());
    } catch (DynamoDbException e) {
      logger.error("DynamoDB getItem failed for commandId {}: {}", commandId, e.getMessage());
      throw new RuntimeException("Service temporarily unavailable", e);
    }
    return Optional.ofNullable(item);
  }
}
