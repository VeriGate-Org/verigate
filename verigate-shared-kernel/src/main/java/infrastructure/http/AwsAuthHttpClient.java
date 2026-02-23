/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.http;

import crosscutting.patterns.SimpleFactory;
import domain.exceptions.PermanentException;
import domain.exceptions.TransientResourceAccessException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.ExecutableHttpRequest;
import software.amazon.awssdk.http.HttpExecuteRequest;
import software.amazon.awssdk.http.HttpExecuteResponse;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.SdkHttpRequest.Builder;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4HttpSigner;
import software.amazon.awssdk.http.auth.spi.signer.SignedRequest;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;

/**
 * An implementation of an HTTP client which supports signing requests with AWS Auth. This will use
 * AWS credentials from the "default AWS credentials provider chain", which supports the usual
 * sources of credentials in the expected order.
 */
public class AwsAuthHttpClient implements HttpClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(AwsAuthHttpClient.class);
  private static final String SERVICE_NAME = "execute-api";

  private final AwsRegionProvider regionProvider;
  private final AwsCredentialsProvider credentialsProvider;
  private final SdkHttpClient httpClient;

  /** Default constructor. */
  public AwsAuthHttpClient() {
    this(
        DefaultAwsRegionProviderChain.builder().build(),
        DefaultCredentialsProvider.create(),
        ApacheHttpClient::create);
  }

  /** Only override the default region provider. */
  public AwsAuthHttpClient(AwsRegionProvider regionProvider) {
    this(regionProvider, DefaultCredentialsProvider.create(), ApacheHttpClient::create);
  }

  /**
   * Ability to override default versions of core dependencies. Useful for testing.
   *
   * @param regionProvider      Provide the relevant AWS region.
   * @param credentialsProvider Provide credentials to sign requests with.
   * @param httpClientFactory   Create a supported HTTP client.
   */
  public AwsAuthHttpClient(
      AwsRegionProvider regionProvider,
      AwsCredentialsProvider credentialsProvider,
      SimpleFactory<SdkHttpClient> httpClientFactory) {
    this.regionProvider = regionProvider;
    this.credentialsProvider = credentialsProvider;
    this.httpClient = httpClientFactory.create();
  }

  private static String inputStreamToString(InputStream inputStream) throws IOException {
    final ByteArrayOutputStream result = new ByteArrayOutputStream();
    final byte[] buffer = new byte[1024];
    for (int length; (length = inputStream.read(buffer)) != -1; ) {
      result.write(buffer, 0, length);
    }
    return result.toString(StandardCharsets.UTF_8);
  }

  private SdkHttpRequest buildBasicRequest(HttpUrl url, SdkHttpMethod sdkHttpMethod) {
    try {
      Builder requestBuilder = SdkHttpRequest.builder()
          .uri(url.getEndpointUri())
          .method(sdkHttpMethod);
      url.getPath().ifPresent(requestBuilder::encodedPath);
      if (url.hasQueryParameters()) {
        requestBuilder.rawQueryParameters(url.getQueryParameters());
      }
      return requestBuilder.build();
    } catch (Exception e) {
      throw new PermanentException(
          "Could not build request for %s %s".formatted(sdkHttpMethod.name(), url.toString()),
          e
      );
    }
  }

  private SignedRequest signRequest(SdkHttpRequest request, ContentStreamProvider payload) {
    try {
      final AwsV4HttpSigner signer = AwsV4HttpSigner.create();
      final AwsCredentialsIdentity awsCredentials = credentialsProvider.resolveCredentials();

      return signer.sign(
          awsCredentialsIdentityBuilder -> {
            awsCredentialsIdentityBuilder
                .identity(awsCredentials)
                .request(request)
                .putProperty(AwsV4HttpSigner.SERVICE_SIGNING_NAME, SERVICE_NAME)
                .putProperty(AwsV4HttpSigner.REGION_NAME, regionProvider.getRegion().toString());
            if (payload != null) {
              awsCredentialsIdentityBuilder.payload(payload);
            }
          });
    } catch (Exception e) {
      throw new PermanentException("Could not sign request %s".formatted(request.getUri()), e);
    }
  }

  private HttpResponse executeRequest(SignedRequest signedRequest) {
    LOGGER.info("Executing request: {}", signedRequest.request());

    try {
      final HttpExecuteRequest httpExecuteRequest =
          HttpExecuteRequest.builder()
              .request(signedRequest.request())
              .contentStreamProvider(signedRequest.payload().orElse(null))
              .build();

      final ExecutableHttpRequest executableHttpRequest =
          httpClient.prepareRequest(httpExecuteRequest);
      final HttpExecuteResponse response = executableHttpRequest.call();
      final SdkHttpResponse sdkHttpResponse = response.httpResponse();
      LOGGER.info("Response code: {}", sdkHttpResponse.statusCode());
      String responseBody = null;
      if (response.responseBody().isPresent()) {
        responseBody = inputStreamToString(response.responseBody().get());
      }
      return new HttpResponse(sdkHttpResponse.statusCode(), responseBody);

      // exception mapping could be tweaked further / be more specific if needed
    } catch (IOException e) {
      throw new TransientResourceAccessException("Could not execute request %s %s".formatted(
          signedRequest.request().method().name(),
          signedRequest.request().getUri()), e
      );
    } catch (Exception e) {
      throw new PermanentException("Could not execute request %s %s".formatted(
          signedRequest.request().method().name(),
          signedRequest.request().getUri()), e
      );
    }
  }

  @Override
  public HttpResponse get(HttpUrl url) {
    final SdkHttpRequest request = buildBasicRequest(url, SdkHttpMethod.GET);
    final SignedRequest signedRequest = signRequest(request, null);
    return executeRequest(signedRequest);
  }

  @Override
  public HttpResponse post(HttpUrl url, String requestBody) {
    final SdkHttpRequest request = buildBasicRequest(url, SdkHttpMethod.POST);
    final SignedRequest signedRequest =
        signRequest(request, ContentStreamProvider.fromUtf8String(requestBody));
    return executeRequest(signedRequest);
  }
}
