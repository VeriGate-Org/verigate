/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.commands.handler.sqs;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse.BatchItemFailure;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import crosscutting.resiliency.Retryable;
import domain.commands.CommandHandler;
import domain.exceptions.DeferredException;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import domain.messages.DeadLetterQueue;
import domain.messages.InvalidMessageQueue;
import domain.messages.MessageQueue;
import infrastructure.commands.handler.ResilientCommandLambdaHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * A base implementation of a Lambda handler consuming messages from an SQS queue.
 *
 * <p>The implementation adds resilient processing features on two levels:
 * 1. De-serialization of the command by a supplied function. If this fails or produces no result,
 * the message will be moved to an invalid-message queue. 2. Command handling by a supplied
 * {@link CommandHandler} (which is also wrapped in a supplied {@link Retryable}). The final result
 * of command handling can then be one of these: 2.1. Success (no exceptions). This is the happy
 * path and the message will be removed from the source queue automatically by SQS. 2.2.
 * {@link TransientException}. The message will be kept on the source queue. SQS will automatically
 * re-drive this. Depending on queue configuration, SQS might move it to a dead-letter queue if it
 * continues to fail. 2.3. {@link PermanentException}. The message will be moved to a dead-letter
 * queue. This could be the same DLQ one configures on the source queue for SQS to move perpetual
 * failed messages to.
 *
 * @param <CommandT> The type of command the source queue supports.
 */
public class ResilientSqsCommandLambdaHandler<CommandT, CommandHandlerReturnT>
    implements ResilientCommandLambdaHandler<SQSEvent, SQSBatchResponse, CommandT> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ResilientSqsCommandLambdaHandler.class);

  public static final String INVALID_MESSAGE_QUEUE_NAME = "IMQ";
  public static final String DEAD_LETTER_QUEUE_NAME = "DLQ";

  private enum MessageProcessingResult {
    SUCCESS,
    TRANSPORT_LAYER_RETRY,
    INVALID_MESSAGE,
    DEAD_LETTER,
    DEFER_MESSAGE_PROCESSING,
    NULL_MESSAGE
  }

  private final Function<String, CommandT> extractCommandFromMessageBody;
  private final CommandHandler<CommandT, CommandHandlerReturnT> commandHandler;
  private final InvalidMessageQueue<SQSMessage> invalidMessageQueue;
  private final DeadLetterQueue<SQSMessage> deadLetterQueue;
  private final Function<SQSMessage, Boolean> deferMessageProcessing;

  /**
   * Construct a new handler with all required (non-null) dependencies.
   *
   * @param extractCommandFromMessageBody Function to de-serialize a message to a command.
   * @param commandHandler                The command handler processing the command.
   * @param invalidMessageQueue           The message queue for invalid messages.
   * @param deadLetterQueue               The message queue for dead-letter messages.
   */
  public ResilientSqsCommandLambdaHandler(
      Function<String, CommandT> extractCommandFromMessageBody,
      CommandHandler<CommandT, CommandHandlerReturnT> commandHandler,
      InvalidMessageQueue<SQSMessage> invalidMessageQueue,
      DeadLetterQueue<SQSMessage> deadLetterQueue,
      Function<SQSMessage, Boolean> deferMessageProcessing) {
    this.extractCommandFromMessageBody = Objects.requireNonNull(extractCommandFromMessageBody);
    this.commandHandler = Objects.requireNonNull(commandHandler);
    this.invalidMessageQueue = Objects.requireNonNull(invalidMessageQueue);
    this.deadLetterQueue = Objects.requireNonNull(deadLetterQueue);
    this.deferMessageProcessing = Objects.requireNonNull(deferMessageProcessing);
  }

  /**
   * Construct a new handler with all required (non-null) dependencies.
   *
   * @param extractCommandFromMessageBody Function to de-serialize a message to a command.
   * @param commandHandler                The command handler processing the command.
   * @param invalidMessageQueue           The message queue for invalid messages.
   * @param deadLetterQueue               The message queue for dead-letter messages.
   */
  public ResilientSqsCommandLambdaHandler(
      Function<String, CommandT> extractCommandFromMessageBody,
      CommandHandler<CommandT, CommandHandlerReturnT> commandHandler,
      InvalidMessageQueue<SQSMessage> invalidMessageQueue,
      DeadLetterQueue<SQSMessage> deadLetterQueue) {
    this(
        extractCommandFromMessageBody,
        commandHandler,
        invalidMessageQueue,
        deadLetterQueue,
        (message) -> false);
  }

  @Override
  public SQSBatchResponse handleRequest(SQSEvent sqsEvent, Context context) {
    final List<SQSMessage> sqsMessages = Objects.requireNonNull(sqsEvent).getRecords();
    if (sqsMessages == null) {
      LOGGER.warn("Unexpected SQS event with no SQS records. Non-SQS invocation?");
      return new SQSBatchResponse(Collections.emptyList());
    }
    LOGGER.info("Received SQS event with {} records", sqsMessages.size());
    final List<BatchItemFailure> batchItemFailures = new ArrayList<>();
    for (SQSMessage sqsMessage : sqsMessages) {
      MessageProcessingResult messageProcessingResult;
      try {
        messageProcessingResult = processMessage(sqsMessage);
      } catch (Exception e) {
        // ProcessMessage is designed to not throw any exceptions. If this happens unexpectedly,
        // we'll treat it the same as a PermanentException and move it to the DLQ immediately.
        LOGGER.error(
            "Unexpected exception processing message [{}]. Fallback is to move to DLQ.",
            sqsMessage.getMessageId(),
            e);
        messageProcessingResult = MessageProcessingResult.DEAD_LETTER;
      }
      switch (messageProcessingResult) {
        case SUCCESS -> {
          // Successful processing is a no-op since it will be removed from the queue automatically
          // by the transport layer.
        }
        case NULL_MESSAGE -> {
          // Nothing can be done with a message with a null body. There's no point in retrying or
          // moving to a different queue as there is nothing to be debugged about the message.
          // This can probably just be added to metrics to get a handle on how often this happens,
          // as it is unexpected. For the resilient handler, this will be considered a success
          // (no-op) to remove it from the queue.
        }
        case TRANSPORT_LAYER_RETRY ->
            // Notify the transport layer about these failures to drive retries.
            recordBatchItemFailure(batchItemFailures, sqsMessage);
        case INVALID_MESSAGE ->
            // Move the message to the invalid message queue
            enqueueMessage(
                INVALID_MESSAGE_QUEUE_NAME, invalidMessageQueue, sqsMessage, batchItemFailures);
        case DEAD_LETTER ->
            // Move the message to the dead letter queue
            enqueueMessage(DEAD_LETTER_QUEUE_NAME, deadLetterQueue, sqsMessage, batchItemFailures);
        case DEFER_MESSAGE_PROCESSING ->
            // Defer processing of the message to a later time
            deferMessageProcessing(sqsMessage, batchItemFailures);
        default -> {
          // Must be a programmer mistake to not support a new kind of processing result
          // Will handle this the same as dead-letter, because there shouldn't be value
          // in retrying this repeatedly.
          LOGGER.error(
              "Unexpected result ({}) processing message [{}]",
              messageProcessingResult,
              sqsMessage.getMessageId());
          enqueueMessage(DEAD_LETTER_QUEUE_NAME, deadLetterQueue, sqsMessage, batchItemFailures);
        }
      }
    }
    LOGGER.info("Completed processing, returning {} batch item failures", batchItemFailures.size());
    return new SQSBatchResponse(batchItemFailures);
  }

  /**
   * Processing of the message involves 1) extracting the command from the message body, and 2)
   * requesting the commandHandler to handle the command. The intention is that this implementation
   * does not propagate any exception. Any failure should be logged and translated to an appropriate
   * MessageProcessingResult kind for the main control flow to decide on how to handle.
   */
  private MessageProcessingResult processMessage(SQSMessage sqsMessage) {
    final var messageId = sqsMessage.getMessageId();
    final var messageBody = sqsMessage.getBody();
    if (messageBody == null) {
      LOGGER.warn("Message [{}] contains a null body", messageId);
      return MessageProcessingResult.NULL_MESSAGE;
    }

    populateMdcFromMessageAttributes(sqsMessage);

    try {
      CommandT extractedCommand;
      try {
        extractedCommand = extractCommandFromMessageBody.apply(messageBody);
      } catch (Exception e) {
        LOGGER.error("Failed to extract command from message [{}] body.", messageId, e);
        return MessageProcessingResult.INVALID_MESSAGE;
      }
      if (extractedCommand == null) {
        LOGGER.error("Extracted command from message [{}] body is null.", messageId);
        return MessageProcessingResult.INVALID_MESSAGE;
      }
      try {
        commandHandler.handle(extractedCommand);
        LOGGER.info("Command processed from message [{}] successfully.", messageId);
        return MessageProcessingResult.SUCCESS;
      } catch (DeferredException e) {
        LOGGER.warn(
            "Command processing from message [{}] resulted in a deferred exception.",
            messageId, e);
        return MessageProcessingResult.DEFER_MESSAGE_PROCESSING;
      } catch (TransientException e) {
        LOGGER.warn(
            "Command processing from message [{}] resulted in a transient exception.",
            messageId, e);
        return MessageProcessingResult.TRANSPORT_LAYER_RETRY;
      } catch (PermanentException e) {
        LOGGER.error(
            "Command processing from message [{}] resulted in a permanent exception.",
            messageId, e);
        return MessageProcessingResult.DEAD_LETTER;
      }
    } finally {
      MDC.clear();
    }
  }

  private void populateMdcFromMessageAttributes(SQSMessage sqsMessage) {
    if (sqsMessage.getMessageAttributes() == null) {
      return;
    }
    var correlationAttr = sqsMessage.getMessageAttributes().get("correlationId");
    if (correlationAttr != null && correlationAttr.getStringValue() != null) {
      MDC.put("correlationId", correlationAttr.getStringValue());
    }
  }

  private void enqueueMessage(
      String queueName,
      MessageQueue<SQSMessage> messageQueue,
      SQSMessage sqsMessage,
      List<BatchItemFailure> batchItemFailures) {
    final var messageId = sqsMessage.getMessageId();
    try {
      LOGGER.info("Enqueuing message [{}] to [{}]", messageId, queueName);
      messageQueue.enqueue(sqsMessage);
    } catch (Exception e) {
      LOGGER.error(
          "Enqueuing of message [{}] failed. "
              + "This will be ignored and returned as batch item failure.",
          messageId,
          e);
      recordBatchItemFailure(batchItemFailures, sqsMessage);
    }
  }

  private void recordBatchItemFailure(
      List<BatchItemFailure> batchItemFailures, SQSMessage sqsMessage) {
    batchItemFailures.add(new BatchItemFailure(sqsMessage.getMessageId()));
  }

  private void deferMessageProcessing(
      SQSMessage sqsMessage, List<BatchItemFailure> batchItemFailures) {
    LOGGER.info("Deferring processing of message [{}]", sqsMessage.getMessageId());
    if (!deferMessageProcessing.apply(sqsMessage)) {
      LOGGER.error("Failed to defer processing of message [{}]", sqsMessage.getMessageId());
      enqueueMessage(DEAD_LETTER_QUEUE_NAME, deadLetterQueue, sqsMessage, batchItemFailures);
    }
    recordBatchItemFailure(batchItemFailures, sqsMessage);
  }
}
