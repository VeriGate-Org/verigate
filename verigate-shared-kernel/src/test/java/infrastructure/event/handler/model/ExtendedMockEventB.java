package infrastructure.event.handler.model;

import crosscutting.serialization.DataContract;
import java.time.Instant;
import java.util.UUID;

public final class ExtendedMockEventB extends MockEvent {

  @DataContract private final String b;

  public ExtendedMockEventB() {
    super(UUID.randomUUID(), "", Instant.now(), Instant.now(), 0, null);
    this.b = null;
  }

  public ExtendedMockEventB(String b) {
    super(UUID.randomUUID(), "", Instant.now(), Instant.now(), 0, null);
    this.b = b;
  }

  public ExtendedMockEventB(
      UUID id,
      String detailType,
      Instant noticedDate,
      Instant effectedDate,
      int causalKey,
      String b,
      Integer logicalClockReading) {
    super(id, detailType, noticedDate, effectedDate, causalKey, logicalClockReading);
    this.b = b;
  }

  public String getB() {
    return b;
  }
}
