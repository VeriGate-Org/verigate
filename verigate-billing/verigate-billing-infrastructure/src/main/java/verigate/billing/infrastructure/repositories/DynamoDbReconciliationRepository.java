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
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import verigate.billing.application.services.DefaultReportingService;
import verigate.billing.domain.models.ReconciliationReport;
import verigate.billing.infrastructure.repositories.datamodels.ReconciliationReportDataModel;

/**
 * DynamoDB implementation of the reconciliation report repository.
 * Provides read and write access to reconciliation reports stored in the
 * {@code verigate-reconciliation-reports} table.
 *
 * <p>Table schema:
 * <ul>
 *   <li>PK: {@code period} (e.g., {@code 2025-01})</li>
 *   <li>SK: {@code reconciliationId}</li>
 * </ul>
 */
public class DynamoDbReconciliationRepository
    implements DefaultReportingService.ReconciliationRepository {

    private static final Logger LOG =
        LoggerFactory.getLogger(DynamoDbReconciliationRepository.class);

    private final DynamoDbTable<ReconciliationReportDataModel> reconciliationTable;

    /**
     * Constructs a new {@link DynamoDbReconciliationRepository}.
     *
     * @param enhancedClient the DynamoDB enhanced client
     * @param tableName      the name of the reconciliation reports table
     */
    @Inject
    public DynamoDbReconciliationRepository(
        DynamoDbEnhancedClient enhancedClient,
        @Named("reconciliationReportsTableName") String tableName) {
        this.reconciliationTable = enhancedClient.table(
            tableName, TableSchema.fromBean(ReconciliationReportDataModel.class));
    }

    @Override
    public void save(ReconciliationReport report) {
        LOG.debug("Saving reconciliation report: reconciliationId={}, period={}",
            report.reconciliationId(), report.period());

        try {
            ReconciliationReportDataModel dataModel =
                ReconciliationReportDataModel.fromDomain(report);
            reconciliationTable.putItem(dataModel);

            LOG.debug("Reconciliation report saved: reconciliationId={}",
                report.reconciliationId());

        } catch (Exception e) {
            LOG.error("Failed to save reconciliation report: reconciliationId={}, error={}",
                report.reconciliationId(), e.getMessage(), e);
            throw new RuntimeException("Failed to save reconciliation report", e);
        }
    }

    @Override
    public Optional<ReconciliationReport> findByPeriod(YearMonth period) {
        LOG.debug("Querying reconciliation report by period={}", period);

        QueryConditional queryConditional = QueryConditional.keyEqualTo(
            Key.builder()
                .partitionValue(period.toString())
                .build()
        );

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .build();

        try {
            List<ReconciliationReport> results = new ArrayList<>();
            reconciliationTable.query(queryRequest)
                .items()
                .forEach(item -> results.add(item.toDomain()));

            // Return the most recent report for the period
            Optional<ReconciliationReport> result = results.stream().findFirst();
            LOG.debug("Find reconciliation report by period result: period={}, found={}",
                period, result.isPresent());
            return result;

        } catch (Exception e) {
            LOG.error("Failed to query reconciliation report: period={}, error={}",
                period, e.getMessage(), e);
            throw new RuntimeException("Failed to query reconciliation report", e);
        }
    }
}
