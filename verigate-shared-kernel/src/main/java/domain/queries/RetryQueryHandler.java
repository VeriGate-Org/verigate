/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.queries;

import crosscutting.resiliency.Retryable;
import domain.exceptions.PermanentException;
import domain.queries.handlers.QueryHandler;
import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles Querys with retry capabilities.
 */
@Slf4j
public final class RetryQueryHandler<RequestT, ResponseT>
    extends QueryHandlerDecorator<RequestT, ResponseT> {
  private final Retryable retryable;
  private final String retryName;

  /**
   * Constructs a new instance.
   *
   * @param queryHandler the handler that this instruments with retry capabilities
   */
  public RetryQueryHandler(
      QueryHandler<RequestT, ResponseT> queryHandler, Retryable retryable, String retryName) {

    super(queryHandler);
    this.retryable = retryable;
    this.retryName = retryName;
    log.debug(
        "Initialized RetryqueryHandler with retryable: {}, retry name: {}", retryable, retryName);
  }

  @Override
  public ResponseT handle(RequestT query) {
    log.debug("Handling query with retry: {}", query);

    final Callable<ResponseT> callable =
        retryable.createCallable(
            retryName,
            () -> {
              ResponseT result = super.handle(query);
              log.debug("Query handled successfully: {}", query);
              return result;
            });

    try {
      return callable.call();
    } catch (Exception e) {
      log.error("Failed to handle query after retries: {}", query, e);
      // Note that this logic does not own higher-level resiliency features like moving
      // failed messages to dead-letter queues. Use a ResilientQueryLambdaHandler wrapping
      // this RetryqueryHandler if this is required.
      throw new PermanentException(retryName, e);
    }
  }
}
