/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.opensanctions.infrastructure.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.events.BaseEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;
import verigate.verification.cg.domain.events.VerificationEventPublisher;

/**
 * Publishes verification domain events to an AWS Kinesis stream.
 */
public class KinesisVerificationEventPublisher implements VerificationEventPublisher {

  private static final Logger LOGGER =
      Logger.getLogger(KinesisVerificationEventPublisher.class.getName());

  private final KinesisClient kinesisClient;
  private final String streamName;
  private final ObjectMapper objectMapper;

  public KinesisVerificationEventPublisher(
      KinesisClient kinesisClient, String streamName, ObjectMapper objectMapper) {
    this.kinesisClient = kinesisClient;
    this.streamName = streamName;
    this.objectMapper = objectMapper;
  }

  @Override
  public void publish(List<BaseEvent> events) {
    for (BaseEvent event : events) {
      publishSingle(event);
    }
  }

  private void publishSingle(BaseEvent event) {
    try {
      byte[] payload = objectMapper.writeValueAsBytes(event);

      PutRecordRequest request =
          PutRecordRequest.builder()
              .streamName(streamName)
              .partitionKey(event.getId().toString())
              .data(SdkBytes.fromByteArray(payload))
              .build();

      var response = kinesisClient.putRecord(request);

      LOGGER.info(
          "Published verification event "
              + event.getId()
              + " to stream "
              + streamName
              + " (seq="
              + response.sequenceNumber()
              + ")");

    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "Failed to publish event " + event.getId() + " to Kinesis", e);
      throw new RuntimeException("Failed to publish verification event", e);
    }
  }
}
