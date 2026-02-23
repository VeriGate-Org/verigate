/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.invariants;

import java.util.HashSet;

/**
 * This interface represents a specification that can be used to check if an entity satisfies
 * certain conditions.
 *
 * @param <T> the type of the entity that the specification checks
 */
public interface Specification<T> {

  /**
   * Evaluates the given entity against the criteria defined by this specification. This method is
   * the cornerstone of the Specification pattern, determining whether an entity satisfies the
   * conditions of the specification.
   *
   * @param entity The entity to be evaluated against the specification.
   * @return A {@link SpecificationResult} containing the outcome of the evaluation, which includes
   *     whether the specification was satisfied and any associated error messages.
   */
  SpecificationResult isSatisfiedBy(T entity);

  /**
   * Combines this specification with another specification using a logical AND operation. The
   * result will only be satisfied if both specifications are satisfied. All error messages from
   * both specifications are aggregated.
   *
   * @param other The other specification to combine with this one.
   * @return A new specification that represents the logical AND of this specification and the
   *     other.
   */
  default Specification<T> and(Specification<T> other) {
    return (t) -> {
      SpecificationResult result1 = this.isSatisfiedBy(t);
      SpecificationResult result2 = other.isSatisfiedBy(t);
      var allMessages = new HashSet<ErrorRecord>();

      if (result1.errorMessages() != null && result1.errorMessages().size() > 0) {
        allMessages.addAll(result1.errorMessages());
      }

      if (result2.errorMessages() != null && result2.errorMessages().size() > 0) {
        allMessages.addAll(result2.errorMessages());
      }

      // Return a new result combining the satisfaction results with all collected messages.
      return new SpecificationResult(result1.satisfied() && result2.satisfied(), allMessages);
    };
  }

  /**
   * Combines this specification with another specification using a logical OR operation. The result
   * will be satisfied if at least one of the specifications is satisfied. All error messages from
   * both specifications are aggregated.
   *
   * @param other The other specification to combine with this one.
   * @return A new specification that represents the logical OR of this specification and the other.
   */
  default Specification<T> or(Specification<T> other) {
    return (t) -> {
      SpecificationResult result1 = this.isSatisfiedBy(t);
      SpecificationResult result2 = other.isSatisfiedBy(t);
      var allMessages = new HashSet<ErrorRecord>();

      // Collect error messages from both specifications, handling null cases.
      allMessages.addAll(
          result1.errorMessages() == null ? new HashSet<>() : result1.errorMessages());
      allMessages.addAll(
          result2.errorMessages() == null ? new HashSet<>() : result2.errorMessages());

      // Return a new result combining the satisfaction results with all collected messages.
      return new SpecificationResult(result1.satisfied() || result2.satisfied(), allMessages);
    };
  }

  /**
   * Combines this specification with another specification using the AND operator only if the
   * passed boolean condition is true.
   *
   * @param other the other specification to combine with
   * @param condition the boolean condition that must be true to perform the AND operation
   * @return a new specification that is either the combination of this and the other specification
   *     if the condition is true, or this specification alone if the condition is false.
   */
  default Specification<T> conditionalAnd(Specification<T> other, boolean condition) {
    return (t) -> {
      if (condition) {
        return this.and(other).isSatisfiedBy(t);
      } else {
        return this.isSatisfiedBy(t);
      }
    };
  }

  /**
   * Creates a new specification that always succeeds. This specification returns a positive result
   * with no error messages, making it useful as a default or neutral element in composite
   * specification scenarios where no actual validation is required but a non-null specification is
   * needed.
   *
   * @param <T> The type of entity being specified.
   * @return A specification that always returns a successful {@link SpecificationResult}.
   */
  static <T> Specification<T> create() {
    return entity -> SpecificationResult.success();
  }
}
