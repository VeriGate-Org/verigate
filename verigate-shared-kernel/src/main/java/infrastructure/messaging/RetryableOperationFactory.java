/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.messaging;

/**
 * This interface represents a factory for creating {@code RetryableOperation} objects. It defines a
 * method to create instances of a generic {@code RetryableOperation} that can handle operations of
 * type {@code MessageT}.
 */
public interface RetryableOperationFactory {

  /**
   * Creates and returns a new instance of {@code RetryableOperation} that handles messages of type
   * {@code MessageT}. Each call to this method should return a new instance, configured to perform
   * some operation that can be retried upon failure.
   *
   * @param <MessageT> the generic type parameter specifying the type of message the operation will
   *     handle
   * @return a new instance of {@code RetryableOperation<MessageT>}, ready to execute operations
   *     that may require retries
   */
  <MessageT> RetryableOperation<MessageT> create();
}
