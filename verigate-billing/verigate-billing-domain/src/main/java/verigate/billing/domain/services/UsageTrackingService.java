/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.services;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import verigate.billing.domain.models.UsageRecord;
import verigate.billing.domain.models.UsageSummary;

/**
 * Service interface for tracking API usage and retrieving usage summaries.
 * Implementations must ensure idempotent recording of usage events.
 */
public interface UsageTrackingService {

    /**
     * Records a single usage event. This operation must be idempotent: if a
     * usage record with the same {@code verificationId} already exists, the
     * duplicate is silently ignored.
     *
     * @param usageRecord the usage record to persist
     * @return {@code true} if the record was successfully stored, {@code false} if it was a duplicate
     */
    boolean recordUsage(UsageRecord usageRecord);

    /**
     * Retrieves all usage records for a given partner within a date range.
     *
     * @param partnerId the partner identifier
     * @param from      the start date (inclusive)
     * @param to        the end date (inclusive)
     * @return a list of usage records within the specified range
     */
    List<UsageRecord> getUsageRecords(String partnerId, LocalDate from, LocalDate to);

    /**
     * Retrieves the aggregated usage summary for a given partner and billing period.
     *
     * @param partnerId the partner identifier
     * @param period    the billing period (year and month)
     * @return a list of usage summaries grouped by verification type
     */
    List<UsageSummary> getUsageSummary(String partnerId, YearMonth period);

    /**
     * Aggregates usage records for a given partner and period into summaries.
     * This is called by the scheduled aggregation Lambda.
     *
     * @param partnerId the partner identifier
     * @param period    the billing period to aggregate
     * @return the list of generated usage summaries
     */
    List<UsageSummary> aggregateUsage(String partnerId, YearMonth period);
}
