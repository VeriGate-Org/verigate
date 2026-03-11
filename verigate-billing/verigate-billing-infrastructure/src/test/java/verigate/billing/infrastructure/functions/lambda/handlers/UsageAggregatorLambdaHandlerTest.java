package verigate.billing.infrastructure.functions.lambda.handlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import java.time.YearMonth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import verigate.billing.application.handlers.UsageAggregationHandler;
import verigate.billing.infrastructure.functions.lambda.di.factories.UsageAggregatorDependencyFactory;

@ExtendWith(MockitoExtension.class)
class UsageAggregatorLambdaHandlerTest {

    @Mock
    private UsageAggregatorDependencyFactory factory;

    @Mock
    private UsageAggregationHandler usageAggregationHandler;

    @Mock
    private Context context;

    private UsageAggregatorLambdaHandler handler;

    @BeforeEach
    void setUp() {
        when(factory.getUsageAggregationHandler()).thenReturn(usageAggregationHandler);
        handler = new UsageAggregatorLambdaHandler(factory);
    }

    @Test
    void handleRequest_shouldDelegateToAggregationHandler() {
        ScheduledEvent event = new ScheduledEvent();

        handler.handleRequest(event, context);

        ArgumentCaptor<YearMonth> captor = ArgumentCaptor.forClass(YearMonth.class);
        verify(usageAggregationHandler).handle(captor.capture());

        // Should use current month
        assertEquals(YearMonth.now(), captor.getValue());
    }

    @Test
    void handleRequest_shouldReturnNull() {
        ScheduledEvent event = new ScheduledEvent();

        Void result = handler.handleRequest(event, context);

        assertNull(result);
    }

    @Test
    void handleRequest_shouldHandleNullEvent() {
        // The handler checks for null event to log time, but doesn't fail
        Void result = handler.handleRequest(null, context);

        assertNull(result);
        verify(usageAggregationHandler).handle(any());
    }

    @Test
    void handleRequest_shouldPropagateHandlerException() {
        ScheduledEvent event = new ScheduledEvent();

        doThrow(new RuntimeException("Aggregation failed"))
            .when(usageAggregationHandler).handle(any());

        assertThrows(RuntimeException.class, () -> handler.handleRequest(event, context));
    }
}
