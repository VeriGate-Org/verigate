/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.repositories;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import verigate.billing.application.services.DefaultUsageTrackingService;
import verigate.billing.domain.constants.DomainConstants;
import verigate.billing.domain.models.UsageRecord;
import verigate.billing.infrastructure.repositories.datamodels.UsageRecordDataModel;

/**
 * DynamoDB implementation of the usage record repository.
 * Uses conditional writes on {@code verificationId} to ensure idempotent
 * recording of usage events. If a record with the same verificationId already
 * exists for the partner, the write is silently rejected.
 */
public class DynamoDbUsageRepository implements DefaultUsageTrackingService.UsageRecordRepository {

    private static final Logger LOG = LoggerFactory.getLogger(DynamoDbUsageRepository.class);

    private final DynamoDbTable<UsageRecordDataModel> usageTable;

    /**
     * Constructs a new {@link DynamoDbUsageRepository}.
     *
     * @param enhancedClient the DynamoDB enhanced client
     * @param tableName      the name of the usage records table
     */
    @Inject
    public DynamoDbUsageRepository(
        DynamoDbEnhancedClient enhancedClient,
        @Named("usageRecordsTableName") String tableName) {
        this.usageTable = enhancedClient.table(
            tableName, TableSchema.fromBean(UsageRecordDataModel.class));
    }

    @Override
    public boolean save(UsageRecord usageRecord) {
        LOG.debug("Saving usage record: usageId={}, verificationId={}",
            usageRecord.usageId(), usageRecord.verificationId());

        UsageRecordDataModel dataModel = UsageRecordDataModel.fromDomain(usageRecord);

        try {
            // Conditional write: only succeed if no record with the same
            // verificationId exists for this partner. This ensures idempotency.
            Expression conditionExpression = Expression.builder()
                .expression("attribute_not_exists(partnerId) OR verificationId <> :vid")
                .putExpressionValue(":vid",
                    AttributeValue.builder().s(usageRecord.verificationId()).build())
                .build();

            PutItemEnhancedRequest<UsageRecordDataModel> putRequest =
                PutItemEnhancedRequest.builder(UsageRecordDataModel.class)
                    .item(dataModel)
                    .conditionExpression(conditionExpression)
                    .build();

            usageTable.putItem(putRequest);

            LOG.debug("Usage record saved successfully: usageId={}", usageRecord.usageId());
            return true;

        } catch (ConditionalCheckFailedException e) {
            LOG.info("Duplicate usage record detected, skipping: verificationId={}",
                usageRecord.verificationId());
            return false;
        } catch (Exception e) {
            LOG.error("Failed to save usage record: usageId={}, error={}",
                usageRecord.usageId(), e.getMessage(), e);
            throw new RuntimeException("Failed to save usage record: " + usageRecord.usageId(), e);
        }
    }

    @Override
    public List<UsageRecord> findByPartnerIdAndDateRange(
        String partnerId, LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        LOG.debug("Querying usage records: partnerId={}, from={}, to={}",
            partnerId, fromDateTime, toDateTime);

        String fromSortKey = fromDateTime.toString();
        String toSortKey = toDateTime.toString() + DomainConstants.SORT_KEY_SEPARATOR + "~";

        QueryConditional queryConditional = QueryConditional.sortBetween(
            Key.builder().partitionValue(partnerId).sortValue(fromSortKey).build(),
            Key.builder().partitionValue(partnerId).sortValue(toSortKey).build()
        );

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .build();

        List<UsageRecord> records = new ArrayList<>();
        usageTable.query(queryRequest)
            .items()
            .forEach(item -> records.add(item.toDomain()));

        LOG.debug("Found {} usage records for partnerId={}", records.size(), partnerId);
        return records;
    }

    @Override
    public List<String> findAllPartnerIds() {
        LOG.debug("Scanning for all distinct partner IDs");

        // Scan with projection to only retrieve partnerId
        List<String> partnerIds = new ArrayList<>();
        usageTable.scan()
            .items()
            .forEach(item -> {
                String partnerId = item.getPartnerId();
                if (!partnerIds.contains(partnerId)) {
                    partnerIds.add(partnerId);
                }
            });

        LOG.debug("Found {} distinct partner IDs", partnerIds.size());
        return partnerIds;
    }
}
