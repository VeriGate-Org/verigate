package crosscutting.resiliency;

import domain.EventRecord;
import domain.events.BaseEvent;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of the EventIdempotentProcessor interface.
 */
public final class DefaultEventIdempotentProcessor implements EventIdempotentProcessor {

  private static final Logger logger =
      LoggerFactory.getLogger(DefaultEventIdempotentProcessor.class);

  private final Predicate isCorrectClass;

  public <EventT extends BaseEvent> DefaultEventIdempotentProcessor(
      Predicate<EventT> isCorrectClass) {
    this.isCorrectClass = isCorrectClass;
  }

  // The data read must happen before this and include all data that is required for an update to be
  // applied
  // the accept must take the data and the event and apply the update
  @Override
  public <EventT extends BaseEvent, DataT extends EventRecord> void executeIfNotProcessed(
      EventT event, DataT data, BiConsumer<EventT, DataT> eventAction) {
    if (!isCorrectClass.test(event)) {
      logger.error("Event is not of the correct class");
      return;
    }
    var currentClockReading = event.getLogicalClockReading();
    var previousClockReading = data.getLogicalClockReading();

    if (currentClockReading > previousClockReading) {
      eventAction.accept(event, data);
    }
  }
}