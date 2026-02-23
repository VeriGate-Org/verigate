/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.invariants;

/** Concrete implementation of the ErrorRecord interface. */
public record DefaultErrorRecord(String specificationName, InvariantError errorCode)
    implements ErrorRecord {

  /**
   * Returns the name of the specification that caused the error.
   *
   * @return the name of the specification that caused the error
   */
  @Override
  public String getSpecificationName() {
    return specificationName;
  }

  /**
   * Returns the error that was caused by the specification.
   *
   * @return the error that was caused by the specification
   */
  @Override
  public InvariantError getError() {
    return errorCode;
  }
}
