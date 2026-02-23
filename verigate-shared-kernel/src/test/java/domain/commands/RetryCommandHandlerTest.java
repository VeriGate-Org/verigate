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
import java.util.concurrent.Callable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
final class RetryCommandHandlerTest {

  private final String retryName = "testRetry";

  @Mock private CommandHandler<BaseCommand, Void> baseCommandHandler;

  @Mock private Retryable retryable;

  @Captor private ArgumentCaptor<Callable> callableCaptor;

  @InjectMocks private RetryCommandHandler<BaseCommand, Void> retryDecorator;

  @Test
  void testHandleSuccessful() {
    var realRetryable = new DefaultRetry(3, Duration.ofMillis(5), Set.of(RuntimeException.class));
    retryDecorator = new RetryCommandHandler<>(baseCommandHandler, realRetryable, retryName);
    var command = new BaseCommand(UUID.randomUUID(), Instant.now(), "test") {};

    retryDecorator.handle(command);

    verify(baseCommandHandler).handle(command);
  }

  @Test
  void testHandlingWithRetry() {
    retryDecorator = new RetryCommandHandler<>(baseCommandHandler, retryable, retryName);

    var command = new BaseCommand(UUID.randomUUID(), Instant.now(), "test") {};
    doAnswer(invocation -> invocation.<Callable>getArgument(1))
        .when(retryable)
        .createCallable(eq(retryName), any(Callable.class));

    retryDecorator.handle(command);

    verify(retryable).createCallable(eq(retryName), callableCaptor.capture());
    verify(baseCommandHandler).handle(command);
  }

  @Test
  void testHandlinghWithRetries() throws Exception {
    retryDecorator = new RetryCommandHandler<>(baseCommandHandler, retryable, retryName);

    var command = new BaseCommand(UUID.randomUUID(), Instant.now(), "test") {};
    doAnswer(
            invocation -> {
              Callable originalCallable = invocation.getArgument(1);
              return (Callable)
                  () -> {
                    try {
                      return originalCallable.call();
                    } catch (Exception e) {
                      // Simulate a retry mechanism
                      return originalCallable.call();
                    }
                  };
            })
        .when(retryable)
        .createCallable(eq(retryName), any(Callable.class));

    retryDecorator.handle(command);

    verify(retryable).createCallable(eq(retryName), callableCaptor.capture());
    callableCaptor.getValue().call();
    verify(baseCommandHandler, times(2)).handle(command);
  }

  @Test
  void testHandlingWithException() {
    retryDecorator = new RetryCommandHandler<>(baseCommandHandler, retryable, retryName);

    var command = new BaseCommand(UUID.randomUUID(), Instant.now(), "test") {};
    doAnswer(
            invocation -> {
              Callable originalCallable = invocation.getArgument(1);
              return (Callable)
                  () -> {
                    return originalCallable.call();
                  };
            })
        .when(retryable)
        .createCallable(eq(retryName), any(Callable.class));

    try {
      retryDecorator.handle(command);
    } catch (Exception ignored) {
    }

    verify(retryable).createCallable(eq(retryName), callableCaptor.capture());
    verify(baseCommandHandler).handle(command);
  }
}
