/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.commands;

import domain.exceptions.InvariantViolationException;
import domain.invariants.Specification;
import domain.invariants.SpecificationResult;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Abstract base implementation for commands.
 */
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public abstract class BaseCommand implements Serializable {
  protected final UUID id;
  protected final Instant createdDate;
  protected final String createdBy;

  /**
   * Validates this command.
   *
   * @throws InvariantViolationException if the command does not satisfy the specification.
   * @throws ClassCastException          if the parameterized type of {@link Specification} does
   *                                     not match the type of the command.
   */
  public <T> void validate(Specification<T> specification) {

    // TODO: A proper fix for this will change too many files. This is a temporary fix.
    @SuppressWarnings("unchecked")
    var command = (T) this;

    final SpecificationResult result = specification.isSatisfiedBy(command);
    if (!result.satisfied()) {
      throw new InvariantViolationException(result.errorMessages());
    }
  }
}
