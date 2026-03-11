/*
 * VeriGate (c) 2025. All    rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.cg.infrastructure.commandstore;

import com.google.inject.Inject;
import domain.commands.BaseCommand;
import domain.exceptions.DeferredException;
import domain.exceptions.InvariantViolationException;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import infrastructure.commands.commandstore.AggregateCommandStoreDao;
import infrastructure.persistence.CommandStoreDatabaseRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.verification.cg.domain.commands.commandstore.VerificationCommandStatusEnum;
import verigate.verification.cg.domain.commands.commandstore.VerificationCommandStoreRecord;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * A concrete implementation of the AggregateCommandStoreDao interface for the policy domain.
 */
public class VerificationCommandStoreDao
    implements AggregateCommandStoreDao<
        BaseCommand, Map<String, String>, VerificationCommandStoreRecord> {
  private final CommandStoreDatabaseRepository<VerificationCommandStoreRecord, UUID> repo;
  private static final Logger logger =
      LoggerFactory.getLogger(VerificationCommandStoreDao.class.getSimpleName());

  @Inject
  public VerificationCommandStoreDao(CommandStoreDatabaseRepository repo) {
    this.repo = repo;
  }

  @Override
  public void initialize(BaseCommand command) {
    String partnerId = extractPartnerId(command);
    String createdAt = formatCreatedAt(command);
    var record =
        new VerificationCommandStoreRecord(
            command.getId(),
            command.getClass().getSimpleName(),
            VerificationCommandStatusEnum.PENDING,
            null,
            null,
            partnerId,
            createdAt);
    repo.add(record);
    return;
  }

  @Override
  public void setSuccess(BaseCommand command, Map<String, String> handlerReturn) {
    String partnerId = extractPartnerId(command);
    String createdAt = formatCreatedAt(command);
    repo.add(
        new VerificationCommandStoreRecord(
            command.getId(),
            command.getClass().getSimpleName(),
            VerificationCommandStatusEnum.COMPLETED,
            null,
            handlerReturn,
            partnerId,
            createdAt));
  }

  @Override
  public void setDeferredFailed(
      BaseCommand command, DeferredException e, Map<String, String> handlerReturn) {
    String partnerId = extractPartnerId(command);
    String createdAt = formatCreatedAt(command);
    repo.add(
        new VerificationCommandStoreRecord(
            command.getId(),
            command.getClass().getSimpleName(),
            VerificationCommandStatusEnum.TRANSIENT_ERROR,
            null,
            handlerReturn,
            partnerId,
            createdAt));
  }

  @Override
  public void setTransientFailed(
      BaseCommand command, TransientException e, Map<String, String> handlerReturn) {
    String partnerId = extractPartnerId(command);
    String createdAt = formatCreatedAt(command);
    repo.add(
        new VerificationCommandStoreRecord(
            command.getId(),
            command.getClass().getSimpleName(),
            VerificationCommandStatusEnum.TRANSIENT_ERROR,
            null,
            handlerReturn,
            partnerId,
            createdAt));
  }

  @Override
  public void setInvariantFailed(
      BaseCommand command, InvariantViolationException e, Map<String, String> handlerReturn) {
    String partnerId = extractPartnerId(command);
    String createdAt = formatCreatedAt(command);
    repo.add(
        new VerificationCommandStoreRecord(
            command.getId(),
            command.getClass().getSimpleName(),
            VerificationCommandStatusEnum.INVARIANT_FAILURE,
            getInvariantErrors(e),
            handlerReturn,
            partnerId,
            createdAt));
  }

  @Override
  public void setFailed(
      BaseCommand command, PermanentException e, Map<String, String> handlerReturn) {
    String partnerId = extractPartnerId(command);
    String createdAt = formatCreatedAt(command);
    repo.add(
        new VerificationCommandStoreRecord(
            command.getId(),
            command.getClass().getSimpleName(),
            VerificationCommandStatusEnum.PERMANENT_FAILURE,
            null,
            handlerReturn,
            partnerId,
            createdAt));
  }

  private String extractPartnerId(BaseCommand command) {
    if (command instanceof VerifyPartyCommand verifyPartyCommand) {
      return verifyPartyCommand.getPartnerId();
    }
    return null;
  }

  private String formatCreatedAt(BaseCommand command) {
    Instant createdDate = command.getCreatedDate();
    return createdDate != null ? createdDate.toString() : Instant.now().toString();
  }

  private List<String> getInvariantErrors(Exception e) {
    try {
      var errorsSet = ((InvariantViolationException) e).getErrorMessages();
      return errorsSet.stream().map(err -> err.getError().getCode()).toList();

    } catch (Exception ex) {
      logger.info(
          "Error getting invariant errors: "
              + ex.getMessage()
              + " from exception: "
              + e.toString());
      return null;
    }
  }
}
