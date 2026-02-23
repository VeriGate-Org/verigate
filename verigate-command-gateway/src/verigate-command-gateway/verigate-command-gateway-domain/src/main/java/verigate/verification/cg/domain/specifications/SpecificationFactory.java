/*
 * Arthmatic + Karisani(c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.cg.domain.specifications;

import domain.invariants.Specification;

/**
 * Represents a factory for creating a specification for a given type.
 *
 * @param <T> The type for which the specification is created.
 */
public interface SpecificationFactory<T> {
  Specification<T> createSpecification(T command);
}
