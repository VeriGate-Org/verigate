package verigate.billing.infrastructure.repositories.datamodels;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;
import verigate.billing.domain.models.UsageSummary;

class UsageSummaryDataModelTest {

    @Test
    void fromDomain_shouldMapAllFields() {
        UsageSummary summary = new UsageSummary(
            "partner-001", "SANCTIONS_SCREENING", YearMonth.of(2025, 6),
            10, 2, 12, new BigDecimal("30.00"));

        UsageSummaryDataModel model = UsageSummaryDataModel.fromDomain(summary);

        assertEquals("partner-001", model.getPartnerId());
        assertEquals("SANCTIONS_SCREENING", model.getVerificationType());
        assertEquals("2025-06", model.getPeriod());
        assertEquals(10, model.getSuccessCount());
        assertEquals(2, model.getFailureCount());
        assertEquals(12, model.getTotalCount());
        assertEquals("30.00", model.getTotalCost());
    }

    @Test
    void fromDomain_shouldCreateSortKey() {
        UsageSummary summary = new UsageSummary(
            "partner-001", "SANCTIONS_SCREENING", YearMonth.of(2025, 6),
            10, 2, 12, new BigDecimal("30.00"));

        UsageSummaryDataModel model = UsageSummaryDataModel.fromDomain(summary);

        assertEquals("2025-06#SANCTIONS_SCREENING", model.getSortKey());
    }

    @Test
    void toDomain_shouldMapAllFields() {
        UsageSummaryDataModel model = new UsageSummaryDataModel();
        model.setPartnerId("partner-001");
        model.setVerificationType("SANCTIONS_SCREENING");
        model.setPeriod("2025-06");
        model.setSuccessCount(10);
        model.setFailureCount(2);
        model.setTotalCount(12);
        model.setTotalCost("30.00");
        model.setSortKey("2025-06#SANCTIONS_SCREENING");

        UsageSummary summary = model.toDomain();

        assertEquals("partner-001", summary.partnerId());
        assertEquals("SANCTIONS_SCREENING", summary.verificationType());
        assertEquals(YearMonth.of(2025, 6), summary.period());
        assertEquals(10, summary.successCount());
        assertEquals(2, summary.failureCount());
        assertEquals(12, summary.totalCount());
        assertEquals(new BigDecimal("30.00"), summary.totalCost());
    }

    @Test
    void roundTrip_shouldPreserveAllData() {
        UsageSummary original = new UsageSummary(
            "partner-001", "CREDIT_BUREAU_CHECK", YearMonth.of(2025, 12),
            100, 25, 125, new BigDecimal("625.50"));

        UsageSummaryDataModel model = UsageSummaryDataModel.fromDomain(original);
        UsageSummary restored = model.toDomain();

        assertEquals(original.partnerId(), restored.partnerId());
        assertEquals(original.verificationType(), restored.verificationType());
        assertEquals(original.period(), restored.period());
        assertEquals(original.successCount(), restored.successCount());
        assertEquals(original.failureCount(), restored.failureCount());
        assertEquals(original.totalCount(), restored.totalCount());
        assertEquals(0, original.totalCost().compareTo(restored.totalCost()));
    }

    @Test
    void roundTrip_shouldPreserveZeroCost() {
        UsageSummary original = new UsageSummary(
            "partner-001", "SANCTIONS_SCREENING", YearMonth.of(2025, 1),
            0, 0, 0, BigDecimal.ZERO);

        UsageSummaryDataModel model = UsageSummaryDataModel.fromDomain(original);
        UsageSummary restored = model.toDomain();

        assertEquals(0, BigDecimal.ZERO.compareTo(restored.totalCost()));
    }

    @Test
    void sortKey_shouldEnablePeriodOrdering() {
        UsageSummaryDataModel jan = UsageSummaryDataModel.fromDomain(
            new UsageSummary("p", "TYPE", YearMonth.of(2025, 1), 0, 0, 0, BigDecimal.ZERO));
        UsageSummaryDataModel dec = UsageSummaryDataModel.fromDomain(
            new UsageSummary("p", "TYPE", YearMonth.of(2025, 12), 0, 0, 0, BigDecimal.ZERO));

        assertTrue(jan.getSortKey().compareTo(dec.getSortKey()) < 0);
    }
}
