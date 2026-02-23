package verigate.verification.cg.domain.factories;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import domain.events.EventIdFactory;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;
import verigate.verification.cg.domain.events.VerificationEvent;
import verigate.verification.cg.domain.events.VerificationHardFailEvent;
import verigate.verification.cg.domain.events.VerificationSoftFailEvent;
import verigate.verification.cg.domain.events.VerificationSucceededEvent;
import verigate.verification.cg.domain.factories.VerificationEventFactory;
import verigate.verification.cg.domain.models.VerificationOutcome;
import verigate.verification.cg.domain.models.VerificationResult;

class VerificationEventFactoryTest {

  private VerificationEventFactory factory;
  private EventIdFactory eventIdFactory;

  @BeforeEach
  void setUp() {
    eventIdFactory = mock(EventIdFactory.class);
    factory = new VerificationEventFactory(eventIdFactory);
    when(eventIdFactory.createFromCommand(any(), any()))
        .thenReturn(UUID.fromString("fa2cf2ea-76a9-4fd2-9b56-64c5a8cbb9b7"));
  }

  @Test
  void testCreateEvent_Succeeded() {
    VerifyPartyCommand command = new VerifyPartyCommand(null, null, null, null, null, null);

    VerificationResult result = new VerificationResult(VerificationOutcome.SUCCEEDED, null);

    VerificationEvent event = factory.createEvent(VerificationOutcome.SUCCEEDED, command, result);

    assertTrue(event instanceof VerificationSucceededEvent);
    assertEquals(UUID.fromString("fa2cf2ea-76a9-4fd2-9b56-64c5a8cbb9b7"), event.getId());
  }

  @Test
  void testCreateEvent_SoftFail() {
    VerifyPartyCommand command =
        new VerifyPartyCommand(
            UUID.fromString("fa2cf2ea-76a9-4fd2-9b56-64c5a8cbb9b7"), null, null, null, null, null);
    VerificationResult result = new VerificationResult(VerificationOutcome.SOFT_FAIL, null);

    VerificationEvent event = factory.createEvent(VerificationOutcome.SOFT_FAIL, command, result);

    assertTrue(event instanceof VerificationSoftFailEvent);
    assertEquals(UUID.fromString("fa2cf2ea-76a9-4fd2-9b56-64c5a8cbb9b7"), event.getId());
  }

  @Test
  void testCreateEvent_HardFail() {
    VerifyPartyCommand command =
        new VerifyPartyCommand(
            UUID.fromString("fa2cf2ea-76a9-4fd2-9b56-64c5a8cbb9b7"), null, null, null, null, null);
    VerificationResult result = new VerificationResult(VerificationOutcome.HARD_FAIL, null);

    VerificationEvent event = factory.createEvent(VerificationOutcome.HARD_FAIL, command, result);

    assertTrue(event instanceof VerificationHardFailEvent);
    assertEquals(UUID.fromString("fa2cf2ea-76a9-4fd2-9b56-64c5a8cbb9b7"), event.getId());
  }

  @Test
  void testCreateEvent_UnknownOutcome() {
    VerifyPartyCommand command =
        new VerifyPartyCommand(
            UUID.fromString("fa2cf2ea-76a9-4fd2-9b56-64c5a8cbb9b7"), null, null, null, null, null);
    VerificationResult result = new VerificationResult(null, null);

    assertThrows(NullPointerException.class, () -> factory.createEvent(null, command, result));
  }
}
