/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.cg.infrastructure.persistence.commandstore;

import com.google.inject.Inject;
import crosscutting.environment.Environment;
import domain.exceptions.NotFoundException;
import domain.exceptions.PermanentException;
import domain.exceptions.StringExceptionBuilder;
import domain.exceptions.TransientException;
import infrastructure.functions.lambda.serializers.internal.DefaultInternalTransportJsonSerializer;
import infrastructure.persistence.CommandStoreDatabaseRepository;
import infrastructure.persistence.dynamodb.DynamoDbBootstrap;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughputExceededException;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import verigate.verification.cg.domain.commands.commandstore.VerificationCommandStoreRecord;
import verigate.verification.cg.infrastructure.constants.EnvironmentConstants;
import verigate.verification.cg.infrastructure.persistence.commandstore.datamodels.VerificationCommandStoreDataModel;

/**
 * This class is the repository for command store DynamoDB table. It contains functionality for
 * adding and retrieving commands from the table.
 */
public final class VerificationCommandStoreDynamoDbRepository
    extends DynamoDbBootstrap<VerificationCommandStoreDataModel>
    implements CommandStoreDatabaseRepository<VerificationCommandStoreRecord, UUID> {

  private static final Logger logger =
      LoggerFactory.getLogger(VerificationCommandStoreDynamoDbRepository.class);
  private DefaultInternalTransportJsonSerializer jsonSerializer;

  @Inject
  public VerificationCommandStoreDynamoDbRepository(
      Environment environment, DefaultInternalTransportJsonSerializer jsonSerializer) {
    super(environment.get(EnvironmentConstants.COMMAND_STORE_DB));
    this.jsonSerializer = jsonSerializer;
  }

  /**
   * Adds a new command to the repository.
   *
   * @param record the record to be added
   * @throws TransientException if the resource is not found - must not be retried
   * @throws DatabaseConnectionException if there is a connection issue - must be retried
   * @throws PermanentException if the conditional check fails - must not be retried
   * @throws DatabaseMappingException if there is a mapping issue - must not be retried
   */
  @Override
  public void add(VerificationCommandStoreRecord record)
      throws TransientException, PermanentException {
    if (record == null || record.getCommandId() == null) {
      throw new PermanentException(
          StringExceptionBuilder.builder().withDetail("Record and its id cannot be null").build());
    }

    try {

      var datamodel = new VerificationCommandStoreDataModel();

      datamodel.setCommandId(record.getCommandId().toString());
      datamodel.setCommandName(record.getCommandName());
      datamodel.setStatus(record.getStatus());
      datamodel.setErrorDetails(record.getErrorDetails());
      datamodel.setAuxiliaryData(record.getAuxiliaryData());

      logger.info("Persisting record to command store: " + jsonSerializer.serialize(datamodel));

      myTable.putItem(datamodel);

    } catch (IllegalArgumentException e) {
      throw new PermanentException(
          StringExceptionBuilder.builder().withDetail(e.getMessage()).build());
    } catch (ProvisionedThroughputExceededException e) {
      throw new TransientException(
          StringExceptionBuilder.builder().withDetail(e.getMessage()).build());
    } catch (final ResourceNotFoundException e) {
      throw new TransientException(e.getMessage());
    } catch (final ConditionalCheckFailedException e) {
      throw new PermanentException(
          StringExceptionBuilder.builder()
              .withDetail(StringExceptionBuilder.builder().withDetail(e.getMessage()).build())
              .build());
    } catch (final AwsServiceException e) {
      throw new TransientException(
          StringExceptionBuilder.builder().withDetail(e.getMessage()).build());
    } catch (final Exception e) {
      throw new TransientException(
          StringExceptionBuilder.builder().withDetail(e.getMessage()).build());
    }
  }

  /**
   * Retrieves a command store record from the DynamoDB table.
   *
   * @param id The id of the record to retrieve.
   * @return The retrieved record.
   * @throws IllegalArgumentException if id is null.
   */
  @Override
  public VerificationCommandStoreRecord get(UUID id) throws NotFoundException, PermanentException {
    if (id == null) {
      throw new PermanentException(
          StringExceptionBuilder.builder().withDetail("id cannot be null").build());
    }

    VerificationCommandStoreDataModel dataModel =
        myTable.getItem(Key.builder().partitionValue(id.toString()).build());

    if (dataModel == null) {
      return null;
    }

    var commandStore =
        new VerificationCommandStoreRecord(
            UUID.fromString(dataModel.getCommandId()),
            dataModel.getCommandName(),
            dataModel.getStatus(),
            dataModel.getErrorDetails(),
            dataModel.getAuxiliaryData());

    return commandStore;
  }

  @Override
  protected TableSchema<VerificationCommandStoreDataModel> getTableSchema() {
    return TableSchema.fromBean(VerificationCommandStoreDataModel.class);
  }
}
