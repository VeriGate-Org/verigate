/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.http;

import crosscutting.constants.HttpProtocols;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

/**
 * Represents components of a URL used for HTTP communication. Components are supported in the
 * following structure:
 *  "endpoint-uri/path-elements?query-parameters"
 *  Notes:
 *  1. Only "endpoint-uri" is mandatory.
 *  2. "path-elements" can consist of 0 or more elements which will be "/" separated in full URLs
 */
public class HttpUrl {

  private final URI endpointUri;
  private final List<String> pathElements;
  private final Map<String, List<String>> queryParameters;

  /**
   * Private to only allow the builder to construct with expected values.
   */
  private HttpUrl(
      URI endpointUri, List<String> pathElements, Map<String, List<String>> queryParameters) {
    this.endpointUri = endpointUri;
    this.pathElements =
        pathElements == null ? Collections.emptyList() : Collections.unmodifiableList(pathElements);
    this.queryParameters =
        queryParameters == null
            ? Collections.emptyMap()
            : Collections.unmodifiableMap(queryParameters);
  }

  public static Builder builder() {
    return new Builder();
  }

  public URI getEndpointUri() {
    return endpointUri;
  }

  /**
   * Obtain the individual path elements. This could be an empty list for no path elements.
   */
  public List<String> getPathElements() {
    return pathElements;
  }

  /**
   * Obtain the fully constructed path part of the url.
   */
  public Optional<String> getPath() {
    if (getPathElements().isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(String.join("/", getPathElements()));
  }

  public boolean hasQueryParameters() {
    return queryParameters != null && !queryParameters.isEmpty();
  }

  public Map<String, List<String>> getQueryParameters() {
    return queryParameters;
  }

  @Override
  public String toString() {
    return "HttpUrl{"
        + "endpointUri="
        + endpointUri
        + ", pathElements="
        + pathElements
        + ", queryParameters="
        + queryParameters
        + '}';
  }

  /**
   * A builder with validation for {@link HttpUrl}.
   */
  public static class Builder {
    private HttpProtocols protocol;
    private String endPoint;
    private List<String> pathElements = new ArrayList<>();
    private Map<String, List<String>> queryParameters = new HashMap<>();

    /**
     * Set the protocol.
     */
    public Builder withProtocol(HttpProtocols protocol) {
      // A protocol could already be set. Either by the caller, or extracted from an endpoint.
      // In these cases, make sure it is not changing.
      if (this.protocol != null && this.protocol != protocol) {
        throw new IllegalArgumentException(
            "Protocol changing from %s to %s".formatted(this.protocol, protocol));
      }
      this.protocol = protocol;
      return this;
    }

    /**
     * Set the endpoint. This supports capturing out the protocol and/or path parts.
     */
    public Builder withEndpoint(String endpoint) {
      if (StringUtils.isBlank(endpoint)) {
        throw new IllegalArgumentException("Non-blank endpoint required");
      }
      // If the endpoint is supplied with a protocol, we split it off to get a clear endpoint for
      // easier validation.
      String effectiveEndpoint = endpoint;
      final String[] protocolSplit = endpoint.split("://");
      if (protocolSplit.length == 2) {
        withProtocol(HttpProtocols.valueOf(protocolSplit[0].toUpperCase()));
        effectiveEndpoint = protocolSplit[1];
      } else if (protocolSplit.length > 2) {
        throw new IllegalArgumentException("Multiple protocols in endpoint?");
      }
      // Some endpoints are configured with a path element already. Those are technically not
      // endpoints for HTTP URL purposes. Instead of failing those, we'll conveniently split it
      // out here as the first path elements
      final String[] pathSplit = effectiveEndpoint.split("/");
      if (pathSplit.length > 1) {
        effectiveEndpoint = pathSplit[0];
        for (int i = 1; i < pathSplit.length; i++) {
          pathElements.add(i - 1, pathSplit[i]);
        }
      }
      this.endPoint = effectiveEndpoint;
      return this;
    }

    /**
     * Add a single path element. The order in which this is called determines the final path
     * order.
     */
    public Builder addPathElement(String pathElement) {
      if (StringUtils.isBlank(pathElement)) {
        throw new IllegalArgumentException("Non-blank path element required");
      }
      // For convenience, we'll split off any trailing slashes from paths
      String effectivePathElement = pathElement;
      if (effectivePathElement.endsWith("/")) {
        effectivePathElement = effectivePathElement.substring(0, effectivePathElement.length() - 1);
      }
      if (effectivePathElement.contains("/")) {
        throw new IllegalArgumentException("Multiple path elements combined");
      }
      if (effectivePathElement.contains("?")) {
        throw new IllegalArgumentException("Query parameters should be supplied outside paths");
      }
      this.pathElements.add(effectivePathElement);
      return this;
    }

    public Builder withQueryParameter(String name, String value) {
      return withQueryParameter(name, List.of(value));
    }

    public Builder withQueryParameter(String name, List<String> values) {
      this.queryParameters.put(name, values);
      return this;
    }

    /**
     * Construct a new HttpUrl.
     */
    public HttpUrl build() throws IllegalArgumentException {
      // No protocol defaults to HTTPS
      if (protocol == null) {
        protocol = HttpProtocols.HTTPS;
      }
      if (StringUtils.isBlank(endPoint)) {
        throw new IllegalArgumentException("Endpoint is required");
      }
      try {
        return new HttpUrl(
            new URI(protocol.getProtocol(), endPoint, null, null), pathElements, queryParameters);
      } catch (URISyntaxException e) {
        throw new IllegalArgumentException("URI syntax error", e);
      }
    }
  }
}
