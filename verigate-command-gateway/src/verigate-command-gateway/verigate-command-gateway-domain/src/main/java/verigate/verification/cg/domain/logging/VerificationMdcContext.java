/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.cg.domain.logging;

import org.slf4j.MDC;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Manages MDC (Mapped Diagnostic Context) for structured logging across
 * verification processing. Sets partnerId, verificationId, commandId,
 * and correlationId on the MDC so they appear in all log output.
 */
public final class VerificationMdcContext {

  private static final String PARTNER_ID = "partnerId";
  private static final String VERIFICATION_ID = "verificationId";
  private static final String COMMAND_ID = "commandId";
  private static final String CORRELATION_ID = "correlationId";

  private VerificationMdcContext() {
  }

  /**
   * Populates MDC from a VerifyPartyCommand.
   */
  public static void populate(VerifyPartyCommand command) {
    if (command == null) {
      return;
    }
    putIfNotNull(COMMAND_ID, command.getId() != null
        ? command.getId().toString() : null);
    putIfNotNull(PARTNER_ID, command.getPartnerId());
    if (command.getMetadata() != null) {
      putIfNotNull(VERIFICATION_ID,
          objectToString(command.getMetadata().get("verificationId")));
      putIfNotNull(CORRELATION_ID,
          objectToString(command.getMetadata().get("correlationId")));
    }
  }

  /**
   * Populates MDC with explicit values.
   */
  public static void populate(String partnerId, String verificationId,
      String commandId, String correlationId) {
    putIfNotNull(PARTNER_ID, partnerId);
    putIfNotNull(VERIFICATION_ID, verificationId);
    putIfNotNull(COMMAND_ID, commandId);
    putIfNotNull(CORRELATION_ID, correlationId);
  }

  /**
   * Clears all verification-related MDC entries.
   */
  public static void clear() {
    MDC.remove(PARTNER_ID);
    MDC.remove(VERIFICATION_ID);
    MDC.remove(COMMAND_ID);
    MDC.remove(CORRELATION_ID);
  }

  private static void putIfNotNull(String key, String value) {
    if (value != null && !value.isEmpty()) {
      MDC.put(key, value);
    }
  }

  private static String objectToString(Object value) {
    return value != null ? value.toString() : null;
  }
}
