/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.exceptions;

/**
 * A specialized exception type for handling errors related to invalid messages within an
 * application. This exception extends {@link RuntimeException}, allowing it to integrate smoothly
 * within a system that uses custom exception handling frameworks. It is typically thrown when a
 * message does not meet the necessary criteria or fails validation checks.
 */
public final class InvalidMessageException extends RuntimeException {

  /**
   * Constructs a new {@code InvalidMessageException} with the causal exception.
   */
  public InvalidMessageException(Throwable cause) {
    super(cause);
  }
}
