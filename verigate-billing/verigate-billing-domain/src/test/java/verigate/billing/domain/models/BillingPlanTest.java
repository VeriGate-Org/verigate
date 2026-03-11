package verigate.billing.domain.models;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class BillingPlanTest {

    private static final String PLAN_ID = "plan-001";
    private static final String PARTNER_ID = "partner-001";
    private static final BigDecimal MONTHLY_MIN = new BigDecimal("500.00");

    @Test
    void shouldCreateValidBillingPlan() {
        Map<String, BigDecimal> prices = Map.of(
            "SANCTIONS_SCREENING", new BigDecimal("3.00"),
            "CREDIT_BUREAU_CHECK", new BigDecimal("5.00"));

        BillingPlan plan = new BillingPlan(PLAN_ID, PARTNER_ID, prices, MONTHLY_MIN, true);

        assertEquals(PLAN_ID, plan.planId());
        assertEquals(PARTNER_ID, plan.partnerId());
        assertEquals(MONTHLY_MIN, plan.monthlyMinimum());
        assertTrue(plan.active());
        assertEquals(2, plan.pricePerVerificationType().size());
    }

    @Test
    void shouldReturnPriceForConfiguredType() {
        Map<String, BigDecimal> prices = Map.of("SANCTIONS_SCREENING", new BigDecimal("3.00"));
        BillingPlan plan = new BillingPlan(PLAN_ID, PARTNER_ID, prices, MONTHLY_MIN, true);

        assertEquals(new BigDecimal("3.00"), plan.getPriceForType("SANCTIONS_SCREENING"));
    }

    @Test
    void shouldReturnZeroForUnconfiguredType() {
        Map<String, BigDecimal> prices = Map.of("SANCTIONS_SCREENING", new BigDecimal("3.00"));
        BillingPlan plan = new BillingPlan(PLAN_ID, PARTNER_ID, prices, MONTHLY_MIN, true);

        assertEquals(BigDecimal.ZERO, plan.getPriceForType("UNKNOWN_TYPE"));
    }

    @Test
    void shouldDefensiveCopyPriceMap() {
        Map<String, BigDecimal> prices = new HashMap<>();
        prices.put("SANCTIONS_SCREENING", new BigDecimal("3.00"));

        BillingPlan plan = new BillingPlan(PLAN_ID, PARTNER_ID, prices, MONTHLY_MIN, true);

        // Modifying original map should not affect the plan
        prices.put("NEW_TYPE", new BigDecimal("10.00"));
        assertFalse(plan.pricePerVerificationType().containsKey("NEW_TYPE"));
    }

    @Test
    void shouldMakePriceMapImmutable() {
        Map<String, BigDecimal> prices = Map.of("SANCTIONS_SCREENING", new BigDecimal("3.00"));
        BillingPlan plan = new BillingPlan(PLAN_ID, PARTNER_ID, prices, MONTHLY_MIN, true);

        assertThrows(UnsupportedOperationException.class,
            () -> plan.pricePerVerificationType().put("NEW", BigDecimal.ONE));
    }

    @Test
    void shouldRejectNullPlanId() {
        assertThrows(IllegalArgumentException.class,
            () -> new BillingPlan(null, PARTNER_ID, Map.of(), MONTHLY_MIN, true));
    }

    @Test
    void shouldRejectBlankPlanId() {
        assertThrows(IllegalArgumentException.class,
            () -> new BillingPlan("  ", PARTNER_ID, Map.of(), MONTHLY_MIN, true));
    }

    @Test
    void shouldRejectNullPartnerId() {
        assertThrows(IllegalArgumentException.class,
            () -> new BillingPlan(PLAN_ID, null, Map.of(), MONTHLY_MIN, true));
    }

    @Test
    void shouldRejectNullPriceMap() {
        assertThrows(IllegalArgumentException.class,
            () -> new BillingPlan(PLAN_ID, PARTNER_ID, null, MONTHLY_MIN, true));
    }

    @Test
    void shouldRejectNullMonthlyMinimum() {
        assertThrows(IllegalArgumentException.class,
            () -> new BillingPlan(PLAN_ID, PARTNER_ID, Map.of(), null, true));
    }

    @Test
    void shouldRejectNegativeMonthlyMinimum() {
        assertThrows(IllegalArgumentException.class,
            () -> new BillingPlan(PLAN_ID, PARTNER_ID, Map.of(), new BigDecimal("-1"), true));
    }

    @Test
    void shouldAllowZeroMonthlyMinimum() {
        BillingPlan plan = new BillingPlan(PLAN_ID, PARTNER_ID, Map.of(), BigDecimal.ZERO, true);
        assertEquals(BigDecimal.ZERO, plan.monthlyMinimum());
    }

    @Test
    void shouldAllowEmptyPriceMap() {
        BillingPlan plan = new BillingPlan(PLAN_ID, PARTNER_ID, Map.of(), MONTHLY_MIN, true);
        assertTrue(plan.pricePerVerificationType().isEmpty());
    }

    @Test
    void shouldSupportInactivePlan() {
        BillingPlan plan = new BillingPlan(PLAN_ID, PARTNER_ID, Map.of(), MONTHLY_MIN, false);
        assertFalse(plan.active());
    }
}
