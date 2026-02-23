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

import domain.commands.BaseCommand;
import domain.exceptions.TransientException;
import infrastructure.functions.lambda.serializers.internal.DefaultInternalTransportJsonSerializer;
import infrastructure.mapping.PassthroughMapper;
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

class SqsQueueCommandDispatcherTest {

  AutoCloseable openMocks;
  @Mock SqsClient sqsClientMock;
  @Captor ArgumentCaptor<SendMessageRequest> sendMessageRequestCaptor;
  DefaultInternalTransportJsonSerializer serializer = new DefaultInternalTransportJsonSerializer();
  SqsQueueCommandDispatcher<BaseCommand, BaseCommand> dispatcher; // class under test

  @BeforeEach
  void setUp() {
    openMocks = openMocks(this);

    when(sqsClientMock.getQueueUrl(any(GetQueueUrlRequest.class)))
        .thenReturn(GetQueueUrlResponse.builder().queueUrl("testQueueUrl").build());

    dispatcher =
        new SqsQueueCommandDispatcher<>(
            sqsClientMock, "testQueueName", serializer, new PassthroughMapper());
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

    final BaseCommand mockCommand = new BaseCommand() {};

    // method under test
    dispatcher.dispatch(mockCommand);

    verify(sqsClientMock).sendMessage(sendMessageRequestCaptor.capture());
    assertEquals(
        serializer.serialize(mockCommand), sendMessageRequestCaptor.getValue().messageBody());
  }

  @Test
  void dispatch_when_http_status_not_200_then_throws_TransientException() {
    when(sqsClientMock.sendMessage(any(SendMessageRequest.class)))
        .thenReturn(
            (SendMessageResponse)
                SendMessageResponse.builder()
                    .sdkHttpResponse(SdkHttpResponse.builder().statusCode(500).build())
                    .build());

    final BaseCommand mockCommand = new BaseCommand() {};

    // method under test
    assertThrows(TransientException.class, () -> dispatcher.dispatch(mockCommand));
  }
}
