/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain;

import static org.junit.jupiter.api.Assertions.*;

import crosscutting.patterns.Memento;
import crosscutting.patterns.MementoFactory;
import domain.commands.BaseCommand;
import domain.invariants.SpecificationResult;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

final class AggregateRootTest {

  static final class MockAggregate extends AggregateRoot<UUID, MockAggregate> {

    private boolean executed = false;

    /**
     * Constructs a new instance of the AggregateRoot class.
     *
     * @param id The identifier of the aggregate root.
     * @param isActive Indicates whether the aggregate root is active.
     * @param processedCommands The set of processed commands for the aggregate root.
     * @param version The version of the aggregate root.
     */
    public MockAggregate(
        UUID id,
        boolean isActive,
        Set<UUID> processedCommands,
        Integer version,
        MementoFactory<Memento, MockAggregate> mementoFactory) {
      super(id, isActive, processedCommands, version, mementoFactory);
    }

    public void apply(MockCommand command) {
      this.executeIfNotProcessed(command, () -> executed = true);
    }

    public boolean isExecuted() {
      return executed;
    }

    @Override
    public SpecificationResult checkSpecification() {
      return null;
    }
  }

  static final class MockCommand extends BaseCommand {

    /**
     * Creates a new instance of the BaseCommand.
     *
     * @param id The unique identifier of the command.
     * @param createdDate The date when the command was created.
     */
    public MockCommand(UUID id, Instant createdDate, String createdBy) {
      super(id, createdDate, createdBy);
    }
  }

  @Test
  void shouldExecuteIfCommandNotPresent() {
    var aggregate = new MockAggregate(UUID.randomUUID(), true, null, 0, null);
    aggregate.apply(new MockCommand(UUID.randomUUID(), Instant.now(), "user"));

    assertTrue(aggregate.isExecuted());
  }

  @Test
  void shouldNotExecuteIfCommandIsPresent() {
    var commandUuid = UUID.randomUUID();
    var aggregate =
        new MockAggregate(
            UUID.randomUUID(), true, new HashSet<>(Collections.singleton(commandUuid)), 0, null);
    aggregate.apply(new MockCommand(commandUuid, Instant.now(), "user"));

    assertFalse(aggregate.isExecuted());
  }

  @Test
  void shouldBeDirtyIfCommandNotPresent() {
    var aggregate = new MockAggregate(UUID.randomUUID(), true, null, 0, null);
    aggregate.apply(new MockCommand(UUID.randomUUID(), Instant.now(), "user"));

    assertTrue(aggregate.isDirty());
  }

  @Test
  void shouldNotBeDirtyIfCommandIsPresent() {
    var commandUuid = UUID.randomUUID();
    var aggregate =
        new MockAggregate(
            UUID.randomUUID(), true, new HashSet<>(Collections.singleton(commandUuid)), 0, null);
    aggregate.apply(new MockCommand(commandUuid, Instant.now(), "user"));

    assertFalse(aggregate.isDirty());
  }

  @Test
  void shouldHaveOneCommandIfCommandNotPresent() {
    var aggregate = new MockAggregate(UUID.randomUUID(), true, null, 0, null);
    aggregate.apply(new MockCommand(UUID.randomUUID(), Instant.now(), "user"));

    assertEquals(1, aggregate.getProcessedCommands().size());
  }

  @Test
  void shouldHaveSameCommandIdIfCommandNotPresent() {
    var aggregate = new MockAggregate(UUID.randomUUID(), true, null, 0, null);
    var command = new MockCommand(UUID.randomUUID(), Instant.now(), "user");
    aggregate.apply(command);

    var commandId = aggregate.getProcessedCommands().stream().findFirst().get();
    assertEquals(command.getId(), commandId);
  }

  @Test
  void shouldHaveOneCommandIfCommandIsPresent() {
    var commandUuid = UUID.randomUUID();
    var aggregate =
        new MockAggregate(
            UUID.randomUUID(), true, new HashSet<>(Collections.singleton(commandUuid)), 0, null);
    aggregate.apply(new MockCommand(commandUuid, Instant.now(), "user"));

    assertEquals(1, aggregate.getProcessedCommands().size());
  }
}
