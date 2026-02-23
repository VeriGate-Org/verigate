/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.event.handler.sqs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import application.handlers.EventHandler;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import crosscutting.resiliency.DefaultRetry;
import domain.events.RetryEventHandler;
import domain.exceptions.DeferredException;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import domain.messages.DeadLetterQueue;
import domain.messages.InvalidMessageQueue;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

final class ResilientSqsEventLambdaHandlerTest {

  private final TestEvent testEvent1 = new TestEvent("payload1");
  private final TestEvent testEvent2 = new TestEvent("payload2");

  private ResilientSqsEventLambdaHandler<TestEvent> handler;
  @Mock private Function<String, TestEvent> extractEventFromMessageBody;
  @Mock private EventHandler<TestEvent> eventHandler;
  @Mock private InvalidMessageQueue<SQSMessage> invalidMessageQueue;
  @Mock private DeadLetterQueue<SQSMessage> deadLetterQueue;
  @Captor private ArgumentCaptor<SQSMessage> invalidMessageCaptor;
  @Captor private ArgumentCaptor<SQSMessage> deadLetterCaptor;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    handler =
        new ResilientSqsEventLambdaHandler<>(
            extractEventFromMessageBody, eventHandler, invalidMessageQueue, deadLetterQueue);
  }

  private SQSEvent createTestSQSEventBatch() {
    SQSMessage sqsMessage1 = new SQSMessage();
    sqsMessage1.setMessageId("1");
    sqsMessage1.setBody(testEvent1.payload());

    SQSMessage sqsMessage2 = new SQSMessage();
    sqsMessage2.setMessageId("2");
    sqsMessage2.setBody(testEvent2.payload());

    SQSEvent sqsEvent = new SQSEvent();
    sqsEvent.setRecords(List.of(sqsMessage1, sqsMessage2));

    return sqsEvent;
  }

  @Test
  public void batchAllSuccessful() {
    when(extractEventFromMessageBody.apply(eq(testEvent1.payload()))).thenReturn(testEvent1);
    when(extractEventFromMessageBody.apply(eq(testEvent2.payload()))).thenReturn(testEvent2);

    SQSBatchResponse actualSqsBatchResponse =
        handler.handleRequest(createTestSQSEventBatch(), null);
    assertTrue(actualSqsBatchResponse.getBatchItemFailures().isEmpty());

    verify(eventHandler).handle(testEvent1);
    verify(eventHandler).handle(testEvent2);
    verifyNoMoreInteractions(eventHandler, invalidMessageQueue, deadLetterQueue);
  }

  @Test
  public void batchSingleFailureInvalidMessage() {
    when(extractEventFromMessageBody.apply(eq(testEvent1.payload())))
        .thenThrow(new RuntimeException("Refusing to extract payload1"));
    when(extractEventFromMessageBody.apply(eq(testEvent2.payload()))).thenReturn(testEvent2);

    SQSBatchResponse actualSqsBatchResponse =
        handler.handleRequest(createTestSQSEventBatch(), null);
    assertTrue(actualSqsBatchResponse.getBatchItemFailures().isEmpty());

    verify(eventHandler).handle(testEvent2);
    verify(invalidMessageQueue).enqueue(invalidMessageCaptor.capture());
    assertEquals(testEvent1.payload(), invalidMessageCaptor.getValue().getBody());
    verifyNoMoreInteractions(eventHandler, invalidMessageQueue, deadLetterQueue);
  }

  @Test
  public void batchSingleFailureTransientException() {
    when(extractEventFromMessageBody.apply(eq(testEvent1.payload()))).thenReturn(testEvent1);
    when(extractEventFromMessageBody.apply(eq(testEvent2.payload()))).thenReturn(testEvent2);
    org.mockito.Mockito.doThrow(new TransientException("Transient failure"))
        .when(eventHandler)
        .handle(testEvent1);

    SQSBatchResponse actualSqsBatchResponse =
        handler.handleRequest(createTestSQSEventBatch(), null);
    assertEquals(actualSqsBatchResponse.getBatchItemFailures().size(), 1);
    assertEquals(actualSqsBatchResponse.getBatchItemFailures().get(0).getItemIdentifier(), "1");

    verify(eventHandler).handle(testEvent1);
    verify(eventHandler).handle(testEvent2);
    verifyNoMoreInteractions(eventHandler, invalidMessageQueue, deadLetterQueue);
  }

  @Test
  public void batchSingleFailurePermanentException() {
    when(extractEventFromMessageBody.apply(eq(testEvent1.payload()))).thenReturn(testEvent1);
    when(extractEventFromMessageBody.apply(eq(testEvent2.payload()))).thenReturn(testEvent2);
    org.mockito.Mockito.doThrow(new PermanentException("Permanent failure"))
        .when(eventHandler)
        .handle(testEvent1);

    SQSBatchResponse actualSqsBatchResponse =
        handler.handleRequest(createTestSQSEventBatch(), null);
    assertTrue(actualSqsBatchResponse.getBatchItemFailures().isEmpty());

    verify(eventHandler).handle(testEvent1);
    verify(eventHandler).handle(testEvent2);
    verify(deadLetterQueue).enqueue(deadLetterCaptor.capture());
    assertEquals(testEvent1.payload(), deadLetterCaptor.getValue().getBody());
    verifyNoMoreInteractions(eventHandler, invalidMessageQueue, deadLetterQueue);
  }

  @Test
  public void wrappedInRetryablePermanentException() {
    when(extractEventFromMessageBody.apply(eq(testEvent1.payload()))).thenReturn(testEvent1);
    when(extractEventFromMessageBody.apply(eq(testEvent2.payload()))).thenReturn(testEvent2);
    final var retryable =
        new DefaultRetry(2, Duration.ofMillis(2), Set.of(TransientException.class));
    org.mockito.Mockito.doThrow(new TransientException("Transient failure"))
        .when(eventHandler)
        .handle(testEvent1);
    org.mockito.Mockito.doThrow(new PermanentException("Permanent failure"))
        .when(eventHandler)
        .handle(testEvent2);
    final var retryEventHandler = new RetryEventHandler<>(eventHandler, retryable, "retryName");
    final var resilientSqsEventLambdaHandler =
        new ResilientSqsEventLambdaHandler<>(
            extractEventFromMessageBody, retryEventHandler, invalidMessageQueue, deadLetterQueue);
    final var actualSqsBatchResponse =
        resilientSqsEventLambdaHandler.handleRequest(createTestSQSEventBatch(), null);
    assertTrue(actualSqsBatchResponse.getBatchItemFailures().isEmpty());
    // testEvent1 is expected to be retried (TransientException)
    verify(eventHandler, times(2)).handle(testEvent1);
    // testEvent2 is not expected to be retried (PermanentException)
    verify(eventHandler, times(1)).handle(testEvent2);
    // Both should go to the DLQ though
    verify(deadLetterQueue, times(2)).enqueue(deadLetterCaptor.capture());
    verifyNoMoreInteractions(eventHandler, invalidMessageQueue, deadLetterQueue);
  }

  @Test
  public void unexpectedProcessingException() {
    when(extractEventFromMessageBody.apply(eq(testEvent1.payload()))).thenReturn(testEvent1);
    when(extractEventFromMessageBody.apply(eq(testEvent2.payload()))).thenReturn(testEvent2);
    org.mockito.Mockito.doThrow(new RuntimeException("Unexpected"))
        .when(eventHandler)
        .handle(testEvent1);

    SQSBatchResponse actualSqsBatchResponse =
        handler.handleRequest(createTestSQSEventBatch(), null);
    assertTrue(actualSqsBatchResponse.getBatchItemFailures().isEmpty());

    verify(eventHandler).handle(testEvent1);
    verify(eventHandler).handle(testEvent2);
    verify(deadLetterQueue).enqueue(deadLetterCaptor.capture());
    assertEquals(testEvent1.payload(), deadLetterCaptor.getValue().getBody());
    verifyNoMoreInteractions(eventHandler, invalidMessageQueue, deadLetterQueue);
  }

  @Test
  public void batchSingleDeferException() {
    class AlwaysTrueFunction implements Function<SQSMessage, Boolean> {

      @Override
      public Boolean apply(SQSMessage message) {
        return true;
      }
    }

    when(extractEventFromMessageBody.apply(eq(testEvent1.payload()))).thenReturn(testEvent1);
    when(extractEventFromMessageBody.apply(eq(testEvent2.payload()))).thenReturn(testEvent2);
    org.mockito.Mockito.doThrow(new DeferredException("Deferred"))
        .when(eventHandler)
        .handle(testEvent1);
    var deferMessageProcessing = spy(new AlwaysTrueFunction());

    var deferredHandler =
        new ResilientSqsEventLambdaHandler<>(
            extractEventFromMessageBody,
            eventHandler,
            invalidMessageQueue,
            deadLetterQueue,
            deferMessageProcessing);

    var sqsEvent = createTestSQSEventBatch();
    SQSBatchResponse actualSqsBatchResponse = deferredHandler.handleRequest(sqsEvent, null);
    assertEquals(actualSqsBatchResponse.getBatchItemFailures().size(), 1);

    verify(eventHandler).handle(testEvent1);
    verify(eventHandler).handle(testEvent2);
    verify(deferMessageProcessing).apply(sqsEvent.getRecords().getFirst());
    verifyNoMoreInteractions(eventHandler, invalidMessageQueue, deadLetterQueue);
  }

  @Test
  public void singleMessageNullBody() {
    final SQSMessage sqsMessage = new SQSMessage();
    sqsMessage.setMessageId("1");
    sqsMessage.setBody(null);
    final SQSEvent sqsEvent = new SQSEvent();
    sqsEvent.setRecords(List.of(sqsMessage));

    SQSBatchResponse actualSqsBatchResponse = handler.handleRequest(sqsEvent, null);
    assertTrue(actualSqsBatchResponse.getBatchItemFailures().isEmpty());

    verifyNoMoreInteractions(eventHandler, invalidMessageQueue, deadLetterQueue);
  }

  @Test
  public void nullRecords() {
    final SQSEvent sqsEvent = new SQSEvent();
    SQSBatchResponse actualSqsBatchResponse = handler.handleRequest(sqsEvent, null);
    assertTrue(actualSqsBatchResponse.getBatchItemFailures().isEmpty());
    verifyNoMoreInteractions(eventHandler, invalidMessageQueue, deadLetterQueue);
  }

  private record TestEvent(String payload) {}
}
