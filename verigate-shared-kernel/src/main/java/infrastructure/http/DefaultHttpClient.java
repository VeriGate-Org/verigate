/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.http;

import java.io.IOException;

/**
 * A basic http client implementation.
 * TODO: Implement similar to {@link DefaultInternalRequestBuilder}.
 */
public class DefaultHttpClient implements HttpClient {

  @Override
  public HttpResponse get(HttpUrl url) {
    throw new UnsupportedOperationException("Not implemented yet.");
  }

  @Override
  public HttpResponse post(HttpUrl url, String requestBody) {
    throw new UnsupportedOperationException("Not implemented yet.");
  }

}
