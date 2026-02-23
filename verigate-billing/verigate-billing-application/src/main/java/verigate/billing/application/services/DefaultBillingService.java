/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.application.services;

import com.google.inject.Inject;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.billing.domain.constants.DomainConstants;
import verigate.billing.domain.models.BillingPlan;
import verigate.billing.domain.models.UsageSummary;
import verigate.billing.domain.services.BillingService;
import verigate.billing.domain.services.UsageTrackingService;

/**
 * Default implementation of {@link BillingService}.
 * Applies billing plan pricing to aggregated usage summaries and enforces
 * the monthly minimum charge.
 */
public class DefaultBillingService implements BillingService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultBillingService.class);

    private final UsageTrackingService usageTrackingService;
    private final BillingPlanRepository billingPlanRepository;

    /**
     * Constructs a new {@link DefaultBillingService}.
     *
     * @param usageTrackingService  the service for retrieving usage summaries
     * @param billingPlanRepository the repository for billing plans
     */
    @Inject
    public DefaultBillingService(
        UsageTrackingService usageTrackingService,
        BillingPlanRepository billingPlanRepository) {
        this.usageTrackingService = usageTrackingService;
        this.billingPlanRepository = billingPlanRepository;
    }

    @Override
    public List<UsageSummary> calculateBilling(String partnerId, YearMonth period) {
        LOG.info("Calculating billing for partnerId={}, period={}", partnerId, period);

        Optional<BillingPlan> planOptional = getBillingPlan(partnerId);
        if (planOptional.isEmpty()) {
            LOG.warn("No active billing plan found for partnerId={}, "
                + "using default pricing", partnerId);
        }

        BillingPlan plan = planOptional.orElse(null);
        List<UsageSummary> summaries = usageTrackingService.getUsageSummary(partnerId, period);

        if (summaries.isEmpty()) {
            LOG.info("No usage summaries found for partnerId={} in period={}", partnerId, period);
            return List.of();
        }

        List<UsageSummary> billedSummaries = new ArrayList<>();
        for (UsageSummary summary : summaries) {
            BigDecimal unitPrice = resolveUnitPrice(plan, summary.verificationType());
            BigDecimal totalCost = unitPrice.multiply(BigDecimal.valueOf(summary.totalCount()));

            UsageSummary billedSummary = new UsageSummary(
                summary.partnerId(),
                summary.verificationType(),
                summary.period(),
                summary.successCount(),
                summary.failureCount(),
                summary.totalCount(),
                totalCost
            );

            billedSummaries.add(billedSummary);

            LOG.debug("Calculated cost for partnerId={}, type={}: {} x {} = {}",
                partnerId, summary.verificationType(),
                summary.totalCount(), unitPrice, totalCost);
        }

        LOG.info("Billing calculation complete for partnerId={}, period={}: {} line items",
            partnerId, period, billedSummaries.size());

        return billedSummaries;
    }

    @Override
    public Optional<BillingPlan> getBillingPlan(String partnerId) {
        LOG.debug("Retrieving billing plan for partnerId={}", partnerId);
        return billingPlanRepository.findActiveByPartnerId(partnerId);
    }

    @Override
    public BigDecimal getInvoiceTotal(String partnerId, YearMonth period) {
        LOG.info("Calculating invoice total for partnerId={}, period={}", partnerId, period);

        List<UsageSummary> billedSummaries = calculateBilling(partnerId, period);

        BigDecimal usageTotal = billedSummaries.stream()
            .map(UsageSummary::totalCost)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Optional<BillingPlan> planOptional = getBillingPlan(partnerId);
        BigDecimal monthlyMinimum = planOptional
            .map(BillingPlan::monthlyMinimum)
            .orElse(DomainConstants.DEFAULT_MONTHLY_MINIMUM);

        // Apply monthly minimum: the invoice total is the greater of
        // the actual usage cost and the monthly minimum.
        BigDecimal invoiceTotal = usageTotal.max(monthlyMinimum);

        LOG.info("Invoice total for partnerId={}, period={}: usage={}, minimum={}, total={}",
            partnerId, period, usageTotal, monthlyMinimum, invoiceTotal);

        return invoiceTotal;
    }

    @Override
    public List<String> getActivePartnerIds() {
        LOG.debug("Retrieving all active partner IDs");

        List<BillingPlan> activePlans = billingPlanRepository.findAllActive();
        List<String> partnerIds = activePlans.stream()
            .map(BillingPlan::partnerId)
            .distinct()
            .toList();

        LOG.debug("Found {} active partners", partnerIds.size());
        return partnerIds;
    }

    /**
     * Resolves the unit price for a verification type based on the billing plan.
     * Falls back to the default price if no plan or no specific type price exists.
     *
     * @param plan             the billing plan (may be null)
     * @param verificationType the verification type
     * @return the unit price
     */
    private BigDecimal resolveUnitPrice(BillingPlan plan, String verificationType) {
        if (plan != null) {
            BigDecimal planPrice = plan.getPriceForType(verificationType);
            if (planPrice.compareTo(BigDecimal.ZERO) > 0) {
                return planPrice;
            }
        }
        return DomainConstants.DEFAULT_PRICE_PER_VERIFICATION;
    }

    /**
     * Repository interface for billing plans.
     * Implemented by the infrastructure layer.
     */
    public interface BillingPlanRepository {

        /**
         * Finds the active billing plan for a partner.
         *
         * @param partnerId the partner identifier
         * @return the active billing plan, or empty if none exists
         */
        Optional<BillingPlan> findActiveByPartnerId(String partnerId);

        /**
         * Retrieves all active billing plans.
         *
         * @return the list of active billing plans
         */
        List<BillingPlan> findAllActive();
    }
}
