package verigate.billing.domain.models;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;

class UsageSummaryTest {

    private static final String PARTNER_ID = "partner-001";
    private static final String VERIFICATION_TYPE = "SANCTIONS_SCREENING";
    private static final YearMonth PERIOD = YearMonth.of(2025, 6);

    @Test
    void shouldCreateValidUsageSummary() {
        UsageSummary summary = new UsageSummary(
            PARTNER_ID, VERIFICATION_TYPE, PERIOD, 10, 2, 12, new BigDecimal("30.00"));

        assertEquals(PARTNER_ID, summary.partnerId());
        assertEquals(VERIFICATION_TYPE, summary.verificationType());
        assertEquals(PERIOD, summary.period());
        assertEquals(10, summary.successCount());
        assertEquals(2, summary.failureCount());
        assertEquals(12, summary.totalCount());
        assertEquals(new BigDecimal("30.00"), summary.totalCost());
    }

    @Test
    void shouldAllowZeroCounts() {
        UsageSummary summary = new UsageSummary(
            PARTNER_ID, VERIFICATION_TYPE, PERIOD, 0, 0, 0, BigDecimal.ZERO);

        assertEquals(0, summary.successCount());
        assertEquals(0, summary.failureCount());
        assertEquals(0, summary.totalCount());
        assertEquals(BigDecimal.ZERO, summary.totalCost());
    }

    @Test
    void shouldRejectNullPartnerId() {
        assertThrows(IllegalArgumentException.class,
            () -> new UsageSummary(null, VERIFICATION_TYPE, PERIOD, 0, 0, 0, BigDecimal.ZERO));
    }

    @Test
    void shouldRejectBlankPartnerId() {
        assertThrows(IllegalArgumentException.class,
            () -> new UsageSummary("", VERIFICATION_TYPE, PERIOD, 0, 0, 0, BigDecimal.ZERO));
    }

    @Test
    void shouldRejectNullVerificationType() {
        assertThrows(IllegalArgumentException.class,
            () -> new UsageSummary(PARTNER_ID, null, PERIOD, 0, 0, 0, BigDecimal.ZERO));
    }

    @Test
    void shouldRejectNullPeriod() {
        assertThrows(IllegalArgumentException.class,
            () -> new UsageSummary(PARTNER_ID, VERIFICATION_TYPE, null, 0, 0, 0, BigDecimal.ZERO));
    }

    @Test
    void shouldRejectNegativeSuccessCount() {
        assertThrows(IllegalArgumentException.class,
            () -> new UsageSummary(PARTNER_ID, VERIFICATION_TYPE, PERIOD, -1, 0, 0, BigDecimal.ZERO));
    }

    @Test
    void shouldRejectNegativeFailureCount() {
        assertThrows(IllegalArgumentException.class,
            () -> new UsageSummary(PARTNER_ID, VERIFICATION_TYPE, PERIOD, 0, -1, 0, BigDecimal.ZERO));
    }

    @Test
    void shouldRejectNegativeTotalCount() {
        assertThrows(IllegalArgumentException.class,
            () -> new UsageSummary(PARTNER_ID, VERIFICATION_TYPE, PERIOD, 0, 0, -1, BigDecimal.ZERO));
    }

    @Test
    void shouldRejectNullTotalCost() {
        assertThrows(IllegalArgumentException.class,
            () -> new UsageSummary(PARTNER_ID, VERIFICATION_TYPE, PERIOD, 0, 0, 0, null));
    }

    @Test
    void shouldRejectNegativeTotalCost() {
        assertThrows(IllegalArgumentException.class,
            () -> new UsageSummary(PARTNER_ID, VERIFICATION_TYPE, PERIOD, 0, 0, 0, new BigDecimal("-1.00")));
    }

    @Test
    void shouldSupportEquality() {
        UsageSummary s1 = new UsageSummary(
            PARTNER_ID, VERIFICATION_TYPE, PERIOD, 10, 2, 12, new BigDecimal("30.00"));
        UsageSummary s2 = new UsageSummary(
            PARTNER_ID, VERIFICATION_TYPE, PERIOD, 10, 2, 12, new BigDecimal("30.00"));

        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
    }
}
