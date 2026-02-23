/*
 * Arthmatic + Karisani(c) 2024 - 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.cg.infrastructure.lambda.mock;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import infrastructure.persistence.CommandStoreDatabaseRepository;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import verigate.verification.cg.domain.commands.commandstore.VerificationCommandStoreRecord;

/**
 * An in-memory implementation of the QuoteRepository interface. This repository stores quote
 * objects in a HashSet.
 */
public final class InMemoryVerificationCommandStoreRepository
    implements CommandStoreDatabaseRepository<VerificationCommandStoreRecord, UUID> {
  private Set<VerificationCommandStoreRecord> commands;
  private int transactions;

  public InMemoryVerificationCommandStoreRepository() {
    this.commands = new HashSet<>();
    this.transactions = 0;
  }

  /**
   * Gets a quote by its commandId.
   *
   * @param commandId The commandId of the command to get.
   * @return The command with the given id, or null if it does not exist.
   * @throws TransientException If a transient error occurs.
   * @throws PermanentException If a permanent error occurs.
   */
  public VerificationCommandStoreRecord get(UUID commandId)
      throws TransientException, PermanentException {
    return commands == null
        ? null
        : commands.stream()
            .filter(q -> q.getCommandId().equals(commandId))
            .findFirst()
            .orElse(null);
  }

  public int getTransactions() {
    return transactions;
  }

  /**
   * Adds a command to the repository.
   *
   * @param record The quote to add.
   * @throws TransientException If a transient error occurs.
   * @throws PermanentException If a permanent error occurs.
   */
  @Override
  public void add(VerificationCommandStoreRecord record)
      throws TransientException, PermanentException {
    if (commands == null) {
      commands = new HashSet<>();
    }

    // replace the command if it exists
    commands.removeIf(q -> q.getCommandId().equals(record.getCommandId()));

    commands.add(record);
    transactions = getTransactions() + 1;
  }
}
