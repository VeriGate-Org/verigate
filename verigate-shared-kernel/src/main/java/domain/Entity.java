/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain;

import crosscutting.serialization.DataContract;
import domain.exceptions.InvariantViolationException;
import domain.invariants.Specification;
import domain.invariants.SpecificationResult;
import java.io.Serializable;

/**
 * `Entity` is an abstract class that encapsulates common properties for an entity. It includes an
 * ID, audit details, and an active status. The class provides getter methods for each of these
 * properties, but does not provide setter methods, because these properties should not be changed
 * after the entity is created.
 *
 * @param <IdT> the type of the ID for the entity
 */
public abstract class Entity<IdT> implements Serializable {

  @DataContract protected final IdT id;

  @DataContract protected Boolean isActive;

  public Entity() {
    this.id = null;
    this.isActive = false;
  }

  /**
   * Constructs a new Entity with the given id and active status.
   *
   * @param id the ID of the entity
   * @param isActive the active status of the entity
   */
  public Entity(IdT id, Boolean isActive) {
    this.id = id;
    this.isActive = isActive;
  }

  /**
   * Returns the ID of the entity.
   *
   * @return the ID of the entity
   */
  public IdT getId() {
    return id;
  }

  /**
   * Returns the active status of the entity.
   *
   * @return the active status of the entity
   */
  public Boolean getIsActive() {
    return isActive;
  }

  protected <EntityT> void validate(Specification<EntityT> specification, EntityT entity)
      throws InvariantViolationException {
    var result = checkSpecification(specification, entity);

    if (!result.satisfied()) {
      throw new InvariantViolationException(result.errorMessages());
    }
  }

  protected <EntityT> SpecificationResult checkSpecification(
      Specification<EntityT> specification, EntityT entity) {
    if (specification == null) {
      return SpecificationResult.success();
    }
    return specification.isSatisfiedBy(entity);
  }

  public abstract SpecificationResult checkSpecification();

  /**
   * Compares this entity to another entity based on their IDs.
   *
   * @param obj the other object to compare to
   * @return true if the other entity has the same ID as this entity, false otherwise
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Entity<?> other = (Entity<?>) obj;
    return id != null && id.equals(other.id);
  }

  /**
   * Returns the hash code of the entity based on its ID.
   *
   * @return the hash code of the entity
   */
  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }
}
