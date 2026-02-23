/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import crosscutting.patterns.SimpleFactory;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientResourceAccessException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.http.ExecutableHttpRequest;
import software.amazon.awssdk.http.HttpExecuteRequest;
import software.amazon.awssdk.http.HttpExecuteResponse;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;

@ExtendWith(MockitoExtension.class)
class AwsAuthHttpClientTest {

  @Mock
  SdkHttpClient mockHttpClient;

  @Mock
  ExecutableHttpRequest mockHttpRequest;

  @Mock
  SdkHttpResponse mockSdkHttpResponse;

  @Mock
  HttpExecuteResponse mockHttpResponse;

  // class under test
  AwsAuthHttpClient client;

  @BeforeEach
  void setUp() {

    final AwsRegionProvider regionProvider = () -> Region.EU_WEST_1;
    final AwsCredentialsProvider credentialsProvider = () -> new AwsCredentials() {
      @Override
      public String accessKeyId() {
        return "accesKeyId";
      }

      @Override
      public String secretAccessKey() {
        return "secretAccessKey";
      }
    };
    final SimpleFactory<SdkHttpClient> httpClientFactory = () -> mockHttpClient;
    client = new AwsAuthHttpClient(regionProvider, credentialsProvider, httpClientFactory);
  }

  @Test
  public void authHeaders() throws Exception {

    when(mockSdkHttpResponse.statusCode()).thenReturn(200);
    when(mockHttpResponse.httpResponse()).thenReturn(mockSdkHttpResponse);
    when(mockHttpRequest.call()).thenReturn(mockHttpResponse);
    when(mockHttpClient.prepareRequest(any())).thenReturn(mockHttpRequest);

    HttpResponse httpResponse = client.get(
        HttpUrl.builder().withEndpoint("https://endpoint.com").build());

    assertEquals(200, httpResponse.statusCode());
    ArgumentCaptor<HttpExecuteRequest> requestCaptor = ArgumentCaptor.forClass(
        HttpExecuteRequest.class
    );
    verify(mockHttpClient).prepareRequest(requestCaptor.capture());
    HttpExecuteRequest actualExecuteRequest = requestCaptor.getValue();
    final Map<String, List<String>> headers = actualExecuteRequest.httpRequest().headers();
    assertTrue(headers.containsKey("Authorization"));
    assertTrue(headers.containsKey("x-amz-content-sha256"));
    assertTrue(headers.containsKey("X-Amz-Date"));
    assertEquals(List.of("endpoint.com"), headers.get("host"));
  }

  @Test
  public void whenIOExceptionThenThrowsTransientResourceAccessException() throws IOException {

    when(mockHttpRequest.call()).thenThrow(new IOException("test"));
    when(mockHttpClient.prepareRequest(any())).thenReturn(mockHttpRequest);

    TransientResourceAccessException exception = assertThrows(
        TransientResourceAccessException.class,
        () -> client.get(HttpUrl.builder().withEndpoint("https://endpoint.com").build()));

    assertEquals("Could not execute request GET https://endpoint.com", exception.getMessage());
  }

  @Test
  public void whenUnexpectedThenThrowsPermanentException() throws IOException {

    when(mockHttpRequest.call()).thenThrow(new RuntimeException("test"));
    when(mockHttpClient.prepareRequest(any())).thenReturn(mockHttpRequest);

    assertThrows(PermanentException.class,
        () -> client.get(HttpUrl.builder().withEndpoint("https://endpoint.com").build()));
  }
}
