package verigate.billing.infrastructure.functions.lambda.handlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import verigate.billing.application.handlers.UsageEventHandler;
import verigate.billing.infrastructure.functions.lambda.di.factories.UsageEventDependencyFactory;

@ExtendWith(MockitoExtension.class)
class UsageEventConsumerLambdaHandlerTest {

    @Mock
    private UsageEventDependencyFactory factory;

    @Mock
    private UsageEventHandler usageEventHandler;

    @Mock
    private Context context;

    private UsageEventConsumerLambdaHandler handler;

    @BeforeEach
    void setUp() {
        when(factory.getUsageEventHandler()).thenReturn(usageEventHandler);
        handler = new UsageEventConsumerLambdaHandler(factory);
    }

    @Test
    void handleRequest_shouldProcessAllRecords() {
        String json1 = """
            {"verificationId":"ver-001","partnerId":"p1","verificationType":"TYPE","outcome":"SUCCESS"}""";
        String json2 = """
            {"verificationId":"ver-002","partnerId":"p2","verificationType":"TYPE","outcome":"FAILURE"}""";

        KinesisEvent event = createKinesisEvent(List.of(json1, json2));

        handler.handleRequest(event, context);

        verify(usageEventHandler, times(2)).handle(anyString());
    }

    @Test
    void handleRequest_shouldReturnNullForEmptyEvent() {
        KinesisEvent event = new KinesisEvent();
        event.setRecords(List.of());

        Void result = handler.handleRequest(event, context);

        assertNull(result);
        verify(usageEventHandler, never()).handle(anyString());
    }

    @Test
    void handleRequest_shouldReturnNullForNullEvent() {
        Void result = handler.handleRequest(null, context);

        assertNull(result);
        verify(usageEventHandler, never()).handle(anyString());
    }

    @Test
    void handleRequest_shouldReturnNullForNullRecords() {
        KinesisEvent event = new KinesisEvent();
        event.setRecords(null);

        Void result = handler.handleRequest(event, context);

        assertNull(result);
    }

    @Test
    void handleRequest_shouldContinueAfterSingleRecordFailure() {
        String json1 = "invalid-json";
        String json2 = """
            {"verificationId":"ver-002","partnerId":"p2","verificationType":"TYPE","outcome":"SUCCESS"}""";

        doThrow(new RuntimeException("Parse failure")).when(usageEventHandler).handle(eq("invalid-json"));

        KinesisEvent event = createKinesisEvent(List.of(json1, json2));

        // Should not throw - partial failures are tolerated
        handler.handleRequest(event, context);

        verify(usageEventHandler, times(2)).handle(anyString());
    }

    @Test
    void handleRequest_shouldThrowWhenAllRecordsFail() {
        String json1 = "bad1";
        String json2 = "bad2";

        doThrow(new RuntimeException("Failure")).when(usageEventHandler).handle(anyString());

        KinesisEvent event = createKinesisEvent(List.of(json1, json2));

        assertThrows(RuntimeException.class, () -> handler.handleRequest(event, context));
    }

    @Test
    void handleRequest_shouldExtractDataFromKinesisRecord() {
        String json = """
            {"verificationId":"ver-001","partnerId":"p1","verificationType":"SANCTIONS_SCREENING","outcome":"SUCCESS"}""";

        KinesisEvent event = createKinesisEvent(List.of(json));

        handler.handleRequest(event, context);

        verify(usageEventHandler).handle(json);
    }

    private KinesisEvent createKinesisEvent(List<String> payloads) {
        KinesisEvent event = new KinesisEvent();
        List<KinesisEvent.KinesisEventRecord> records = new ArrayList<>();

        int seq = 1;
        for (String payload : payloads) {
            KinesisEvent.KinesisEventRecord record = new KinesisEvent.KinesisEventRecord();
            KinesisEvent.Record kinesis = new KinesisEvent.Record();
            kinesis.setData(ByteBuffer.wrap(payload.getBytes(StandardCharsets.UTF_8)));
            kinesis.setSequenceNumber(String.valueOf(seq++));
            kinesis.setPartitionKey("partition-key");
            record.setKinesis(kinesis);
            records.add(record);
        }

        event.setRecords(records);
        return event;
    }
}
