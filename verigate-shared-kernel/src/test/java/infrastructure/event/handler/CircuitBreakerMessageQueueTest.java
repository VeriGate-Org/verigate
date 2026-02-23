/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.event.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import crosscutting.resiliency.CircuitBreaker;
import domain.messages.MessageQueue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CircuitBreakerMessageQueueTest {

  AutoCloseable openMocks;

  @Mock
  CircuitBreaker circuitBreakerMock;

  @Mock
  MessageQueue<String> messageQueueMock;

  @InjectMocks
  CircuitBreakerMessageQueue<String> circuitBreakerMessageQueue;

  @BeforeEach
  void setUp() {
    openMocks = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() throws Exception {
    openMocks.close();
  }

  @Test
  void enqueue() {

    circuitBreakerMessageQueue.enqueue("testMessage");

    verify(circuitBreakerMock).executeRunnable(any());
  }
}
