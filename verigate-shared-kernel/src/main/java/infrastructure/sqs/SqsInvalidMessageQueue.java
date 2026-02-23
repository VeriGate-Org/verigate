/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.sqs;

import domain.messages.InvalidMessageQueue;
import domain.messages.MessageQueue;
import infrastructure.functions.lambda.serializers.internal.InternalTransportJsonSerializer;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;

/**
 * SQS implementation of {@link MessageQueue} that assumes the message is invalid.
 * As such, this implementation does NOT throw a {@link domain.exceptions.PermanentException} if
 * the message could not be serialized, but tries on a best-effort basis to send useful information
 * to the queue.
 */
public final class SqsInvalidMessageQueue<T> implements InvalidMessageQueue<T> {

  private static final Logger logger = LoggerFactory.getLogger(SqsInvalidMessageQueue.class);

  private final SqsClient sqsClient;
  private final InternalTransportJsonSerializer jsonSerializer;
  private final String queueUrl;

  /** Constructs a new instance. */
  public SqsInvalidMessageQueue(
      SqsClient sqsClient, String queueName, InternalTransportJsonSerializer jsonSerializer) {

    this.sqsClient = sqsClient;
    this.jsonSerializer = jsonSerializer;
    this.queueUrl =
        this.sqsClient
            .getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build())
            .queueUrl();
  }

  /** {@inheritDoc} */
  @Override
  public void enqueue(T rawMessage) {
    SqsClientUtils.sendMessage(marshall(rawMessage), sqsClient, queueUrl);
  }

  /** {@inheritDoc} */
  @Override
  public void dequeue(T rawMessage) {
    SqsClientUtils.deleteMessage(marshall(rawMessage), sqsClient, queueUrl);
  }

  /** {@inheritDoc} */
  @Override
  public T peek() {
    return (T) SqsClientUtils.peekAtMessage(sqsClient, queueUrl);
  }

  /**
   * Marshalls the specified object on a best effort basis into a string.
   */
  private String marshall(T rawMessage) {

    String returnString;
    try {
      returnString = jsonSerializer.serialize(rawMessage); // if byte[], will base64-encode
    } catch (Exception e) {
      logger.warn(
          "Failed to serialize invalid message to JSON: {}." + " Using Objects.toString instead.",
          rawMessage,
          e);
      returnString = Objects.toString(rawMessage);
    }
    return returnString;
  }
}
