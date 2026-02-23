/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.sqs;

import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import domain.messages.DeadLetterQueue;
import domain.messages.InvalidMessageQueue;
import domain.messages.MessageQueue;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;

/**
 * A message queue backed by SQS with support for handling the SQS Lambda event message type
 * ({@link SQSMessage}). It is called a "raw" message queue because it will not perform any
 * serialization or de-serialization of messages. The supplied {@link SQSMessage} already
 * has a serialized {@link String} version of the message in {@link SQSMessage#getBody()}.
 * This will be used as-is to enqueue new messages on the queue for example.
 */
public class SqsLambdaEventRawMessageQueue implements MessageQueue<SQSMessage>,
    InvalidMessageQueue<SQSMessage>, DeadLetterQueue<SQSMessage> {

  private final SqsClient sqsClient;
  private final String queueUrl;

  /**
   * Construct a new instance, notably without any serializer by design.
   */
  public SqsLambdaEventRawMessageQueue(
      SqsClient sqsClient, String queueName) {

    this.sqsClient = sqsClient;
    this.queueUrl =
        this.sqsClient
            .getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build())
            .queueUrl();
  }

  @Override
  public void enqueue(SQSMessage message) {
    SqsClientUtils.sendMessage(message.getBody(), sqsClient, queueUrl);
  }

  @Override
  public void dequeue(SQSMessage message) {
    SqsClientUtils.deleteMessage(message.getReceiptHandle(), sqsClient, queueUrl);
  }

  @Override
  public SQSMessage peek() {
    //TODO: Peeking on the queue returns a native "Message" from SQS. This will need conversion to
    //the Lambda event version ("SQSMessage") if needed.
    throw new UnsupportedOperationException(
        "SqsLambdaEventRawMessageQueue does not support peek()");
  }
}
