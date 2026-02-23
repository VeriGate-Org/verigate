/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.repositories;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import verigate.billing.application.services.DefaultUsageTrackingService;
import verigate.billing.domain.constants.DomainConstants;
import verigate.billing.domain.models.UsageSummary;
import verigate.billing.infrastructure.repositories.datamodels.UsageSummaryDataModel;

/**
 * DynamoDB implementation of the usage summary repository.
 * Provides read and write access to aggregated usage summaries
 * stored in the {@code verigate-usage-summaries} table.
 */
public class DynamoDbUsageSummaryRepository
    implements DefaultUsageTrackingService.UsageSummaryRepository {

    private static final Logger LOG =
        LoggerFactory.getLogger(DynamoDbUsageSummaryRepository.class);

    private final DynamoDbTable<UsageSummaryDataModel> summaryTable;

    /**
     * Constructs a new {@link DynamoDbUsageSummaryRepository}.
     *
     * @param enhancedClient the DynamoDB enhanced client
     * @param tableName      the name of the usage summaries table
     */
    @Inject
    public DynamoDbUsageSummaryRepository(
        DynamoDbEnhancedClient enhancedClient,
        @Named("usageSummariesTableName") String tableName) {
        this.summaryTable = enhancedClient.table(
            tableName, TableSchema.fromBean(UsageSummaryDataModel.class));
    }

    @Override
    public void save(UsageSummary usageSummary) {
        LOG.debug("Saving usage summary: partnerId={}, period={}, type={}",
            usageSummary.partnerId(), usageSummary.period(), usageSummary.verificationType());

        try {
            UsageSummaryDataModel dataModel = UsageSummaryDataModel.fromDomain(usageSummary);
            summaryTable.putItem(dataModel);

            LOG.debug("Usage summary saved: partnerId={}, period={}, type={}",
                usageSummary.partnerId(), usageSummary.period(), usageSummary.verificationType());

        } catch (Exception e) {
            LOG.error("Failed to save usage summary: partnerId={}, period={}, type={}, error={}",
                usageSummary.partnerId(), usageSummary.period(),
                usageSummary.verificationType(), e.getMessage(), e);
            throw new RuntimeException("Failed to save usage summary", e);
        }
    }

    @Override
    public List<UsageSummary> findByPartnerIdAndPeriod(String partnerId, YearMonth period) {
        LOG.debug("Querying usage summaries: partnerId={}, period={}", partnerId, period);

        String sortKeyPrefix = period.toString() + DomainConstants.SORT_KEY_SEPARATOR;

        QueryConditional queryConditional = QueryConditional.sortBeginsWith(
            Key.builder()
                .partitionValue(partnerId)
                .sortValue(sortKeyPrefix)
                .build()
        );

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .build();

        List<UsageSummary> summaries = new ArrayList<>();
        try {
            summaryTable.query(queryRequest)
                .items()
                .forEach(item -> summaries.add(item.toDomain()));

            LOG.debug("Found {} usage summaries for partnerId={}, period={}",
                summaries.size(), partnerId, period);
            return summaries;

        } catch (Exception e) {
            LOG.error("Failed to query usage summaries: partnerId={}, period={}, error={}",
                partnerId, period, e.getMessage(), e);
            throw new RuntimeException("Failed to query usage summaries", e);
        }
    }
}
