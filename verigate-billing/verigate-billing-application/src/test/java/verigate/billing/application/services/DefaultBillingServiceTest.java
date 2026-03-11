package verigate.billing.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import verigate.billing.domain.constants.DomainConstants;
import verigate.billing.domain.models.BillingPlan;
import verigate.billing.domain.models.UsageSummary;
import verigate.billing.domain.services.UsageTrackingService;

@ExtendWith(MockitoExtension.class)
class DefaultBillingServiceTest {

    @Mock
    private UsageTrackingService usageTrackingService;

    @Mock
    private DefaultBillingService.BillingPlanRepository billingPlanRepository;

    private DefaultBillingService service;

    private static final String PARTNER_ID = "partner-001";
    private static final YearMonth PERIOD = YearMonth.of(2025, 6);

    @BeforeEach
    void setUp() {
        service = new DefaultBillingService(usageTrackingService, billingPlanRepository);
    }

    // --- calculateBilling ---

    @Test
    void calculateBilling_shouldApplyPlanPricing() {
        BillingPlan plan = new BillingPlan("plan-001", PARTNER_ID,
            Map.of("SANCTIONS_SCREENING", new BigDecimal("3.00")),
            new BigDecimal("500.00"), true);

        UsageSummary summary = new UsageSummary(
            PARTNER_ID, "SANCTIONS_SCREENING", PERIOD, 8, 2, 10, BigDecimal.ZERO);

        when(billingPlanRepository.findActiveByPartnerId(PARTNER_ID))
            .thenReturn(Optional.of(plan));
        when(usageTrackingService.getUsageSummary(PARTNER_ID, PERIOD))
            .thenReturn(List.of(summary));

        List<UsageSummary> result = service.calculateBilling(PARTNER_ID, PERIOD);

        assertEquals(1, result.size());
        // 10 verifications x $3.00 = $30.00
        assertEquals(new BigDecimal("30.00"), result.get(0).totalCost());
        assertEquals(PARTNER_ID, result.get(0).partnerId());
        assertEquals("SANCTIONS_SCREENING", result.get(0).verificationType());
        assertEquals(8, result.get(0).successCount());
        assertEquals(2, result.get(0).failureCount());
        assertEquals(10, result.get(0).totalCount());
    }

    @Test
    void calculateBilling_shouldUseDefaultPriceWhenNoPlan() {
        UsageSummary summary = new UsageSummary(
            PARTNER_ID, "SANCTIONS_SCREENING", PERIOD, 5, 0, 5, BigDecimal.ZERO);

        when(billingPlanRepository.findActiveByPartnerId(PARTNER_ID))
            .thenReturn(Optional.empty());
        when(usageTrackingService.getUsageSummary(PARTNER_ID, PERIOD))
            .thenReturn(List.of(summary));

        List<UsageSummary> result = service.calculateBilling(PARTNER_ID, PERIOD);

        assertEquals(1, result.size());
        // 5 verifications x $2.50 (default) = $12.50
        BigDecimal expected = DomainConstants.DEFAULT_PRICE_PER_VERIFICATION
            .multiply(BigDecimal.valueOf(5));
        assertEquals(expected, result.get(0).totalCost());
    }

    @Test
    void calculateBilling_shouldUseDefaultPriceForUnconfiguredType() {
        BillingPlan plan = new BillingPlan("plan-001", PARTNER_ID,
            Map.of("SANCTIONS_SCREENING", new BigDecimal("3.00")),
            new BigDecimal("500.00"), true);

        // CREDIT_BUREAU_CHECK is not in the plan
        UsageSummary summary = new UsageSummary(
            PARTNER_ID, "CREDIT_BUREAU_CHECK", PERIOD, 4, 0, 4, BigDecimal.ZERO);

        when(billingPlanRepository.findActiveByPartnerId(PARTNER_ID))
            .thenReturn(Optional.of(plan));
        when(usageTrackingService.getUsageSummary(PARTNER_ID, PERIOD))
            .thenReturn(List.of(summary));

        List<UsageSummary> result = service.calculateBilling(PARTNER_ID, PERIOD);

        // 4 x $2.50 (default) = $10.00
        BigDecimal expected = DomainConstants.DEFAULT_PRICE_PER_VERIFICATION
            .multiply(BigDecimal.valueOf(4));
        assertEquals(expected, result.get(0).totalCost());
    }

    @Test
    void calculateBilling_shouldReturnEmptyForNoSummaries() {
        when(billingPlanRepository.findActiveByPartnerId(PARTNER_ID))
            .thenReturn(Optional.empty());
        when(usageTrackingService.getUsageSummary(PARTNER_ID, PERIOD))
            .thenReturn(List.of());

        List<UsageSummary> result = service.calculateBilling(PARTNER_ID, PERIOD);

        assertTrue(result.isEmpty());
    }

    @Test
    void calculateBilling_shouldHandleMultipleVerificationTypes() {
        BillingPlan plan = new BillingPlan("plan-001", PARTNER_ID,
            Map.of(
                "SANCTIONS_SCREENING", new BigDecimal("3.00"),
                "CREDIT_BUREAU_CHECK", new BigDecimal("5.00")),
            new BigDecimal("500.00"), true);

        List<UsageSummary> summaries = List.of(
            new UsageSummary(PARTNER_ID, "SANCTIONS_SCREENING", PERIOD, 10, 0, 10, BigDecimal.ZERO),
            new UsageSummary(PARTNER_ID, "CREDIT_BUREAU_CHECK", PERIOD, 5, 0, 5, BigDecimal.ZERO)
        );

        when(billingPlanRepository.findActiveByPartnerId(PARTNER_ID))
            .thenReturn(Optional.of(plan));
        when(usageTrackingService.getUsageSummary(PARTNER_ID, PERIOD))
            .thenReturn(summaries);

        List<UsageSummary> result = service.calculateBilling(PARTNER_ID, PERIOD);

        assertEquals(2, result.size());

        UsageSummary sanctions = result.stream()
            .filter(s -> s.verificationType().equals("SANCTIONS_SCREENING"))
            .findFirst().orElseThrow();
        UsageSummary credit = result.stream()
            .filter(s -> s.verificationType().equals("CREDIT_BUREAU_CHECK"))
            .findFirst().orElseThrow();

        assertEquals(new BigDecimal("30.00"), sanctions.totalCost()); // 10 x 3.00
        assertEquals(new BigDecimal("25.00"), credit.totalCost()); // 5 x 5.00
    }

    // --- getBillingPlan ---

    @Test
    void getBillingPlan_shouldReturnActivePlan() {
        BillingPlan plan = new BillingPlan("plan-001", PARTNER_ID,
            Map.of(), new BigDecimal("500.00"), true);

        when(billingPlanRepository.findActiveByPartnerId(PARTNER_ID))
            .thenReturn(Optional.of(plan));

        Optional<BillingPlan> result = service.getBillingPlan(PARTNER_ID);

        assertTrue(result.isPresent());
        assertEquals("plan-001", result.get().planId());
    }

    @Test
    void getBillingPlan_shouldReturnEmptyWhenNoPlan() {
        when(billingPlanRepository.findActiveByPartnerId(PARTNER_ID))
            .thenReturn(Optional.empty());

        Optional<BillingPlan> result = service.getBillingPlan(PARTNER_ID);

        assertTrue(result.isEmpty());
    }

    // --- getInvoiceTotal ---

    @Test
    void getInvoiceTotal_shouldReturnUsageTotalWhenAboveMinimum() {
        BillingPlan plan = new BillingPlan("plan-001", PARTNER_ID,
            Map.of("SANCTIONS_SCREENING", new BigDecimal("3.00")),
            new BigDecimal("100.00"), true);

        UsageSummary summary = new UsageSummary(
            PARTNER_ID, "SANCTIONS_SCREENING", PERIOD, 100, 0, 100, BigDecimal.ZERO);

        when(billingPlanRepository.findActiveByPartnerId(PARTNER_ID))
            .thenReturn(Optional.of(plan));
        when(usageTrackingService.getUsageSummary(PARTNER_ID, PERIOD))
            .thenReturn(List.of(summary));

        BigDecimal total = service.getInvoiceTotal(PARTNER_ID, PERIOD);

        // 100 x $3.00 = $300.00, which is above $100 minimum
        assertEquals(new BigDecimal("300.00"), total);
    }

    @Test
    void getInvoiceTotal_shouldReturnMonthlyMinimumWhenUsageBelowMinimum() {
        BillingPlan plan = new BillingPlan("plan-001", PARTNER_ID,
            Map.of("SANCTIONS_SCREENING", new BigDecimal("3.00")),
            new BigDecimal("500.00"), true);

        UsageSummary summary = new UsageSummary(
            PARTNER_ID, "SANCTIONS_SCREENING", PERIOD, 10, 0, 10, BigDecimal.ZERO);

        when(billingPlanRepository.findActiveByPartnerId(PARTNER_ID))
            .thenReturn(Optional.of(plan));
        when(usageTrackingService.getUsageSummary(PARTNER_ID, PERIOD))
            .thenReturn(List.of(summary));

        BigDecimal total = service.getInvoiceTotal(PARTNER_ID, PERIOD);

        // 10 x $3.00 = $30.00, which is below $500 minimum
        assertEquals(new BigDecimal("500.00"), total);
    }

    @Test
    void getInvoiceTotal_shouldUseDefaultMinimumWhenNoPlan() {
        UsageSummary summary = new UsageSummary(
            PARTNER_ID, "SANCTIONS_SCREENING", PERIOD, 5, 0, 5, BigDecimal.ZERO);

        when(billingPlanRepository.findActiveByPartnerId(PARTNER_ID))
            .thenReturn(Optional.empty());
        when(usageTrackingService.getUsageSummary(PARTNER_ID, PERIOD))
            .thenReturn(List.of(summary));

        BigDecimal total = service.getInvoiceTotal(PARTNER_ID, PERIOD);

        // 5 x $2.50 (default) = $12.50, below $500 default minimum
        assertEquals(DomainConstants.DEFAULT_MONTHLY_MINIMUM, total);
    }

    @Test
    void getInvoiceTotal_shouldReturnMinimumForNoUsage() {
        BillingPlan plan = new BillingPlan("plan-001", PARTNER_ID,
            Map.of(), new BigDecimal("500.00"), true);

        when(billingPlanRepository.findActiveByPartnerId(PARTNER_ID))
            .thenReturn(Optional.of(plan));
        when(usageTrackingService.getUsageSummary(PARTNER_ID, PERIOD))
            .thenReturn(List.of());

        BigDecimal total = service.getInvoiceTotal(PARTNER_ID, PERIOD);

        // No usage, so total = monthly minimum
        assertEquals(new BigDecimal("500.00"), total);
    }

    // --- getActivePartnerIds ---

    @Test
    void getActivePartnerIds_shouldReturnDistinctPartnerIds() {
        BillingPlan plan1 = new BillingPlan("plan-001", "partner-001",
            Map.of(), new BigDecimal("500.00"), true);
        BillingPlan plan2 = new BillingPlan("plan-002", "partner-002",
            Map.of(), new BigDecimal("500.00"), true);

        when(billingPlanRepository.findAllActive()).thenReturn(List.of(plan1, plan2));

        List<String> result = service.getActivePartnerIds();

        assertEquals(2, result.size());
        assertTrue(result.contains("partner-001"));
        assertTrue(result.contains("partner-002"));
    }

    @Test
    void getActivePartnerIds_shouldReturnEmptyWhenNoActivePlans() {
        when(billingPlanRepository.findAllActive()).thenReturn(List.of());

        List<String> result = service.getActivePartnerIds();

        assertTrue(result.isEmpty());
    }

    @Test
    void getActivePartnerIds_shouldDeduplicatePartnerIds() {
        // Two plans for same partner (edge case)
        BillingPlan plan1 = new BillingPlan("plan-001", "partner-001",
            Map.of(), new BigDecimal("500.00"), true);
        BillingPlan plan2 = new BillingPlan("plan-002", "partner-001",
            Map.of(), new BigDecimal("300.00"), true);

        when(billingPlanRepository.findAllActive()).thenReturn(List.of(plan1, plan2));

        List<String> result = service.getActivePartnerIds();

        assertEquals(1, result.size());
        assertEquals("partner-001", result.get(0));
    }
}
