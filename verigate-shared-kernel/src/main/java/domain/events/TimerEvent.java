/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.events;

import java.time.Instant;
import java.util.UUID;

/**
 * An event that is triggered by a timer.
 * Can be used for a variety of purposes.
 */
public final class TimerEvent extends BaseEvent<Void> {

  /**
   * Creates a new instance of the {@link TimerEvent} class, with fields set to null.
   * Useful tooling that requires a parameterless constructor.
   */
  public TimerEvent() {
    super();
  }

  /**
   * Creates a new instance of the {@link TimerEvent} class.
   *
   * @param id The unique identifier of the event.
   * @param detailType The type detail of the event.
   * @param noticedDate The date when the event was noticed. A defensive copy is made.
   * @param effectedDate The date when the event takes effect. A defensive copy is made.
   */
  public TimerEvent(
      UUID id,
      String detailType,
      Instant noticedDate,
      Instant effectedDate,
      Integer logicalClockReading) {

    super(id, detailType, noticedDate, effectedDate, logicalClockReading);
  }
}
