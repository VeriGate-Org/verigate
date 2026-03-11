package verigate.billing.infrastructure.functions.lambda.handlers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import verigate.billing.application.handlers.UsageEventHandler;
import verigate.billing.infrastructure.functions.lambda.di.factories.UsageEventDependencyFactory;

/**
 * Integration tests verifying event propagation from Kinesis to the billing
 * handler. Tests event schema compatibility between the command-gateway
 * (producer) and billing service (consumer), and idempotent processing.
 */
@ExtendWith(MockitoExtension.class)
class EventPropagationIT {

  @Mock private UsageEventDependencyFactory factory;
  @Mock private UsageEventHandler usageEventHandler;
  @Mock private Context context;

  private UsageEventConsumerLambdaHandler handler;

  @BeforeEach
  void setUp() {
    when(factory.getUsageEventHandler()).thenReturn(usageEventHandler);
    handler = new UsageEventConsumerLambdaHandler(factory);
  }

  @Test
  @DisplayName("VerificationCompletedEvent schema from command-gateway is compatible")
  void verificationCompletedEventSchemaIsCompatible() {
    String event = buildVerificationCompletedEvent(
        "ver-001",
        "partner-123",
        "IDENTITY_VERIFICATION",
        "SUCCESS",
        "DHA",
        LocalDateTime.of(2025, 6, 15, 10, 30, 0));

    KinesisEvent kinesisEvent = createKinesisEvent(List.of(event));

    assertDoesNotThrow(() -> handler.handleRequest(kinesisEvent, context));
    verify(usageEventHandler).handle(event);
  }

  @Test
  @DisplayName("CIPC adapter event schema is compatible with billing consumer")
  void cipcAdapterEventSchemaIsCompatible() {
    String event = buildVerificationCompletedEvent(
        "ver-cipc-001",
        "partner-456",
        "COMPANY_VERIFICATION",
        "SUCCESS",
        "CIPC",
        LocalDateTime.of(2025, 6, 15, 11, 0, 0));

    KinesisEvent kinesisEvent = createKinesisEvent(List.of(event));

    assertDoesNotThrow(() -> handler.handleRequest(kinesisEvent, context));
    verify(usageEventHandler).handle(event);
  }

  @Test
  @DisplayName("Event with FAILURE outcome is processed correctly")
  void failureOutcomeEventIsProcessed() {
    String event = buildVerificationCompletedEvent(
        "ver-fail-001",
        "partner-789",
        "SANCTIONS_SCREENING",
        "FAILURE",
        "WorldCheck",
        LocalDateTime.now());

    KinesisEvent kinesisEvent = createKinesisEvent(List.of(event));

    assertDoesNotThrow(() -> handler.handleRequest(kinesisEvent, context));
    verify(usageEventHandler).handle(event);
  }

  @Test
  @DisplayName("Event with SYSTEM_ERROR outcome is processed correctly")
  void systemErrorOutcomeEventIsProcessed() {
    String event = buildVerificationCompletedEvent(
        "ver-err-001",
        "partner-101",
        "CREDIT_CHECK",
        "SYSTEM_ERROR",
        "CreditBureau",
        LocalDateTime.now());

    KinesisEvent kinesisEvent = createKinesisEvent(List.of(event));

    assertDoesNotThrow(() -> handler.handleRequest(kinesisEvent, context));
    verify(usageEventHandler).handle(event);
  }

  @Test
  @DisplayName("Duplicate events are passed to handler (handler ensures idempotency)")
  void duplicateEventsArePassedToHandler() {
    String event = buildVerificationCompletedEvent(
        "ver-dup-001",
        "partner-dup",
        "IDENTITY_VERIFICATION",
        "SUCCESS",
        "DHA",
        LocalDateTime.now());

    KinesisEvent kinesisEvent = createKinesisEvent(List.of(event, event));

    assertDoesNotThrow(() -> handler.handleRequest(kinesisEvent, context));
    verify(usageEventHandler, times(2)).handle(event);
  }

  @Test
  @DisplayName("Event with extra unknown fields is still processed (forward compatible)")
  void eventWithExtraFieldsIsProcessed() {
    String event =
        """
        {
          "eventId": "%s",
          "verificationId": "ver-extra-001",
          "partnerId": "partner-extra",
          "verificationType": "IDENTITY_VERIFICATION",
          "outcome": "SUCCESS",
          "eventTimestamp": "2025-06-15T10:30:00",
          "provider": "DHA",
          "correlationId": "corr-extra-001",
          "unknownNewField": "some-value",
          "anotherNewField": 42
        }
        """
            .formatted(UUID.randomUUID());

    KinesisEvent kinesisEvent = createKinesisEvent(List.of(event));

    assertDoesNotThrow(() -> handler.handleRequest(kinesisEvent, context));
    verify(usageEventHandler).handle(argThat(json -> json.contains("ver-extra-001")));
  }

  @Test
  @DisplayName("Batch of events from multiple verification types processes all records")
  void batchFromMultipleVerificationTypesProcessesAll() {
    String event1 = buildVerificationCompletedEvent(
        "ver-batch-1", "partner-batch", "IDENTITY_VERIFICATION",
        "SUCCESS", "DHA", LocalDateTime.now());
    String event2 = buildVerificationCompletedEvent(
        "ver-batch-2", "partner-batch", "COMPANY_VERIFICATION",
        "SUCCESS", "CIPC", LocalDateTime.now());
    String event3 = buildVerificationCompletedEvent(
        "ver-batch-3", "partner-batch", "SANCTIONS_SCREENING",
        "FAILURE", "WorldCheck", LocalDateTime.now());

    KinesisEvent kinesisEvent = createKinesisEvent(List.of(event1, event2, event3));

    assertDoesNotThrow(() -> handler.handleRequest(kinesisEvent, context));
    verify(usageEventHandler, times(3)).handle(org.mockito.ArgumentMatchers.anyString());
  }

  @Test
  @DisplayName("Event with null timestamp uses current time (handler responsibility)")
  void eventWithNullTimestampIsProcessed() {
    String event =
        """
        {
          "eventId": "%s",
          "verificationId": "ver-notime-001",
          "partnerId": "partner-notime",
          "verificationType": "IDENTITY_VERIFICATION",
          "outcome": "SUCCESS",
          "provider": "DHA"
        }
        """
            .formatted(UUID.randomUUID());

    KinesisEvent kinesisEvent = createKinesisEvent(List.of(event));

    assertDoesNotThrow(() -> handler.handleRequest(kinesisEvent, context));
    verify(usageEventHandler).handle(argThat(json -> json.contains("ver-notime-001")));
  }

  @Test
  @DisplayName("Empty Kinesis batch returns null without processing")
  void emptyBatchReturnsNull() {
    KinesisEvent event = new KinesisEvent();
    event.setRecords(List.of());

    Void result = handler.handleRequest(event, context);

    assertNull(result);
  }

  // --- helpers ---

  private String buildVerificationCompletedEvent(
      String verificationId,
      String partnerId,
      String verificationType,
      String outcome,
      String provider,
      LocalDateTime timestamp) {
    return """
        {
          "eventId": "%s",
          "verificationId": "%s",
          "partnerId": "%s",
          "verificationType": "%s",
          "outcome": "%s",
          "eventTimestamp": "%s",
          "provider": "%s",
          "correlationId": "%s"
        }
        """
        .formatted(
            UUID.randomUUID(),
            verificationId,
            partnerId,
            verificationType,
            outcome,
            timestamp.toString(),
            provider,
            "corr-" + UUID.randomUUID());
  }

  private KinesisEvent createKinesisEvent(List<String> payloads) {
    KinesisEvent event = new KinesisEvent();
    List<KinesisEvent.KinesisEventRecord> records = new ArrayList<>();

    int seq = 1;
    for (String payload : payloads) {
      KinesisEvent.KinesisEventRecord record = new KinesisEvent.KinesisEventRecord();
      KinesisEvent.Record kinesis = new KinesisEvent.Record();
      kinesis.setData(ByteBuffer.wrap(payload.getBytes(StandardCharsets.UTF_8)));
      kinesis.setSequenceNumber(String.valueOf(seq++));
      kinesis.setPartitionKey("partition-key");
      record.setKinesis(kinesis);
      records.add(record);
    }

    event.setRecords(records);
    return event;
  }
}
