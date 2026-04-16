/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.soap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.soap.SOAPFaultException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import verigate.adapter.deedsweb.domain.models.OfficeRegistry;
import verigate.adapter.deedsweb.infrastructure.soap.generated.DeedsRegistrationEnquiryService;
import verigate.adapter.deedsweb.infrastructure.soap.generated.OfficeRegistryInformationResponse;

class CachingOfficeRegistryTest {

  private DeedsRegistrationEnquiryService port;
  private CachingOfficeRegistry cache;

  @BeforeEach
  void setUp() {
    port = mock(DeedsRegistrationEnquiryService.class);
    cache = new CachingOfficeRegistry(port);
  }

  @Test
  void getAll_firstCall_fetchesAndCachesResult() throws Exception {
    OfficeRegistryInformationResponse t = new OfficeRegistryInformationResponse();
    t.setOfficeCode("T");
    t.setFullDescription("Pretoria");
    when(port.getOfficeRegistryList()).thenReturn(List.of(t));

    List<OfficeRegistry> first = cache.getAll();
    List<OfficeRegistry> second = cache.getAll();

    assertEquals(1, first.size());
    assertEquals("T", first.get(0).officeCode());
    // Cached: only one underlying SOAP call.
    verify(port, times(1)).getOfficeRegistryList();
    // Same instance returned the second time.
    assertEquals(first, second);
  }

  @Test
  void getAll_invalidate_forcesRefetch() throws Exception {
    OfficeRegistryInformationResponse t = new OfficeRegistryInformationResponse();
    t.setOfficeCode("T");
    t.setFullDescription("Pretoria");
    when(port.getOfficeRegistryList()).thenReturn(List.of(t));

    cache.getAll();
    cache.invalidate();
    cache.getAll();

    verify(port, times(2)).getOfficeRegistryList();
  }

  @Test
  void getAll_soapFault_propagatesAsClassifiedException() {
    SOAPFaultException fault = mock(SOAPFaultException.class);
    when(fault.getMessage()).thenReturn("Invalid credentials");
    when(port.getOfficeRegistryList()).thenThrow(fault);

    PermanentException ex = assertThrows(PermanentException.class, () -> cache.getAll());
    assertInstanceOf(PermanentException.class, ex);
  }

  @Test
  void getAll_webServiceException_throwsTransient() {
    when(port.getOfficeRegistryList()).thenThrow(new WebServiceException("connect timed out"));

    TransientException ex = assertThrows(TransientException.class, () -> cache.getAll());
    assertInstanceOf(TransientException.class, ex);
  }
}
