/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.idempotency;

import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@code InMemoryCommandStoreDao} class provides an in-memory implementation of the
 * {@link CommandStoreDao} interface. It stores commands in a HashSet for quick access and
 * persistence.
 *
 * <p>This class is useful for testing and development purposes where a full-fledged database might
 * not be necessary. However, it should not be used in production as the data stored in it will be
 * lost once the application is stopped.
 *
 * <p>Note: This class is not thread-safe. If multiple threads access an instance of this class
 * concurrently, it must be synchronized externally.
 */
public final class InMemoryCommandStoreDao implements CommandStoreDao {

  /**
   * A set of command IDs. This set is used to store the IDs of commands that have been processed.
   * It allows for quick lookup to check if a command has already been processed.
   */
  private final Map<UUID, CommandStoreRecord> commands = new ConcurrentHashMap<>();

  private final Boolean checkOrder;

  public InMemoryCommandStoreDao(Boolean checkOrder) {
    this.checkOrder = checkOrder;
  }

  /**
   * Persists a command ID in the store. This method adds the given ID to the set of processed
   * commands.
   *
   * @param id The ID of the command to be persisted.
   */
  @Override
  public void add(final UUID id, String contractId, Integer sequenceNumber) {
    commands.put(id, new CommandStoreRecord(id, contractId, CommandStatus.PENDING, sequenceNumber));
  }

  @Override
  public CommandStatus getStatus(UUID id) {
    var command = commands.get(id);
    return (command != null) ? command.status() : null;
  }

  @Override
  public boolean checkStatusOrAdd(final UUID id, String contractId, Integer sequenceNumber) {
    var previousCommand =
        commands.putIfAbsent(
            id, new CommandStoreRecord(id, contractId, CommandStatus.PENDING, sequenceNumber));

    // Command newly added to the store?
    if (previousCommand == null) {
      return false;
    }

    // If the command already existed, only FAILED ones should be allowed to be retried
    if (CommandStatus.FAILED.equals(previousCommand.status())) {
      add(id, contractId, sequenceNumber);
      return false;
    }

    // Otherwise the command is still pending or already succeeded and shouldn't be re-processed
    return true;
  }

  @Override
  public void setToSucceeded(final UUID id) {
    var record = commands.get(id);
    commands.replace(
        id,
        new CommandStoreRecord(
            id, record.contractId(), CommandStatus.SUCCEEDED, record.sequenceNumber()));
  }

  @Override
  public void setToFailed(final UUID id) {
    var record = commands.get(id);
    commands.replace(
        id,
        new CommandStoreRecord(
            id, record.contractId(), CommandStatus.FAILED, record.sequenceNumber()));
  }

  /**
   * Checks if a command ID exists in the store. This method checks if the given ID is in the set of
   * processed commands.
   *
   * @param id The ID of the command to check.
   * @return {@code true} if the command ID exists in the store, {@code false} otherwise.
   */
  @Override
  public boolean exists(final UUID id) {
    return commands.containsKey(id);
  }

  @Override
  public boolean hasPendingCommandOrOutOfSequence(String contractId, Integer sequenceNumber) {
    boolean hasPendingCommands =
        commands.values().stream()
            .anyMatch(command -> command.status().equals(CommandStatus.PENDING));
    if (hasPendingCommands) {
      return true;
    }

    if (!checkOrder) {
      return false;
    }

    var record =
        commands.values().stream()
            .filter(command -> command.contractId().equals(contractId))
            .max(Comparator.comparingInt(CommandStoreRecord::sequenceNumber));

    return record.isPresent() && record.get().sequenceNumber() + 1 < sequenceNumber;
  }
}
