/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.builders;

import crosscutting.constants.HttpProtocols;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * `ParameterExceptionBuilder` is the class that represents
 * the builder for the parameter exceptions.
 */
public class DefaultUrlBuilder implements UrlBuilder {
  private String route = "";

  @Override
  public UrlBuilder addPath(String path) {
    route += path + "/";
    return this;
  }

  @Override
  public UrlBuilder addPathParameter(String path) {
    route += path + "/";
    return this;
  }

  @Override
  public UrlBuilder addQueryStringParameters(Map<String, String> query) {
    if (query != null && !query.isEmpty()) {
      route += "?";
      String queryString =
          query.entrySet().stream()
              .map(entry -> entry.getKey() + "=" + entry.getValue())
              .collect(Collectors.joining("&"));
      route += queryString;
    }
    return this;
  }

  @Override
  public UrlBuilder setProtocol(HttpProtocols protocol) {
    var currentPath = this.route.split("://");
    if (currentPath.length > 1) {
      this.route = protocol.getProtocol() + "://" + currentPath[1];
    } else {
      this.route = protocol.getProtocol() + "://" + this.route;
    }
    return this;
  }

  @Override
  public String build() {
    var finalRoute = this.route;
    this.route = "";
    return finalRoute;
  }
}
