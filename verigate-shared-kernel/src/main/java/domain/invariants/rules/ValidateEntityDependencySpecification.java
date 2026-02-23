/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.invariants.rules;

import domain.Entity;
import domain.invariants.Specification;
import domain.invariants.SpecificationResult;
import domain.invariants.SpecificationUtils;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A specification class used to validate dependencies between entities. This class implements the
 * {@link Specification} interface and provides functionality to validate if an entity satisfies all
 * specifications related to its dependencies.
 *
 * @param <EntityT> The type of the entity being validated.
 * @param <DependencyEntityT> The type of the dependency entities associated with the primary
 *     entity. Both {@code EntityT} and {@code DependencyEntityT} must extend from {@code Entity}.
 */
public final class ValidateEntityDependencySpecification<
        EntityT extends Entity<?>, DependencyEntityT extends Entity<?>>
    implements Specification<EntityT> {

  /** Function mapping an entity to a stream of its dependencies. */
  private final Function<EntityT, Stream<DependencyEntityT>> entities;

  /**
   * Constructs a new {@code ValidateDependencySpecification} with the provided mapping function.
   * This function should return a stream of dependency entities for a given entity.
   *
   * @param entities A function that maps an entity of type {@code EntityT} to a stream of its
   *     dependencies of type {@code DependencyEntityT}.
   */
  public ValidateEntityDependencySpecification(
      Function<EntityT, Stream<DependencyEntityT>> entities) {
    this.entities = entities;
  }

  /**
   * Evaluates the specification for the provided entity. It applies the specification check to each
   * dependency of the entity and aggregates the results.
   *
   * @param entity The entity for which to evaluate the specification.
   * @return {@link SpecificationResult} containing the results of the specification checks for all
   *     dependencies.
   */
  @Override
  public SpecificationResult isSatisfiedBy(EntityT entity) {
    var results = entities.apply(entity).map(Entity::checkSpecification);
    return SpecificationUtils.getResult(this.getClass(), results.collect(Collectors.toSet()));
  }
}
