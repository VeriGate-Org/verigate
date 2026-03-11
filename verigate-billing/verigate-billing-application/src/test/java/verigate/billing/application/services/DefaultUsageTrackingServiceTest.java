package verigate.billing.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import verigate.billing.domain.models.UsageRecord;
import verigate.billing.domain.models.UsageSummary;

@ExtendWith(MockitoExtension.class)
class DefaultUsageTrackingServiceTest {

    @Mock
    private DefaultUsageTrackingService.UsageRecordRepository usageRecordRepository;

    @Mock
    private DefaultUsageTrackingService.UsageSummaryRepository usageSummaryRepository;

    private DefaultUsageTrackingService service;

    @BeforeEach
    void setUp() {
        service = new DefaultUsageTrackingService(usageRecordRepository, usageSummaryRepository);
    }

    // --- recordUsage ---

    @Test
    void recordUsage_shouldDelegateToRepository() {
        UsageRecord record = createRecord("USG-1", "partner-001", "SANCTIONS_SCREENING",
            "ver-001", "SUCCESS", LocalDateTime.of(2025, 6, 15, 10, 0));
        when(usageRecordRepository.save(record)).thenReturn(true);

        boolean result = service.recordUsage(record);

        assertTrue(result);
        verify(usageRecordRepository).save(record);
    }

    @Test
    void recordUsage_shouldReturnFalseForDuplicate() {
        UsageRecord record = createRecord("USG-1", "partner-001", "SANCTIONS_SCREENING",
            "ver-001", "SUCCESS", LocalDateTime.of(2025, 6, 15, 10, 0));
        when(usageRecordRepository.save(record)).thenReturn(false);

        boolean result = service.recordUsage(record);

        assertFalse(result);
    }

    // --- getUsageRecords ---

    @Test
    void getUsageRecords_shouldQueryWithDateTimeRange() {
        LocalDate from = LocalDate.of(2025, 6, 1);
        LocalDate to = LocalDate.of(2025, 6, 30);

        UsageRecord record = createRecord("USG-1", "partner-001", "SANCTIONS_SCREENING",
            "ver-001", "SUCCESS", LocalDateTime.of(2025, 6, 15, 10, 0));
        when(usageRecordRepository.findByPartnerIdAndDateRange(
            eq("partner-001"), any(), any())).thenReturn(List.of(record));

        List<UsageRecord> result = service.getUsageRecords("partner-001", from, to);

        assertEquals(1, result.size());
        assertEquals(record, result.get(0));

        // Verify correct datetime conversion
        ArgumentCaptor<LocalDateTime> fromCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> toCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(usageRecordRepository).findByPartnerIdAndDateRange(
            eq("partner-001"), fromCaptor.capture(), toCaptor.capture());

        assertEquals(LocalDateTime.of(2025, 6, 1, 0, 0, 0), fromCaptor.getValue());
        assertEquals(LocalDateTime.of(2025, 6, 30, 23, 59, 59), toCaptor.getValue());
    }

    @Test
    void getUsageRecords_shouldReturnEmptyForNoRecords() {
        when(usageRecordRepository.findByPartnerIdAndDateRange(
            anyString(), any(), any())).thenReturn(List.of());

        List<UsageRecord> result = service.getUsageRecords(
            "partner-001", LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 30));

        assertTrue(result.isEmpty());
    }

    // --- getUsageSummary ---

    @Test
    void getUsageSummary_shouldDelegateToSummaryRepository() {
        YearMonth period = YearMonth.of(2025, 6);
        UsageSummary summary = new UsageSummary(
            "partner-001", "SANCTIONS_SCREENING", period, 10, 2, 12, BigDecimal.ZERO);
        when(usageSummaryRepository.findByPartnerIdAndPeriod("partner-001", period))
            .thenReturn(List.of(summary));

        List<UsageSummary> result = service.getUsageSummary("partner-001", period);

        assertEquals(1, result.size());
        assertEquals(summary, result.get(0));
    }

    // --- aggregateUsage ---

    @Test
    void aggregateUsage_shouldReturnEmptyForNoRecords() {
        YearMonth period = YearMonth.of(2025, 6);
        when(usageRecordRepository.findByPartnerIdAndDateRange(anyString(), any(), any()))
            .thenReturn(List.of());

        List<UsageSummary> result = service.aggregateUsage("partner-001", period);

        assertTrue(result.isEmpty());
        verify(usageSummaryRepository, never()).save(any());
    }

    @Test
    void aggregateUsage_shouldGroupByVerificationType() {
        YearMonth period = YearMonth.of(2025, 6);

        List<UsageRecord> records = List.of(
            createRecord("USG-1", "partner-001", "SANCTIONS_SCREENING", "ver-001", "SUCCESS",
                LocalDateTime.of(2025, 6, 10, 10, 0)),
            createRecord("USG-2", "partner-001", "SANCTIONS_SCREENING", "ver-002", "SUCCESS",
                LocalDateTime.of(2025, 6, 11, 10, 0)),
            createRecord("USG-3", "partner-001", "CREDIT_BUREAU_CHECK", "ver-003", "FAILURE",
                LocalDateTime.of(2025, 6, 12, 10, 0))
        );

        when(usageRecordRepository.findByPartnerIdAndDateRange(anyString(), any(), any()))
            .thenReturn(records);

        List<UsageSummary> result = service.aggregateUsage("partner-001", period);

        assertEquals(2, result.size());

        // Find each summary by type
        UsageSummary sanctionsSummary = result.stream()
            .filter(s -> s.verificationType().equals("SANCTIONS_SCREENING"))
            .findFirst().orElseThrow();
        UsageSummary creditSummary = result.stream()
            .filter(s -> s.verificationType().equals("CREDIT_BUREAU_CHECK"))
            .findFirst().orElseThrow();

        assertEquals(2, sanctionsSummary.successCount());
        assertEquals(0, sanctionsSummary.failureCount());
        assertEquals(2, sanctionsSummary.totalCount());

        assertEquals(0, creditSummary.successCount());
        assertEquals(1, creditSummary.failureCount());
        assertEquals(1, creditSummary.totalCount());

        verify(usageSummaryRepository, times(2)).save(any());
    }

    @Test
    void aggregateUsage_shouldCountSuccessAndFailureSeparately() {
        YearMonth period = YearMonth.of(2025, 6);

        List<UsageRecord> records = List.of(
            createRecord("USG-1", "partner-001", "SANCTIONS_SCREENING", "ver-001", "SUCCESS",
                LocalDateTime.of(2025, 6, 10, 10, 0)),
            createRecord("USG-2", "partner-001", "SANCTIONS_SCREENING", "ver-002", "FAILURE",
                LocalDateTime.of(2025, 6, 11, 10, 0)),
            createRecord("USG-3", "partner-001", "SANCTIONS_SCREENING", "ver-003", "SYSTEM_ERROR",
                LocalDateTime.of(2025, 6, 12, 10, 0))
        );

        when(usageRecordRepository.findByPartnerIdAndDateRange(anyString(), any(), any()))
            .thenReturn(records);

        List<UsageSummary> result = service.aggregateUsage("partner-001", period);

        assertEquals(1, result.size());
        UsageSummary summary = result.get(0);

        // Only "SUCCESS" counts as success; everything else is failure
        assertEquals(1, summary.successCount());
        assertEquals(2, summary.failureCount());
        assertEquals(3, summary.totalCount());
    }

    @Test
    void aggregateUsage_shouldSetCostToZero() {
        YearMonth period = YearMonth.of(2025, 6);

        List<UsageRecord> records = List.of(
            createRecord("USG-1", "partner-001", "SANCTIONS_SCREENING", "ver-001", "SUCCESS",
                LocalDateTime.of(2025, 6, 10, 10, 0))
        );

        when(usageRecordRepository.findByPartnerIdAndDateRange(anyString(), any(), any()))
            .thenReturn(records);

        List<UsageSummary> result = service.aggregateUsage("partner-001", period);

        // Cost should be ZERO - it's calculated later by BillingService
        assertEquals(BigDecimal.ZERO, result.get(0).totalCost());
    }

    @Test
    void aggregateUsage_shouldSaveEachSummary() {
        YearMonth period = YearMonth.of(2025, 6);

        List<UsageRecord> records = List.of(
            createRecord("USG-1", "partner-001", "TYPE_A", "ver-001", "SUCCESS",
                LocalDateTime.of(2025, 6, 10, 10, 0)),
            createRecord("USG-2", "partner-001", "TYPE_B", "ver-002", "SUCCESS",
                LocalDateTime.of(2025, 6, 11, 10, 0)),
            createRecord("USG-3", "partner-001", "TYPE_C", "ver-003", "SUCCESS",
                LocalDateTime.of(2025, 6, 12, 10, 0))
        );

        when(usageRecordRepository.findByPartnerIdAndDateRange(anyString(), any(), any()))
            .thenReturn(records);

        service.aggregateUsage("partner-001", period);

        verify(usageSummaryRepository, times(3)).save(any());
    }

    @Test
    void aggregateUsage_shouldQueryCorrectDateRange() {
        YearMonth period = YearMonth.of(2025, 2);

        when(usageRecordRepository.findByPartnerIdAndDateRange(anyString(), any(), any()))
            .thenReturn(List.of());

        service.aggregateUsage("partner-001", period);

        // Feb 2025: 1st to 28th
        ArgumentCaptor<LocalDateTime> fromCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> toCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(usageRecordRepository).findByPartnerIdAndDateRange(
            eq("partner-001"), fromCaptor.capture(), toCaptor.capture());

        assertEquals(LocalDateTime.of(2025, 2, 1, 0, 0, 0), fromCaptor.getValue());
        assertEquals(LocalDateTime.of(2025, 2, 28, 23, 59, 59), toCaptor.getValue());
    }

    private UsageRecord createRecord(String usageId, String partnerId, String verificationType,
                                     String verificationId, String outcome, LocalDateTime timestamp) {
        return new UsageRecord(usageId, partnerId, verificationType, verificationId, outcome, timestamp);
    }
}
