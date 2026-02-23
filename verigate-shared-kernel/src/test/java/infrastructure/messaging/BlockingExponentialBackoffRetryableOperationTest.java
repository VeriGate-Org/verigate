/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.messaging;

import static org.junit.jupiter.api.Assertions.*;

import domain.exceptions.CircuitState;
import domain.exceptions.TransientException;
import infrastructure.event.handler.RetryConfig;
import infrastructure.messaging.model.MockMessageProcessor;
import org.junit.jupiter.api.Test;

final class BlockingExponentialBackoffRetryableOperationTest {

  @Test
  void shouldExitIfNoProcessingException() {
    var backoffStrategy = new ExponentialBackoffWithJitterStrategy();
    var retryableOperation = new BlockingRetryableOperation<>(backoffStrategy);
    var retryConfig = new RetryConfig(3, 10, 1000);

    retryableOperation.process("The message", (message) -> {}, retryConfig);
  }

  @Test
  void shouldProcessSuccessfulAfterThirdRetry() {
    var mockMessageProcessor = new MockMessageProcessor(TransientException::new, 2);

    var backoffStrategy = new ExponentialBackoffWithJitterStrategy();
    var retryableOperation = new BlockingRetryableOperation<>(backoffStrategy);
    var retryConfig = new RetryConfig(3, 10, 1000);
    retryableOperation.process(
        "The message", (message) -> mockMessageProcessor.process(), retryConfig);

    assertEquals(3, mockMessageProcessor.getProcessCount());
  }

  @Test
  void shouldProcessSuccessfulAfterForthRetry() {
    var mockMessageProcessor = new MockMessageProcessor(TransientException::new, 3);

    var backoffStrategy = new ExponentialBackoffWithJitterStrategy();
    var retryableOperation = new BlockingRetryableOperation<>(backoffStrategy);
    var retryConfig = new RetryConfig(3, 10, 1000);

    assertThrows(
        TransientException.class,
        () ->
            retryableOperation.process(
                "The message", (message) -> mockMessageProcessor.process(), retryConfig));

    assertEquals(3, mockMessageProcessor.getProcessCount());
  }

  @Test
  void shouldExitAfterThirdRetry() {
    var mockMessageProcessor = new MockMessageProcessor(TransientException::new, 10);

    var backoffStrategy = new ExponentialBackoffWithJitterStrategy();
    var retryableOperation = new BlockingRetryableOperation<>(backoffStrategy);
    var retryConfig = new RetryConfig(3, 10, 1000);

    assertThrows(
        TransientException.class,
        () ->
            retryableOperation.process(
                "The message", (message) -> mockMessageProcessor.process(), retryConfig));

    assertEquals(3, mockMessageProcessor.getProcessCount());
  }

  @Test
  void shouldExitAfterSeventhRetry() {
    var mockMessageProcessor = new MockMessageProcessor(TransientException::new, 10);

    var backoffStrategy = new ExponentialBackoffWithJitterStrategy();
    var retryableOperation = new BlockingRetryableOperation<>(backoffStrategy);
    var retryConfig = new RetryConfig(3, 10, 1000);

    assertThrows(
        TransientException.class,
        () ->
            retryableOperation.process(
                "The message", (message) -> mockMessageProcessor.process(), retryConfig));

    assertEquals(3, mockMessageProcessor.getProcessCount());
  }

  @Test
  void shouldExitAfterFirstRetryDueToCircuitStateBeingOpen() {
    var mockMessageProcessor =
        new MockMessageProcessor(() -> new TransientException(null, CircuitState.OPEN), 10);

    var backoffStrategy = new ExponentialBackoffWithJitterStrategy();
    var retryableOperation = new BlockingRetryableOperation<>(backoffStrategy);
    var retryConfig = new RetryConfig(3, 10, 1000);

    assertThrows(
        TransientException.class,
        () ->
            retryableOperation.process(
                "The message", (message) -> mockMessageProcessor.process(), retryConfig));

    assertEquals(1, mockMessageProcessor.getProcessCount());
  }

  @Test
  void shouldExitAfterFirstRetryDueToCircuitStateBeingHalfOpen() {
    var mockMessageProcessor =
        new MockMessageProcessor(() -> new TransientException(null, CircuitState.HALF_OPEN), 10);

    var backoffStrategy = new ExponentialBackoffWithJitterStrategy();
    var retryableOperation = new BlockingRetryableOperation<>(backoffStrategy);
    var retryConfig = new RetryConfig(3, 10, 1000);

    assertThrows(
        TransientException.class,
        () ->
            retryableOperation.process(
                "The message", (message) -> mockMessageProcessor.process(), retryConfig));

    assertEquals(1, mockMessageProcessor.getProcessCount());
  }

  @Test
  void shouldExitAfterFirstRetryDueToCircuitStateBeingClosed() {
    var mockMessageProcessor =
        new MockMessageProcessor(() -> new TransientException(null, CircuitState.CLOSED), 10);

    var backoffStrategy = new ExponentialBackoffWithJitterStrategy();
    var retryableOperation = new BlockingRetryableOperation<>(backoffStrategy);
    var retryConfig = new RetryConfig(3, 10, 1000);

    assertThrows(
        TransientException.class,
        () ->
            retryableOperation.process(
                "The message", (message) -> mockMessageProcessor.process(), retryConfig));

    assertEquals(3, mockMessageProcessor.getProcessCount());
  }
}
