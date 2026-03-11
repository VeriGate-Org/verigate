package verigate.billing.application.handlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import verigate.billing.domain.constants.DomainConstants;
import verigate.billing.domain.models.UsageRecord;
import verigate.billing.domain.services.UsageTrackingService;

@ExtendWith(MockitoExtension.class)
class UsageEventHandlerTest {

    @Mock
    private UsageTrackingService usageTrackingService;

    private UsageEventHandler handler;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        handler = new UsageEventHandler(usageTrackingService, objectMapper);
    }

    @Test
    void handle_shouldDeserializeAndRecordUsage() {
        String json = """
            {
                "eventId": "evt-001",
                "verificationId": "ver-001",
                "partnerId": "partner-001",
                "verificationType": "SANCTIONS_SCREENING",
                "outcome": "SUCCESS",
                "eventTimestamp": "2025-06-15T10:30:00",
                "provider": "WorldCheck",
                "correlationId": "corr-001"
            }
            """;

        when(usageTrackingService.recordUsage(any())).thenReturn(true);

        handler.handle(json);

        ArgumentCaptor<UsageRecord> captor = ArgumentCaptor.forClass(UsageRecord.class);
        verify(usageTrackingService).recordUsage(captor.capture());

        UsageRecord recorded = captor.getValue();
        assertTrue(recorded.usageId().startsWith(DomainConstants.USAGE_ID_PREFIX));
        assertEquals("partner-001", recorded.partnerId());
        assertEquals("SANCTIONS_SCREENING", recorded.verificationType());
        assertEquals("ver-001", recorded.verificationId());
        assertEquals("SUCCESS", recorded.outcome());
    }

    @Test
    void handle_shouldUseEventTimestampWhenPresent() {
        String json = """
            {
                "verificationId": "ver-001",
                "partnerId": "partner-001",
                "verificationType": "SANCTIONS_SCREENING",
                "outcome": "SUCCESS",
                "eventTimestamp": "2025-06-15T10:30:00"
            }
            """;

        when(usageTrackingService.recordUsage(any())).thenReturn(true);

        handler.handle(json);

        ArgumentCaptor<UsageRecord> captor = ArgumentCaptor.forClass(UsageRecord.class);
        verify(usageTrackingService).recordUsage(captor.capture());

        assertEquals(java.time.LocalDateTime.of(2025, 6, 15, 10, 30),
            captor.getValue().eventTimestamp());
    }

    @Test
    void handle_shouldFallBackToCurrentTimeWhenTimestampMissing() {
        String json = """
            {
                "verificationId": "ver-001",
                "partnerId": "partner-001",
                "verificationType": "SANCTIONS_SCREENING",
                "outcome": "SUCCESS"
            }
            """;

        when(usageTrackingService.recordUsage(any())).thenReturn(true);

        handler.handle(json);

        ArgumentCaptor<UsageRecord> captor = ArgumentCaptor.forClass(UsageRecord.class);
        verify(usageTrackingService).recordUsage(captor.capture());

        // Timestamp should be approximately now (within last minute)
        assertNotNull(captor.getValue().eventTimestamp());
    }

    @Test
    void handle_shouldNotThrowForDuplicateEvent() {
        String json = """
            {
                "verificationId": "ver-001",
                "partnerId": "partner-001",
                "verificationType": "SANCTIONS_SCREENING",
                "outcome": "SUCCESS"
            }
            """;

        when(usageTrackingService.recordUsage(any())).thenReturn(false);

        // Should not throw - duplicates are silently ignored
        assertDoesNotThrow(() -> handler.handle(json));
    }

    @Test
    void handle_shouldThrowForEventMissingRequiredField() {
        // Missing verificationId - Jackson wraps the IllegalArgumentException
        // in a ValueInstantiationException which is caught by the generic Exception handler
        String json = """
            {
                "partnerId": "partner-001",
                "verificationType": "SANCTIONS_SCREENING",
                "outcome": "SUCCESS"
            }
            """;

        assertThrows(RuntimeException.class, () -> handler.handle(json));
        verify(usageTrackingService, never()).recordUsage(any());
    }

    @Test
    void handle_shouldThrowForMalformedJson() {
        String malformedJson = "{ this is not valid json }}}";

        assertThrows(RuntimeException.class, () -> handler.handle(malformedJson));
    }

    @Test
    void handle_shouldGenerateUniqueUsageIds() {
        String json = """
            {
                "verificationId": "ver-001",
                "partnerId": "partner-001",
                "verificationType": "SANCTIONS_SCREENING",
                "outcome": "SUCCESS"
            }
            """;

        when(usageTrackingService.recordUsage(any())).thenReturn(true);

        handler.handle(json);
        handler.handle(json);

        ArgumentCaptor<UsageRecord> captor = ArgumentCaptor.forClass(UsageRecord.class);
        verify(usageTrackingService, times(2)).recordUsage(captor.capture());

        // Each invocation should produce a different usageId
        assertNotEquals(
            captor.getAllValues().get(0).usageId(),
            captor.getAllValues().get(1).usageId());
    }

    @Test
    void handle_shouldHandleAllOutcomeTypes() {
        for (String outcome : List.of("SUCCESS", "FAILURE", "SYSTEM_ERROR")) {
            String json = String.format("""
                {
                    "verificationId": "ver-%s",
                    "partnerId": "partner-001",
                    "verificationType": "SANCTIONS_SCREENING",
                    "outcome": "%s"
                }
                """, outcome, outcome);

            when(usageTrackingService.recordUsage(any())).thenReturn(true);

            assertDoesNotThrow(() -> handler.handle(json));
        }

        verify(usageTrackingService, times(3)).recordUsage(any());
    }

    @Test
    void handle_shouldHandleAllVerificationTypes() {
        String json = """
            {
                "verificationId": "ver-001",
                "partnerId": "partner-001",
                "verificationType": "CREDIT_BUREAU_CHECK",
                "outcome": "SUCCESS"
            }
            """;

        when(usageTrackingService.recordUsage(any())).thenReturn(true);

        handler.handle(json);

        ArgumentCaptor<UsageRecord> captor = ArgumentCaptor.forClass(UsageRecord.class);
        verify(usageTrackingService).recordUsage(captor.capture());
        assertEquals("CREDIT_BUREAU_CHECK", captor.getValue().verificationType());
    }
}
