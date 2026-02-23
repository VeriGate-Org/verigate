/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 *
 */

package verigate.verification.cg.infrastructure.routing;

import com.google.inject.Inject;
import infrastructure.commands.QueueCommandDispatcher;
import infrastructure.functions.lambda.serializers.internal.InternalTransportJsonSerializer;
import infrastructure.mapping.Mapper;
import infrastructure.sqs.SqsQueueCommandDispatcher;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import software.amazon.awssdk.services.sqs.SqsClient;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.routing.QueueDispatcherFactory;

/**
 * Default implementation of {@link QueueDispatcherFactory} that creates and caches
 * {@link QueueCommandDispatcher} instances.
 *
 * <p>This implementation uses a ConcurrentHashMap to store dispatchers, which are created
 * on-demand and cached for future use. Each dispatcher is uniquely identified by queue name.
 */
public class DefaultQueueDispatcherFactory implements QueueDispatcherFactory {

  private final Map<String, QueueCommandDispatcher<VerifyPartyCommand>> dispatcherCache =
      new ConcurrentHashMap<>();

  private final SqsClient sqsClient;
  private final InternalTransportJsonSerializer jsonSerializer;
  private final Mapper mapper;

  /**
   * Creates a new instance.
   */
  @Inject
  public DefaultQueueDispatcherFactory(
      SqsClient sqsClient, InternalTransportJsonSerializer jsonSerializer, Mapper mapper) {

    this.sqsClient = sqsClient;
    this.jsonSerializer = jsonSerializer;
    this.mapper = mapper;
  }

  @Override
  public QueueCommandDispatcher<VerifyPartyCommand> getDispatcher(String queueName) {
    return dispatcherCache.computeIfAbsent(queueName, k -> createDispatcher(queueName));
  }

  /**
   * Creates a new QueueCommandDispatcher for the given queue name.
   *
   * @param queueName The name of the queue to create a dispatcher for
   * @return A new QueueCommandDispatcher instance
   */
  private QueueCommandDispatcher<VerifyPartyCommand> createDispatcher(String queueName) {
    return new SqsQueueCommandDispatcher<VerifyPartyCommand, VerifyPartyCommand>(
        sqsClient, queueName, jsonSerializer, mapper);
  }
}
