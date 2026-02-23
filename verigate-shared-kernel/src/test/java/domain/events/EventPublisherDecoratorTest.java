/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.events;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import crosscutting.resiliency.CircuitBreaker;
import crosscutting.resiliency.Retryable;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

final class EventPublisherDecoratorTest {

  @Test
  void testDecoratorChain() {
    final AtomicBoolean published = new AtomicBoolean(false);

    @SuppressWarnings("unchecked")
    EventPublisher<String> publisher = mock(EventPublisher.class);
    doAnswer(
            invocation -> {
              published.set(true);
              return null;
            })
        .when(publisher)
        .publish(anyString());

    CircuitBreaker circuitBreaker = mock(CircuitBreaker.class);
    when(circuitBreaker.isOpen()).thenReturn(false);
    when(circuitBreaker.isClosed()).thenReturn(true);
    when(circuitBreaker.<String>decorate(any()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Retryable retryable = mock(Retryable.class);
    when(retryable.createRunnable(anyString(), any()))
        .thenAnswer(invocation -> invocation.getArgument(1));

    var factory =
        new PrimaryPublisherDecoratedFactory<String>(circuitBreaker, retryable, "testRetry");
    var decoratedPublisher = factory.create(publisher);

    decoratedPublisher.publish("event");

    assertNotNull(decoratedPublisher);
    assert (published.get());
    verify(publisher).publish("event");
    verify(circuitBreaker).<String>decorate(any());
    verify(retryable).createRunnable(anyString(), any());
  }
}
