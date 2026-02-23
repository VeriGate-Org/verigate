/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.events;

import crosscutting.resiliency.CircuitBreaker;
import domain.exceptions.CircuitState;
import domain.exceptions.TransientException;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Decorator class that adds circuit breaker functionality to an {@link EventPublisher}.
 *
 * <p>This class ensures that events are published only when the circuit breaker allows it. If the
 * circuit breaker is open, a {@link TransientException} is thrown, indicating that the operation
 * should be retried later.
 *
 * @param <EventT> the type of the event to be published
 */
public final class CircuitBreakerEventPublisher<EventT>
    extends EventPublisherDecorator<EventT> {

  private static final Logger logger =
      LoggerFactory.getLogger(CircuitBreakerEventPublisher.class);
  private final CircuitBreaker circuitBreaker;

  /**
   * Constructs a new {@code CircuitBreakerEventPublisherDecorator} with the specified decorated
   * publisher and circuit breaker.
   *
   * @param decoratedPublisher the event publisher to be decorated
   * @param circuitBreaker the circuit breaker to be used for controlling event publishing
   */
  public CircuitBreakerEventPublisher(
      final EventPublisher<EventT> decoratedPublisher, final CircuitBreaker circuitBreaker) {
    super(decoratedPublisher);
    this.circuitBreaker = circuitBreaker;
    logger.debug(
        "Initialized CircuitBreakerEventPublisherDecorator with circuit breaker: {}",
        circuitBreaker);
  }

  /**
   * Publishes an event, using the circuit breaker to control whether the event should be published.
   *
   * <p>If the circuit breaker is open, a {@link TransientException} is thrown.
   *
   * @param event the event to be published
   * @throws TransientException if the circuit breaker is open
   */
  @Override
  public void publish(final EventT event) {
    logger.debug("Publishing event: {}", event);

    Supplier<Void> originalSupplier =
        () -> {
          decoratedPublisher.publish(event);
          logger.debug("Event published successfully: {}", event);
          return null;
        };

    var circuitBreakerSupplier = circuitBreaker.decorate(originalSupplier);

    try {
      logger.debug("Attempting to publish event through circuit breaker");
      circuitBreakerSupplier.get();
      logger.debug("Event published successfully through circuit breaker: {}", event);
    } catch (CircuitBreaker.CircuitBreakerOpenException e) {
      logger.error("Circuit breaker is open: {}", e.getMessage(), e);
      throw new TransientException(e, CircuitState.OPEN, "Circuit breaker is open");
    }
  }
}
