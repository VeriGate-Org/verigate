/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.kinesis;

import domain.events.BaseEvent;
import domain.events.EventPublisher;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.nio.ByteBuffer;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.InternalFailureException;
import software.amazon.awssdk.services.kinesis.model.InvalidArgumentException;
import software.amazon.awssdk.services.kinesis.model.ProvisionedThroughputExceededException;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;
import software.amazon.awssdk.services.kinesis.model.ResourceNotFoundException;
import software.amazon.awssdk.services.sns.model.KmsAccessDeniedException;
import software.amazon.awssdk.services.sns.model.KmsDisabledException;
import software.amazon.awssdk.services.sns.model.KmsInvalidStateException;
import software.amazon.awssdk.services.sns.model.KmsNotFoundException;
import software.amazon.awssdk.services.sns.model.KmsThrottlingException;

/**
 * A publisher that sends events to an AWS Kinesis stream.
 * Subclasses are responsible for implementing any mapping and serialization.
 *
 * @param <EventT> The type of event to be published
 */
public abstract class AbstractKinesisEventPublisher <EventT extends BaseEvent<?>>
    implements EventPublisher<EventT> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(AbstractKinesisEventPublisher.class);

  private final KinesisClient kinesisClient;
  private final String streamName;

  protected AbstractKinesisEventPublisher(KinesisClient kinesisClient, String streamName) {
    this.kinesisClient = Objects.requireNonNull(kinesisClient);
    this.streamName = Objects.requireNonNull(streamName);
  }

  /**
   * Publishes an event to the Kinesis stream.
   *
   * @param event the event to publish
   * @throws PermanentException if a non-recoverable error occurs
   * @throws TransientException if a recoverable error occurs
   */
  @Override
  public final void publish(final EventT event) {
    LOGGER.debug("Publishing event");
    final ByteBuffer eventByteBuffer = eventToByteBuffer(event);
    publishEventToKinesis(event, eventByteBuffer);
  }

  /**
   * Converts the event to a raw byte buffer ready for publication to Kinesis.
   */
  protected abstract ByteBuffer eventToByteBuffer(final EventT event);

  /**
   * Publishes the bytebuffer version of the event to Kinesis.
   */
  private void publishEventToKinesis(final EventT event, final ByteBuffer eventByteBuffer) {
    try {
      final SdkBytes eventBytes = SdkBytes.fromByteBuffer(eventByteBuffer);
      final var request =
          PutRecordRequest.builder()
              .streamName(streamName)
              .partitionKey(event.getId().toString())
              .data(eventBytes)
              .build();
      LOGGER.debug("Created PutRecordRequest");
      final var response = kinesisClient.putRecord(request);
      LOGGER.info(
          "Successfully published event with response sequence number [{}] onto stream [{}]",
          response.sequenceNumber(),
          streamName);
    } catch (ProvisionedThroughputExceededException e) {
      throw new TransientException("Provisioned throughput exceeded for the stream", e);
    } catch (ResourceNotFoundException e) {
      throw new PermanentException("The specified stream does not exist", e);
    } catch (InvalidArgumentException e) {
      throw new PermanentException("An invalid argument was specified", e);
    } catch (KmsDisabledException e) {
      throw new PermanentException("The specified customer master key (CMK) is disabled", e);
    } catch (KmsInvalidStateException e) {
      throw new PermanentException("The specified CMK is not in a valid state", e);
    } catch (KmsAccessDeniedException e) {
      throw new PermanentException("Access to the specified CMK was denied", e);
    } catch (KmsNotFoundException e) {
      throw new PermanentException("The specified CMK was not found", e);
    } catch (KmsThrottlingException e) {
      throw new PermanentException("The request was throttled by KMS", e);
    } catch (InternalFailureException e) {
      throw new PermanentException("Internal service failure occurred", e);
    } catch (SdkException e) {
      throw new TransientException("An error occurred while trying to communicate with AWS", e);
    } catch (Exception e) {
      throw new PermanentException("An unexpected error occurred", e);
    }
  }
}
