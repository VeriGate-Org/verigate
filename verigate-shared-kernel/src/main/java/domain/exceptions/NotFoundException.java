/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.exceptions;

/**
 * An exception that represents a not found error that occurred while processing a request. This
 * exception should be thrown when the error is not found and the request cannot be retried.
 */
public class NotFoundException extends PermanentException {

  /**
   * Constructs a new NotFoundException with the given message.
   */
  public NotFoundException(String msg) {
    super(msg);
  }
}
