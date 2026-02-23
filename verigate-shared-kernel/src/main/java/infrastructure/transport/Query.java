/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.transport;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;

/**
 * Represents an API service.
 * <p>
 * This class includes a method to send a GET request to an API and return the response.
 * </p>
 */
public interface Query<T, R> {

  /**
   * Executes the request and returns the response.
   *
   * @param request the request
   * @return the response
   * @throws TransientException the transient exception
   * @throws PermanentException the permanent exception
   */
  R execute(T request) throws TransientException, PermanentException;
}
