package verigate.billing.domain.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BillingPeriodTest {

    @Test
    void shouldHaveDailyAndMonthlyPeriods() {
        assertNotNull(BillingPeriod.DAILY);
        assertNotNull(BillingPeriod.MONTHLY);
    }

    @Test
    void shouldHaveExactlyTwoPeriods() {
        assertEquals(2, BillingPeriod.values().length);
    }

    @Test
    void shouldResolveFromName() {
        assertEquals(BillingPeriod.DAILY, BillingPeriod.valueOf("DAILY"));
        assertEquals(BillingPeriod.MONTHLY, BillingPeriod.valueOf("MONTHLY"));
    }
}
