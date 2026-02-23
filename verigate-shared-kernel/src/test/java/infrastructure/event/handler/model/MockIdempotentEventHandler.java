/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.event.handler.model;

import application.handlers.EventHandler;
import crosscutting.resiliency.DefaultEventIdempotentProcessor;
import domain.MockEventRecord;

public final class MockIdempotentEventHandler implements EventHandler<MockEvent> {

  private int callCount = 0;
  private DefaultEventIdempotentProcessor idempotentProcessor;

  public MockIdempotentEventHandler() {
    this.idempotentProcessor =
        new DefaultEventIdempotentProcessor(
            (e) -> {
              if (e instanceof MockEvent) {
                return true;
              }
              return false;
            });
  }

  @Override
  public void handle(MockEvent event) {
    idempotentProcessor.executeIfNotProcessed(
        event,
        // This is a mock object that would be read from the database
        // in this mock it is a copy of the inbound event, indicating that the event was processed
        // previously
        new MockEventRecord(event.getId().toString(), event.getLogicalClockReading(), 0),
        (e, d) -> {
          callCount++;
        });
  }

  public int getCallCount() {
    return callCount;
  }
}
