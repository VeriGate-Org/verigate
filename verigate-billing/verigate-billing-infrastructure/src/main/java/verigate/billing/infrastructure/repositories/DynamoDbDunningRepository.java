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
import verigate.billing.application.services.DefaultDunningService;
import verigate.billing.domain.enums.DunningStatus;
import verigate.billing.domain.models.DunningSchedule;
import verigate.billing.infrastructure.repositories.datamodels.DunningScheduleDataModel;

/**
 * DynamoDB implementation of the dunning schedule repository.
 * Provides read and write access to dunning schedules stored in the
 * {@code verigate-dunning-schedules} table.
 *
 * <p>Table schema:
 * <ul>
 *   <li>PK: {@code partnerId}</li>
 *   <li>SK: {@code invoiceId#dunningId}</li>
 *   <li>GSI {@code status-nextRetryDate-index}: PK: {@code status}, SK: {@code nextRetryDate}</li>
 * </ul>
 */
public class DynamoDbDunningRepository implements DefaultDunningService.DunningRepository {

    private static final Logger LOG =
        LoggerFactory.getLogger(DynamoDbDunningRepository.class);

    private static final String STATUS_NEXT_RETRY_INDEX = "nextRetry-index";

    private final DynamoDbTable<DunningScheduleDataModel> dunningTable;

    /**
     * Constructs a new {@link DynamoDbDunningRepository}.
     *
     * @param enhancedClient the DynamoDB enhanced client
     * @param tableName      the name of the dunning schedules table
     */
    @Inject
    public DynamoDbDunningRepository(
        DynamoDbEnhancedClient enhancedClient,
        @Named("dunningSchedulesTableName") String tableName) {
        this.dunningTable = enhancedClient.table(
            tableName, TableSchema.fromBean(DunningScheduleDataModel.class));
    }

    @Override
    public void save(DunningSchedule schedule) {
        LOG.debug("Saving dunning schedule: dunningId={}, partnerId={}",
            schedule.dunningId(), schedule.partnerId());

        try {
            DunningScheduleDataModel dataModel = DunningScheduleDataModel.fromDomain(schedule);
            dunningTable.putItem(dataModel);

            LOG.debug("Dunning schedule saved: dunningId={}", schedule.dunningId());

        } catch (Exception e) {
            LOG.error("Failed to save dunning schedule: dunningId={}, error={}",
                schedule.dunningId(), e.getMessage(), e);
            throw new RuntimeException("Failed to save dunning schedule", e);
        }
    }

    @Override
    public Optional<DunningSchedule> findById(String dunningId) {
        LOG.debug("Finding dunning schedule by id: dunningId={}", dunningId);

        try {
            // dunningId is embedded in the sort key; without knowing the partnerId we must scan
            List<DunningSchedule> results = new ArrayList<>();
            dunningTable.scan().items().forEach(item -> {
                if (dunningId.equals(item.getDunningId())) {
                    results.add(item.toDomain());
                }
            });

            Optional<DunningSchedule> result = results.stream().findFirst();
            LOG.debug("Find dunning schedule by id result: dunningId={}, found={}",
                dunningId, result.isPresent());
            return result;

        } catch (Exception e) {
            LOG.error("Failed to find dunning schedule: dunningId={}, error={}",
                dunningId, e.getMessage(), e);
            throw new RuntimeException("Failed to find dunning schedule", e);
        }
    }

    @Override
    public Optional<DunningSchedule> findByInvoiceId(String invoiceId) {
        LOG.debug("Querying dunning schedule by invoiceId={}", invoiceId);

        try {
            // Scan and filter by invoiceId since the sort key contains invoiceId as a prefix
            List<DunningSchedule> results = new ArrayList<>();
            dunningTable.scan().items().forEach(item -> {
                if (invoiceId.equals(item.getInvoiceId())) {
                    results.add(item.toDomain());
                }
            });

            Optional<DunningSchedule> result = results.stream().findFirst();
            LOG.debug("Find dunning schedule by invoiceId result: invoiceId={}, found={}",
                invoiceId, result.isPresent());
            return result;

        } catch (Exception e) {
            LOG.error("Failed to query dunning schedule: invoiceId={}, error={}",
                invoiceId, e.getMessage(), e);
            throw new RuntimeException("Failed to query dunning schedule by invoiceId", e);
        }
    }

    @Override
    public List<DunningSchedule> findDueRetries(LocalDate asOfDate) {
        LOG.debug("Querying due dunning retries as of date={}", asOfDate);

        DynamoDbIndex<DunningScheduleDataModel> index = dunningTable.index(STATUS_NEXT_RETRY_INDEX);

        QueryConditional queryConditional = QueryConditional.sortLessThanOrEqualTo(
            Key.builder()
                .partitionValue(DunningStatus.ACTIVE.name())
                .sortValue(asOfDate.toString())
                .build()
        );

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .build();

        List<DunningSchedule> schedules = new ArrayList<>();
        try {
            index.query(queryRequest).forEach(page ->
                page.items().forEach(item -> schedules.add(item.toDomain())));

            LOG.debug("Found {} due dunning retries as of date={}",
                schedules.size(), asOfDate);
            return schedules;

        } catch (Exception e) {
            LOG.error("Failed to query due dunning retries: asOfDate={}, error={}",
                asOfDate, e.getMessage(), e);
            throw new RuntimeException("Failed to query due dunning retries", e);
        }
    }
}
