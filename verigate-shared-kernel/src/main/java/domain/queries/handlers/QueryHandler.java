/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.queries.handlers;

import domain.exceptions.InvariantViolationException;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;

/**
 * Represents a handler for executing queries.
 *
 * @param <T> The type of query to be handled.
 */
public interface QueryHandler<RequestT, ResponseT> {

  /**
   * Handles the given query.
   *
   * @param query The query to be handled.
   * @throws TransientException If a transient error occurs during query execution.
   * @throws PermanentException If a permanent error occurs during query execution.
   * @throws InvariantViolationException If an invariant violation occurs during query execution.
   */
  ResponseT handle(RequestT query)
      throws TransientException, PermanentException, InvariantViolationException;
}
