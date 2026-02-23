/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.event.handler;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

import domain.exceptions.HandlerDelayException;
import domain.messages.MessageQueue;
import infrastructure.messaging.RetryableOperation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

class RetryMessageQueueTest {

  AutoCloseable openMocks;

  @Mock
  MessageQueue<String> delegateQueueMock;

  @Mock
  RetryableOperation<String> retryableOperationMock;

  @Spy
  RetryConfig retryConfig = new RetryConfig(3, 1000, 2);

  /** Class under test. */
  @InjectMocks
  RetryMessageQueue<String> retryQueue;

  @BeforeEach
  void beforeEach() {
    openMocks = openMocks(this);
  }

  @AfterEach
  void afterEach() throws Exception {
    openMocks.close();
  }

  @Test
  void enqueue() {

    // method under test
    retryQueue.enqueue("test");

    verify(retryableOperationMock).process(eq("test"), any(), eq(retryConfig));
  }

  @Test
  void enqueueWhenExceptionThenThrowsHandlerDelayException() {

    doThrow(new RuntimeException("test"))
        .when(retryableOperationMock).process(any(), any(), any());

    // method under test
    assertThrows(HandlerDelayException.class, () -> retryQueue.enqueue("test"));
  }
}
