/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.builders;

import crosscutting.constants.HttpProtocols;
import java.util.Map;

/**
 * `ParameterExceptionBuilder` is the class that represents
 * the builder for the parameter exceptions.
 */
public interface UrlBuilder {
  UrlBuilder addPath(String path);

  UrlBuilder addPathParameter(String path);

  UrlBuilder addQueryStringParameters(Map<String, String> query);

  UrlBuilder setProtocol(HttpProtocols protocol);

  String build();
}
