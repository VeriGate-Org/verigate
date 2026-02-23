/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.kinesis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import domain.events.BaseEvent;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import infrastructure.functions.lambda.serializers.internal.InternalTransportJsonSerializer;
import infrastructure.mapping.PassthroughMapper;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Supplier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.InternalFailureException;
import software.amazon.awssdk.services.kinesis.model.InvalidArgumentException;
import software.amazon.awssdk.services.kinesis.model.ProvisionedThroughputExceededException;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;
import software.amazon.awssdk.services.kinesis.model.PutRecordResponse;
import software.amazon.awssdk.services.kinesis.model.ResourceNotFoundException;
import software.amazon.awssdk.services.sns.model.KmsAccessDeniedException;
import software.amazon.awssdk.services.sns.model.KmsDisabledException;
import software.amazon.awssdk.services.sns.model.KmsInvalidStateException;
import software.amazon.awssdk.services.sns.model.KmsNotFoundException;
import software.amazon.awssdk.services.sns.model.KmsThrottlingException;

final class KinesisEventPublisherTest {

  private static final UUID TEST_EVENT_ID = UUID.randomUUID();
  private static final Integer LOGICAL_CLOCK_READING = null;
  private static final String TEST_EVENT_ID_STR = TEST_EVENT_ID.toString();
  private static final String STREAM_NAME = "testStream";

  @Mock private KinesisClient kinesisClient;
  @Mock private InternalTransportJsonSerializer serializer;
  @Captor private ArgumentCaptor<PutRecordRequest> putRecordRequestCaptor;
  private KinesisEventPublisher<TestEvent, TestEvent> kinesisEventPublisher;
  private AutoCloseable closeable;
  private TestEvent testEvent;

  @BeforeEach
  public void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    testEvent = createTestEvent();
    kinesisEventPublisher =
        new KinesisEventPublisher<>(
            kinesisClient, STREAM_NAME, serializer, new PassthroughMapper());
  }

  @AfterEach
  public void tearDown() throws Exception {
    closeable.close();
  }

  private TestEvent createTestEvent() {
    return new TestEvent(
        TEST_EVENT_ID, "detailType", Instant.now(), Instant.now(), LOGICAL_CLOCK_READING);
  }

  @Test
  public void testPublishSuccess() {
    ByteBuffer byteBuffer = ByteBuffer.wrap("serializedData".getBytes());
    when(serializer.serializeToBytes(testEvent)).thenReturn(byteBuffer);

    PutRecordResponse response = PutRecordResponse.builder().sequenceNumber("12345").build();
    when(kinesisClient.putRecord(any(PutRecordRequest.class))).thenReturn(response);

    kinesisEventPublisher.publish(testEvent);

    verify(kinesisClient).putRecord(putRecordRequestCaptor.capture());
    PutRecordRequest capturedRequest = putRecordRequestCaptor.getValue();
    assertEquals(STREAM_NAME, capturedRequest.streamName());
    assertEquals(TEST_EVENT_ID_STR, capturedRequest.partitionKey());
    assertEquals(SdkBytes.fromByteBuffer(byteBuffer), capturedRequest.data());
  }

  @Test
  public void testSerializationFailureThrowsPermanentException() {
    when(serializer.serializeToBytes(testEvent))
        .thenThrow(new RuntimeException("Serialization failed"));

    PermanentException exception =
        assertThrows(PermanentException.class, () -> kinesisEventPublisher.publish(testEvent));

    assertEquals("Failed to serialize event to bytes", exception.getMessage());
    verify(kinesisClient, never()).putRecord(any(PutRecordRequest.class));
  }

  /**
   * Utility test method to simplify exception testing.
   *
   * @param expectedExceptionClass The class of exception expected when calling
   *     eventPublisher.publish().
   * @param underlyingExceptionSupplier When kinesisClient.putRecord() is called, throw this
   *     exception from the mock.
   * @param expectedExceptionMessage The string value expected when interrogating the returned
   *     exception message
   * @param <ExpectedT> Type of the expected exception from eventPublisher.publish()
   * @param <UnderlyingT> Type of the underlying exception thrown by kinesisClient.putRecord()
   */
  private <ExpectedT extends Exception, UnderlyingT extends Exception>
      void assertKinesisClientException(
          Class<ExpectedT> expectedExceptionClass,
          Supplier<UnderlyingT> underlyingExceptionSupplier,
          String expectedExceptionMessage) {
    ByteBuffer byteBuffer = ByteBuffer.wrap("serializedData".getBytes());
    when(serializer.serializeToBytes(testEvent)).thenReturn(byteBuffer);

    when(kinesisClient.putRecord(any(PutRecordRequest.class)))
        .thenThrow(underlyingExceptionSupplier.get());

    ExpectedT exception =
        assertThrows(expectedExceptionClass, () -> kinesisEventPublisher.publish(testEvent));

    assertEquals(expectedExceptionMessage, exception.getMessage());
  }

  @Test
  public void testProvisionedThroughputExceededExceptionThrowsTransientException()
      throws Exception {
    assertKinesisClientException(
        TransientException.class,
        () -> ProvisionedThroughputExceededException.builder().build(),
        "Provisioned throughput exceeded for the stream");
  }

  @Test
  public void testResourceNotFoundExceptionThrowsPermanentException() {
    assertKinesisClientException(
        PermanentException.class,
        () -> ResourceNotFoundException.builder().build(),
        "The specified stream does not exist");
  }

  @Test
  public void testInvalidArgumentExceptionThrowsPermanentException() {
    assertKinesisClientException(
        PermanentException.class,
        () -> InvalidArgumentException.builder().build(),
        "An invalid argument was specified");
  }

  @Test
  public void testKMSDisabledExceptionThrowsPermanentException() {
    assertKinesisClientException(
        PermanentException.class,
        () -> KmsDisabledException.builder().build(),
        "The specified customer master key (CMK) is disabled");
  }

  @Test
  public void testKMSInvalidStateExceptionThrowsPermanentException() {
    assertKinesisClientException(
        PermanentException.class,
        () -> KmsInvalidStateException.builder().build(),
        "The specified CMK is not in a valid state");
  }

  @Test
  public void testKMSAccessDeniedExceptionThrowsPermanentException() {
    assertKinesisClientException(
        PermanentException.class,
        () -> KmsAccessDeniedException.builder().build(),
        "Access to the specified CMK was denied");
  }

  @Test
  public void testKMSNotFoundExceptionThrowsPermanentException() {
    assertKinesisClientException(
        PermanentException.class,
        () -> KmsNotFoundException.builder().build(),
        "The specified CMK was not found");
  }

  @Test
  public void testKMSThrottlingExceptionThrowsPermanentException() {
    assertKinesisClientException(
        PermanentException.class,
        () -> KmsThrottlingException.builder().build(),
        "The request was throttled by KMS");
  }

  @Test
  public void testInternalFailureExceptionThrowsPermanentException() {
    assertKinesisClientException(
        PermanentException.class,
        () -> InternalFailureException.builder().build(),
        "Internal service failure occurred");
  }

  @Test
  public void testAmazonClientExceptionThrowsTransientException() {
    assertKinesisClientException(
        TransientException.class,
        () -> SdkException.builder().message("").build(),
        "An error occurred while trying to communicate with AWS");
  }

  @Test
  public void testUnexpectedExceptionThrowsPermanentException() {
    assertKinesisClientException(
        PermanentException.class,
        () -> new RuntimeException("Unexpected exception"),
        "An unexpected error occurred");
  }

  private class TestEvent extends BaseEvent<String> {
    public TestEvent(
        UUID id,
        String detailType,
        Instant noticedDate,
        Instant effectedDate,
        Integer logicalClockReading) {
      super(id, detailType, noticedDate, effectedDate, logicalClockReading);
    }
  }
}
