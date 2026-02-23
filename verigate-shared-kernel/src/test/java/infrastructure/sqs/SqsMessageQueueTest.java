/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.sqs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import domain.events.BaseEvent;
import domain.exceptions.TransientException;
import infrastructure.functions.lambda.serializers.internal.DefaultInternalTransportJsonSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

class SqsMessageQueueTest {

  AutoCloseable openMocks;
  @Mock SqsClient sqsClientMock;
  @Captor ArgumentCaptor<SendMessageRequest> sendMessageRequestCaptor;
  DefaultInternalTransportJsonSerializer serializer = new DefaultInternalTransportJsonSerializer();
  SqsMessageQueue<BaseEvent> messageQueue; // class under test

  @BeforeEach
  void setUp() {
    openMocks = openMocks(this);

    when(sqsClientMock.getQueueUrl(any(GetQueueUrlRequest.class)))
        .thenReturn(GetQueueUrlResponse.builder().queueUrl("testQueueUrl").build());

    messageQueue = new SqsMessageQueue<>(sqsClientMock, "testQueueName", serializer);
  }

  @AfterEach
  void afterEach() throws Exception {
    openMocks.close();
  }

  @Test
  void dispatch() {
    when(sqsClientMock.sendMessage(any(SendMessageRequest.class)))
        .thenReturn(
            (SendMessageResponse)
                SendMessageResponse.builder()
                    .sdkHttpResponse(SdkHttpResponse.builder().statusCode(200).build())
                    .build());

    final BaseEvent mockMessage = new BaseEvent() {};

    // method under test
    messageQueue.enqueue(mockMessage);

    verify(sqsClientMock).sendMessage(sendMessageRequestCaptor.capture());
    assertEquals(
        serializer.serialize(mockMessage), sendMessageRequestCaptor.getValue().messageBody());
  }

  @Test
  void dispatchWhenHttpStatusNot200ThenThrowsTransientException() {
    when(sqsClientMock.sendMessage(any(SendMessageRequest.class)))
        .thenReturn(
            (SendMessageResponse)
                SendMessageResponse.builder()
                    .sdkHttpResponse(SdkHttpResponse.builder().statusCode(500).build())
                    .build());

    final BaseEvent mockMessage = new BaseEvent() {};

    // method under test
    assertThrows(TransientException.class, () -> messageQueue.enqueue(mockMessage));
  }
}
