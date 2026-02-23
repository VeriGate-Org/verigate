/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.http;

import com.google.inject.Inject;
import crosscutting.constants.HttpProtocols;
import crosscutting.environment.Environment;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.http.SdkHttpMethod;

/** `DefaultInternalRequestBuilder` is the default implementation. */
public final class DefaultInternalRequestBuilder implements InternalRequestBuilder<String> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultInternalRequestBuilder.class);

  private final HttpClient httpClient;
  private final Environment env;
  private String payload;
  private String path = "";
  private SdkHttpMethod method;
  private String domain;

  /**
   * Initializes a new instance of the DefaultInternalRequestBuilder class.
   *
   * @param env The environment.
   * @param httpClientFactory The HTTP client factory.
   */
  @Inject
  public DefaultInternalRequestBuilder(
      Environment env, HttpClientFactory httpClientFactory) {
    this.httpClient = httpClientFactory.create();
    this.env = env;
  }

  /**
   * Executes the command.
   *
   * @return The result of the command.
   * @throws TransientException If the command fails transiently.
   * @throws PermanentException If the command fails permanently.
   */
  public String execute() throws TransientException, PermanentException {

    final HttpUrl httpUrl =
        HttpUrl.builder()
            .withProtocol(HttpProtocols.HTTPS)
            .withEndpoint(domain)
            .addPathElement(path)
            .build();

    // TODO: Implement circuit breaker
    return httpClient
        .get(httpUrl)
        .onSuccessOrElseThrow(HttpResponse::body);
  }

  @Override
  public DefaultInternalRequestBuilder setPayload(String payload) {
    this.payload = payload;
    return this;
  }

  @Override
  public DefaultInternalRequestBuilder setPath(String path) {
    this.path = path;
    return this;
  }

  @Override
  public DefaultInternalRequestBuilder setMethod(SdkHttpMethod method) {
    this.method = method;
    return this;
  }

  @Override
  public DefaultInternalRequestBuilder setDomain(String domain) {
    this.domain = domain;
    return this;
  }
}
