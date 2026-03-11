package verigate.billing.application.handlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import verigate.billing.domain.models.UsageSummary;
import verigate.billing.domain.services.BillingService;
import verigate.billing.domain.services.UsageTrackingService;

@ExtendWith(MockitoExtension.class)
class UsageAggregationHandlerTest {

    @Mock
    private UsageTrackingService usageTrackingService;

    @Mock
    private BillingService billingService;

    private UsageAggregationHandler handler;

    private static final YearMonth PERIOD = YearMonth.of(2025, 6);

    @BeforeEach
    void setUp() {
        handler = new UsageAggregationHandler(usageTrackingService, billingService);
    }

    @Test
    void handle_shouldProcessAllActivePartners() {
        when(billingService.getActivePartnerIds())
            .thenReturn(List.of("partner-001", "partner-002"));

        UsageSummary summary = new UsageSummary(
            "partner-001", "SANCTIONS_SCREENING", PERIOD, 5, 0, 5, BigDecimal.ZERO);

        when(usageTrackingService.aggregateUsage("partner-001", PERIOD))
            .thenReturn(List.of(summary));
        when(usageTrackingService.aggregateUsage("partner-002", PERIOD))
            .thenReturn(List.of());

        when(billingService.calculateBilling("partner-001", PERIOD))
            .thenReturn(List.of(summary));

        handler.handle(PERIOD);

        verify(usageTrackingService).aggregateUsage("partner-001", PERIOD);
        verify(usageTrackingService).aggregateUsage("partner-002", PERIOD);
        verify(billingService).calculateBilling("partner-001", PERIOD);
        // partner-002 has no summaries, so billing should not be calculated
        verify(billingService, never()).calculateBilling(eq("partner-002"), any());
    }

    @Test
    void handle_shouldDoNothingWhenNoActivePartners() {
        when(billingService.getActivePartnerIds()).thenReturn(List.of());

        handler.handle(PERIOD);

        verify(usageTrackingService, never()).aggregateUsage(anyString(), any());
        verify(billingService, never()).calculateBilling(anyString(), any());
    }

    @Test
    void handle_shouldContinueWhenOnePartnerFails() {
        when(billingService.getActivePartnerIds())
            .thenReturn(List.of("partner-001", "partner-002", "partner-003"));

        // partner-001 succeeds
        UsageSummary summary1 = new UsageSummary(
            "partner-001", "SANCTIONS_SCREENING", PERIOD, 5, 0, 5, BigDecimal.ZERO);
        when(usageTrackingService.aggregateUsage("partner-001", PERIOD))
            .thenReturn(List.of(summary1));
        when(billingService.calculateBilling("partner-001", PERIOD))
            .thenReturn(List.of(summary1));

        // partner-002 fails
        when(usageTrackingService.aggregateUsage("partner-002", PERIOD))
            .thenThrow(new RuntimeException("DynamoDB timeout"));

        // partner-003 succeeds
        UsageSummary summary3 = new UsageSummary(
            "partner-003", "CREDIT_BUREAU_CHECK", PERIOD, 3, 0, 3, BigDecimal.ZERO);
        when(usageTrackingService.aggregateUsage("partner-003", PERIOD))
            .thenReturn(List.of(summary3));
        when(billingService.calculateBilling("partner-003", PERIOD))
            .thenReturn(List.of(summary3));

        // Should not throw despite partner-002 failure
        assertDoesNotThrow(() -> handler.handle(PERIOD));

        // partner-003 should still be processed
        verify(usageTrackingService).aggregateUsage("partner-003", PERIOD);
        verify(billingService).calculateBilling("partner-003", PERIOD);
    }

    @Test
    void handle_shouldNotCalculateBillingForPartnerWithNoUsage() {
        when(billingService.getActivePartnerIds())
            .thenReturn(List.of("partner-001"));
        when(usageTrackingService.aggregateUsage("partner-001", PERIOD))
            .thenReturn(List.of());

        handler.handle(PERIOD);

        verify(billingService, never()).calculateBilling(anyString(), any());
    }

    @Test
    void handle_shouldThrowWhenGetActivePartnerIdsFails() {
        when(billingService.getActivePartnerIds())
            .thenThrow(new RuntimeException("DynamoDB connection failed"));

        assertThrows(RuntimeException.class, () -> handler.handle(PERIOD));
    }

    @Test
    void handle_shouldProcessMultipleSummariesPerPartner() {
        when(billingService.getActivePartnerIds())
            .thenReturn(List.of("partner-001"));

        List<UsageSummary> summaries = List.of(
            new UsageSummary("partner-001", "SANCTIONS_SCREENING", PERIOD, 5, 0, 5, BigDecimal.ZERO),
            new UsageSummary("partner-001", "CREDIT_BUREAU_CHECK", PERIOD, 3, 1, 4, BigDecimal.ZERO)
        );

        when(usageTrackingService.aggregateUsage("partner-001", PERIOD))
            .thenReturn(summaries);
        when(billingService.calculateBilling("partner-001", PERIOD))
            .thenReturn(summaries);

        handler.handle(PERIOD);

        verify(billingService).calculateBilling("partner-001", PERIOD);
    }
}
