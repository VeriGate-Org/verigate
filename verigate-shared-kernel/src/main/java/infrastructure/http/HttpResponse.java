/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.http;

import domain.exceptions.PermanentException;
import java.util.function.Function;

/**
 * A record that represents an HTTP response.
 *
 * @param statusCode the HTTP status code
 * @param body       the body of the HTTP response
 */
public record HttpResponse(int statusCode, String body) {

  /**
   * Returns true if the status code is in the 2xx range.
   */
  public boolean isSuccess() {

    // https://httpwg.org/specs/rfc9110.html#status.2xx
    return statusCode >= 200 && statusCode <= 299;
  }

  /**
   * Returns this if it is a success response, otherwise throws an exception.
   */
  public HttpResponse orElseThrow() throws PermanentException {

    if (isSuccess()) {
      return this;
    }

    // the type of exception could be tweaked if necessary, e.g. based on response code
    throw new PermanentException(
        "HTTP request failed with status code %s and response body: %s".formatted(statusCode, body)
    );
  }

  /**
   * Calls the specified success handler if it is a success response,
   * otherwise throws an exception.
   *
   * @return the return value from the success handler
   */
  public <S> S onSuccessOrElseThrow(Function<HttpResponse, S> successHandler) {

    orElseThrow();
    return successHandler.apply(this);
  }

  /**
   * Calls the specified success handler if it is a success response,
   * otherwise throws the exception provided by the exception handler.
   *
   * @return the return value from the success handler
   */
  public <S, E extends Exception> S onSuccessOrElseThrow(
      Function<HttpResponse, S> successHandler,
      Function<HttpResponse, E> exceptionMapper) throws E {

    if (isSuccess()) {
      return successHandler.apply(this);
    } else {
      throw exceptionMapper.apply(this);
    }
  }
}
