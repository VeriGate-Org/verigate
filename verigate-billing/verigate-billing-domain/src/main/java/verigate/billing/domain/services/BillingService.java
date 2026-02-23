/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.services;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import verigate.billing.domain.models.BillingPlan;
import verigate.billing.domain.models.UsageSummary;

/**
 * Service interface for billing calculations and invoice generation.
 * Applies billing plan pricing to aggregated usage summaries.
 */
public interface BillingService {

    /**
     * Calculates the billing for a given partner and period by applying
     * the partner's billing plan pricing to the aggregated usage summaries.
     *
     * @param partnerId the partner identifier
     * @param period    the billing period (year and month)
     * @return a list of usage summaries with calculated costs
     */
    List<UsageSummary> calculateBilling(String partnerId, YearMonth period);

    /**
     * Retrieves the billing plan for a given partner.
     *
     * @param partnerId the partner identifier
     * @return the active billing plan, or empty if no plan is configured
     */
    Optional<BillingPlan> getBillingPlan(String partnerId);

    /**
     * Calculates the total invoice amount for a partner in a given period.
     * The total is the greater of the sum of all verification costs and the
     * monthly minimum defined in the partner's billing plan.
     *
     * @param partnerId the partner identifier
     * @param period    the billing period
     * @return the total invoice amount
     */
    BigDecimal getInvoiceTotal(String partnerId, YearMonth period);

    /**
     * Retrieves all partner IDs that have an active billing plan.
     * Used by the aggregation process to determine which partners
     * need usage aggregation.
     *
     * @return the list of partner identifiers with active plans
     */
    List<String> getActivePartnerIds();
}
