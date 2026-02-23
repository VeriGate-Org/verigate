package crosscutting.resiliency;

import domain.EventRecord;
import domain.events.BaseEvent;
import java.util.function.BiConsumer;

/**
 * Interface for processing events in an idempotent manner.
 */
@FunctionalInterface
public interface EventIdempotentProcessor {
  <EventT extends BaseEvent, DataT extends EventRecord> void executeIfNotProcessed(
      EventT event, DataT data, BiConsumer<EventT, DataT> eventAction);
}