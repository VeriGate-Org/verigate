/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.events;

import crosscutting.patterns.Factory;
import crosscutting.resiliency.CircuitBreaker;
import crosscutting.resiliency.Retryable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class for creating decorated EventPublisher instances. This class applies both
 * circuitbreaker and retryable decorators to the given EventPublisher.
 *
 * @param <EventT> The type of event to be published.
 */
public final class PrimaryPublisherDecoratedFactory<EventT>
    implements Factory<EventPublisher<EventT>, EventPublisher<EventT>> {

  private static final Logger logger =
      LoggerFactory.getLogger(PrimaryPublisherDecoratedFactory.class);

  private final CircuitBreaker circuitBreaker;
  private final Retryable retryable;
  private final String retryContext;

  /**
   * Constructs an EventPublisherDecoratedFactory with the specified circuit breaker, retryable, and
   * retry context.
   *
   * @param circuitBreaker The circuit breaker to apply.
   * @param retryable The retryable policy to apply.
   * @param retryContext The context for retry operations.
   */
  public PrimaryPublisherDecoratedFactory(
      final CircuitBreaker circuitBreaker, final Retryable retryable, final String retryContext) {
    this.circuitBreaker = circuitBreaker;
    this.retryable = retryable;
    this.retryContext = retryContext;
  }

  /**
   * Creates a decorated EventPublisher instance with both circuit breaker and retryable decorators
   * applied.
   *
   * @param publisher The original EventPublisher to decorate.
   * @return The decorated EventPublisher.
   */
  @Override
  public EventPublisher<EventT> create(EventPublisher<EventT> publisher) {
    logger.debug("Creating decorated EventPublisher with CircuitBreaker and Retryable.");

    var retryPublisher = new RetryEventPublisher<>(publisher, retryable, retryContext);
    logger.debug("Applied Retryable decorator.");

    var circuitBreakerPublisher =
        new CircuitBreakerEventPublisher<>(retryPublisher, circuitBreaker);
    logger.debug("Applied CircuitBreaker decorator.");

    return circuitBreakerPublisher;
  }
}
