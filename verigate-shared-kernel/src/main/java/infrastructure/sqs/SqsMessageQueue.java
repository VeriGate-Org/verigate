package infrastructure.sqs;

import domain.messages.MessageQueue;
import infrastructure.functions.lambda.serializers.internal.InternalTransportJsonSerializer;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;

/**
 * SQS implementation of {@link MessageQueue}.
 */
public final class SqsMessageQueue<T> implements MessageQueue<T> {

  private final SqsClient sqsClient;
  private final InternalTransportJsonSerializer jsonSerializer;
  private final String queueUrl;

  /** Constructs a new instance. */
  public SqsMessageQueue(
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
  public void enqueue(T message) {
    SqsClientUtils.sendMessage(jsonSerializer.serialize(message), sqsClient, queueUrl);
  }

  /** {@inheritDoc} */
  @Override
  public void dequeue(T message) {
    SqsClientUtils.deleteMessage(jsonSerializer.serialize(message), sqsClient, queueUrl);
  }

  /** {@inheritDoc} */
  @Override
  public T peek() {
    return (T) SqsClientUtils.peekAtMessage(sqsClient, queueUrl);
  }
}
