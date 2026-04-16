/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.soap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import jakarta.xml.ws.soap.SOAPFaultException;
import org.junit.jupiter.api.Test;

class SoapErrorClassifierTest {

  @Test
  void isAuthError_detectsAuthKeywords() {
    assertTrue(SoapErrorClassifier.isAuthError("Authentication failed"));
    assertTrue(SoapErrorClassifier.isAuthError("Invalid credentials"));
    assertTrue(SoapErrorClassifier.isAuthError("Unauthorized access"));
    assertTrue(SoapErrorClassifier.isAuthError("Login required"));
    assertTrue(SoapErrorClassifier.isAuthError("AUTH TOKEN MISSING"));
  }

  @Test
  void isAuthError_rejectsNonAuthMessages() {
    assertFalse(SoapErrorClassifier.isAuthError(null));
    assertFalse(SoapErrorClassifier.isAuthError(""));
    assertFalse(SoapErrorClassifier.isAuthError("Timeout waiting for response"));
    assertFalse(SoapErrorClassifier.isAuthError("Server error"));
  }

  @Test
  void classifyFault_authMessage_returnsPermanentException() {
    SOAPFaultException fault = mock(SOAPFaultException.class);
    when(fault.getMessage()).thenReturn("Invalid credentials supplied");

    RuntimeException result = SoapErrorClassifier.classifyFault(fault);

    assertInstanceOf(PermanentException.class, result);
    assertTrue(result.getMessage().contains("authentication failed"));
  }

  @Test
  void classifyFault_otherMessage_returnsTransientException() {
    SOAPFaultException fault = mock(SOAPFaultException.class);
    when(fault.getMessage()).thenReturn("Internal service error");

    RuntimeException result = SoapErrorClassifier.classifyFault(fault);

    assertInstanceOf(TransientException.class, result);
    assertTrue(result.getMessage().contains("SOAP fault"));
  }
}
