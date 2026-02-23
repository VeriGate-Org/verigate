/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.commands;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import crosscutting.resiliency.DefaultRetry;
import crosscutting.resiliency.Retryable;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
final class RetryCommandDispatcherTest {

  private final String retryName = "testRetry";
  
  @Mock private CommandDispatcher<BaseCommand> baseCommandDispatcher;

  @Mock private Retryable retryable;

  @Captor private ArgumentCaptor<Runnable> runnableCaptor;

  @InjectMocks private RetryCommandDispatcher<BaseCommand> retryDecorator;

  @Test
  void testDispatchSuccessful() {
    var realRetryable = new DefaultRetry(3, Duration.ofMillis(5), Set.of(RuntimeException.class));
    retryDecorator =
        new RetryCommandDispatcher<>(baseCommandDispatcher, realRetryable, retryName);
    var command = new BaseCommand(UUID.randomUUID(), Instant.now(), "test"){};

    retryDecorator.dispatch(command);

    verify(baseCommandDispatcher).dispatch(command);
  }

  @Test
  void testDispatchWithRetry() {
    retryDecorator = new RetryCommandDispatcher<>(baseCommandDispatcher, retryable, retryName);

    var command = new BaseCommand(UUID.randomUUID(), Instant.now(), "test"){};
    doAnswer(invocation -> invocation.<Runnable>getArgument(1))
        .when(retryable)
        .createRunnable(eq(retryName), any(Runnable.class));

    retryDecorator.dispatch(command);

    verify(retryable).createRunnable(eq(retryName), runnableCaptor.capture());
    verify(baseCommandDispatcher).dispatch(command);
  }

  @Test
  void testDispatchhWithRetries() {
    retryDecorator = new RetryCommandDispatcher<>(baseCommandDispatcher, retryable, retryName);

    var command = new BaseCommand(UUID.randomUUID(), Instant.now(), "test"){};
    doAnswer(
        invocation -> {
          Runnable originalRunnable = invocation.getArgument(1);
          return (Runnable)
              () -> {
                try {
                  originalRunnable.run();
                } catch (Exception e) {
                  // Simulate a retry mechanism
                  originalRunnable.run();
                }
              };
        })
        .when(retryable)
        .createRunnable(eq(retryName), any(Runnable.class));

    retryDecorator.dispatch(command);

    verify(retryable).createRunnable(eq(retryName), runnableCaptor.capture());
    runnableCaptor.getValue().run();
    verify(baseCommandDispatcher, times(2)).dispatch(command);
  }

  @Test
  void testDispatchWithException() {
    retryDecorator = new RetryCommandDispatcher<>(baseCommandDispatcher, retryable, retryName);

    var command = new BaseCommand(UUID.randomUUID(), Instant.now(), "test"){};
    doAnswer(
        invocation -> {
          Runnable originalRunnable = invocation.getArgument(1);
          return (Runnable)
              () -> {
                originalRunnable.run();
                throw new RuntimeException("Test exception");
              };
        })
        .when(retryable)
        .createRunnable(eq(retryName), any(Runnable.class));

    try {
      retryDecorator.dispatch(command);
    } catch (Exception ignored) {
    }

    verify(retryable).createRunnable(eq(retryName), runnableCaptor.capture());
    verify(baseCommandDispatcher).dispatch(command);
  }
}
