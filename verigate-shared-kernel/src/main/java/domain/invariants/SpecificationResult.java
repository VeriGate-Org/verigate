/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.invariants;

import java.util.HashSet;
import java.util.Set;

/** This class represents the result of a specification check. */
public record SpecificationResult(boolean satisfied, Set<ErrorRecord> errorMessages) {
  public static SpecificationResult success() {
    return new SpecificationResult(true, new HashSet<>());
  }

  public static SpecificationResult failure(Set<ErrorRecord> errorMessages) {
    return new SpecificationResult(false, errorMessages);
  }

  public static SpecificationResult fail(String message) {
    return new SpecificationResult(false, Set.of(
        new DefaultErrorRecord("validation", new InvariantError() {
          @Override public String getMessage() { return message; }
          @Override public String getCode() { return "VALIDATION_FAILED"; }
        })
    ));
  }
}
