/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.event.handler.kinesis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent.KinesisEventRecord;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent.Record;
import domain.events.BaseEvent;
import domain.exceptions.HandlerDelayException;
import infrastructure.event.handler.InfrastructureEventHandler;
import infrastructure.event.handler.factories.InfrastructureEventHandlerFactory;
import infrastructure.functions.lambda.serializers.internal.DefaultInternalTransportJsonSerializer;
import infrastructure.functions.lambda.serializers.internal.InternalTransportJsonSerializer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResilientKinesisHandlerTest {

  private InternalTransportJsonSerializer serializer;
  private TestInfrastructureEventHandler infrastructureEventHandler;
  private ResilientKinesisHandler<TestEvent> resilientKinesisHandler;

  @BeforeEach
  public void setup() {
    serializer = new DefaultInternalTransportJsonSerializer();
    final InfrastructureEventHandlerFactory<KinesisEventRecord, TestEvent> eventHandlerFactory =
        mock(InfrastructureEventHandlerFactory.class);
    infrastructureEventHandler = new TestInfrastructureEventHandler();
    when(eventHandlerFactory.create()).thenReturn(infrastructureEventHandler);
    resilientKinesisHandler = new TestResilientKinesisHandler(
        serializer,
        eventHandlerFactory,
        TestEvent.class
    );
  }

  @Test
  public void twoEventsSuccess() {
    final TestEvent event1 = new TestEvent(UUID.randomUUID());
    final KinesisEventRecord kinesisEventRecord1 = createTestKinesisEventRecord(serializer, event1);

    final TestEvent event2 = new TestEvent(UUID.randomUUID());
    final KinesisEventRecord kinesisEventRecord2 = createTestKinesisEventRecord(serializer, event2);

    final KinesisEvent kinesisEvent = new KinesisEvent();
    kinesisEvent.setRecords(List.of(kinesisEventRecord1, kinesisEventRecord2));

    resilientKinesisHandler.handleRequest(kinesisEvent, null);

    assertEquals(List.of(event1.getId(), event2.getId()), infrastructureEventHandler.getEventIdsHandled());
  }

  @Test
  public void nullRecords() {
    final KinesisEvent kinesisEvent = new KinesisEvent();
    resilientKinesisHandler.handleRequest(kinesisEvent, null);
  }

  private static KinesisEventRecord createTestKinesisEventRecord(InternalTransportJsonSerializer serializer, TestEvent testEvent) {
    final KinesisEventRecord kinesisEventRecord = new KinesisEventRecord();
    final Record kinesisRecord = new Record();
    kinesisRecord.setData(ByteBuffer.wrap(serializer.serialize(testEvent).getBytes(StandardCharsets.UTF_8)));
    kinesisEventRecord.setKinesis(kinesisRecord);
    return kinesisEventRecord;
  }

  private static class TestInfrastructureEventHandler implements
      InfrastructureEventHandler<KinesisEventRecord, TestEvent> {
    private final List<UUID> eventIdsHandled = new ArrayList<>();
    @Override
    public void handle(KinesisEventRecord message,
        Function<KinesisEventRecord, TestEvent> extractEventFromRawMessage) throws HandlerDelayException {
      final TestEvent testEvent = extractEventFromRawMessage.apply(message);
      eventIdsHandled.add(testEvent.getId());
    }

    public List<UUID> getEventIdsHandled() {
      return List.copyOf(eventIdsHandled);
    }
  }

  private static class TestResilientKinesisHandler extends ResilientKinesisHandler<TestEvent> {
    protected TestResilientKinesisHandler(InternalTransportJsonSerializer serializer,
        InfrastructureEventHandlerFactory<KinesisEventRecord, TestEvent> eventHandlerFactory,
        Class<TestEvent> clazz) {
      super(serializer, eventHandlerFactory, clazz);
    }
  }

  private static class TestEvent extends BaseEvent<String> {
    public TestEvent() {
    }

    public TestEvent(UUID id) {
      super(id, null, null, null, null);
    }
  }
}