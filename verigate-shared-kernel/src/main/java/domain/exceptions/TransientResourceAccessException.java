/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.exceptions;

/**
 * A resource could not be accessed.
 */
public class TransientResourceAccessException extends TransientException {

  /**
   * Constructs a new exception with the given resource that could not be accessed, and cause.
   */
  public TransientResourceAccessException(final String resource,
                                          final Throwable cause) {
    super(resource, cause);
  }
}
