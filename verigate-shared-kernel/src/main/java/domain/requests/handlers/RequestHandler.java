/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.requests.handlers;

import domain.exceptions.InvariantViolationException;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;

/**
 * Represents a handler for executing queries.
 *
 * @param <T> The type of request to be handled.
 */
public interface RequestHandler<RequestT, ResponseT> {

  /**
   * Handles the given synchronous request.
   *
   * @param request The request to be handled.
   * @throws TransientException If a transient error occurs during request execution.
   * @throws PermanentException If a permanent error occurs during request execution.
   * @throws InvariantViolationException If an invariant violation occurs during request execution.
   */
  ResponseT handleRequest(RequestT request)
      throws TransientException, PermanentException, InvariantViolationException;
}
