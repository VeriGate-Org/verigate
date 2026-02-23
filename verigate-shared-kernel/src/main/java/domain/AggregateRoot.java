/*
 * VeriGate (c) 2024. All rights reserved. Unauthorized copying of this file, via any medium
 * is strictly prohibited. Proprietary and confidential.
 */

package domain;

import crosscutting.patterns.Factory;
import crosscutting.patterns.Memento;
import crosscutting.patterns.MementoCreator;
import crosscutting.patterns.MementoFactory;
import domain.commands.BaseCommand;
import domain.events.BaseEvent;
import domain.exceptions.PermanentException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Represents a quote aggregate root. */
public abstract class AggregateRoot<IdT, AggregateT> extends Entity<IdT>
    implements MementoCreator<Memento> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AggregateRoot.class);

  // For optimistic locking
  protected transient Integer version;

  protected transient Set<UUID> processedCommands;
  protected transient Map<String, Integer> processedEvents;
  private transient boolean isDirty;

  @SuppressWarnings("rawtypes")
  protected transient Set<BaseEvent> domainEvents;

  protected transient SortedSet<AuditDetail> auditDetail;
  private final transient MementoFactory<Memento, AggregateT> mementoFactory;

  protected AggregateRoot() {
    this(null, false, new HashSet<>(), 0, null);
  }

  /**
   * Constructs a new instance of the AggregateRoot class.
   *
   * @param id The identifier of the aggregate root.
   * @param isActive Indicates whether the aggregate root is active.
   * @param processedCommands The set of processed commands for the aggregate root.
   * @param version The version of the aggregate root.
   */
  public AggregateRoot(
      IdT id,
      Boolean isActive,
      Set<UUID> processedCommands,
      Integer version,
      MementoFactory<Memento, AggregateT> mementoFactory) {
    super(id, isActive);
    this.mementoFactory = mementoFactory;
    this.isDirty = false;
    this.domainEvents = new HashSet<>();
    this.auditDetail = new TreeSet<>(AuditDetail.comparatorByUpdatedAt());
    this.processedCommands = processedCommands == null ? new HashSet<>() : processedCommands;
    this.version = version;
  }

  /**
   * Adds a domain event to this QuoteAggregateRoot instance.
   *
   * <p>This method adds the specified domain event to this QuoteAggregateRoot instance. The domain
   * event will be included in the list of domain events that have been added to this
   * QuoteAggregateRoot instance, and can be retrieved later by calling the pullDomainEvents()
   * method.
   *
   * @param event the domain event to add
   */
  @SuppressWarnings("rawtypes")
  protected void addDomainEvent(BaseEvent event) {
    domainEvents.add(event);
  }

  /**
   * Retrieves all domain events that have been added to this QuoteAggregateRoot instance.
   *
   * <p>This method returns a new list containing all the domain events that have been added to this
   * QuoteAggregateRoot instance. The returned list is a copy of the internal list of domain events,
   * so changes to the returned list will not affect the internal list of domain events.
   *
   * @return a list of domain events
   */
  @SuppressWarnings("rawtypes")
  public List<BaseEvent> pullDomainEvents() {
    return new ArrayList<BaseEvent>(domainEvents);
  }

  /**
   * Adds an audit detail to an Aggregate Root instance.
   *
   * <p>This method adds the specified audit detail to the Aggregate Root instance. The audit detail
   * will be included in the list of audit details that have been added to the Aggregate Root
   * instance, and can be retrieved later by calling the getAuditDetail() method.
   *
   * @param auditDetail to be added to the Aggregate Root instance.
   */
  public void addAuditDetail(AuditDetail auditDetail) {
    if (this.auditDetail == null) {
      this.auditDetail = new TreeSet<>(AuditDetail.comparatorByUpdatedAt());
    }
    this.auditDetail.add(auditDetail);
  }

  public SortedSet<AuditDetail> getAuditDetail() {
    return Collections.unmodifiableSortedSet(
        Objects.requireNonNullElseGet(auditDetail, TreeSet::new));
  }

  public Set<UUID> getProcessedCommands() {
    return Collections.unmodifiableSet(processedCommands);
  }

  public Map<String, Integer> getProcessedEvents() {
    return Collections.unmodifiableMap(
        Objects.requireNonNullElseGet(processedEvents, HashMap::new));
  }

  /**
   * Executes a specified action for a command if it hasn't been processed previously. This method
   * first checks if the given command has already been executed based on its ID. If the command is
   * new or there are no processed commands yet, it executes the provided action and marks the
   * command as processed. If the command has already been processed, it logs a warning. This
   * ensures that each command is processed only once, maintaining idempotency.
   *
   * @param command The command to check for idempotency.
   * @param action The action to be executed if the command has not been processed before.
   */
  protected void executeIfNotProcessed(BaseCommand command, CommandAction action)
      throws PermanentException {
    if (processedCommands == null || !processedCommands.contains(command.getId())) {
      // Execute the provided action as the command is new
      action.execute();
      setDirty();

      // Mark the command as processed by adding its ID to the list
      if (processedCommands != null) {
        processedCommands.add(command.getId());
      }
    } else {
      // Log a warning if the command has already been processed
      LOGGER.warn("Command {} has already been processed", command.getId());
    }
  }

  protected void executeIfNotProcessed(BaseEvent event, String eventNaturalKey, EventAction action)
      throws PermanentException {

    if (event.getLogicalClockReading() == null) {
      LOGGER.warn("Service does not support Logical Clock Readings for Event Id {}", event.getId());
      action.execute();
      setDirty();
      return;
    }

    if (processedEvents == null) {
      action.execute();
      setDirty();
      processedEvents = new HashMap<>();
      processedEvents.put(eventNaturalKey, event.getLogicalClockReading());
      return;
    }

    if (!processedEvents.containsKey(eventNaturalKey)) {
      action.execute();
      setDirty();
      processedEvents.put(eventNaturalKey, event.getLogicalClockReading());
      return;
    }

    Integer processedClock = processedEvents.get(eventNaturalKey);
    if (processedClock < event.getLogicalClockReading()) {
      action.execute();
      setDirty();
      processedEvents.put(eventNaturalKey, event.getLogicalClockReading());
      return;
    }

    LOGGER.warn("Event {} has already been processed", event.getId());
  }

  /**
   * Checks if the current state of the object has been modified since the last save operation. This
   * method is typically used in scenarios where there's a need to determine if an object's state
   * has changed and thus requires saving or further processing.
   *
   * @return true if the object's state has been modified (dirty), false otherwise.
   */
  public boolean isDirty() {
    return isDirty;
  }

  /**
   * Marks the current object as having been modified (dirty). This method should be called whenever
   * changes are made to the object's state that need to be tracked. For example, after modifying
   * fields that are part of the object's persistent state, calling this method indicates that the
   * object should be considered for saving or further processing.
   */
  protected void setDirty() {
    isDirty = true;
  }

  /**
   * Saves the current state of the object to a Memento object for later retrieval. This method is a
   * critical part of the Memento design pattern, allowing the object's state to be saved without
   * exposing its internal structure. The method utilizes a factory to create the Memento, ensuring
   * that the process of capturing the object's state is decoupled from the object itself.
   *
   * @return A Memento object encapsulating the current state of the object. This Memento can later
   *     be used to restore the object's state.
   */
  @Override
  @NotNull
  public Memento saveStateToMemento() {
    // Suppressing unchecked warnings because this cast is assumed to be safe.
    @SuppressWarnings("unchecked")
    AggregateT aggregate = (AggregateT) this;
    return mementoFactory.create(aggregate);
  }

  /**
   * Retrieves the current version of the aggregate root.
   *
   * <p>This version number is crucial for implementing optimistic locking within the domain.
   * Optimistic locking is used to ensure that concurrent transactions do not lead to inconsistent
   * state changes. When an entity is updated, the version number is checked and incremented. This
   * mechanism helps prevent the "lost update" problem, where simultaneous updates from different
   * transactions overwrite each other without awareness of the other's changes.
   *
   * <p>The version number is automatically managed by the persistence framework (e.g., JPA,
   * Hibernate) and should not be manually altered under normal circumstances. Each update to the
   * aggregate through the repository will result in an increment of this version number, ensuring
   * that any stale or concurrent data manipulations can be detected and appropriately handled.
   *
   * <p>Usage of this method is typically reserved for infrastructure concerns rather than domain
   * logic. It enables the infrastructure to handle concurrency and data integrity issues
   * transparently, allowing domain logic to remain focused on business rules rather than
   * concurrency control mechanisms.
   *
   * @return the current version of the aggregate root, used for optimistic locking.
   */
  public Integer getVersion() {
    return version;
  }

  /**
   * Creates an instance of {@code AggregateT} from the provided {@code Memento} object using the
   * specified {@code aggregateFactory}. This method serves as a generic factory method to
   * reconstitute or instantiate objects of type {@code AggregateT} from their memento
   * representation, facilitating the memento design pattern for state restoration.
   *
   * @param <AggregateT> the type of the aggregate object to be created. This type parameter ensures
   *     the method's return type matches the type produced by the provided factory.
   * @param memento the {@code Memento} object from which the aggregate object's state is to be
   *     restored. Must not be {@code null}.
   * @param aggregateFactory a factory capable of creating instances of {@code AggregateT} from a
   *     {@code Memento} object. Must not be {@code null}.
   * @return an instance of {@code AggregateT} with its state restored from the provided {@code
   *     memento}.
   * @throws NullPointerException if either {@code memento} or {@code aggregateFactory} is {@code
   *     null}.
   */
  @NotNull
  public static <AggregateT> AggregateT createFromMemento(
      Memento memento, Factory<AggregateT, Memento> aggregateFactory) {
    return aggregateFactory.create(memento);
  }
}
