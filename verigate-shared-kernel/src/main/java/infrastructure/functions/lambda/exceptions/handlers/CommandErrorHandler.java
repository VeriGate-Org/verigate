/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.functions.lambda.exceptions.handlers;

/**
 * Interface for handling errors that occur during the processing of commands.
 *
 * @param <MessageCollectionT> the type of the message collection
 * @param <MessageT> the type of the message
 * @param <CommandT> the type of the command
 */
public interface CommandErrorHandler<MessageCollectionT, MessageT, CommandT> {

  /**
   * Handles the provided logic with error handling. In case of exceptions, messages are processed
   * accordingly based on the type of exception encountered.
   *
   * @param messageCollection the collection of messages being processed
   * @param clazz the class of the message type
   * @param logic the logic to be executed with error handling
   */
  void handle(
      final MessageCollectionT messageCollection, final Class<?> clazz, final Runnable logic);
}
