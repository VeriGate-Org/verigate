/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.deedsweb.infrastructure.soap;

import domain.exceptions.PermanentException;
import domain.exceptions.TransientException;
import jakarta.xml.ws.soap.SOAPFaultException;
import java.util.Locale;

/**
 * Translates SOAP faults to domain exceptions per the adapter's error mapping table:
 * auth-like faults are {@link PermanentException}; other faults are treated as transient
 * (assume infra blip, let the gateway retry).
 */
public final class SoapErrorClassifier {

  private SoapErrorClassifier() {
    // utility
  }

  /** Returns true if the fault's reason text indicates an authentication failure. */
  public static boolean isAuthError(String reason) {
    if (reason == null) {
      return false;
    }
    String lower = reason.toLowerCase(Locale.ROOT);
    return lower.contains("auth")
        || lower.contains("credential")
        || lower.contains("unauthor")
        || lower.contains("login");
  }

  /** Maps a SOAP fault to a {@link TransientException} or {@link PermanentException}. */
  public static RuntimeException classifyFault(SOAPFaultException fault) {
    String reason = fault.getMessage();
    if (isAuthError(reason)) {
      return new PermanentException("DeedsWeb authentication failed: " + reason, fault);
    }
    return new TransientException("DeedsWeb SOAP fault: " + reason, fault);
  }
}
