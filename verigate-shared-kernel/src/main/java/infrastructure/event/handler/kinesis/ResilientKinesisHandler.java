/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.event.handler.kinesis;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent.KinesisEventRecord;
import domain.events.BaseEvent;
import infrastructure.event.handler.factories.InfrastructureEventHandlerFactory;
import infrastructure.functions.lambda.serializers.internal.InternalTransportJsonSerializer;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** ResilientKinesisHandler. */
public abstract class ResilientKinesisHandler<EventT extends BaseEvent<?>>
    implements RequestHandler<KinesisEvent, Void> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResilientKinesisHandler.class);

  private final InternalTransportJsonSerializer serializer;
  private final Class<EventT> clazz;
  private final InfrastructureEventHandlerFactory<KinesisEventRecord, EventT>
      infrastructureEventHandlerFactory;

  /** Initialises the ResilientKinesisHandler. */
  protected ResilientKinesisHandler(
      InternalTransportJsonSerializer serializer,
      InfrastructureEventHandlerFactory<KinesisEventRecord, EventT> eventHandlerFactory,
      Class<EventT> clazz) {
    this.serializer = serializer;
    this.infrastructureEventHandlerFactory = eventHandlerFactory;
    this.clazz = clazz;
  }

  @Override
  public Void handleRequest(KinesisEvent event, Context context) {
    final List<KinesisEventRecord> records = Objects.requireNonNull(event).getRecords();
    if (records == null) {
      LOGGER.warn("Unexpected KinesisEvent with no records. Non-Kinesis invocation?");
      return null;
    }
    if (records.isEmpty()) {
      LOGGER.warn("Empty KinesisEvent - skip handler.");
      return null;
    }
    LOGGER.info(
        "Handling Kinesis event with {} record(s) from stream {}",
        records.size(),
        records.getFirst().getEventSourceARN());
    var infrastructureHandler = infrastructureEventHandlerFactory.create();
    for (KinesisEventRecord record : records) {
      infrastructureHandler.handle(record, (raw) -> getEvent(record));
    }
    return null;
  }

  /**
   * This tells the resilient infra handler how to extract the event from the raw event where it
   * participates in the resilient algorithm.
   */
  private EventT getEvent(KinesisEventRecord record) {
    final ByteBuffer payload = record.getKinesis().getData();
    final var event = this.serializer.deserialize(payload, clazz);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(this.serializer.serialize(event));
    }
    return event;
  }
}
