/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.messaging;

import domain.exceptions.InvalidMessageException;
import domain.exceptions.PermanentException;
import infrastructure.event.handler.RetryConfig;
import java.util.function.Consumer;

/**
 * The {@code RetryableOperation} interface provides a framework for implementing operations on
 * messages that can be retried in case of failure. It allows for specifying a retry strategy,
 * including the maximum number of retry attempts and the delay between attempts, thereby offering a
 * structured approach to handle transient failures efficiently.
 *
 * @param <MessageT> the type of the message to be processed. This generic type parameter enables
 *     the interface to be used with a wide variety of message types, making it versatile and
 *     adaptable to different contexts within the application.
 */
public interface RetryableOperation<MessageT> {

  /**
   * Processes the given message using the specified operation, with retry logic applied based on
   * the provided parameters. If the operation fails, it will be retried until either it succeeds,
   * the maximum number of retry attempts is reached, or a {@link PermanentException} is
   * encountered, which indicates an unrecoverable error condition.
   *
   * @param message the message to process. It is passed to the operation consumer for processing.
   * @param operation a {@link Consumer} that defines how the message should be processed. This
   *     operation is subject to retry according to the retry parameters if it fails.
   * @throws PermanentException if the operation consistently fails and exceeds the maximum number
   *     of retry attempts, or if an unrecoverable error condition is encountered.
   */
  void process(
      MessageT message,
      Consumer<MessageT> operation,
      RetryConfig retryConfig)
      throws PermanentException, InvalidMessageException;
}
