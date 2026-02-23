/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.http;

import static org.junit.jupiter.api.Assertions.*;

import crosscutting.constants.HttpProtocols;
import infrastructure.http.HttpUrl;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class HttpUrlTest {

  private static final String ENDPOINT = "endpoint";
  private static final String PATH_ELEMENT_1 = "path-element-1";
  private static final String PATH_ELEMENT_2 = "path-element-2";
  private static final String QUERY_PARAM_NAME_1 = "query-param-name-1";
  private static final String QUERY_PARAM_VALUE_1 = "query-param-value-1";

  @Test
  public void buildWithAllAttributes() {
    HttpUrl httpUrl = HttpUrl.builder().withProtocol(HttpProtocols.HTTPS).withEndpoint(ENDPOINT)
        .addPathElement(PATH_ELEMENT_1).addPathElement(PATH_ELEMENT_2)
        .withQueryParameter(QUERY_PARAM_NAME_1, QUERY_PARAM_VALUE_1).build();

    assertEquals(URI.create("https://" + ENDPOINT), httpUrl.getEndpointUri());
    assertEquals(Optional.of(PATH_ELEMENT_1 + "/" + PATH_ELEMENT_2), httpUrl.getPath());
    assertEquals(Map.of(QUERY_PARAM_NAME_1, List.of(QUERY_PARAM_VALUE_1)),
        httpUrl.getQueryParameters());
    assertTrue(httpUrl.hasQueryParameters());
  }

  @Test
  public void noQueryParameters() {
    HttpUrl httpUrl = HttpUrl.builder().withProtocol(HttpProtocols.HTTPS).withEndpoint(ENDPOINT)
        .addPathElement(PATH_ELEMENT_1).addPathElement(PATH_ELEMENT_2).build();
    assertEquals(Optional.of(PATH_ELEMENT_1 + "/" + PATH_ELEMENT_2), httpUrl.getPath());
    assertFalse(httpUrl.hasQueryParameters());
  }

  @Test
  public void noEndpoint() {
    assertThrows(IllegalArgumentException.class,
        () -> HttpUrl.builder().withProtocol(HttpProtocols.HTTPS).build(), "Endpoint is required");
  }

  @Test
  public void noProtocolDefaultsToHttps() {
    HttpUrl httpUrl = HttpUrl.builder().withEndpoint(ENDPOINT).build();
    assertEquals(URI.create("https://" + ENDPOINT), httpUrl.getEndpointUri());
  }

  @Test
  public void duplicatingProtocolIsAccepted() {
    HttpUrl httpUrl = HttpUrl.builder().withProtocol(HttpProtocols.HTTPS)
        .withEndpoint("https://" + ENDPOINT).build();
    assertEquals(URI.create("https://" + ENDPOINT), httpUrl.getEndpointUri());
  }

  @Test
  public void divergingProtocolsNotAllowed() {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> HttpUrl.builder().withProtocol(HttpProtocols.HTTPS).withEndpoint("http://" + ENDPOINT)
            .build());
    assertEquals("Protocol changing from HTTPS to HTTP", exception.getMessage());
  }

  @Test
  public void endpointWithPath() {
    HttpUrl httpUrl = HttpUrl.builder()
        .withEndpoint(ENDPOINT + "/" + PATH_ELEMENT_1)
        .build();
    assertEquals(URI.create("https://" + ENDPOINT), httpUrl.getEndpointUri());
    assertEquals(PATH_ELEMENT_1, httpUrl.getPath().get());
  }

  @Test
  public void splitTrailingSlash() {
    HttpUrl httpUrl = HttpUrl.builder()
        .addPathElement(PATH_ELEMENT_2 + "/")
        .withEndpoint(ENDPOINT + "/" + PATH_ELEMENT_1 + "/")
        .build();
    assertEquals(URI.create("https://" + ENDPOINT), httpUrl.getEndpointUri());
    assertEquals(PATH_ELEMENT_1 + "/" + PATH_ELEMENT_2, httpUrl.getPath().get());
  }

}