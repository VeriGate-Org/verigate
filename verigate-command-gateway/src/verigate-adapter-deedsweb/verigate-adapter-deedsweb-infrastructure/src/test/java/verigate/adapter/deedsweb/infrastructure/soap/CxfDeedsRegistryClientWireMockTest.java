/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.soap;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import verigate.adapter.deedsweb.domain.models.DeedsWebCredentials;
import verigate.adapter.deedsweb.domain.models.OfficeRegistry;
import verigate.adapter.deedsweb.domain.models.PropertyDetails;
import verigate.adapter.deedsweb.domain.services.DeedsWebCredentialsProvider;
import verigate.adapter.deedsweb.infrastructure.config.DeedsWebApiConfiguration;
import verigate.adapter.deedsweb.infrastructure.soap.generated.DeedsRegistrationEnquiryService;

/**
 * Integration test that exercises the full CXF stack (proxy creation, JAXB serialization,
 * SOAP envelope parsing) against a WireMock-stubbed SOAP endpoint. This catches XML/JAXB
 * regressions that pure-Mockito tests cannot.
 */
class CxfDeedsRegistryClientWireMockTest {

  private WireMockServer wireMock;
  private DeedsRegistrationEnquiryService port;
  private CachingOfficeRegistry officeCache;
  private ExecutorService executor;
  private CxfDeedsRegistryClient client;

  @BeforeEach
  void setUp() {
    wireMock = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    wireMock.start();

    Properties props = new Properties();
    props.setProperty("deedsweb.base.url", wireMock.baseUrl() + "/deeds-registration-soap");
    props.setProperty("deedsweb.connection.timeout.ms", "5000");
    props.setProperty("deedsweb.read.timeout.ms", "5000");
    DeedsWebApiConfiguration config = new DeedsWebApiConfiguration(props);

    port = CxfPortFactory.create(config);
    DeedsWebCredentialsProvider creds = () -> new DeedsWebCredentials("alice", "s3cret");
    officeCache = new CachingOfficeRegistry(port);
    executor = Executors.newSingleThreadExecutor();
    client = new CxfDeedsRegistryClient(port, creds, officeCache, executor);
  }

  @AfterEach
  void tearDown() {
    executor.shutdownNow();
    if (wireMock != null) {
      wireMock.stop();
    }
  }

  @Test
  void getOfficeRegistryList_parsesSoapResponse() throws Exception {
    UrlPattern path = urlMatching("/deeds-registration-soap.*");
    String body =
        """
        <?xml version="1.0" encoding="UTF-8"?>
        <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
          <soap:Body>
            <ns2:getOfficeRegistryListResponse xmlns:ns2="http://enquiry.service.registration.deeds.gov.za/">
              <return>
                <fullDescription>Pretoria</fullDescription>
                <officeCode>T</officeCode>
              </return>
              <return>
                <fullDescription>Johannesburg</fullDescription>
                <officeCode>J</officeCode>
              </return>
            </ns2:getOfficeRegistryListResponse>
          </soap:Body>
        </soap:Envelope>
        """;
    wireMock.stubFor(
        post(path)
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "text/xml; charset=UTF-8")
                    .withBody(body)));

    List<OfficeRegistry> offices = client.getOfficeRegistryList();

    assertEquals(2, offices.size());
    assertEquals("T", offices.get(0).officeCode());
    assertEquals("Pretoria", offices.get(0).fullDescription());
    assertEquals("J", offices.get(1).officeCode());
    assertEquals("Johannesburg", offices.get(1).fullDescription());
  }

  @Test
  void findPropertiesByIdNumber_parsesSoapResponseAndForwardsCredentials() throws Exception {
    UrlPattern path = urlMatching("/deeds-registration-soap.*");
    String body =
        """
        <?xml version="1.0" encoding="UTF-8"?>
        <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
          <soap:Body>
            <ns2:getPropertySummaryInformationByIDNumberResponse xmlns:ns2="http://enquiry.service.registration.deeds.gov.za/">
              <return>
                <personFullDetailsResponse>
                  <fullName>Jane Doe</fullName>
                  <idNumber>8001015009087</idNumber>
                </personFullDetailsResponse>
                <propertySummaryResponseList>
                  <titleDeedNumber>T12345/2024</titleDeedNumber>
                  <registrationDate>2024-03-11</registrationDate>
                  <price>R 1,250,000</price>
                  <erf>Erf 101</erf>
                  <township>Pretoria</township>
                  <propertyTypeDescription>Residential</propertyTypeDescription>
                </propertySummaryResponseList>
              </return>
            </ns2:getPropertySummaryInformationByIDNumberResponse>
          </soap:Body>
        </soap:Envelope>
        """;
    wireMock.stubFor(
        post(path)
            // Verify credentials are forwarded in the SOAP body.
            .withRequestBody(containing("alice"))
            .withRequestBody(containing("s3cret"))
            .withRequestBody(containing("8001015009087"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "text/xml; charset=UTF-8")
                    .withBody(body)));

    List<PropertyDetails> result = client.findPropertiesByIdNumber("8001015009087", "T");

    assertEquals(1, result.size());
    PropertyDetails property = result.get(0);
    assertNotNull(property);
    assertEquals("T12345/2024", property.getDeedNumber());
    assertEquals("Jane Doe", property.getRegisteredOwnerName());
    assertEquals("8001015009087", property.getRegisteredOwnerIdNumber());
    assertTrue(property.getPropertyDescription().contains("Erf 101"));
  }
}
