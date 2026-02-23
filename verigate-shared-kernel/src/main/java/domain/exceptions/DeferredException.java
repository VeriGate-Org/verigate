/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.exceptions;

/**
 * An exception that is thrown when an error occurs that can be resolved by retrying the operation.
 */
public final class DeferredException extends TransientException {

  /**
   * Constructs a new exception with the specified message and cause.
   *
   * @param message the detail message
   * @param cause   the cause of the exception
   */
  public DeferredException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new exception with the specified message.
   *
   * @param message the detail message
   */
  public DeferredException(final String message) {
    super(message);
  }
}
