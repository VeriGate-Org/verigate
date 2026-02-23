package infrastructure.event.handler.model;

import crosscutting.serialization.DataContract;
import java.time.Instant;
import java.util.UUID;

public final class ExtendedMockEventC extends MockEvent {

  @DataContract private final String c;

  public ExtendedMockEventC() {
    super(UUID.randomUUID(), "", Instant.now(), Instant.now(), 0, null);
    this.c = null;
  }

  public ExtendedMockEventC(String c) {
    super(UUID.randomUUID(), "", Instant.now(), Instant.now(), 0, null);
    this.c = c;
  }

  public ExtendedMockEventC(
      UUID id,
      String detailType,
      Instant noticedDate,
      Instant effectedDate,
      int causalKey,
      String c,
      Integer logicalClockReading) {
    super(id, detailType, noticedDate, effectedDate, causalKey, logicalClockReading);
    this.c = c;
  }

  public String getC() {
    return c;
  }
}
