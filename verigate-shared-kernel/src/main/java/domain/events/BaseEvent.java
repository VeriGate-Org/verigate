/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.events;

import crosscutting.serialization.DataContract;
import domain.exceptions.InvariantViolationException;
import domain.invariants.Specification;
import domain.invariants.SpecificationResult;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Embodies properties common to all events.
 *
 * @param <DetailT> The type of the event details.
 */
public abstract class BaseEvent<DetailT> {
  @DataContract protected final UUID id;
  @DataContract protected final String detailType;
  @DataContract protected final Instant noticedDate;
  @DataContract protected final Instant effectedDate;
  @DataContract protected Map<String, DetailT> details;
  @DataContract protected final Integer logicalClockReading;

  /** Default constructor for the CollectionEvent class. Initializes all properties to null. */
  protected BaseEvent() {
    this.id = null;
    this.detailType = null;
    this.details = null;
    this.noticedDate = null;
    this.effectedDate = null;
    this.logicalClockReading = null;
  }

  /**
   * Creates a new instance of the BaseEvent class.
   *
   * @param id The unique identifier of the event.
   * @param detailType The type detail of the event.
   * @param noticedDate The date when the event was noticed. A defensive copy is made.
   * @param effectedDate The date when the event takes effect. A defensive copy is made.
   * @param logicalClockReading The logical clock reading of the event.
   */
  public BaseEvent(
      UUID id,
      String detailType,
      Instant noticedDate,
      Instant effectedDate,
      Integer logicalClockReading) {
    this.id = id;
    this.detailType = detailType;
    this.details = null;
    this.noticedDate = noticedDate; // defensive copy
    this.effectedDate = effectedDate; // defensive copy
    this.logicalClockReading = logicalClockReading;
  }

  /**
   * Creates a new instance of the BaseEvent class.
   *
   * @param id The unique identifier of the event.
   * @param detailType The type detail of the event.
   * @param details The details of the event.
   * @param noticedDate The date when the event was noticed. A defensive copy is made.
   * @param effectedDate The date when the event takes effect. A defensive copy is made.
   * @param logicalClockReading The logical clock reading of the event.
   */
  public BaseEvent(
      UUID id,
      String detailType,
      Map<String, DetailT> details,
      Instant noticedDate,
      Instant effectedDate,
      Integer logicalClockReading) {
    this.id = id;
    this.detailType = detailType;
    this.details = details;
    this.noticedDate = noticedDate; // defensive copy
    this.effectedDate = effectedDate; // defensive copy
    this.logicalClockReading = logicalClockReading;
  }

  /**
   * Gets the date when the event takes effect.
   *
   * @return The date when the event takes effect.
   */
  public String getDetailType() {
    return detailType;
  }

  /**
   * Gets the date when the event was noticed.
   *
   * @return The date when the event was noticed.
   */
  public Instant getNoticedDate() {
    return noticedDate;
  }

  /**
   * Gets the date when the event takes effect.
   *
   * @return The date when the event takes effect.
   */
  public Instant getEffectedDate() {
    return effectedDate;
  }

  /**
   * Gets the details of the event.
   *
   * @return The details of the event.
   */
  public Map<String, DetailT> getDetails() {
    return details;
  }

  /**
   * Gets the reading of the logical clock when the event was noticed.
   *
   * @return The reading of the logical clock when the event was noticed.
   */
  public Integer getLogicalClockReading() {
    return logicalClockReading;
  }

  /**
   * Validates this event.
   *
   * @throws InvariantViolationException if the event does not satisfy the specification.
   * @throws ClassCastException          if the parameterized type of {@link Specification} does
   *                                     not match the type of the event.
   */
  public <T extends BaseEvent<DetailT>> void validate(Specification<T> specification) {

    // TODO: A proper fix for this will change too many files. This is a temporary fix.
    @SuppressWarnings("unchecked")
    var event = (T) this;

    final SpecificationResult result = specification.isSatisfiedBy(event);
    if (!result.satisfied()) {
      throw new InvariantViolationException(result.errorMessages());
    }
  }

  /**
   * Returns a string representation of the BaseEvent object.
   *
   * @return The string representation of the BaseEvent object.
   */
  @Override
  public String toString() {
    return "id=" + getId() + ", noticedDate=" + noticedDate + ", effectedDate=" + effectedDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    var baseEvent = (BaseEvent<?>) o;
    return Objects.equals(getId(), baseEvent.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }

  public UUID getId() {
    return id;
  }
}
