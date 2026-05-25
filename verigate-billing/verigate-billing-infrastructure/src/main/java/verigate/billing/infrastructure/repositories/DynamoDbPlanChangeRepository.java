/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.repositories;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import verigate.billing.application.services.DefaultPlanChangeService;
import verigate.billing.domain.enums.PlanChangeStatus;
import verigate.billing.domain.models.PlanChange;
import verigate.billing.infrastructure.repositories.datamodels.PlanChangeDataModel;

/**
 * DynamoDB implementation of the plan change repository.
 * Provides read and write access to plan changes stored in the
 * {@code verigate-plan-changes} table.
 *
 * <p>Table schema:
 * <ul>
 *   <li>PK: {@code partnerId}</li>
 *   <li>SK: {@code planChangeId}</li>
 *   <li>GSI {@code status-index}: PK: {@code status}, SK: {@code effectiveDate}</li>
 * </ul>
 */
public class DynamoDbPlanChangeRepository
    implements DefaultPlanChangeService.PlanChangeRepository {

    private static final Logger LOG =
        LoggerFactory.getLogger(DynamoDbPlanChangeRepository.class);

    private static final String STATUS_INDEX = "status-index";

    private final DynamoDbTable<PlanChangeDataModel> planChangeTable;

    /**
     * Constructs a new {@link DynamoDbPlanChangeRepository}.
     *
     * @param enhancedClient the DynamoDB enhanced client
     * @param tableName      the name of the plan changes table
     */
    @Inject
    public DynamoDbPlanChangeRepository(
        DynamoDbEnhancedClient enhancedClient,
        @Named("planChangesTableName") String tableName) {
        this.planChangeTable = enhancedClient.table(
            tableName, TableSchema.fromBean(PlanChangeDataModel.class));
    }

    @Override
    public void save(PlanChange planChange) {
        LOG.debug("Saving plan change: planChangeId={}, partnerId={}",
            planChange.planChangeId(), planChange.partnerId());

        try {
            PlanChangeDataModel dataModel = PlanChangeDataModel.fromDomain(planChange);
            planChangeTable.putItem(dataModel);

            LOG.debug("Plan change saved: planChangeId={}", planChange.planChangeId());

        } catch (Exception e) {
            LOG.error("Failed to save plan change: planChangeId={}, error={}",
                planChange.planChangeId(), e.getMessage(), e);
            throw new RuntimeException("Failed to save plan change", e);
        }
    }

    @Override
    public Optional<PlanChange> findById(String planChangeId) {
        LOG.debug("Finding plan change by id: planChangeId={}", planChangeId);

        try {
            // planChangeId is the sort key; without knowing the partnerId we must scan
            List<PlanChange> results = new ArrayList<>();
            planChangeTable.scan().items().forEach(item -> {
                if (planChangeId.equals(item.getPlanChangeId())) {
                    results.add(item.toDomain());
                }
            });

            Optional<PlanChange> result = results.stream().findFirst();
            LOG.debug("Find plan change by id result: planChangeId={}, found={}",
                planChangeId, result.isPresent());
            return result;

        } catch (Exception e) {
            LOG.error("Failed to find plan change: planChangeId={}, error={}",
                planChangeId, e.getMessage(), e);
            throw new RuntimeException("Failed to find plan change", e);
        }
    }

    @Override
    public List<PlanChange> findByPartnerId(String partnerId) {
        LOG.debug("Querying plan changes by partnerId={}", partnerId);

        QueryConditional queryConditional = QueryConditional.keyEqualTo(
            Key.builder()
                .partitionValue(partnerId)
                .build()
        );

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .build();

        List<PlanChange> planChanges = new ArrayList<>();
        try {
            planChangeTable.query(queryRequest)
                .items()
                .forEach(item -> planChanges.add(item.toDomain()));

            LOG.debug("Found {} plan changes for partnerId={}",
                planChanges.size(), partnerId);
            return planChanges;

        } catch (Exception e) {
            LOG.error("Failed to query plan changes: partnerId={}, error={}",
                partnerId, e.getMessage(), e);
            throw new RuntimeException("Failed to query plan changes", e);
        }
    }

    @Override
    public List<PlanChange> findPendingByEffectiveDate(LocalDate effectiveDate) {
        LOG.debug("Querying pending plan changes by effectiveDate={}", effectiveDate);

        DynamoDbIndex<PlanChangeDataModel> index = planChangeTable.index(STATUS_INDEX);

        QueryConditional queryConditional = QueryConditional.sortLessThanOrEqualTo(
            Key.builder()
                .partitionValue(PlanChangeStatus.PENDING.name())
                .sortValue(effectiveDate.toString())
                .build()
        );

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .build();

        List<PlanChange> planChanges = new ArrayList<>();
        try {
            index.query(queryRequest).forEach(page ->
                page.items().forEach(item -> planChanges.add(item.toDomain())));

            LOG.debug("Found {} pending plan changes on or before effectiveDate={}",
                planChanges.size(), effectiveDate);
            return planChanges;

        } catch (Exception e) {
            LOG.error("Failed to query pending plan changes: effectiveDate={}, error={}",
                effectiveDate, e.getMessage(), e);
            throw new RuntimeException("Failed to query pending plan changes", e);
        }
    }
}
