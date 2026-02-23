/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.events;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import crosscutting.resiliency.CircuitBreaker;
import domain.exceptions.CircuitState;
import domain.exceptions.TransientException;
import java.util.function.Supplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
final class CircuitBreakerEventPublisherDecoratorTest {

  private final Object testEvent = new Object();

  @Mock
  private EventPublisher<Object> decoratedPublisher;

  @Mock private CircuitBreaker circuitBreaker;

  @InjectMocks
  private CircuitBreakerEventPublisher<Object> decorator;

  @Captor
  private ArgumentCaptor<Supplier<Void>> supplierCaptor;

  @Test
  public void testPublishSuccess() {
    decorator = new CircuitBreakerEventPublisher<>(decoratedPublisher, circuitBreaker);
    Supplier<Void> decoratedSupplier =
        () -> {
          decoratedPublisher.publish(testEvent);
          return null;
        };
    doAnswer(invocation -> decoratedSupplier).when(circuitBreaker).decorate(any());

    decorator.publish(testEvent);

    verify(circuitBreaker).decorate(supplierCaptor.capture());
    verify(decoratedPublisher).publish(testEvent);
  }

  @Test
  public void testPublishCircuitBreakerOpenException() {
    decorator = new CircuitBreakerEventPublisher<>(decoratedPublisher, circuitBreaker);
    Supplier<Void> decoratedSupplier =
        () -> {
          throw new CircuitBreaker.CircuitBreakerOpenException("Circuit breaker is open");
        };
    when(circuitBreaker.decorate(any())).thenAnswer(invocation -> decoratedSupplier);

    TransientException exception =
        assertThrows(TransientException.class, () -> decorator.publish(testEvent));
    Assertions.assertEquals(CircuitState.OPEN, exception.getCircuitState());
    assert (exception.getMessage().contains("Circuit breaker is open"));

    verify(circuitBreaker).decorate(supplierCaptor.capture());
    verify(decoratedPublisher, never()).publish(testEvent);
  }

  @Test
  public void testPublishOtherException() {
    decorator = new CircuitBreakerEventPublisher<>(decoratedPublisher, circuitBreaker);
    Supplier<Void> decoratedSupplier =
        () -> {
          throw new RuntimeException("Unexpected error");
        };
    when(circuitBreaker.decorate(any())).thenAnswer(invocation -> decoratedSupplier);

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> decorator.publish(testEvent));
    Assertions.assertEquals("Unexpected error", exception.getMessage());

    verify(circuitBreaker).decorate(supplierCaptor.capture());
    verify(decoratedPublisher, never()).publish(testEvent);
  }
}
