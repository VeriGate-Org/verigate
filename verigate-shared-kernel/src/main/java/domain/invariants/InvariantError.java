/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.invariants;

/**
 * This is the interface for an invariant error that is caused by a specification.
 */
public interface InvariantError {

  /**
   * Returns the message of the error.
   *
   * @return the message of the error
   */
  public String getMessage();


  /**
   * Returns the code of the error.
   *
   * @return the code of the error
   */
  public String getCode();
}
