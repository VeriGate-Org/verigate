/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.soap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
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
@EnabledIfSystemProperty(named = "integration.test.enabled", matches = "true")
class DeedsWebLiveIntegrationTest {

  private static CxfDeedsRegistryClient client;
  private static ExecutorService executor;

  @BeforeAll
  static void setUp() throws IOException {
    Properties merged = new Properties();
    loadIfPresent(merged, "/integration-test.properties");
    loadIfPresent(merged, "/integration-test-local.properties");

    DeedsWebApiConfiguration config = new DeedsWebApiConfiguration(merged);
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

  @Test
  void getOfficeRegistryList_connectsAndReturnsOffices() throws Exception {
    List<OfficeRegistry> offices = client.getOfficeRegistryList();
    assertNotNull(offices);
    assertFalse(offices.isEmpty(), "Expected at least one office to be returned by DeedsWeb");
  }

  @Test
  void findPropertiesByIdNumber_returnsResultsForKnownTestId() throws Exception {
    // 8001015009087 is a Luhn-valid example ID; the real DeedsWeb will return either a list
    // of properties or an empty list — we only assert that the round-trip succeeds.
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
