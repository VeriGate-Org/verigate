/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.invariants;

/**
 * This is the interface for an error record that is caused by a specification.
 */
public interface ErrorRecord {

  /**
   * Returns the name of the specification that caused the error.
   *
   * @return the name of the specification that caused the error
   */
  String getSpecificationName();

  /**
   * Returns the error that was caused by the specification.
   *
   * @return the error that was caused by the specification
   */
  InvariantError getError();
}
