package crosscutting.resiliency;

import domain.FlagStatus;
import domain.MockEventRecord;
import infrastructure.event.handler.model.MockEvent;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;

public class DefaultEventIdempotentProcessorTest {

  String GUID = UUID.randomUUID().toString();
  int LOGICAL_CLOCK_READING = 0;
  int VERSION = 0;
  BiConsumer<MockEvent, MockEventRecord> EVENT_ACTION =
      (e, d) -> {
        d.setVersion(d.getVersion() + 1);
        d.setDirtyFlag(FlagStatus.DIRTY);
      };

  @Test
  void duplicateEventIsNotProcessed() {
    Predicate isCorrectClass =
        (e) -> {
          return true;
        };

    MockEventRecord eventRecord = new MockEventRecord(GUID, LOGICAL_CLOCK_READING, VERSION);
    MockEvent duplicatedEvent =
        new MockEvent(UUID.fromString(GUID), "", null, null, 0, LOGICAL_CLOCK_READING);

    DefaultEventIdempotentProcessor processor = new DefaultEventIdempotentProcessor(isCorrectClass);
    processor.executeIfNotProcessed(duplicatedEvent, eventRecord, EVENT_ACTION);

    // confirm that the event was not processed by confirming that the eventRecord version has not
    // changed and that the flag is still clean
    assert (eventRecord.getDirtyFlag() == FlagStatus.CLEAN);
    assert (eventRecord.getVersion() == VERSION);
  }

  @Test
  void uniqueEventIsProcessed() {
    Predicate isCorrectClass =
        (e) -> {
          return true;
        };

    MockEventRecord eventRecord = new MockEventRecord(GUID, LOGICAL_CLOCK_READING, VERSION);
    MockEvent uniqueEvent =
        new MockEvent(UUID.fromString(GUID), "", null, null, 0, LOGICAL_CLOCK_READING + 1);

    DefaultEventIdempotentProcessor processor = new DefaultEventIdempotentProcessor(isCorrectClass);
    processor.executeIfNotProcessed(uniqueEvent, eventRecord, EVENT_ACTION);

    // confirm that the event was processed by confirming that the eventRecord version has
    // changed and that the flag is now dirty
    assert (eventRecord.getDirtyFlag() == FlagStatus.DIRTY);
    assert (eventRecord.getVersion() == VERSION + 1);
  }

  @Test
  void incorrectEventIsNotProcessed() {
    Predicate isCorrectClass =
        (e) -> {
          return false;
        };

    MockEventRecord eventRecord = new MockEventRecord(GUID, LOGICAL_CLOCK_READING, VERSION);
    MockEvent uniqueEvent =
        new MockEvent(UUID.fromString(GUID), "", null, null, 0, LOGICAL_CLOCK_READING + 1);

    DefaultEventIdempotentProcessor processor = new DefaultEventIdempotentProcessor(isCorrectClass);
    processor.executeIfNotProcessed(uniqueEvent, eventRecord, EVENT_ACTION);

    // confirm that the event was not processed by confirming that the eventRecord version has not
    // changed and that the flag is still clean
    assert (eventRecord.getDirtyFlag() == FlagStatus.CLEAN);
    assert (eventRecord.getVersion() == VERSION);
  }
}
