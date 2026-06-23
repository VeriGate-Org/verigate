/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.soap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import domain.exceptions.TransientException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import verigate.adapter.deedsweb.domain.models.DeedsWebCredentials;
import verigate.adapter.deedsweb.domain.models.OfficeRegistry;
import verigate.adapter.deedsweb.domain.models.PropertyDetails;
import verigate.adapter.deedsweb.domain.services.DeedsWebCredentialsProvider;
import verigate.adapter.deedsweb.infrastructure.config.DeedsWebApiConfiguration;
import verigate.adapter.deedsweb.infrastructure.soap.generated.DeedsRegistrationEnquiryService;

/**
 * Live integration test against the real DeedsWeb SOAP service. Skipped by default
 * unless {@code -Dintegration.test.enabled=true} is supplied. Credentials are read
 * from {@code src/test/resources/integration-test-local.properties}, which is
 * gitignored.
 *
 * <p>Note: the host running this test must be inside the VPC (or otherwise have
 * its source IP whitelisted by the Deeds Office, currently NAT EIP
 * {@code 13.246.247.144}).</p>
 */
/**
 * Live integration test against the real DeedsWeb SOAP service. Skipped by default
 * unless {@code -Dintegration.test.enabled=true} is supplied. Credentials are read
 * from {@code src/test/resources/integration-test-local.properties}, which is
 * gitignored.
 *
 * <p>Note: the host running this test must be inside the VPC (or otherwise have
 * its source IP whitelisted by the Deeds Office, currently NAT EIP
 * {@code 13.246.247.144}).</p>
 *
 * <p><b>Known infrastructure state (2026-06-23):</b> The BigIP load balancer in front
 * of DeedsWeb is returning HTTP 302 for all SOAP operation-specific POST requests,
 * redirecting them to the base service path ({@code /deeds-registration-soap/}).
 * The backend CXF server uses URL-based dispatch so the base path returns the
 * HTML service-listing page instead of processing the SOAP envelope.
 * Tests that exercise SOAP operations will throw {@link TransientException} until
 * the Deeds Office reconfigures BigIP to pass operation-specific POSTs through.</p>
 */
@EnabledIfSystemProperty(named = "integration.test.enabled", matches = "true")
class DeedsWebLiveIntegrationTest {

  private static CxfDeedsRegistryClient client;
  private static ExecutorService executor;
  private static Properties merged;

  @BeforeAll
  static void setUp() throws IOException {
    merged = new Properties();
    loadIfPresent(merged, "/integration-test.properties");
    loadIfPresent(merged, "/integration-test-local.properties");

    DeedsWebApiConfiguration config = new DeedsWebApiConfiguration(merged);
    // CxfPortFactory.create() triggers the static initializer that relaxes the
    // jdk.tls.disabledAlgorithms DHE minimum from 2048 → 1024 bits so the TLS
    // handshake with deedssoap.deeds.gov.za:443 succeeds.
    DeedsRegistrationEnquiryService port = CxfPortFactory.create(config);

    String username = merged.getProperty("deedsweb.username");
    String password = merged.getProperty("deedsweb.password");
    DeedsWebCredentialsProvider provider =
        () -> new DeedsWebCredentials(username, password);

    CachingOfficeRegistry cache = new CachingOfficeRegistry(port);
    executor = Executors.newFixedThreadPool(4);
    client = new CxfDeedsRegistryClient(port, provider, cache, executor);
  }

  @AfterAll
  static void tearDown() {
    if (executor != null) {
      executor.shutdownNow();
    }
  }

  /**
   * Verifies that TLS handshake to deedssoap.deeds.gov.za:443 succeeds after the
   * DHE constraint relaxation applied by {@link CxfPortFactory}. A 200 or any valid
   * HTTP response code confirms the TLS layer works; we are not testing SOAP routing here.
   */
  @Test
  void httpsEndpoint_tlsHandshakeSucceeds() throws Exception {
    String baseUrl = merged.getProperty("deedsweb.base.url",
        "https://deedssoap.deeds.gov.za:443/deeds-registration-soap/");
    HttpClient http = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(15))
        .build();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl))
        .GET()
        .timeout(Duration.ofSeconds(15))
        .build();
    HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
    // Any HTTP response (HTML service listing, 200, 302, etc.) proves TLS succeeded.
    assertTrue(response.statusCode() > 0,
        "Expected a valid HTTP response, confirming TLS handshake success; got: " + response.statusCode());
  }

  /**
   * Documents the current BigIP redirect behaviour. All SOAP operation POSTs return
   * HTTP 302 which our client surfaces as {@link TransientException} so the gateway
   * retries rather than hard-failing.
   *
   * <p>When the Deeds Office fixes BigIP, replace this assertion with the normal
   * office-list assertion from {@link #getOfficeRegistryList_connectsAndReturnsOffices}.</p>
   */
  @Test
  void getOfficeRegistryList_bigIpRedirect_throwsTransient() {
    TransientException ex = assertThrows(
        TransientException.class,
        () -> client.getOfficeRegistryList(),
        "Expected TransientException due to BigIP HTTP 302 on operation URL");
    assertTrue(
        ex.getMessage().toLowerCase().contains("302")
            || ex.getMessage().toLowerCase().contains("redirect")
            || ex.getMessage().toLowerCase().contains("transport"),
        "Exception message should reference the redirect or transport issue: " + ex.getMessage());
  }

  /**
   * Happy-path test — will pass once the Deeds Office reconfigures BigIP to route
   * SOAP operation-specific POSTs to the backend without redirecting.
   */
  @Test
  void getOfficeRegistryList_connectsAndReturnsOffices() throws Exception {
    List<OfficeRegistry> offices = client.getOfficeRegistryList();
    assertNotNull(offices);
    assertFalse(offices.isEmpty(), "Expected at least one office to be returned by DeedsWeb");
  }

  /**
   * Happy-path test — will pass once BigIP routing is corrected. The ID 8001015009087
   * is Luhn-valid; DeedsWeb returns either properties or an empty list, not an error.
   */
  @Test
  void findPropertiesByIdNumber_returnsResultsForKnownTestId() throws Exception {
    List<PropertyDetails> result = client.findPropertiesByIdNumber("8001015009087", "T");
    assertNotNull(result);
  }

  private static void loadIfPresent(Properties target, String resource) throws IOException {
    try (InputStream in = DeedsWebLiveIntegrationTest.class.getResourceAsStream(resource)) {
      if (in != null) {
        target.load(in);
      }
    }
  }
}
