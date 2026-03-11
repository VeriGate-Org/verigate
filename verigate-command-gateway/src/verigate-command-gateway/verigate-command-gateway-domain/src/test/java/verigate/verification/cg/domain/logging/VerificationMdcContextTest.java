package verigate.verification.cg.domain.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

class VerificationMdcContextTest {

  @AfterEach
  void tearDown() {
    VerificationMdcContext.clear();
  }

  @Test
  void populateFromCommandSetsAllMdcFields() {
    var commandId = UUID.randomUUID();
    var command = createCommand(commandId, "partner-123",
        Map.of("correlationId", "corr-abc", "verificationId", "ver-456"));

    VerificationMdcContext.populate(command);

    assertEquals(commandId.toString(), MDC.get("commandId"));
    assertEquals("partner-123", MDC.get("partnerId"));
    assertEquals("corr-abc", MDC.get("correlationId"));
    assertEquals("ver-456", MDC.get("verificationId"));
  }

  @Test
  void populateFromCommandHandlesNullMetadata() {
    var command = createCommand(UUID.randomUUID(), "partner-1", null);

    VerificationMdcContext.populate(command);

    assertNull(MDC.get("correlationId"));
    assertNull(MDC.get("verificationId"));
  }

  @Test
  void clearRemovesAllMdcFields() {
    VerificationMdcContext.populate("p1", "v1", "c1", "corr1");

    VerificationMdcContext.clear();

    assertNull(MDC.get("partnerId"));
    assertNull(MDC.get("verificationId"));
    assertNull(MDC.get("commandId"));
    assertNull(MDC.get("correlationId"));
  }

  @Test
  void populateWithExplicitValues() {
    VerificationMdcContext.populate("partner-x", "ver-y", "cmd-z", "corr-w");

    assertEquals("partner-x", MDC.get("partnerId"));
    assertEquals("ver-y", MDC.get("verificationId"));
    assertEquals("cmd-z", MDC.get("commandId"));
    assertEquals("corr-w", MDC.get("correlationId"));
  }

  @Test
  void populateFromNullCommandIsNoOp() {
    VerificationMdcContext.populate(null);

    assertNull(MDC.get("commandId"));
    assertNull(MDC.get("partnerId"));
  }

  private VerifyPartyCommand createCommand(UUID id, String partnerId,
      Map<String, Object> metadata) {
    return new VerifyPartyCommand(
        id, Instant.now(), "test-user", partnerId, null, null,
        metadata != null ? metadata : Map.of());
  }
}
