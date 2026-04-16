/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.soap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.soap.SOAPFaultException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import verigate.adapter.deedsweb.domain.models.DeedsWebCredentials;
import verigate.adapter.deedsweb.domain.models.OfficeRegistry;
import verigate.adapter.deedsweb.domain.models.PropertyDetails;
import verigate.adapter.deedsweb.domain.services.DeedsWebCredentialsProvider;
import verigate.adapter.deedsweb.infrastructure.soap.generated.DeedsRegistrationEnquiryService;
import verigate.adapter.deedsweb.infrastructure.soap.generated.OfficeRegistryInformationResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.PersonInformationResponse;
import verigate.adapter.deedsweb.infrastructure.soap.generated.PropertySummaryResponse;

class CxfDeedsRegistryClientTest {

  private DeedsRegistrationEnquiryService port;
  private DeedsWebCredentialsProvider credentialsProvider;
  private CachingOfficeRegistry officeCache;
  private ExecutorService executor;
  private CxfDeedsRegistryClient client;

  @BeforeEach
  void setUp() throws Exception {
    port = mock(DeedsRegistrationEnquiryService.class);
    credentialsProvider = mock(DeedsWebCredentialsProvider.class);
    when(credentialsProvider.get()).thenReturn(new DeedsWebCredentials("alice", "s3cret"));
    officeCache = new CachingOfficeRegistry(port);
    // Single thread keeps fan-out determinism without serialising calls under test.
    executor = Executors.newSingleThreadExecutor();
    client = new CxfDeedsRegistryClient(port, credentialsProvider, officeCache, executor);
  }

  @AfterEach
  void tearDown() {
    executor.shutdownNow();
  }

  // ---------------- Single-office path ----------------

  @Test
  void findPropertiesByIdNumber_singleOffice_callsPortOnce() throws Exception {
    PersonInformationResponse env = personEnv("T0001/2024");
    when(port.getPropertySummaryInformationByIDNumber(
            eq("8001015009087"), eq("T"), eq("alice"), eq("s3cret")))
        .thenReturn(List.of(env));

    List<PropertyDetails> result = client.findPropertiesByIdNumber("8001015009087", "T");

    assertEquals(1, result.size());
    assertEquals("T0001/2024", result.get(0).getDeedNumber());
    verify(port, times(1))
        .getPropertySummaryInformationByIDNumber(anyString(), anyString(), anyString(), anyString());
    // Office registry is NOT consulted in the single-office path.
    verify(port, times(0)).getOfficeRegistryList();
  }

  @Test
  void findPropertiesByIdNumber_blankOfficeCode_fansOutAcrossOffices() throws Exception {
    seedOfficeRegistry("T", "J");

    when(port.getPropertySummaryInformationByIDNumber(
            anyString(), eq("T"), anyString(), anyString()))
        .thenReturn(List.of(personEnv("T0001/2024")));
    when(port.getPropertySummaryInformationByIDNumber(
            anyString(), eq("J"), anyString(), anyString()))
        .thenReturn(List.of(personEnv("J0002/2024")));

    List<PropertyDetails> result = client.findPropertiesByIdNumber("8001015009087", "");

    assertEquals(2, result.size());
    assertTrue(result.stream().anyMatch(p -> "T0001/2024".equals(p.getDeedNumber())));
    assertTrue(result.stream().anyMatch(p -> "J0002/2024".equals(p.getDeedNumber())));
    verify(port, times(1)).getOfficeRegistryList();
  }

  @Test
  void findPropertiesByIdNumber_allKeyword_fansOut() throws Exception {
    seedOfficeRegistry("T");
    when(port.getPropertySummaryInformationByIDNumber(
            anyString(), eq("T"), anyString(), anyString()))
        .thenReturn(List.of(personEnv("T0001/2024")));

    List<PropertyDetails> result = client.findPropertiesByIdNumber("8001015009087", "all");

    assertEquals(1, result.size());
    verify(port, times(1)).getOfficeRegistryList();
  }

  // ---------------- Error paths ----------------

  @Test
  void findPropertiesByIdNumber_authFault_throwsPermanent() {
    SOAPFaultException fault = mock(SOAPFaultException.class);
    when(fault.getMessage()).thenReturn("Invalid credentials");
    when(port.getPropertySummaryInformationByIDNumber(
            anyString(), eq("T"), anyString(), anyString()))
        .thenThrow(fault);

    PermanentException ex =
        assertThrows(
            PermanentException.class,
            () -> client.findPropertiesByIdNumber("8001015009087", "T"));
    assertTrue(ex.getMessage().toLowerCase().contains("auth"));
  }

  @Test
  void findPropertiesByIdNumber_genericFault_throwsTransient() {
    SOAPFaultException fault = mock(SOAPFaultException.class);
    when(fault.getMessage()).thenReturn("Internal service error");
    when(port.getPropertySummaryInformationByIDNumber(
            anyString(), eq("T"), anyString(), anyString()))
        .thenThrow(fault);

    assertThrows(
        TransientException.class, () -> client.findPropertiesByIdNumber("8001015009087", "T"));
  }

  @Test
  void findPropertiesByIdNumber_transportError_throwsTransient() {
    when(port.getPropertySummaryInformationByIDNumber(
            anyString(), eq("T"), anyString(), anyString()))
        .thenThrow(new WebServiceException("connect timed out"));

    assertThrows(
        TransientException.class, () -> client.findPropertiesByIdNumber("8001015009087", "T"));
  }

  @Test
  void findPropertiesByIdNumber_transport401_throwsPermanent() {
    when(port.getPropertySummaryInformationByIDNumber(
            anyString(), eq("T"), anyString(), anyString()))
        .thenThrow(new WebServiceException("HTTP 401 Unauthorized"));

    assertThrows(
        PermanentException.class, () -> client.findPropertiesByIdNumber("8001015009087", "T"));
  }

  // ---------------- Fan-out behaviour under partial failures ----------------

  @Test
  void fanOut_oneOfficeFailsTransiently_otherSucceeds_returnsMergedResults() throws Exception {
    seedOfficeRegistry("T", "J");

    // J fails transiently, T succeeds — merged result keeps T's hits.
    when(port.getPropertySummaryInformationByIDNumber(
            anyString(), eq("T"), anyString(), anyString()))
        .thenReturn(List.of(personEnv("T0001/2024")));
    when(port.getPropertySummaryInformationByIDNumber(
            anyString(), eq("J"), anyString(), anyString()))
        .thenThrow(new WebServiceException("connection refused"));

    List<PropertyDetails> result = client.findPropertiesByIdNumber("8001015009087", null);

    assertEquals(1, result.size());
    assertEquals("T0001/2024", result.get(0).getDeedNumber());
  }

  @Test
  void fanOut_allOfficesFailTransiently_throwsTransient() throws Exception {
    seedOfficeRegistry("T", "J");

    when(port.getPropertySummaryInformationByIDNumber(
            anyString(), anyString(), anyString(), anyString()))
        .thenThrow(new WebServiceException("connection refused"));

    assertThrows(
        TransientException.class, () -> client.findPropertiesByIdNumber("8001015009087", null));
  }

  @Test
  void fanOut_oneOfficeFailsPermanently_propagatesPermanent() throws Exception {
    seedOfficeRegistry("T", "J");

    SOAPFaultException fault = mock(SOAPFaultException.class);
    when(fault.getMessage()).thenReturn("Authentication failed");
    when(port.getPropertySummaryInformationByIDNumber(
            anyString(), anyString(), anyString(), anyString()))
        .thenThrow(fault);

    assertThrows(
        PermanentException.class, () -> client.findPropertiesByIdNumber("8001015009087", null));
  }

  // ---------------- No-credential lookups ----------------

  @Test
  void getOfficeRegistryList_delegatesToCache() throws Exception {
    seedOfficeRegistry("T", "J");

    List<OfficeRegistry> offices = client.getOfficeRegistryList();

    assertEquals(2, offices.size());
  }

  @Test
  void getPropertyTypeList_transportError_throwsTransient() {
    when(port.getDeedsPropertyTypeList()).thenThrow(new WebServiceException("connect timed out"));

    assertThrows(TransientException.class, () -> client.getPropertyTypeList());
  }

  @Test
  void isServiceHealthy_returnsTrueOnSuccess() throws Exception {
    seedOfficeRegistry("T");
    assertTrue(client.isServiceHealthy());
  }

  @Test
  void isServiceHealthy_returnsFalseOnPermanentError() {
    SOAPFaultException fault = mock(SOAPFaultException.class);
    when(fault.getMessage()).thenReturn("Authentication failed");
    when(port.getOfficeRegistryList()).thenThrow(fault);

    assertEquals(false, client.isServiceHealthy());
  }

  // ---------------- Helpers ----------------

  private void seedOfficeRegistry(String... codes) {
    java.util.List<OfficeRegistryInformationResponse> raw = new java.util.ArrayList<>();
    for (String code : codes) {
      OfficeRegistryInformationResponse o = new OfficeRegistryInformationResponse();
      o.setOfficeCode(code);
      o.setFullDescription("Office " + code);
      raw.add(o);
    }
    when(port.getOfficeRegistryList()).thenReturn(raw);
  }

  private static PersonInformationResponse personEnv(String titleDeed) {
    PersonInformationResponse env = new PersonInformationResponse();
    PropertySummaryResponse summary = new PropertySummaryResponse();
    summary.setTitleDeedNumber(titleDeed);
    env.getPropertySummaryResponseList().add(summary);
    return env;
  }
}
