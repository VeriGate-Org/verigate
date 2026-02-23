package infrastructure.event.handler.model;

import crosscutting.serialization.DataContract;
import java.time.Instant;
import java.util.UUID;

public final class ExtendedMockEventA extends MockEvent {

  @DataContract private final String a;

  public ExtendedMockEventA() {
    super(UUID.randomUUID(), "", Instant.now(), Instant.now(), 0, null);
    this.a = null;
  }

  public ExtendedMockEventA(String a) {
    super(UUID.randomUUID(), "", Instant.now(), Instant.now(), 0, null);
    this.a = a;
  }

  public ExtendedMockEventA(
      UUID id,
      String detailType,
      Instant noticedDate,
      Instant effectedDate,
      int causalKey,
      String a,
      Integer logicalClockReading) {
    super(id, detailType, noticedDate, effectedDate, causalKey, logicalClockReading);
    this.a = a;
  }

  public String getA() {
    return a;
  }
}
