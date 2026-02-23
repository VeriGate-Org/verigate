/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.kinesis;

import domain.events.BaseEvent;
import domain.exceptions.PermanentException;
import infrastructure.functions.lambda.serializers.internal.InternalTransportJsonSerializer;
import infrastructure.mapping.Mapper;
import java.nio.ByteBuffer;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.kinesis.KinesisClient;

/**
 * A publisher that sends events to an AWS Kinesis stream, using
 * the InternalTransportJsonSerializer.
 *
 * @param <EventT> the type of event to be published
 */
public final class KinesisEventPublisher<EventT extends BaseEvent<?>, DtoT>
    extends AbstractKinesisEventPublisher<EventT> {

  private static final Logger LOGGER = LoggerFactory.getLogger(KinesisEventPublisher.class);

  private final InternalTransportJsonSerializer serializer;
  private final Mapper mapper;

  /**
   * Constructs a new KinesisEventPublisher.
   *
   * @param streamName the name of the Kinesis stream
   * @param serializer the serializer to convert events to bytes
   * @param mapper for mapping between model and DTO objects
   */
  public KinesisEventPublisher(
      final KinesisClient kinesisClient,
      final String streamName,
      final InternalTransportJsonSerializer serializer,
      final Mapper mapper) {

    super(kinesisClient, streamName);
    this.serializer = Objects.requireNonNull(serializer);
    this.mapper = Objects.requireNonNull(mapper);
  }

  /**
   * Convert the event to a raw byte buffer ready for publication to Kinesis.
   * Uses the InternalTransportJsonSerializer.
   */
  @Override
  protected ByteBuffer eventToByteBuffer(final EventT event) {
    ByteBuffer eventByteBuffer;
    try {
      var dto = mapper.<DtoT, EventT>toDto(event);
      eventByteBuffer = this.serializer.serializeToBytes(dto);
      LOGGER.debug("Serialized event to bytes");
    } catch (Exception e) {
      throw new PermanentException("Failed to serialize event to bytes", e);
    }
    return eventByteBuffer;
  }
}
