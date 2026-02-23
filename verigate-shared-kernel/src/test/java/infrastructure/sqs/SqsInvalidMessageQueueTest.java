/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.sqs;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import domain.exceptions.TransientException;
import infrastructure.functions.lambda.serializers.internal.DefaultInternalTransportJsonSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;
import software.amazon.awssdk.utils.BinaryUtils;

class SqsInvalidMessageQueueTest {

  AutoCloseable openMocks;
  @Mock SqsClient sqsClientMock;
  @Captor ArgumentCaptor<SendMessageRequest> sendMessageRequestCaptor;

  @Spy
  DefaultInternalTransportJsonSerializer serializer = new DefaultInternalTransportJsonSerializer();

  @BeforeEach
  void setUp() {
    openMocks = openMocks(this);

    when(sqsClientMock.getQueueUrl(any(GetQueueUrlRequest.class)))
        .thenReturn(GetQueueUrlResponse.builder().queueUrl("testQueueUrl").build());
  }

  @AfterEach
  void afterEach() throws Exception {
    openMocks.close();
  }

  @Test
  void enqueue() {
    when(sqsClientMock.sendMessage(any(SendMessageRequest.class)))
        .thenReturn(
            (SendMessageResponse)
                SendMessageResponse.builder()
                    .sdkHttpResponse(SdkHttpResponse.builder().statusCode(200).build())
                    .build());

    final var mockMessage = new KinesisEvent.KinesisEventRecord();

    final var invalidMessageQueue =
        new SqsInvalidMessageQueue<KinesisEvent.KinesisEventRecord>(
            sqsClientMock, "testQueueName", serializer);

    // method under test
    invalidMessageQueue.enqueue(mockMessage);

    verify(sqsClientMock).sendMessage(sendMessageRequestCaptor.capture());
    assertEquals(
        serializer.serialize(mockMessage), sendMessageRequestCaptor.getValue().messageBody());
  }

  @Test
  void enqueue_byte_array() {
    when(sqsClientMock.sendMessage(any(SendMessageRequest.class)))
        .thenReturn(
            (SendMessageResponse)
                SendMessageResponse.builder()
                    .sdkHttpResponse(SdkHttpResponse.builder().statusCode(200).build())
                    .build());

    byte[] bytes = "test".getBytes(); // [116, 101, 115, 116]

    final var invalidMessageQueue =
        new SqsInvalidMessageQueue<byte[]>(sqsClientMock, "testQueueName", serializer);

    // method under test
    invalidMessageQueue.enqueue(bytes);

    verify(sqsClientMock).sendMessage(sendMessageRequestCaptor.capture());

    final var base64Encoded = sendMessageRequestCaptor.getValue().messageBody(); // "dGVzdA=="
    assertArrayEquals(bytes, BinaryUtils.fromBase64(base64Encoded.replaceAll("\"", "")));
  }

  /**
   * In the case that the invalid message fails to be published to the queue, as detected by a
   * non-200 status response, a TransientException should be thrown, such that it can be retried.
   * The status code handling logic could be tweaked so as to differentiate between what can be
   * considered permanent vs transient exceptions, if needed.
   */
  @Test
  void enqueue_when_http_status_not_200_then_throws_TransientException() {
    when(sqsClientMock.sendMessage(any(SendMessageRequest.class)))
        .thenReturn(
            (SendMessageResponse)
                SendMessageResponse.builder()
                    .sdkHttpResponse(SdkHttpResponse.builder().statusCode(500).build())
                    .build());

    final var mockMessage = new KinesisEvent.KinesisEventRecord();

    final var invalidMessageQueue =
        new SqsInvalidMessageQueue<KinesisEvent.KinesisEventRecord>(
            sqsClientMock, "testQueueName", serializer);

    // method under test
    assertThrows(TransientException.class, () -> invalidMessageQueue.enqueue(mockMessage));
  }

  /**
   * In the case that the invalid message fails to be published to the queue, as detected by an
   * exception being thrown, a TransientException should be thrown, such that it can be retried. The
   * exception-catching logic could be tweaked so as to differentiate between what can be considered
   * permanent vs transient exceptions, if needed.
   */
  @Test
  void enqueue_when_any_exception_then_throws_TransientException() {
    when(sqsClientMock.sendMessage(any(SendMessageRequest.class)))
        .thenThrow(new RuntimeException("test"));

    final var mockMessage = new KinesisEvent.KinesisEventRecord();

    final var invalidMessageQueue =
        new SqsInvalidMessageQueue<KinesisEvent.KinesisEventRecord>(
            sqsClientMock, "testQueueName", serializer);

    // method under test
    assertThrows(TransientException.class, () -> invalidMessageQueue.enqueue(mockMessage));
  }

  /**
   * In the case where message object serialization fails, we simply do an {@code Objects.toString}
   * on the message. This case should be unlikely as the message object was successfully
   * deserialized from the input queue. Another approach could be to have the invalid message
   * publisher accept the raw input message as a string.
   */
  @Test
  void enqueue_when_serialize_fails_then_uses_Objects_toString() {
    when(sqsClientMock.sendMessage(any(SendMessageRequest.class)))
        .thenReturn(
            (SendMessageResponse)
                SendMessageResponse.builder()
                    .sdkHttpResponse(SdkHttpResponse.builder().statusCode(200).build())
                    .build());

    when(serializer.serialize(any())).thenThrow(new RuntimeException("test exception"));

    final var mockMessage = new KinesisEvent.KinesisEventRecord();

    final var invalidMessageQueue =
        new SqsInvalidMessageQueue<KinesisEvent.KinesisEventRecord>(
            sqsClientMock, "testQueueName", serializer);

    // method under test
    invalidMessageQueue.enqueue(mockMessage);

    verify(sqsClientMock).sendMessage(sendMessageRequestCaptor.capture());
    assertEquals(mockMessage.toString(), sendMessageRequestCaptor.getValue().messageBody());
  }

  /**
   * In the unlikely case where the raw message is null, current behaviour is simply to publish
   * "null".
   */
  @Test
  void enqueue_when_null_then_uses_null_string() {
    when(sqsClientMock.sendMessage(any(SendMessageRequest.class)))
        .thenReturn(
            (SendMessageResponse)
                SendMessageResponse.builder()
                    .sdkHttpResponse(SdkHttpResponse.builder().statusCode(200).build())
                    .build());

    when(serializer.serialize(any())).thenThrow(new RuntimeException("test exception"));

    final var invalidMessageQueue =
        new SqsInvalidMessageQueue<KinesisEvent.KinesisEventRecord>(
            sqsClientMock, "testQueueName", serializer);

    // method under test
    invalidMessageQueue.enqueue(null);

    verify(sqsClientMock).sendMessage(sendMessageRequestCaptor.capture());
    assertEquals("null", sendMessageRequestCaptor.getValue().messageBody());
  }
}
