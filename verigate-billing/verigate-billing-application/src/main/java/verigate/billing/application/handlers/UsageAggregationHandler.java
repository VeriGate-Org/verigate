/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.application.handlers;

import com.google.inject.Inject;
import java.time.YearMonth;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.billing.domain.models.UsageSummary;
import verigate.billing.domain.services.BillingService;
import verigate.billing.domain.services.UsageTrackingService;

/**
 * Handles the scheduled aggregation of usage records into billing summaries.
 * Triggered by EventBridge on a daily schedule, this handler retrieves all
 * active partners and aggregates their usage for the current billing period.
 */
public class UsageAggregationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(UsageAggregationHandler.class);

    private final UsageTrackingService usageTrackingService;
    private final BillingService billingService;

    /**
     * Constructs a new {@link UsageAggregationHandler}.
     *
     * @param usageTrackingService the service used to aggregate usage records
     * @param billingService       the service used to retrieve active partners and billing plans
     */
    @Inject
    public UsageAggregationHandler(
        UsageTrackingService usageTrackingService,
        BillingService billingService) {
        this.usageTrackingService = usageTrackingService;
        this.billingService = billingService;
    }

    /**
     * Executes the aggregation for the current billing period.
     * Iterates over all partners with active billing plans and aggregates
     * their usage records into summaries, then calculates billing costs.
     *
     * @param period the billing period to aggregate. Typically the current month.
     */
    public void handle(YearMonth period) {
        LOG.info("Starting usage aggregation for period: {}", period);

        try {
            List<String> partnerIds = getActivePartnerIds();

            if (partnerIds.isEmpty()) {
                LOG.info("No active partners found for aggregation");
                return;
            }

            LOG.info("Aggregating usage for {} active partners", partnerIds.size());

            int totalSummaries = 0;
            int successfulPartners = 0;
            int failedPartners = 0;

            for (String partnerId : partnerIds) {
                try {
                    List<UsageSummary> summaries = usageTrackingService.aggregateUsage(
                        partnerId, period);

                    if (!summaries.isEmpty()) {
                        List<UsageSummary> billedSummaries = billingService.calculateBilling(
                            partnerId, period);
                        totalSummaries += billedSummaries.size();

                        LOG.info("Aggregated {} summaries for partner: {}",
                            billedSummaries.size(), partnerId);
                    } else {
                        LOG.debug("No usage records found for partner: {} in period: {}",
                            partnerId, period);
                    }

                    successfulPartners++;

                } catch (Exception e) {
                    failedPartners++;
                    LOG.error("Failed to aggregate usage for partner: {} in period: {}",
                        partnerId, period, e);
                }
            }

            LOG.info("Usage aggregation completed for period: {}. "
                    + "Partners processed: {}, successful: {}, failed: {}, summaries generated: {}",
                period, partnerIds.size(), successfulPartners, failedPartners, totalSummaries);

        } catch (Exception e) {
            LOG.error("Usage aggregation failed for period: {}", period, e);
            throw new RuntimeException("Usage aggregation failed for period: " + period, e);
        }
    }

    /**
     * Retrieves the list of partner IDs that have active billing plans.
     * Partners without active plans are excluded from aggregation.
     *
     * @return the list of active partner identifiers
     */
    private List<String> getActivePartnerIds() {
        return billingService.getActivePartnerIds();
    }
}
