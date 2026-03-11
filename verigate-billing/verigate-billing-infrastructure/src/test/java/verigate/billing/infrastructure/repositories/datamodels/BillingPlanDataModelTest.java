package verigate.billing.infrastructure.repositories.datamodels;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.Test;
import verigate.billing.domain.models.BillingPlan;

class BillingPlanDataModelTest {

    @Test
    void fromDomain_shouldMapAllFields() {
        Map<String, BigDecimal> prices = Map.of(
            "SANCTIONS_SCREENING", new BigDecimal("3.00"),
            "CREDIT_BUREAU_CHECK", new BigDecimal("5.00"));

        BillingPlan plan = new BillingPlan(
            "plan-001", "partner-001", prices, new BigDecimal("500.00"), true);

        BillingPlanDataModel model = BillingPlanDataModel.fromDomain(plan);

        assertEquals("partner-001", model.getPartnerId());
        assertEquals("plan-001", model.getPlanId());
        assertEquals("500.00", model.getMonthlyMinimum());
        assertTrue(model.isActive());
        assertEquals(2, model.getPricePerVerificationType().size());
        assertEquals("3.00", model.getPricePerVerificationType().get("SANCTIONS_SCREENING"));
        assertEquals("5.00", model.getPricePerVerificationType().get("CREDIT_BUREAU_CHECK"));
    }

    @Test
    void toDomain_shouldMapAllFields() {
        BillingPlanDataModel model = new BillingPlanDataModel();
        model.setPartnerId("partner-001");
        model.setPlanId("plan-001");
        model.setMonthlyMinimum("500.00");
        model.setActive(true);
        model.setPricePerVerificationType(Map.of(
            "SANCTIONS_SCREENING", "3.00",
            "CREDIT_BUREAU_CHECK", "5.00"));

        BillingPlan plan = model.toDomain();

        assertEquals("plan-001", plan.planId());
        assertEquals("partner-001", plan.partnerId());
        assertEquals(new BigDecimal("500.00"), plan.monthlyMinimum());
        assertTrue(plan.active());
        assertEquals(new BigDecimal("3.00"), plan.pricePerVerificationType().get("SANCTIONS_SCREENING"));
        assertEquals(new BigDecimal("5.00"), plan.pricePerVerificationType().get("CREDIT_BUREAU_CHECK"));
    }

    @Test
    void toDomain_shouldHandleNullPriceMap() {
        BillingPlanDataModel model = new BillingPlanDataModel();
        model.setPartnerId("partner-001");
        model.setPlanId("plan-001");
        model.setMonthlyMinimum("500.00");
        model.setActive(true);
        model.setPricePerVerificationType(null);

        BillingPlan plan = model.toDomain();

        assertTrue(plan.pricePerVerificationType().isEmpty());
    }

    @Test
    void roundTrip_shouldPreserveAllData() {
        Map<String, BigDecimal> prices = Map.of(
            "SANCTIONS_SCREENING", new BigDecimal("3.50"),
            "CREDIT_BUREAU_CHECK", new BigDecimal("5.75"),
            "DOCUMENT_VERIFICATION", new BigDecimal("10.00"));

        BillingPlan original = new BillingPlan(
            "plan-001", "partner-001", prices, new BigDecimal("750.00"), true);

        BillingPlanDataModel model = BillingPlanDataModel.fromDomain(original);
        BillingPlan restored = model.toDomain();

        assertEquals(original.planId(), restored.planId());
        assertEquals(original.partnerId(), restored.partnerId());
        assertEquals(0, original.monthlyMinimum().compareTo(restored.monthlyMinimum()));
        assertEquals(original.active(), restored.active());
        assertEquals(original.pricePerVerificationType().size(),
            restored.pricePerVerificationType().size());

        for (Map.Entry<String, BigDecimal> entry : original.pricePerVerificationType().entrySet()) {
            assertEquals(0, entry.getValue().compareTo(
                restored.pricePerVerificationType().get(entry.getKey())));
        }
    }

    @Test
    void roundTrip_shouldPreserveInactiveState() {
        BillingPlan original = new BillingPlan(
            "plan-001", "partner-001", Map.of(), new BigDecimal("500.00"), false);

        BillingPlanDataModel model = BillingPlanDataModel.fromDomain(original);
        BillingPlan restored = model.toDomain();

        assertFalse(restored.active());
    }

    @Test
    void fromDomain_shouldConvertBigDecimalToPlainString() {
        // Ensure scientific notation is not used
        Map<String, BigDecimal> prices = Map.of(
            "TYPE", new BigDecimal("0.01"));

        BillingPlan plan = new BillingPlan(
            "plan-001", "partner-001", prices, new BigDecimal("100.00"), true);

        BillingPlanDataModel model = BillingPlanDataModel.fromDomain(plan);

        assertEquals("0.01", model.getPricePerVerificationType().get("TYPE"));
        assertEquals("100.00", model.getMonthlyMinimum());
    }
}
