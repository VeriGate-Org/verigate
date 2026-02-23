/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.exceptions;

import java.util.function.Function;

/**
 * An exception that represents a permanent error that occurred while processing a request. This
 * exception should be thrown when the error is permanent and the request cannot be retried.
 */
public class PermanentException extends RuntimeException {

  /**
   * Constructs a new PermanentException with the given message.
   *
   * @param msg The message that describes the error.
   */
  public PermanentException(String msg) {
    super(msg);
  }

  public PermanentException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public PermanentException(Throwable cause) {
    super(cause);
  }

  /**
   * A convenience function to convert a supplied checked exception into a PermanentException.
   *
   * @param message The message to apply to the new PermanentException.
   */
  public static Function<Exception, PermanentException> wrapInPermanentException(String message) {
    return e -> new PermanentException(message, e);
  }
}
