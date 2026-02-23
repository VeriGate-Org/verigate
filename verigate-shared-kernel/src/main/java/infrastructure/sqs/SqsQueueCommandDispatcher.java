/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.sqs;

import com.google.inject.Inject;
import infrastructure.commands.QueueCommandDispatcher;
import infrastructure.functions.lambda.serializers.internal.InternalTransportJsonSerializer;
import infrastructure.mapping.Mapper;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;

/** SQS implementation of {@link QueueCommandDispatcher}. */
public final class SqsQueueCommandDispatcher<CommandT, DtoT>
    implements QueueCommandDispatcher<CommandT> {

  private final SqsClient sqsClient;
  private final InternalTransportJsonSerializer jsonSerializer;
  private final String queueUrl;
  private final Mapper mapper;

  /** Constructs a new instance. */
  @Inject
  public SqsQueueCommandDispatcher(
      SqsClient sqsClient,
      String queueName,
      InternalTransportJsonSerializer jsonSerializer,
      Mapper mapper) {

    this.sqsClient = sqsClient;
    this.jsonSerializer = jsonSerializer;
    this.queueUrl =
        this.sqsClient
            .getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build())
            .queueUrl();
    this.mapper = mapper;
  }

  /** {@inheritDoc} */
  @Override
  public void dispatch(CommandT command) {
    var commandDto = mapper.toDto(command);
    SqsClientUtils.sendMessage(jsonSerializer.serialize(commandDto), sqsClient, queueUrl);
  }
}
