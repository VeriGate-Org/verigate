/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.commands.handler.sqs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import crosscutting.resiliency.DefaultRetry;
import domain.commands.CommandHandler;
import domain.commands.RetryCommandHandler;
import domain.exceptions.DeferredException;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import domain.messages.DeadLetterQueue;
import domain.messages.InvalidMessageQueue;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.MDC;

final class ResilientSqsCommandLambdaHandlerTest {

  private final TestCommand testCommand1 = new TestCommand("payload1");
  private final TestCommand testCommand2 = new TestCommand("payload2");

  private ResilientSqsCommandLambdaHandler<TestCommand, Void> handler;
  @Mock private Function<String, TestCommand> extractCommandFromMessageBody;
  @Mock private CommandHandler<TestCommand, Void> commandHandler;
  @Mock private InvalidMessageQueue<SQSMessage> invalidMessageQueue;
  @Mock private DeadLetterQueue<SQSMessage> deadLetterQueue;
  @Captor private ArgumentCaptor<SQSMessage> invalidMessageCaptor;
  @Captor private ArgumentCaptor<SQSMessage> deadLetterCaptor;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    handler =
        new ResilientSqsCommandLambdaHandler<>(
            extractCommandFromMessageBody, commandHandler, invalidMessageQueue, deadLetterQueue);
  }

  private SQSEvent createTestSQSEventBatch() {
    SQSMessage sqsMessage1 = new SQSMessage();
    sqsMessage1.setMessageId("1");
    sqsMessage1.setBody(testCommand1.payload);

    SQSMessage sqsMessage2 = new SQSMessage();
    sqsMessage2.setMessageId("2");
    sqsMessage2.setBody(testCommand2.payload);

    SQSEvent sqsEvent = new SQSEvent();
    sqsEvent.setRecords(List.of(sqsMessage1, sqsMessage2));

    return sqsEvent;
  }

  @Test
  public void batchAllSuccessful() {
    when(extractCommandFromMessageBody.apply(eq(testCommand1.payload))).thenReturn(testCommand1);
    when(extractCommandFromMessageBody.apply(eq(testCommand2.payload))).thenReturn(testCommand2);

    SQSBatchResponse actualSqsBatchResponse =
        handler.handleRequest(createTestSQSEventBatch(), null);
    assertTrue(actualSqsBatchResponse.getBatchItemFailures().isEmpty());

    verify(commandHandler).handle(testCommand1);
    verify(commandHandler).handle(testCommand2);
    verifyNoMoreInteractions(commandHandler, invalidMessageQueue, deadLetterQueue);
  }

  @Test
  public void batchSingleFailureInvalidMessage() {
    when(extractCommandFromMessageBody.apply(eq(testCommand1.payload)))
        .thenThrow(new RuntimeException("Refusing to extract payload1"));
    when(extractCommandFromMessageBody.apply(eq(testCommand2.payload))).thenReturn(testCommand2);

    SQSBatchResponse actualSqsBatchResponse =
        handler.handleRequest(createTestSQSEventBatch(), null);
    assertTrue(actualSqsBatchResponse.getBatchItemFailures().isEmpty());

    verify(commandHandler).handle(testCommand2);
    verify(invalidMessageQueue).enqueue(invalidMessageCaptor.capture());
    assertEquals(testCommand1.payload, invalidMessageCaptor.getValue().getBody());
    verifyNoMoreInteractions(commandHandler, invalidMessageQueue, deadLetterQueue);
  }

  @Test
  public void batchSingleFailureTransientException() {
    when(extractCommandFromMessageBody.apply(eq(testCommand1.payload))).thenReturn(testCommand1);
    when(extractCommandFromMessageBody.apply(eq(testCommand2.payload))).thenReturn(testCommand2);
    when(commandHandler.handle(testCommand1))
        .thenThrow(new TransientException("Transient failure"));

    SQSBatchResponse actualSqsBatchResponse =
        handler.handleRequest(createTestSQSEventBatch(), null);
    assertEquals(actualSqsBatchResponse.getBatchItemFailures().size(), 1);
    assertEquals(actualSqsBatchResponse.getBatchItemFailures().get(0).getItemIdentifier(), "1");

    verify(commandHandler).handle(testCommand1);
    verify(commandHandler).handle(testCommand2);
    verifyNoMoreInteractions(commandHandler, invalidMessageQueue, deadLetterQueue);
  }

  @Test
  public void batchSingleFailurePermanentException() {
    when(extractCommandFromMessageBody.apply(eq(testCommand1.payload))).thenReturn(testCommand1);
    when(extractCommandFromMessageBody.apply(eq(testCommand2.payload))).thenReturn(testCommand2);
    when(commandHandler.handle(testCommand1))
        .thenThrow(new PermanentException("Permanent failure"));

    SQSBatchResponse actualSqsBatchResponse =
        handler.handleRequest(createTestSQSEventBatch(), null);
    assertTrue(actualSqsBatchResponse.getBatchItemFailures().isEmpty());

    verify(commandHandler).handle(testCommand1);
    verify(commandHandler).handle(testCommand2);
    verify(deadLetterQueue).enqueue(deadLetterCaptor.capture());
    assertEquals(testCommand1.payload, deadLetterCaptor.getValue().getBody());
    verifyNoMoreInteractions(commandHandler, invalidMessageQueue, deadLetterQueue);
  }

  /**
   * This test is arguably too deep in scope in that it asserts the Retryable behavior too. But I
   * wanted to make sure the resilient handler behaves as expected when the underlying command
   * handler is wrapped in a retryable decorator as this pattern is expected to be common.
   */
  @Test
  public void wrappedInRetryablePermanentException() {
    when(extractCommandFromMessageBody.apply(eq(testCommand1.payload))).thenReturn(testCommand1);
    when(extractCommandFromMessageBody.apply(eq(testCommand2.payload))).thenReturn(testCommand2);
    final var retryable =
        new DefaultRetry(2, Duration.ofMillis(2), Set.of(TransientException.class));
    when(commandHandler.handle(testCommand1))
        .thenThrow(new TransientException("Transient failure"));
    when(commandHandler.handle(testCommand2))
        .thenThrow(new PermanentException("Permanent failure"));
    final var retryCommandHandler =
        new RetryCommandHandler<>(commandHandler, retryable, "retryName");
    final var resilientSqsCommandLambdaHandler =
        new ResilientSqsCommandLambdaHandler<>(
            extractCommandFromMessageBody,
            retryCommandHandler,
            invalidMessageQueue,
            deadLetterQueue);
    final var actualSqsBatchResponse =
        resilientSqsCommandLambdaHandler.handleRequest(createTestSQSEventBatch(), null);
    assertTrue(actualSqsBatchResponse.getBatchItemFailures().isEmpty());
    // testCommand1 is expected to be retried (TransientException)
    verify(commandHandler, times(2)).handle(testCommand1);
    // testCommand2 is not expected to be retried (PermanentException)
    verify(commandHandler, times(1)).handle(testCommand2);
    // Both should go to the DLQ though
    verify(deadLetterQueue, times(2)).enqueue(deadLetterCaptor.capture());
    verifyNoMoreInteractions(commandHandler, invalidMessageQueue, deadLetterQueue);
  }

  /**
   * The implementation is designed to handle all expected exceptions from the command handler, but
   * if it throws an unexpected exception, we want the message to be moved to the DLQ too.
   */
  @Test
  public void unexpectedProcessingException() {
    when(extractCommandFromMessageBody.apply(eq(testCommand1.payload))).thenReturn(testCommand1);
    when(extractCommandFromMessageBody.apply(eq(testCommand2.payload))).thenReturn(testCommand2);
    when(commandHandler.handle(testCommand1)).thenThrow(new RuntimeException("Unexpected"));

    SQSBatchResponse actualSqsBatchResponse =
        handler.handleRequest(createTestSQSEventBatch(), null);
    assertTrue(actualSqsBatchResponse.getBatchItemFailures().isEmpty());

    verify(commandHandler).handle(testCommand1);
    verify(commandHandler).handle(testCommand2);
    verify(deadLetterQueue).enqueue(deadLetterCaptor.capture());
    assertEquals(testCommand1.payload, deadLetterCaptor.getValue().getBody());
    verifyNoMoreInteractions(commandHandler, invalidMessageQueue, deadLetterQueue);
  }

  @Test
  public void batchSingleDeferException() {
    class AlwaysTrueFunction implements Function<SQSMessage, Boolean> {

      @Override
      public Boolean apply(SQSMessage message) {
        return true;
      }
    }

    when(extractCommandFromMessageBody.apply(eq(testCommand1.payload))).thenReturn(testCommand1);
    when(extractCommandFromMessageBody.apply(eq(testCommand2.payload))).thenReturn(testCommand2);
    when(commandHandler.handle(testCommand1)).thenThrow(new DeferredException("Deferred"));
    var deferMessageProcessing = spy(new AlwaysTrueFunction());

    var deferredHandler =
        new ResilientSqsCommandLambdaHandler<>(
            extractCommandFromMessageBody,
            commandHandler,
            invalidMessageQueue,
            deadLetterQueue,
            deferMessageProcessing);

    var sqsEvent = createTestSQSEventBatch();
    SQSBatchResponse actualSqsBatchResponse = deferredHandler.handleRequest(sqsEvent, null);
    assertEquals(actualSqsBatchResponse.getBatchItemFailures().size(), 1);

    verify(commandHandler).handle(testCommand1);
    verify(commandHandler).handle(testCommand2);
    verify(deferMessageProcessing).apply(sqsEvent.getRecords().getFirst());
    verifyNoMoreInteractions(commandHandler, invalidMessageQueue, deadLetterQueue);
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

    verifyNoMoreInteractions(commandHandler, invalidMessageQueue, deadLetterQueue);
  }

  @Test
  public void nullRecords() {
    final SQSEvent sqsEvent = new SQSEvent();
    SQSBatchResponse actualSqsBatchResponse = handler.handleRequest(sqsEvent, null);
    assertTrue(actualSqsBatchResponse.getBatchItemFailures().isEmpty());
    verifyNoMoreInteractions(commandHandler, invalidMessageQueue, deadLetterQueue);
  }

  @Test
  public void populatesMdcFromCorrelationIdMessageAttribute() {
    // Use a real capturing command handler instead of Mockito
    AtomicReference<String> capturedCorrelationId = new AtomicReference<>();
    CommandHandler<TestCommand, Void> capturingHandler = command -> {
      capturedCorrelationId.set(MDC.get("correlationId"));
      return null;
    };
    var capturingResilientHandler = new ResilientSqsCommandLambdaHandler<>(
        body -> new TestCommand(body), capturingHandler, invalidMessageQueue, deadLetterQueue);

    SQSMessage sqsMessage = new SQSMessage();
    sqsMessage.setMessageId("corr-1");
    sqsMessage.setBody("payload1");

    SQSEvent.MessageAttribute correlationAttr = new SQSEvent.MessageAttribute();
    correlationAttr.setStringValue("test-corr-id-789");
    correlationAttr.setDataType("String");
    var attrs = new java.util.HashMap<String, SQSEvent.MessageAttribute>();
    attrs.put("correlationId", correlationAttr);
    sqsMessage.setMessageAttributes(attrs);

    SQSEvent sqsEvent = new SQSEvent();
    sqsEvent.setRecords(List.of(sqsMessage));

    capturingResilientHandler.handleRequest(sqsEvent, null);

    assertEquals("test-corr-id-789", capturedCorrelationId.get());
    // MDC should be cleared after processing
    assertEquals(null, MDC.get("correlationId"));
  }

  @Test
  public void mdcClearedEvenOnException() {
    SQSMessage sqsMessage = new SQSMessage();
    sqsMessage.setMessageId("corr-2");
    sqsMessage.setBody(testCommand1.payload);

    SQSEvent.MessageAttribute correlationAttr = new SQSEvent.MessageAttribute();
    correlationAttr.setStringValue("should-be-cleared");
    correlationAttr.setDataType("String");
    var attrs = new java.util.HashMap<String, SQSEvent.MessageAttribute>();
    attrs.put("correlationId", correlationAttr);
    sqsMessage.setMessageAttributes(attrs);

    SQSEvent sqsEvent = new SQSEvent();
    sqsEvent.setRecords(List.of(sqsMessage));

    when(extractCommandFromMessageBody.apply(testCommand1.payload)).thenReturn(testCommand1);
    when(commandHandler.handle(org.mockito.ArgumentMatchers.any()))
        .thenThrow(new PermanentException("fail"));

    handler.handleRequest(sqsEvent, null);

    assertEquals("MDC should be cleared even when exception occurs",
        null, MDC.get("correlationId"));
  }

  private record TestCommand(String payload) {}
}
