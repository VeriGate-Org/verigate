/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.event.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import domain.exceptions.CircuitState;
import domain.exceptions.HandlerDelayException;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import infrastructure.event.handler.factories.DefaultResilientQueueFactory;
import infrastructure.event.handler.factories.ResilientQueueFactory;
import infrastructure.event.handler.model.ExtendedMockEventA;
import infrastructure.event.handler.model.ExtendedMockEventB;
import infrastructure.event.handler.model.ExtendedMockEventC;
import infrastructure.event.handler.model.MockDeadLetterQueue;
import infrastructure.event.handler.model.MockDeadLetterTracking;
import infrastructure.event.handler.model.MockEvent;
import infrastructure.event.handler.model.MockEventHandler;
import infrastructure.event.handler.model.MockExEventHandler;
import infrastructure.event.handler.model.MockIdempotentEventHandler;
import infrastructure.event.handler.model.MockInvalidMessageQueue;
import infrastructure.event.handler.model.MockReorderQueue;
import infrastructure.functions.lambda.serializers.http.DefaultJsonSerializer;
import infrastructure.functions.lambda.serializers.internal.DefaultInternalTransportJsonSerializer;
import infrastructure.messaging.BlockingRetryableOperation;
import infrastructure.messaging.DefaultRetryableOperationFactory;
import infrastructure.messaging.ExponentialBackoffWithJitterStrategy;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

final class ResilientEventHandlerTest {

  private static final RetryConfig RETRY_CONFIG = new RetryConfig(3, 2, 50);

  private static ResilientQueueFactory<String, MockEvent> createQueueFactory(
      RetryConfig retryConfig) {
    return new DefaultResilientQueueFactory<>(
        new MockInvalidMessageQueue(),
        new BlockingRetryableOperation<>(new ExponentialBackoffWithJitterStrategy()),
        new MockDeadLetterQueue<>(),
        new BlockingRetryableOperation<>(new ExponentialBackoffWithJitterStrategy()),
        new MockReorderQueue<>(),
        new BlockingRetryableOperation<>(new ExponentialBackoffWithJitterStrategy()),
        retryConfig);
  }

  private static ResilientQueueFactory<String, MockEvent> createQueueFactory(
      RetryConfig retryConfig,
      MockInvalidMessageQueue mockInvalidMessageQueue,
      MockDeadLetterQueue<MockEvent> mockDeadLetterQueue,
      MockReorderQueue<MockEvent> mockReorderQueue) {
    return new DefaultResilientQueueFactory<>(
        mockInvalidMessageQueue,
        new BlockingRetryableOperation<>(new ExponentialBackoffWithJitterStrategy()),
        mockDeadLetterQueue,
        new BlockingRetryableOperation<>(new ExponentialBackoffWithJitterStrategy()),
        mockReorderQueue,
        new BlockingRetryableOperation<>(new ExponentialBackoffWithJitterStrategy()),
        retryConfig);
  }

  // TODO: review this tests behaviour in light of idempotency changes

  @Test
  void shouldPassIfEventHandlerSucceeds() {
    var eventHandler = new MockEventHandler();
    var queueFactory = createQueueFactory(RETRY_CONFIG);

    var deadLetterEventHandler =
        new ResilientEventHandler<>(
            RETRY_CONFIG,
            new DefaultRetryableOperationFactory(),
            eventHandler,
            queueFactory,
            (e) -> false);

    assertNotNull(deadLetterEventHandler);

    deadLetterEventHandler.handle("", (raw) -> new MockEvent());

    assertEquals(1, eventHandler.getCallCount());
  }

  /**
   * Verifies that when an event handler throws a PermanentException, the event is directly enqueued
   * in the Dead Letter Queue (DLQ) with no retry attempts. This test simulates the occurrence of a
   * PermanentException to assess the system's response: 1. Immediate action is taken to move the
   * event to the DLQ, bypassing any retry mechanisms. 2. This approach is intended to efficiently
   * manage events that are identified as unprocessable on a permanent basis, facilitating their
   * segregation for subsequent analysis or alternative handling. The rationale behind this behavior
   * is to prevent futile retry attempts that are destined to fail, ensuring system resources are
   * utilized judiciously and only viable events are processed.
   */
  @Test
  void shouldHaveNoRetriesIfEventHandlerThrowsPermanentException() {
    var eventHandler = new MockEventHandler(() -> new PermanentException(null, null));
    var mockInvalidMessageQueue = new MockInvalidMessageQueue();
    var mockDeadLetterQueue = new MockDeadLetterQueue<MockEvent>();
    var mockReorderQueue = new MockReorderQueue<MockEvent>();
    var queueFactory =
        createQueueFactory(
            RETRY_CONFIG, mockInvalidMessageQueue, mockDeadLetterQueue, mockReorderQueue);

    AtomicInteger causalityTracker = new AtomicInteger();
    var deadLetterEventHandler =
        new ResilientEventHandler<>(
            RETRY_CONFIG,
            new DefaultRetryableOperationFactory(),
            eventHandler,
            queueFactory,
            (e) -> {
              causalityTracker.getAndIncrement();
              return false;
            });

    assertNotNull(deadLetterEventHandler);

    deadLetterEventHandler.handle("", (raw) -> new MockEvent());

    assertEquals(1, eventHandler.getCallCount());
    assertEquals(1, mockDeadLetterQueue.getCallCount());
    assertEquals(1, causalityTracker.get());
  }

  /**
   * Verifies that when an event handler throws an unexpected exception,
   * the event is directly enqueued in the Dead Letter Queue (DLQ) with no retry attempts.
   * Similar to PermanentException.
   */
  @Test
  void shouldHaveNoRetriesIfEventHandlerThrowsUnexpectedException() {
    var eventHandler = new MockEventHandler(() -> new RuntimeException("test"));
    var mockInvalidMessageQueue = new MockInvalidMessageQueue();
    var mockDeadLetterQueue = new MockDeadLetterQueue<MockEvent>();
    var mockReorderQueue = new MockReorderQueue<MockEvent>();
    var queueFactory =
        createQueueFactory(
            RETRY_CONFIG, mockInvalidMessageQueue, mockDeadLetterQueue, mockReorderQueue);

    AtomicInteger causalityTracker = new AtomicInteger();
    var deadLetterEventHandler =
        new ResilientEventHandler<>(
            RETRY_CONFIG,
            new DefaultRetryableOperationFactory(),
            eventHandler,
            queueFactory,
            (e) -> {
              causalityTracker.getAndIncrement();
              return false;
            });

    assertNotNull(deadLetterEventHandler);

    deadLetterEventHandler.handle("", (raw) -> new MockEvent());

    assertEquals(1, eventHandler.getCallCount());
    assertEquals(1, mockDeadLetterQueue.getCallCount());
    assertEquals(1, causalityTracker.get());
  }

  /**
   * Tests the system's ability to correctly move a valid event into the Dead Letter Queue (DLQ)
   * when an event with the same key already exists in the DLQ. This key represents a causal
   * relationship, indicating that events linked by this key must be processed in sequence. The test
   * ensures that: 1. The system can handle duplicate keys within the DLQ without loss or
   * duplication of data. 2. Sequential integrity and causal relationships between events are
   * maintained in error handling. 3. Events are correctly identified and routed to the DLQ,
   * preserving the order necessary for reprocessing.
   */
  @Test
  void shouldMoveValidEventToDlqIfKeyIsAlreadyPresentInTheDlq() {
    var eventHandler = new MockEventHandler(() -> null);
    var mockInvalidMessageQueue = new MockInvalidMessageQueue();
    var mockDeadLetterQueue = new MockDeadLetterQueue<MockEvent>();
    var mockReorderQueue = new MockReorderQueue<MockEvent>();
    var queueFactory =
        createQueueFactory(
            RETRY_CONFIG, mockInvalidMessageQueue, mockDeadLetterQueue, mockReorderQueue);

    AtomicInteger causalityTracker = new AtomicInteger();
    var deadLetterEventHandler =
        new ResilientEventHandler<>(
            RETRY_CONFIG,
            new DefaultRetryableOperationFactory(),
            eventHandler,
            queueFactory,
            (e) -> {
              causalityTracker.getAndIncrement();
              return true;
            });

    assertNotNull(deadLetterEventHandler);

    deadLetterEventHandler.handle("", (raw) -> new MockEvent());

    // The eventHandler must never be called as the event must be moved to the dead letter queue
    assertEquals(0, eventHandler.getCallCount());
    assertEquals(0, mockDeadLetterQueue.getCallCount());
    assertEquals(1, causalityTracker.get());
  }

  /**
   * Tests the handling of an event when a transient exception occurs, but the circuit to the
   * resource is open, indicating a temporary unavailability or fault. This test verifies that: 1.
   * In-process retries are immediately halted to prevent unnecessary load or further errors due to
   * the open circuit state. 2. The transient exception is promptly thrown to the designated
   * handler, bypassing standard retry mechanisms that might otherwise attempt immediate
   * reprocessing. 3. The system initiates a maximum delay out-of-process retry mechanism, such as
   * suspending a lambda function, to allow time for the circuit breaker to reset and close. This
   * approach ensures resources are not overburdened and provides a pause, increasing the likelihood
   * that subsequent processing attempts to occur under more favorable conditions. This process is
   * critical for maintaining system stability and ensuring efficient handling of temporary issues
   * without compromising data integrity or processing reliability.
   */
  @Test
  void shouldMoveOpenTransientExceptionEventToDlq() {
    var eventHandler = new MockEventHandler(() -> new TransientException(null, CircuitState.OPEN));
    var mockInvalidMessageQueue = new MockInvalidMessageQueue();
    var mockDeadLetterQueue = new MockDeadLetterQueue<MockEvent>();
    var mockReorderQueue = new MockReorderQueue<MockEvent>();
    var queueFactory =
        createQueueFactory(
            RETRY_CONFIG, mockInvalidMessageQueue, mockDeadLetterQueue, mockReorderQueue);

    AtomicInteger causalityTracker = new AtomicInteger();
    var deadLetterEventHandler =
        new ResilientEventHandler<>(
            RETRY_CONFIG,
            new DefaultRetryableOperationFactory(),
            eventHandler,
            queueFactory,
            (e) -> {
              causalityTracker.getAndIncrement();
              return false;
            });

    assertNotNull(deadLetterEventHandler);

    var thrownException =
        assertThrows(
            HandlerDelayException.class,
            () -> deadLetterEventHandler.handle("", (raw) -> new MockEvent()));

    assertEquals(RETRY_CONFIG.maxDelayMs(), thrownException.getDelay().delayToNext());

    assertEquals(1, eventHandler.getCallCount());
    assertEquals(0, mockDeadLetterQueue.getCallCount());
    assertEquals(1, causalityTracker.get());
  }

  /**
   * Tests the handling of an event when a transient exception occurs, but the circuit to the
   * resource is not closed, indicating a temporary unavailability or fault. This test verifies
   * that: 1. In-process retries are immediately halted to prevent unnecessary load or further
   * errors due to the open circuit state. 2. The transient exception is promptly thrown to the
   * designated handler, bypassing standard retry mechanisms that might otherwise attempt immediate
   * reprocessing. 3. The system initiates a maximum delay out-of-process retry mechanism, such as
   * suspending a lambda function, to allow time for the circuit breaker to reset and close. This
   * approach ensures resources are not overburdened and provides a pause, increasing the likelihood
   * that subsequent processing attempts to occur under more favorable conditions. This process is
   * critical for maintaining system stability and ensuring efficient handling of temporary issues
   * without compromising data integrity or processing reliability.
   */
  @Test
  void shouldMoveNotClosedTransientExceptionEventToDlq() {
    var eventHandler =
        new MockEventHandler(() -> new TransientException(null, CircuitState.HALF_OPEN));
    var mockInvalidMessageQueue = new MockInvalidMessageQueue();
    var mockDeadLetterQueue = new MockDeadLetterQueue<MockEvent>();
    var mockReorderQueue = new MockReorderQueue<MockEvent>();
    var queueFactory =
        createQueueFactory(
            RETRY_CONFIG, mockInvalidMessageQueue, mockDeadLetterQueue, mockReorderQueue);

    AtomicInteger causalityTracker = new AtomicInteger();
    var deadLetterEventHandler =
        new ResilientEventHandler<>(
            RETRY_CONFIG,
            new DefaultRetryableOperationFactory(),
            eventHandler,
            queueFactory,
            (e) -> {
              causalityTracker.getAndIncrement();
              return false;
            });

    assertNotNull(deadLetterEventHandler);

    var thrownException =
        assertThrows(
            HandlerDelayException.class,
            () -> deadLetterEventHandler.handle("", (raw) -> new MockEvent()));

    assertEquals(RETRY_CONFIG.maxDelayMs(), thrownException.getDelay().delayToNext());

    assertEquals(1, eventHandler.getCallCount());
    assertEquals(0, mockDeadLetterQueue.getCallCount());
    assertEquals(1, causalityTracker.get());
  }

  /**
   * This test validates the system's response to a TransientException under conditions where the
   * circuit breaker remains closed. It ensures that: 1. The system attempts retries up to a
   * predefined maximum threshold when encountering a TransientException, reflecting temporary
   * issues that might be resolved upon reattempt. 2. If the issue persists and the maximum number
   * of retries is reached without successful resolution, the system escalates the exception from
   * transient to permanent. 3. Upon escalation to a PermanentException, the event is immediately
   * enqueued in the Dead Letter Queue (DLQ), halting further retries and shifting focus to manual
   * review or alternative handling procedures. This process is crucial for balancing between giving
   * temporary issues a chance to be rectified through retries and recognizing when an issue is
   * unlikely to be resolved, necessitating its movement to the DLQ for further attention.
   */
  @Test
  void shouldEscalateClosedTransientExceptionToPermanentAfterSomeRetriesAndMoveToDlq() {
    var eventHandler =
        new MockEventHandler(() -> new TransientException(null, CircuitState.CLOSED));
    var mockInvalidMessageQueue = new MockInvalidMessageQueue();
    var mockDeadLetterQueue = new MockDeadLetterQueue<MockEvent>();
    var mockReorderQueue = new MockReorderQueue<MockEvent>();
    var queueFactory =
        createQueueFactory(
            RETRY_CONFIG, mockInvalidMessageQueue, mockDeadLetterQueue, mockReorderQueue);

    AtomicInteger causalityTracker = new AtomicInteger();
    var deadLetterEventHandler =
        new ResilientEventHandler<>(
            RETRY_CONFIG,
            new DefaultRetryableOperationFactory(),
            eventHandler,
            queueFactory,
            (e) -> {
              causalityTracker.getAndIncrement();
              return false;
            });

    assertNotNull(deadLetterEventHandler);
    var thrownException =
        assertThrows(
            HandlerDelayException.class,
            () -> deadLetterEventHandler.handle("", (raw) -> new MockEvent()));
    assertEquals(RETRY_CONFIG.maxDelayMs(), thrownException.getDelay().delayToNext());

    assertEquals(RETRY_CONFIG.maxRetryAttempts(), eventHandler.getCallCount());
    assertEquals(0, mockDeadLetterQueue.getCallCount());
    assertEquals(RETRY_CONFIG.maxRetryAttempts(), causalityTracker.get());
  }

  /**
   * This test validates the system's response to a TransientException under conditions where the
   * circuit breaker remains closed. It ensures that: 1. The system attempts retries up to a
   * predefined maximum threshold when encountering a TransientException, reflecting temporary
   * issues that might be resolved upon reattempt. 2. If the issue persists and the maximum number
   * of retries is reached without successful resolution, the system escalates the exception from
   * transient to permanent. 3. Upon escalation to a PermanentException, the event is immediately
   * enqueued in the Dead Letter Queue (DLQ), halting further retries and shifting focus to manual
   * review or alternative handling procedures. This process is crucial for balancing between giving
   * temporary issues a chance to be rectified through retries and recognizing when an issue is
   * unlikely to be resolved, necessitating its movement to the DLQ for further attention.
   */
  @Test
  void shouldRethrowClosedTransientExceptionAfterManyRetriesAndNotMoveToDlq() {
    var eventHandler =
        new MockEventHandler(() -> new TransientException(null, CircuitState.CLOSED));
    var mockInvalidMessageQueue = new MockInvalidMessageQueue();
    var mockDeadLetterQueue = new MockDeadLetterQueue<MockEvent>();
    var mockReorderQueue = new MockReorderQueue<MockEvent>();
    var queueFactory =
        createQueueFactory(
            RETRY_CONFIG, mockInvalidMessageQueue, mockDeadLetterQueue, mockReorderQueue);

    AtomicInteger causalityTracker = new AtomicInteger();
    var deadLetterEventHandler =
        new ResilientEventHandler<>(
            RETRY_CONFIG,
            new DefaultRetryableOperationFactory(),
            eventHandler,
            queueFactory,
            (e) -> {
              causalityTracker.getAndIncrement();
              return false;
            });

    assertNotNull(deadLetterEventHandler);
    var thrownException =
        assertThrows(
            HandlerDelayException.class,
            () -> deadLetterEventHandler.handle("", (raw) -> new MockEvent()));
    assertEquals(RETRY_CONFIG.maxDelayMs(), thrownException.getDelay().delayToNext());

    assertEquals(RETRY_CONFIG.maxRetryAttempts(), eventHandler.getCallCount());
    assertEquals(0, mockDeadLetterQueue.getCallCount());
    assertEquals(RETRY_CONFIG.maxRetryAttempts(), causalityTracker.get());
  }

  /**
   * This test validates the system's response to a TransientException under conditions where the
   * circuit breaker remains closed. It ensures that: 1. The system attempts retries up to a
   * predefined maximum threshold when encountering a TransientException, reflecting temporary
   * issues that might be resolved upon reattempt. 2. If the issue persists and the maximum number
   * of retries is reached without successful resolution, the system escalates the exception from
   * transient to permanent. 3. Upon escalation to a PermanentException, the event is immediately
   * enqueued in the Dead Letter Queue (DLQ), halting further retries and shifting focus to manual
   * review or alternative handling procedures. This process is crucial for balancing between giving
   * temporary issues a chance to be rectified through retries and recognizing when an issue is
   * unlikely to be resolved, necessitating its movement to the DLQ for further attention.
   */
  @Test
  void shouldRethrowClosedTransientExceptionWithLargeBaseDelayAndMoveToDlq() {
    var eventHandler =
        new MockEventHandler(() -> new TransientException(null, CircuitState.CLOSED));
    var mockInvalidMessageQueue = new MockInvalidMessageQueue();
    var mockDeadLetterQueue = new MockDeadLetterQueue<MockEvent>();
    var mockReorderQueue = new MockReorderQueue<MockEvent>();
    var queueFactory =
        createQueueFactory(
            RETRY_CONFIG, mockInvalidMessageQueue, mockDeadLetterQueue, mockReorderQueue);

    AtomicInteger causalityTracker = new AtomicInteger();
    var deadLetterEventHandler =
        new ResilientEventHandler<>(
            RETRY_CONFIG,
            new DefaultRetryableOperationFactory(),
            eventHandler,
            queueFactory,
            (e) -> {
              causalityTracker.getAndIncrement();
              return false;
            });

    assertNotNull(deadLetterEventHandler);
    var thrownException =
        assertThrows(
            HandlerDelayException.class,
            () -> deadLetterEventHandler.handle("", (raw) -> new MockEvent()));
    assertEquals(RETRY_CONFIG.maxDelayMs(), thrownException.getDelay().delayToNext());

    assertEquals(RETRY_CONFIG.maxRetryAttempts(), eventHandler.getCallCount());
    assertEquals(0, mockDeadLetterQueue.getCallCount());
    assertEquals(RETRY_CONFIG.maxRetryAttempts(), causalityTracker.get());
  }

  /**
   * This test verifies the system's behavior in a scenario where the Dead Letter Queue (DLQ) is
   * down, but the circuit breaker remains closed. Specifically, it tests the system's resilience
   * and retry mechanisms under adverse conditions by ensuring the following:
   *
   * <p>1. The system employs a retry strategy, specifically an Exponential Backoff with Jitter, to
   * attempt re-enqueuing messages to the DLQ. This approach aims to mitigate potential "thundering
   * herd" problems and smooth out the load on the recovering DLQ.
   *
   * <p>2. The test simulates a TransientException to mimic the DLQ being temporarily unreachable,
   * ensuring that the retry logic is invoked as expected. It checks that retries occur up to a
   * predefined maximum threshold, reflecting the system's persistence in the face of transient
   * failures.
   *
   * <p>3. It confirms that the system correctly tracks the number of retry attempts, both for the
   * event handler and the DLQ, to ensure compliance with the retry policy.
   *
   * <p>4. Upon exceeding the retry limit without success, the system should throw a
   * HandlerDelayException, indicating a temporary failure to process the event. This exception
   * includes a delayToNext value, which suggests when to attempt processing again, based on the
   * retry strategy's calculations.
   *
   * <p>This test is crucial for ensuring the system's robustness, particularly in handling
   * scenarios where critical components like the DLQ are temporarily unavailable but the system
   * must continue to operate effectively without immediate escalation to a permanent failure state.
   */
  @Test
  void shouldRetryEventPublicationToDLQOnTransientFailureWithClosedCircuit() {
    var eventHandler =
        new MockEventHandler(() -> new TransientException(null, CircuitState.CLOSED));
    var mockInvalidMessageQueue = new MockInvalidMessageQueue();
    var mockDeadLetterQueue =
        new MockDeadLetterQueue<MockEvent>(() -> new TransientException(null, CircuitState.CLOSED));
    var mockReorderQueue =
        new MockReorderQueue<MockEvent>(() -> new TransientException(null, CircuitState.CLOSED));
    var queueFactory =
        createQueueFactory(
            RETRY_CONFIG, mockInvalidMessageQueue, mockDeadLetterQueue, mockReorderQueue);

    AtomicInteger causalityTracker = new AtomicInteger();
    var deadLetterEventHandler =
        new ResilientEventHandler<>(
            RETRY_CONFIG,
            new DefaultRetryableOperationFactory(),
            eventHandler,
            queueFactory,
            (e) -> {
              causalityTracker.getAndIncrement();
              return false;
            });

    assertNotNull(deadLetterEventHandler);
    var thrownException =
        assertThrows(
            HandlerDelayException.class,
            () -> deadLetterEventHandler.handle("", (raw) -> new MockEvent()));
    assertEquals(RETRY_CONFIG.maxDelayMs(), thrownException.getDelay().delayToNext());

    assertEquals(RETRY_CONFIG.maxRetryAttempts(), eventHandler.getCallCount());
    assertEquals(0, mockDeadLetterQueue.getCallCount());
    assertEquals(RETRY_CONFIG.maxRetryAttempts(), causalityTracker.get());
  }

  /**
   * This test assesses the system's strategy for handling events intended for a Dead Letter Queue
   * (DLQ) that is currently down, with an additional complexity of the circuit breaker being in an
   * OPEN state for the DLQ. It aims to validate the following behaviors under these specific
   * conditions:
   *
   * <p>1. The system initiates retries for enqueueing messages in the DLQ using an Exponential
   * Backoff with Jitter strategy, which is designed to efficiently manage retry attempts under
   * failure conditions while avoiding sudden surges in demand (thundering herd problem) when the
   * DLQ becomes available again.
   *
   * <p>2. Despite the circuit for the DLQ being OPEN, indicating that previous attempts to interact
   * with the DLQ have failed and it should not be attempted again until the circuit closes, the
   * system still makes an initial attempt to enqueue in the DLQ. This reflects the system's
   * resilience in trying to ensure no event is lost without at least one attempt.
   *
   * <p>3. If the DLQ remains unreachable after the initial attempt, the system correctly identifies
   * the situation as requiring a retry later, demonstrated by throwing a HandlerDelayException with
   * a specified delay until the next attempt. This exception is crucial for controlling the flow of
   * retries and ensuring that the system does not overwhelm resources or lead to immediate
   * escalation.
   *
   * <p>4. The test verifies the correct functioning of the retry mechanism by ensuring that the
   * event handler and DLQ's call counts match the expected retry logic, despite the DLQ being
   * unreachable and the circuit state being open.
   *
   * <p>This test is fundamental in ensuring that the system can handle transient DLQ downtimes
   * gracefully, even when the circuit breaker suggests that new attempts should be temporarily
   * halted, thereby balancing robustness with system load considerations.
   */
  @Test
  void shouldNotAttemptInitialDLQPublishWithOpenCircuitOnTransientFailure() {
    var eventHandler = new MockEventHandler(() -> new TransientException(null, CircuitState.OPEN));
    var mockInvalidMessageQueue = new MockInvalidMessageQueue();
    var mockDeadLetterQueue = new MockDeadLetterQueue<MockEvent>();
    var mockReorderQueue = new MockReorderQueue<MockEvent>();
    var queueFactory =
        createQueueFactory(
            RETRY_CONFIG, mockInvalidMessageQueue, mockDeadLetterQueue, mockReorderQueue);

    AtomicInteger causalityTracker = new AtomicInteger();
    var deadLetterEventHandler =
        new ResilientEventHandler<>(
            RETRY_CONFIG,
            new DefaultRetryableOperationFactory(),
            eventHandler,
            queueFactory,
            (e) -> {
              causalityTracker.getAndIncrement();
              return false;
            });

    assertNotNull(deadLetterEventHandler);
    var thrownException =
        assertThrows(
            HandlerDelayException.class,
            () -> deadLetterEventHandler.handle("", (raw) -> new MockEvent()));
    assertEquals(RETRY_CONFIG.maxDelayMs(), thrownException.getDelay().delayToNext());

    assertEquals(1, eventHandler.getCallCount());
    assertEquals(0, mockDeadLetterQueue.getCallCount());
    assertEquals(1, causalityTracker.get());
  }

  /**
   * This test scrutinizes the system's response when attempting to enqueue messages in a DLQ that
   * is unresponsive, with the circuit breaker state set to HALF_OPEN. The HALF_OPEN state is
   * indicative of a system that is tentatively resuming operations with the DLQ to test if the
   * underlying issue has been resolved. The objectives of this test include:
   *
   * <p>1. To validate that the system initiates a cautious retry mechanism for events directed
   * towards the DLQ, employing an Exponential Backoff with Jitter strategy. This strategy aims to
   * minimize the risk of overloading the DLQ and to manage the distribution of retry attempts over
   * time efficiently.
   *
   * <p>2. To confirm that in a HALF_OPEN circuit state, where the system is uncertain about the
   * DLQ's availability, it proceeds with an initial attempt to send the event. This behavior is
   * critical for testing the DLQ's responsiveness without fully committing to normal operations.
   *
   * <p>3. To ensure that upon failure to enqueue the message due to DLQ unavailability, the system
   * gracefully handles the situation by throwing a HandlerDelayException. This exception indicates
   * a planned delay before the next retry attempt, in line with the exponential backoff strategy,
   * thus managing the balance between retry efforts and system stability.
   *
   * <p>4. The test checks that the call counts to the event handler and the DLQ align with the
   * expected behavior under a HALF_OPEN circuit state, demonstrating the system's adherence to a
   * cautious approach in potentially unstable conditions.
   *
   * <p>By focusing on the HALF_OPEN circuit breaker state, this test plays a vital role in
   * verifying the system's capability to cautiously resume interaction with the DLQ, ensuring that
   * events are not lost while also protecting the system from potential issues arising from
   * premature full-scale operations.
   */
  @Test
  void shouldCautiouslyRetryDLQPublishWithHalfOpenCircuitOnTransientFailure() {
    var eventHandler =
        new MockEventHandler(() -> new TransientException(null, CircuitState.CLOSED));
    var mockInvalidMessageQueue = new MockInvalidMessageQueue();
    var mockDeadLetterQueue =
        new MockDeadLetterQueue<MockEvent>(
            () -> new TransientException(null, CircuitState.HALF_OPEN));
    var mockReorderQueue =
        new MockReorderQueue<MockEvent>(() -> new TransientException(null, CircuitState.HALF_OPEN));
    var queueFactory =
        createQueueFactory(
            RETRY_CONFIG, mockInvalidMessageQueue, mockDeadLetterQueue, mockReorderQueue);

    AtomicInteger causalityTracker = new AtomicInteger();
    var deadLetterEventHandler =
        new ResilientEventHandler<>(
            RETRY_CONFIG,
            new DefaultRetryableOperationFactory(),
            eventHandler,
            queueFactory,
            (e) -> {
              causalityTracker.getAndIncrement();
              return false;
            });

    assertNotNull(deadLetterEventHandler);
    var thrownException =
        assertThrows(
            HandlerDelayException.class,
            () -> deadLetterEventHandler.handle("", (raw) -> new MockEvent()));
    assertEquals(RETRY_CONFIG.maxDelayMs(), thrownException.getDelay().delayToNext());

    assertEquals(RETRY_CONFIG.maxRetryAttempts(), eventHandler.getCallCount());
    assertEquals(0, mockDeadLetterQueue.getCallCount());
    assertEquals(RETRY_CONFIG.maxRetryAttempts(), causalityTracker.get());
  }

  /**
   * This test examines the system's robustness and retry logic when facing issues with the Dead
   * Letter Tracking DAO, specifically when it is unresponsive and its associated circuit breaker is
   * in a CLOSED state. The CLOSED circuit state typically indicates that the system should proceed
   * with operations under normal circumstances, yet the unresponsive DAO presents a unique
   * challenge. The key objectives of this test are:
   *
   * <p>1. To confirm that the system employs a retry strategy, using an Exponential Backoff with
   * Jitter, for operations that involve the Dead Letter Tracking DAO. This retry strategy is
   * crucial for managing attempts to interact with the DAO following a failure, aiming to mitigate
   * potential spikes in load and to distribute retry attempts over time.
   *
   * <p>2. To ensure that, despite the CLOSED circuit state of the Dead Letter Tracking DAO—which
   * would normally suggest that operations can proceed normally—the system identifies and handles
   * the unresponsiveness of the DAO appropriately by initiating a retry sequence.
   *
   * <p>3. To verify that upon continuous failure to interact with the Dead Letter Tracking DAO, the
   * system gracefully escalates the issue by throwing a HandlerDelayException. This exception
   * indicates a delay before the next retry attempt, adhering to the principles of the exponential
   * backoff strategy and ensuring a controlled approach to error handling.
   *
   * <p>4. The test ensures that no operations are mistakenly carried out by the event handler or
   * the Dead Letter Queue in response to the DAO's failure, focusing solely on the retry logic
   * associated with the DAO's operations.
   *
   * <p>By concentrating on the interaction with a critical yet failing component like the Dead
   * Letter Tracking DAO under a presumed operational circuit state, this test underscores the
   * importance of resilient error handling and adaptive retry mechanisms within the system's
   * architecture, ensuring continuity and stability in the face of component-specific issues.
   */
  @Test
  void shouldRetryWithClosedCircuitOnDeadLetterTrackingDaoFailure() {
    var eventHandler = new MockEventHandler();
    var mockInvalidMessageQueue = new MockInvalidMessageQueue();
    var mockDeadLetterQueue = new MockDeadLetterQueue<MockEvent>();
    var mockReorderQueue = new MockReorderQueue<MockEvent>();
    var queueFactory =
        createQueueFactory(
            RETRY_CONFIG, mockInvalidMessageQueue, mockDeadLetterQueue, mockReorderQueue);

    AtomicInteger causalityTracker = new AtomicInteger();
    var deadLetterEventHandler =
        new ResilientEventHandler<>(
            RETRY_CONFIG,
            new DefaultRetryableOperationFactory(),
            eventHandler,
            queueFactory,
            (e) -> {
              throw new TransientException(null, CircuitState.CLOSED);
            });

    assertNotNull(deadLetterEventHandler);
    var thrownException =
        assertThrows(
            HandlerDelayException.class,
            () -> deadLetterEventHandler.handle("", (raw) -> new MockEvent()));
    assertEquals(RETRY_CONFIG.maxDelayMs(), thrownException.getDelay().delayToNext());

    assertEquals(0, eventHandler.getCallCount());
    assertEquals(0, mockDeadLetterQueue.getCallCount());
    assertEquals(0, causalityTracker.get());
  }

  /**
   * This test delves into the system's approach to dealing with an unresponsive Dead Letter
   * Tracking DAO when its circuit breaker is in an OPEN state. An OPEN state typically indicates
   * that previous attempts to use the DAO have failed, and the system should temporarily halt
   * further attempts to allow for recovery. The test aims to validate the following aspects:
   *
   * <p>1. It examines whether the system adheres to a prudent retry strategy, in this case,
   * Exponential Backoff with Jitter, under the premise that no immediate retries should be
   * performed due to the OPEN state of the circuit breaker, reflecting a system-level safeguard
   * against exacerbating the issue.
   *
   * <p>2. Despite the OPEN circuit state signaling a halt on operations with the Dead Letter
   * Tracking DAO, the test checks for the correct implementation of an immediate response strategy.
   * This strategy involves assessing whether the system appropriately escalates or delays
   * operations, potentially bypassing immediate retries in favor of scheduled attempts based on the
   * system's backoff strategy.
   *
   * <p>3. The focus is on ensuring that the system recognizes the OPEN circuit state of the DAO as
   * a critical factor in its retry logic, ultimately verifying that no attempts are made to
   * interact with the DAO, thereby adhering to the circuit breaker's intent of preventing further
   * immediate strain on the component.
   *
   * <p>4. Through assertions, the test confirms that no calls are made to either the event handler
   * or the Dead Letter Queue in response to the DAO's state, emphasizing the test's focus on the
   * system's handling of the DAO's circuit breaker status rather than its interaction with other
   * components.
   *
   * <p>This test is critical for ensuring the system's resilience and operational integrity by
   * respecting the circuit breaker patterns and implementing appropriate response mechanisms in
   * scenarios where critical components are not in a state to perform their functions reliably.
   */
  @Test
  void shouldHonorOpenCircuitWithoutImmediateRetriesOnTrackingDaoFailure() {
    var eventHandler = new MockEventHandler();
    var mockInvalidMessageQueue = new MockInvalidMessageQueue();
    var mockDeadLetterQueue = new MockDeadLetterQueue<MockEvent>();
    var mockReorderQueue = new MockReorderQueue<MockEvent>();
    var queueFactory =
        createQueueFactory(
            RETRY_CONFIG, mockInvalidMessageQueue, mockDeadLetterQueue, mockReorderQueue);

    var deadLetterEventHandler =
        new ResilientEventHandler<>(
            RETRY_CONFIG,
            new DefaultRetryableOperationFactory(),
            eventHandler,
            queueFactory,
            (e) -> {
              throw new TransientException(null, CircuitState.OPEN);
            });

    assertNotNull(deadLetterEventHandler);
    var thrownException =
        assertThrows(
            HandlerDelayException.class,
            () -> deadLetterEventHandler.handle("", (raw) -> new MockEvent()));
    assertEquals(RETRY_CONFIG.maxDelayMs(), thrownException.getDelay().delayToNext());

    assertEquals(0, eventHandler.getCallCount());
    assertEquals(0, mockDeadLetterQueue.getCallCount());
    assertEquals(0, mockInvalidMessageQueue.getCallCount());
  }

  /**
   * This test explores the system's strategy for engaging with a Dead Letter Tracking DAO that is
   * in a HALF_OPEN circuit state, indicating a transitional phase where the system tests the waters
   * to see if the DAO is ready to resume normal operations. The HALF_OPEN state is crucial for
   * implementing a cautious approach to recovery, allowing the system to attempt recovery without
   * fully committing to normal operations. This test aims to validate:
   *
   * <p>1. The implementation of a prudent retry strategy, specifically an Exponential Backoff with
   * Jitter, tailored to the nuanced conditions presented by the HALF_OPEN state. This approach
   * balances the need to retry operations with the DAO against the risk of prematurely overloading
   * it before it's confirmed to be fully operational.
   *
   * <p>2. The test assesses whether the system recognizes the HALF_OPEN state as a signal to
   * initiate a carefully measured attempt to interact with the DAO, rather than proceeding with
   * full confidence as it would in a CLOSED state or halting attempts as in an OPEN state.
   *
   * <p>3. It verifies that the system properly escalates or adjusts its operations in light of the
   * DAO's HALF_OPEN status by throwing a HandlerDelayException, thus demonstrating a sophisticated
   * response that acknowledges the need for a cautious progression towards resuming normal
   * activity.
   *
   * <p>4. Assertions within the test ensure that no interactions are made with the event handler or
   * the Dead Letter Queue as a result of the DAO's HALF_OPEN state, focusing solely on the retry
   * logic associated with the DAO itself.
   *
   * <p>This test underscores the importance of the system's ability to adapt its retry mechanisms
   * to the specific states of circuit breakers, ensuring that it can handle intermediary states
   * like HALF_OPEN with the appropriate level of caution and strategic planning.
   */
  @Test
  void shouldAdaptRetryStrategyForHalfOpenCircuitOnTrackingDaoFailure() {
    var eventHandler = new MockEventHandler();
    var mockInvalidMessageQueue = new MockInvalidMessageQueue();
    var mockDeadLetterQueue = new MockDeadLetterQueue<MockEvent>();
    var mockDeadLetterTrackingDao =
        new MockDeadLetterTracking(
            () -> new TransientException(null, CircuitState.HALF_OPEN), false);
    var mockReorderQueue = new MockReorderQueue<MockEvent>();
    var queueFactory =
        createQueueFactory(
            RETRY_CONFIG, mockInvalidMessageQueue, mockDeadLetterQueue, mockReorderQueue);

    var deadLetterEventHandler =
        new ResilientEventHandler<>(
            RETRY_CONFIG,
            new DefaultRetryableOperationFactory(),
            eventHandler,
            queueFactory,
            (e) -> {
              throw new TransientException(null, CircuitState.HALF_OPEN);
            });

    assertNotNull(deadLetterEventHandler);
    var thrownException =
        assertThrows(
            HandlerDelayException.class,
            () -> deadLetterEventHandler.handle("", (raw) -> new MockEvent()));
    assertEquals(RETRY_CONFIG.maxDelayMs(), thrownException.getDelay().delayToNext());

    assertEquals(0, eventHandler.getCallCount());
    assertEquals(0, mockDeadLetterQueue.getCallCount());
    assertEquals(0, mockDeadLetterTrackingDao.getTrackCount());
  }

  @Test
  void shouldPassIfEventHandlerSucceedsWithDifferentEventTypes() {
    var eventHandler = new MockExEventHandler();
    var queueFactory = createQueueFactory(RETRY_CONFIG);

    var deadLetterEventHandler =
        new ResilientEventHandler<>(
            RETRY_CONFIG,
            new DefaultRetryableOperationFactory(),
            eventHandler,
            queueFactory,
            (e) -> false);

    assertNotNull(deadLetterEventHandler);

    var serializer = DefaultJsonSerializer.withTyping();
    serializer.registerClassType(
        ExtendedMockEventA.class.getSimpleName(), ExtendedMockEventA.class);
    serializer.registerClassType(
        ExtendedMockEventB.class.getSimpleName(), ExtendedMockEventB.class);
    serializer.registerClassType(
        ExtendedMockEventC.class.getSimpleName(), ExtendedMockEventC.class);

    var data1 = serializer.serialize(new ExtendedMockEventA("a"));
    var data2 = serializer.serialize(new ExtendedMockEventB("b"));
    var data3 = serializer.serialize(new ExtendedMockEventC("c"));

    deadLetterEventHandler.handle("", (raw) -> serializer.deserialize(data1, MockEvent.class));
    deadLetterEventHandler.handle("", (raw) -> serializer.deserialize(data2, MockEvent.class));
    deadLetterEventHandler.handle("", (raw) -> serializer.deserialize(data3, MockEvent.class));

    assertEquals(0, eventHandler.getCallCount());
    assertEquals(1, eventHandler.getCallCountA());
    assertEquals(1, eventHandler.getCallCountB());
    assertEquals(1, eventHandler.getCallCountC());
  }

  @Test
  void skippingReorderingDoesntEndUpOnDLQ() {
    var eventHandler = new MockEventHandler();
    var mockInvalidMessageQueue = new MockInvalidMessageQueue();
    var mockDeadLetterQueue = new MockDeadLetterQueue<MockEvent>();
    var mockReorderQueue = new MockReorderQueue<MockEvent>();
    var resilientQueueFactory =
        createQueueFactory(
            RETRY_CONFIG, mockInvalidMessageQueue, mockDeadLetterQueue, mockReorderQueue);

    var deadLetterEventHandler =
        new ResilientEventHandler<>(
            RETRY_CONFIG,
            new DefaultRetryableOperationFactory(),
            eventHandler,
            resilientQueueFactory);

    assertNotNull(deadLetterEventHandler);

    deadLetterEventHandler.handle("", (raw) -> new MockEvent());

    assertEquals(1, eventHandler.getCallCount());
    assertEquals(0, mockReorderQueue.getCallCount());
    assertEquals(0, mockDeadLetterQueue.getCallCount());
  }

  @Test
  void idempotentProcessorWithoutReordering() {
    var eventHandler = new MockIdempotentEventHandler();
    var mockInvalidMessageQueue = new MockInvalidMessageQueue();
    var mockDeadLetterQueue = new MockDeadLetterQueue<MockEvent>();
    var mockReorderQueue = new MockReorderQueue<MockEvent>();
    var resilientQueueFactory =
        createQueueFactory(
            RETRY_CONFIG, mockInvalidMessageQueue, mockDeadLetterQueue, mockReorderQueue);
    var deadLetterEventHandler =
        new ResilientEventHandler<>(
            RETRY_CONFIG,
            new DefaultRetryableOperationFactory(),
            eventHandler,
            resilientQueueFactory);

    assertNotNull(deadLetterEventHandler);

    String guid = UUID.randomUUID().toString();
    int logicalClockReading = 0;
    MockEvent initialEvent =
        new MockEvent(UUID.fromString(guid), "", null, null, 0, logicalClockReading);
    deadLetterEventHandler.handle("", (raw) -> initialEvent);
    assertEquals(0, eventHandler.getCallCount());
    assertEquals(0, mockReorderQueue.getCallCount());
    assertEquals(0, mockDeadLetterQueue.getCallCount());
  }

  @Test
  void unsupportedEventTypeIsIgnored() {
    var eventHandler = new MockExEventHandler();
    var mockInvalidMessageQueue = new MockInvalidMessageQueue();
    var mockDeadLetterQueue = new MockDeadLetterQueue<MockEvent>();
    var mockReorderQueue = new MockReorderQueue<MockEvent>();
    var queueFactory =
        createQueueFactory(
            RETRY_CONFIG, mockInvalidMessageQueue, mockDeadLetterQueue, mockReorderQueue);

    AtomicInteger causalityTracker = new AtomicInteger();
    var deadLetterEventHandler =
        new ResilientEventHandler<>(
            RETRY_CONFIG,
            new DefaultRetryableOperationFactory(),
            eventHandler,
            queueFactory,
            (e) -> {
              causalityTracker.getAndIncrement();
              return false;
            });

    assertNotNull(deadLetterEventHandler);

    // supported event types are handled fine
    var serializerA = DefaultInternalTransportJsonSerializer.withTyping();
    serializerA.registerClassType(
        ExtendedMockEventA.class.getSimpleName(), ExtendedMockEventA.class);

    var dataA = serializerA.serialize(new ExtendedMockEventA("a"));

    deadLetterEventHandler.handle("", (raw) -> serializerA.deserialize(dataA, MockEvent.class));

    assertEquals(0, eventHandler.getCallCount());
    assertEquals(1, eventHandler.getCallCountA());
    assertEquals(0, eventHandler.getCallCountB());
    assertEquals(0, eventHandler.getCallCountC());
    assertEquals(0, mockDeadLetterQueue.getCallCount());
    assertEquals(0, mockInvalidMessageQueue.getCallCount());
    assertEquals(1, mockReorderQueue.getCallCount());
    assertEquals(1, causalityTracker.get());

    // unsupported event types are ignored
    var serializerB = DefaultInternalTransportJsonSerializer.withTyping();
    serializerB.registerClassType(
        ExtendedMockEventB.class.getSimpleName(), ExtendedMockEventB.class);

    var bData = serializerB.serialize(new ExtendedMockEventB("b"));

    deadLetterEventHandler.handle("", (raw) -> serializerA.deserialize(bData, MockEvent.class));

    assertEquals(0, eventHandler.getCallCount());
    assertEquals(1, eventHandler.getCallCountA());
    assertEquals(0, eventHandler.getCallCountB());
    assertEquals(0, eventHandler.getCallCountC());
    assertEquals(0, mockDeadLetterQueue.getCallCount());
    assertEquals(0, mockInvalidMessageQueue.getCallCount());
    assertEquals(1, mockReorderQueue.getCallCount());
    assertEquals(1, causalityTracker.get());
  }
}
