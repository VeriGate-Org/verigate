/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.queries;

import domain.queries.handlers.QueryHandler;
import lombok.AllArgsConstructor;

/**
 * Abstract decorator class for the handling of query.
 *
 * @param <T> the type of query this handles
 */
@AllArgsConstructor
public abstract class QueryHandlerDecorator<RequestT, ResponseT>
    implements QueryHandler<RequestT, ResponseT> {

  private final QueryHandler<RequestT, ResponseT> delegate;

  @Override
  public ResponseT handle(RequestT command) {
    return delegate.handle(command);
  }
}
