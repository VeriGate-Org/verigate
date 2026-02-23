/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.application.services;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.billing.domain.constants.DomainConstants;
import verigate.billing.domain.models.UsageRecord;
import verigate.billing.domain.models.UsageSummary;
import verigate.billing.domain.services.UsageTrackingService;

/**
 * Default implementation of {@link UsageTrackingService}.
 * Delegates persistence to repository interfaces injected via Guice.
 * Ensures idempotent recording of usage events using the verificationId
 * as a deduplication key via DynamoDB conditional writes.
 */
public class DefaultUsageTrackingService implements UsageTrackingService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultUsageTrackingService.class);

    private final UsageRecordRepository usageRecordRepository;
    private final UsageSummaryRepository usageSummaryRepository;

    /**
     * Constructs a new {@link DefaultUsageTrackingService}.
     *
     * @param usageRecordRepository  the repository for usage records
     * @param usageSummaryRepository the repository for usage summaries
     */
    @Inject
    public DefaultUsageTrackingService(
        UsageRecordRepository usageRecordRepository,
        UsageSummaryRepository usageSummaryRepository) {
        this.usageRecordRepository = usageRecordRepository;
        this.usageSummaryRepository = usageSummaryRepository;
    }

    @Override
    public boolean recordUsage(UsageRecord usageRecord) {
        LOG.debug("Recording usage: usageId={}, partnerId={}, verificationId={}",
            usageRecord.usageId(), usageRecord.partnerId(), usageRecord.verificationId());

        return usageRecordRepository.save(usageRecord);
    }

    @Override
    public List<UsageRecord> getUsageRecords(String partnerId, LocalDate from, LocalDate to) {
        LOG.debug("Retrieving usage records: partnerId={}, from={}, to={}", partnerId, from, to);

        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(23, 59, 59);

        return usageRecordRepository.findByPartnerIdAndDateRange(partnerId, fromDateTime, toDateTime);
    }

    @Override
    public List<UsageSummary> getUsageSummary(String partnerId, YearMonth period) {
        LOG.debug("Retrieving usage summary: partnerId={}, period={}", partnerId, period);

        return usageSummaryRepository.findByPartnerIdAndPeriod(partnerId, period);
    }

    @Override
    public List<UsageSummary> aggregateUsage(String partnerId, YearMonth period) {
        LOG.info("Aggregating usage for partnerId={}, period={}", partnerId, period);

        LocalDate startOfMonth = period.atDay(1);
        LocalDate endOfMonth = period.atEndOfMonth();

        List<UsageRecord> records = getUsageRecords(partnerId, startOfMonth, endOfMonth);

        if (records.isEmpty()) {
            LOG.info("No usage records found for partnerId={} in period={}", partnerId, period);
            return List.of();
        }

        // Group records by verification type and compute counts
        Map<String, long[]> countsByType = new HashMap<>();
        for (UsageRecord record : records) {
            long[] counts = countsByType.computeIfAbsent(
                record.verificationType(), k -> new long[3]); // [success, failure, total]

            counts[2]++; // total
            if (DomainConstants.OUTCOME_SUCCESS.equals(record.outcome())) {
                counts[0]++; // success
            } else {
                counts[1]++; // failure
            }
        }

        List<UsageSummary> summaries = new ArrayList<>();
        for (Map.Entry<String, long[]> entry : countsByType.entrySet()) {
            String verificationType = entry.getKey();
            long[] counts = entry.getValue();

            UsageSummary summary = new UsageSummary(
                partnerId,
                verificationType,
                period,
                counts[0],
                counts[1],
                counts[2],
                BigDecimal.ZERO // Cost will be calculated by BillingService
            );

            usageSummaryRepository.save(summary);
            summaries.add(summary);

            LOG.debug("Aggregated summary: partnerId={}, type={}, success={}, failure={}, total={}",
                partnerId, verificationType, counts[0], counts[1], counts[2]);
        }

        LOG.info("Aggregation complete for partnerId={}, period={}: {} summary records",
            partnerId, period, summaries.size());

        return summaries;
    }

    /**
     * Repository interface for usage records.
     * Implemented by the infrastructure layer.
     */
    public interface UsageRecordRepository {

        /**
         * Saves a usage record with deduplication on verificationId.
         *
         * @param usageRecord the usage record to save
         * @return {@code true} if saved, {@code false} if duplicate
         */
        boolean save(UsageRecord usageRecord);

        /**
         * Finds usage records for a partner within a date range.
         *
         * @param partnerId     the partner identifier
         * @param fromDateTime  the start datetime (inclusive)
         * @param toDateTime    the end datetime (inclusive)
         * @return the list of matching usage records
         */
        List<UsageRecord> findByPartnerIdAndDateRange(
            String partnerId, LocalDateTime fromDateTime, LocalDateTime toDateTime);

        /**
         * Finds all distinct partner IDs that have usage records.
         *
         * @return the list of partner identifiers
         */
        List<String> findAllPartnerIds();
    }

    /**
     * Repository interface for usage summaries.
     * Implemented by the infrastructure layer.
     */
    public interface UsageSummaryRepository {

        /**
         * Saves or updates a usage summary.
         *
         * @param usageSummary the usage summary to persist
         */
        void save(UsageSummary usageSummary);

        /**
         * Finds summaries for a partner in a given period.
         *
         * @param partnerId the partner identifier
         * @param period    the billing period
         * @return the list of matching usage summaries
         */
        List<UsageSummary> findByPartnerIdAndPeriod(String partnerId, YearMonth period);
    }
}
