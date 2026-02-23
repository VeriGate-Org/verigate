/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.resiliency;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import java.util.concurrent.Callable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class DefaultCircuitBreakerTest {

  AutoCloseable openMocks;

  @Mock private CircuitBreaker resilience4JCircuitBreakerMock;

  @InjectMocks private crosscutting.resiliency.DefaultCircuitBreaker circuitBreaker;

  @BeforeEach
  void setUp() {
    openMocks = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() throws Exception {
    openMocks.close();
  }

  @Test
  void isOpen() {
    when(resilience4JCircuitBreakerMock.getState()).thenReturn(CircuitBreaker.State.OPEN);
    assertTrue(circuitBreaker.isOpen());
  }

  @Test
  void isClosed() {
    when(resilience4JCircuitBreakerMock.getState()).thenReturn(CircuitBreaker.State.CLOSED);
    assertTrue(circuitBreaker.isClosed());
  }

  @Test
  void open() {
    circuitBreaker.open();
    verify(resilience4JCircuitBreakerMock).transitionToOpenState();
  }

  @Test
  void close() {
    circuitBreaker.close();
    verify(resilience4JCircuitBreakerMock).transitionToClosedState();
  }

  @Test
  void reset() {
    circuitBreaker.reset();
    verify(resilience4JCircuitBreakerMock).reset();
  }

  @Test
  void executeCallableWhenCallNotPermittedExceptionThenThrowsCircuitBreakerOpenException()
      throws Exception {

    Callable<String> callable = () -> "test";
    when(resilience4JCircuitBreakerMock.executeCallable(callable))
        .thenThrow(mock(CallNotPermittedException.class));

    assertThrows(
        crosscutting.resiliency.CircuitBreaker.CircuitBreakerOpenException.class,
        () -> circuitBreaker.executeCallable(callable));
  }

  @Test
  void executeRunnableWhenCallNotPermittedExceptionThenThrowsCircuitBreakerOpenException() {

    Runnable runnable = () -> {};
    doThrow(Mockito.mock(CallNotPermittedException.class))
        .when(resilience4JCircuitBreakerMock)
        .executeRunnable(runnable);

    assertThrows(
        crosscutting.resiliency.CircuitBreaker.CircuitBreakerOpenException.class,
        () -> circuitBreaker.executeRunnable(runnable));
  }

  // TODO - Test broken on Mac
  // @Test
  // void decorate() {

  //   Supplier<String> supplierMock = mock(Supplier.class);
  //   when(resilience4JCircuitBreakerMock.decorateSupplier(any(Supplier.class)))
  //       .thenReturn(() -> supplierMock.get());
  //   when(supplierMock.get()).thenReturn("test");

  //   assertEquals("test", circuitBreaker.decorate(supplierMock).get());

  //   verify(supplierMock).get();
  // }
}
