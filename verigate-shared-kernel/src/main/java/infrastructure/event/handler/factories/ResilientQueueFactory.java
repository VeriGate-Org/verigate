/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.event.handler.factories;

import domain.messages.MessageQueue;

/**
 * This interface defines a factory for creating connections to resilient queues specialized in
 * enqueueing two types of data: events and raw messages. It provides implementations
 * for dead letter scenarios and for handling invalid messages, each equipped with resilience and
 * retry mechanisms.
 *
 * <p> The queue itself could be concretely implemented using SQS, Kafka, or any other messaging
 * service while the factory provides a connection to this service. The connection to the queue can
 * be implemented with DynamoDB or similar storage service. </p>
 *
 * @param <RawMessageT> the type of the raw message handled by the invalid message queue
 * @param <EventT> the type of event handled by the dead letter queue
 */
public interface ResilientQueueFactory<RawMessageT, EventT> {

  /**
   * Creates and returns an implementation that is responsible for enqueueing
   * messages to a dead letter queue. This is typically used for messages that cannot be
   * processed normally and need special handling, possibly due to errors or failures in the
   * processing pipeline.
   */
  MessageQueue<EventT> createDeadLetterQueue();

  /**
   * Creates and returns an implementation that is responsible for enqueueing raw
   * messages that are deemed invalid. This ensures that invalid messages are handled in a
   * way that can facilitate error handling, diagnostics, or alternative processing workflows.
   */
  MessageQueue<RawMessageT> createInvalidMessageQueue();

  /**
   * Creates and returns an implementation that is responsible for reordering
   * messages queue.
   */
  MessageQueue<EventT> createReorderQueue(EventT event);
}
