/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.idempotency;

import java.util.UUID;

/**
 * The {@code CommandStoreDao} interface defines the contract for a data access object (DAO) that
 * manages command persistence in the command store. The command store is a mechanism that ensures
 * idempotency by keeping track of processed commands.
 *
 * <p>Implementations of this interface are responsible for defining how commands are persisted and
 * how their existence is checked in the command store.
 */
public interface CommandStoreDao {

  /**
   * Persists a command ID in the command store. Implementations of this method should ensure that
   * the command ID is stored in such a way that it can be quickly retrieved for existence checks.
   *
   * @param id The ID of the command to be persisted.
   */
  void add(final UUID id, String contractId, Integer sequenceNumber);

  /**
   * Retrieves the status of a command from the command store. Implementations of this method should
   * provide a quick way to retrieve the status of a command with the given ID.
   *
   * @param id The ID of the command to retrieve the status for.
   * @return The status of the command with the given ID.
   */
  CommandStatus getStatus(final UUID id);

  /**
   * Checks the status of a command in the command store. If the command ID does not exist in the
   * command store, the command ID is added to the command store and the status is set to pending.
   *
   * @param id The ID of the command to check.
   * @return {@code true} if the command ID exists in the command store and should not be processed,
   *         {@code false} otherwise (command didn't exist or can be re-processed).
   */
  boolean checkStatusOrAdd(final UUID id, String contractId, Integer sequenceNumber);

  /**
   * Sets the status of a command to succeeded in the command store.
   *
   * @param id The ID of the command to set to succeeded state.
   */
  void setToSucceeded(final UUID id);

  /**
   * Sets the status of a command to failed in the command store.
   *
   * @param id The ID of the command to set to failed.
   */
  void setToFailed(final UUID id);

  /**
   * Checks if a command ID exists in the command store. Implementations of this method should
   * provide a quick way to check if a command with the given ID has already been processed.
   *
   * @param id The ID of the command to check.
   * @return {@code true} if the command ID exists in the command store, {@code false} otherwise.
   */
  boolean exists(final UUID id);

  /**
   * Checks if there is any pending command and if the sequence number is in order. Implementations
   * of this method should ensure that commands are processed in the correct sequence.
   *
   * @param contractId The contract identifier associated with the command, this can be a policy
   *       code, contract number, etc.
   * @param sequenceNumber The sequence number of the command.
   * @return {@code true} if there is any pending command with the given correlation ID and the
   *         sequence number is in order, {@code false} otherwise.
   */
  boolean hasPendingCommandOrOutOfSequence(final String contractId, final Integer sequenceNumber);
}
