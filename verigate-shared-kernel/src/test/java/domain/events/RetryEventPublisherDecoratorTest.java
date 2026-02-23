/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.events;

import static org.mockito.Mockito.*;

import crosscutting.resiliency.DefaultRetry;
import crosscutting.resiliency.Retryable;
import domain.events.model.SimpleEvent;
import java.time.Duration;
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
final class RetryEventPublisherDecoratorTest {

  private final String retryName = "testRetry";
  @Mock private EventPublisher<SimpleEvent> decoratedPublisher;

  @Mock private Retryable retryable;

  @Captor private ArgumentCaptor<Runnable> runnableCaptor;

  @InjectMocks private RetryEventPublisher<SimpleEvent> retryDecorator;

  @Test
  void testPublishSuccessful() {
    var realRetryable = new DefaultRetry(3, Duration.ofMillis(5), Set.of(RuntimeException.class));
    retryDecorator =
        new RetryEventPublisher<>(decoratedPublisher, realRetryable, retryName);
    var event = new SimpleEvent(UUID.randomUUID().toString(), "test");

    retryDecorator.publish(event);

    verify(decoratedPublisher).publish(event);
  }

  @Test
  void testPublishWithRetry() {
    retryDecorator = new RetryEventPublisher<>(decoratedPublisher, retryable, retryName);

    SimpleEvent event = mock(SimpleEvent.class);
    doAnswer(invocation -> invocation.<Runnable>getArgument(1))
        .when(retryable)
        .createRunnable(eq(retryName), any(Runnable.class));

    retryDecorator.publish(event);

    verify(retryable).createRunnable(eq(retryName), runnableCaptor.capture());
    verify(decoratedPublisher).publish(event);
  }

  @Test
  void testPublishWithRetries() {
    retryDecorator = new RetryEventPublisher<>(decoratedPublisher, retryable, retryName);

    SimpleEvent event = mock(SimpleEvent.class);
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

    retryDecorator.publish(event);

    verify(retryable).createRunnable(eq(retryName), runnableCaptor.capture());
    runnableCaptor.getValue().run();
    verify(decoratedPublisher, times(2)).publish(event);
  }

  @Test
  void testPublishWithException() {
    retryDecorator = new RetryEventPublisher<>(decoratedPublisher, retryable, retryName);

    SimpleEvent event = mock(SimpleEvent.class);
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
      retryDecorator.publish(event);
    } catch (Exception ignored) {
    }

    verify(retryable).createRunnable(eq(retryName), runnableCaptor.capture());
    verify(decoratedPublisher).publish(event);
  }
}
