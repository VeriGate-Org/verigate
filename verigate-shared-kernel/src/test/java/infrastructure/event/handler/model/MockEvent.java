package infrastructure.event.handler.model;

import domain.events.BaseEvent;
import java.time.Instant;
import java.util.UUID;

public class MockEvent extends BaseEvent<String> {

  private int causalKey;

  public MockEvent() {
    super(UUID.randomUUID(), "", Instant.now(), Instant.now(), null);
  }

  public MockEvent(
      UUID id,
      String detailType,
      Instant noticedDate,
      Instant effectedDate,
      int causalKey,
      Integer logicalClockReading) {
    super(id, detailType, noticedDate, effectedDate, logicalClockReading);
    this.causalKey = causalKey;
  }

  public UUID getId() {
    return this.id;
  }

  public Integer getLogicalClockReading() {
    return this.logicalClockReading;
  }

  public int getCausalKey() {
    return this.causalKey;
  }
}
