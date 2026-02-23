/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.functions.lambda.exceptions.handlers;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import crosscutting.resiliency.Retryable;
import domain.exceptions.DeserializeException;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import domain.messages.MessageQueue;
import java.util.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests for the {@link DefaultCommandErrorHandler} class.
 */
@ExtendWith(MockitoExtension.class)
public class DefaultCommandErrorHandlerTest {

  @Mock private MessageQueue<Object> deadLetterQueueSender;

  @Mock private MessageQueue<Object> invalidMessageQueueSender;

  @Mock private Retryable retryable;

  @Mock private Logger logger;

  private DefaultCommandErrorHandler<Object, Object, Object> errorHandler;

  @Captor private ArgumentCaptor<Runnable> runnableCaptor;

  /**
   * Sets up the test environment before each test.
   */
  @BeforeEach
  public void setUp() {
    errorHandler =
        new DefaultCommandErrorHandler<>(
            retryable, deadLetterQueueSender, invalidMessageQueueSender);
  }

  @Test
  public void testHandleSuccess() {
    Runnable logic = mock(Runnable.class);
    when(retryable.createRunnable(anyString(), any())).thenReturn(logic);

    errorHandler.handle(new Object(), Object.class, logic);

    verify(logic).run();
    verifyNoMoreInteractions(deadLetterQueueSender, invalidMessageQueueSender, logger);
  }

  @Test
  public void testHandleDeserializeException() {
    Runnable logic =
        () -> {
          throw new RuntimeException(new DeserializeException(new byte[0], "Test"));
        };
    when(retryable.createRunnable(anyString(), any())).thenReturn(logic);

    errorHandler.handle(new Object(), Object.class, logic);

    verify(logger, never())
        .severe(
            argThat(
                (String argument) ->
                    argument != null
                        && argument.startsWith("DeserializeException processing message: ")));
    verify(invalidMessageQueueSender).enqueue(any());
    verifyNoMoreInteractions(deadLetterQueueSender);
  }

  @Test
  public void testHandleTransientException() {
    Runnable logic =
        () -> {
          throw new RuntimeException(new TransientException("Test"));
        };
    when(retryable.createRunnable(anyString(), any())).thenReturn(logic);

    assertThrows(
        TransientException.class,
        () -> {
          errorHandler.handle(new Object(), Object.class, logic);
        });

    verify(logger, never())
        .severe(
            argThat(
                (String argument) ->
                    argument != null
                        && argument.startsWith("Transient error processing SQS event:")));
    verifyNoMoreInteractions(deadLetterQueueSender, invalidMessageQueueSender);
  }

  @Test
  public void testHandlePermanentException() {
    Runnable logic =
        () -> {
          throw new RuntimeException(new PermanentException("Test"));
        };
    when(retryable.createRunnable(anyString(), any())).thenReturn(logic);

    errorHandler.handle(new Object(), Object.class, logic);

    verify(logger, never())
        .severe(
            argThat(
                (String argument) ->
                    argument != null
                        && argument.startsWith("Permanent error processing SQS event:")));
    verify(deadLetterQueueSender).enqueue(any());
    verifyNoMoreInteractions(invalidMessageQueueSender);
  }

  @Test
  public void testHandleUnknownRuntimeException() {
    Runnable logic =
        () -> {
          throw new RuntimeException(new Exception("Test"));
        };
    when(retryable.createRunnable(anyString(), any())).thenReturn(logic);

    errorHandler.handle(new Object(), Object.class, logic);

    verify(logger, never())
        .severe(
            argThat(
                (String argument) ->
                    argument != null
                        && argument.startsWith("Permanent error processing SQS event:")));
    verify(deadLetterQueueSender).enqueue(any());
    verifyNoMoreInteractions(invalidMessageQueueSender);
  }

  @Test
  public void testHandleGeneralException() {
    Runnable logic =
        () -> {
          throw new RuntimeException("Test");
        };
    when(retryable.createRunnable(anyString(), any())).thenReturn(logic);

    errorHandler.handle(new Object(), Object.class, logic);

    verify(logger, never())
        .severe(
            argThat(
                (String argument) ->
                    argument != null && argument.startsWith("Error processing SQS event:")));
    verify(deadLetterQueueSender).enqueue(any());
    verifyNoMoreInteractions(invalidMessageQueueSender);
  }
}
