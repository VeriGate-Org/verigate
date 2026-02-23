/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.http;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import java.io.IOException;

/**
 * An interface that defines the contract for an HTTP client.
 */
public interface HttpClient {
  /**
   * Sends a GET request to the specified URL.
   *
   * @param url the URL to send the GET request to
   * @return the response body as a String
   */
  HttpResponse get(HttpUrl url) throws TransientException, PermanentException;

  /**
   * Sends a POST request to the specified URL with the given request body.
   *
   * @param url the URL to send the POST request to
   * @param requestBody the request body as a String
   * @return the response body as a String
   */
  HttpResponse post(HttpUrl url, String requestBody) throws TransientException, PermanentException;

}
