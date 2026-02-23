/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.functions.lambda.exceptions.handlers;

import com.google.inject.Inject;
import crosscutting.resiliency.Retryable;
import domain.exceptions.DeserializeException;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import domain.messages.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of the {@link CommandErrorHandler} interface.
 * Handles errors encountered during the processing of commands and sends
 * messages to appropriate queues based on the type of error.
 *
 * @param <MessageCollectionT> the type of the message collection
 * @param <MessageT> the type of the message
 * @param <CommandT> the type of the command
 */
public final class DefaultCommandErrorHandler<MessageCollectionT, MessageT, CommandT>
    implements CommandErrorHandler<MessageCollectionT, MessageT, CommandT> {

  private static final Logger logger = LoggerFactory.getLogger(DefaultCommandErrorHandler.class);
  private final MessageQueue<MessageCollectionT> deadLetterQueueSender;
  private final MessageQueue<MessageT> invalidMessageQueueSender;
  private final Retryable retryable;

  /**
   * Constructs a new {@code DefaultCommandErrorHandler} with the specified retryable,
   * dead letter queue sender, invalid message queue sender, and logger factory.
   *
   * @param retryable the retryable mechanism for handling retry logic
   * @param deadLetterQueueSender the sender for the dead letter queue
   * @param invalidMessageQueueSender the sender for the invalid message queue
   */
  @Inject
  public DefaultCommandErrorHandler(
      Retryable retryable,
      MessageQueue<MessageCollectionT> deadLetterQueueSender,
      MessageQueue<MessageT> invalidMessageQueueSender) {
    this.retryable = retryable;
    this.deadLetterQueueSender = deadLetterQueueSender;
    this.invalidMessageQueueSender = invalidMessageQueueSender;
  }

  /**
   * Handles the provided logic with error handling. In case of exceptions, messages
   * are sent to the appropriate queues based on the type of exception encountered.
   *
   * @param messageCollection the collection of messages being processed
   * @param clazz the class of the message type
   * @param logic the logic to be executed with error handling
   */
  public void handle(
      final MessageCollectionT messageCollection, final Class<?> clazz, final Runnable logic) {
    var runnable = retryable.createRunnable("CommandHandler", logic);

    try {
      runnable.run();
    } catch (Exception e) {
      switch (e.getCause()) {
        case DeserializeException deserializeException -> {
          logger.error("DeserializeException processing message: " + e);
          invalidMessageQueueSender.enqueue(deserializeException.getData(clazz));
        }
        case TransientException transientException -> {
          logger.error("Transient error processing SQS event: " + e);
          throw transientException;
        }
        case PermanentException permanentException -> {
          logger.error("Permanent error processing SQS event: " + e);
          this.deadLetterQueueSender.enqueue(messageCollection);
        }
        case null, default -> {
          logger.error("Error processing SQS event: " + e);
          this.deadLetterQueueSender.enqueue(messageCollection);
        }
      }
    }
  }
}
