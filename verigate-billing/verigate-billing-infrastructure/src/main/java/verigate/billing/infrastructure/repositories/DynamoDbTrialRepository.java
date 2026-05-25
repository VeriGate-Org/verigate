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
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import verigate.billing.application.services.DefaultTrialService;
import verigate.billing.domain.enums.TrialStatus;
import verigate.billing.domain.models.Trial;
import verigate.billing.infrastructure.repositories.datamodels.TrialDataModel;

/**
 * DynamoDB implementation of the trial repository.
 * Provides read and write access to trials stored in the
 * {@code verigate-trials} table.
 *
 * <p>Table schema:
 * <ul>
 *   <li>PK: {@code partnerId}</li>
 *   <li>SK: {@code trialId}</li>
 *   <li>GSI {@code status-endDate-index}: PK: {@code status}, SK: {@code endDate}</li>
 * </ul>
 */
public class DynamoDbTrialRepository implements DefaultTrialService.TrialRepository {

    private static final Logger LOG =
        LoggerFactory.getLogger(DynamoDbTrialRepository.class);

    private static final String STATUS_END_DATE_INDEX = "status-endDate-index";

    private final DynamoDbTable<TrialDataModel> trialTable;

    /**
     * Constructs a new {@link DynamoDbTrialRepository}.
     *
     * @param enhancedClient the DynamoDB enhanced client
     * @param tableName      the name of the trials table
     */
    @Inject
    public DynamoDbTrialRepository(
        DynamoDbEnhancedClient enhancedClient,
        @Named("trialsTableName") String tableName) {
        this.trialTable = enhancedClient.table(
            tableName, TableSchema.fromBean(TrialDataModel.class));
    }

    @Override
    public void save(Trial trial) {
        LOG.debug("Saving trial: trialId={}, partnerId={}",
            trial.trialId(), trial.partnerId());

        try {
            TrialDataModel dataModel = TrialDataModel.fromDomain(trial);
            trialTable.putItem(dataModel);

            LOG.debug("Trial saved: trialId={}", trial.trialId());

        } catch (Exception e) {
            LOG.error("Failed to save trial: trialId={}, error={}",
                trial.trialId(), e.getMessage(), e);
            throw new RuntimeException("Failed to save trial", e);
        }
    }

    @Override
    public Optional<Trial> findById(String trialId) {
        LOG.debug("Finding trial by id: trialId={}", trialId);

        try {
            // trialId is the sort key; without knowing the partnerId we must scan
            List<Trial> results = new ArrayList<>();
            trialTable.scan().items().forEach(item -> {
                if (trialId.equals(item.getTrialId())) {
                    results.add(item.toDomain());
                }
            });

            Optional<Trial> result = results.stream().findFirst();
            LOG.debug("Find trial by id result: trialId={}, found={}",
                trialId, result.isPresent());
            return result;

        } catch (Exception e) {
            LOG.error("Failed to find trial: trialId={}, error={}",
                trialId, e.getMessage(), e);
            throw new RuntimeException("Failed to find trial", e);
        }
    }

    @Override
    public Optional<Trial> findActiveByPartnerId(String partnerId) {
        LOG.debug("Querying active trial for partnerId={}", partnerId);

        QueryConditional queryConditional = QueryConditional.keyEqualTo(
            Key.builder()
                .partitionValue(partnerId)
                .build()
        );

        Expression filterExpression = Expression.builder()
            .expression("#s = :status")
            .putExpressionName("#s", "status")
            .putExpressionValue(":status",
                AttributeValue.builder().s(TrialStatus.ACTIVE.name()).build())
            .build();

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .filterExpression(filterExpression)
            .build();

        try {
            List<Trial> results = new ArrayList<>();
            trialTable.query(queryRequest)
                .items()
                .forEach(item -> results.add(item.toDomain()));

            Optional<Trial> result = results.stream().findFirst();
            LOG.debug("Find active trial result: partnerId={}, found={}",
                partnerId, result.isPresent());
            return result;

        } catch (Exception e) {
            LOG.error("Failed to query active trial: partnerId={}, error={}",
                partnerId, e.getMessage(), e);
            throw new RuntimeException("Failed to query active trial", e);
        }
    }

    @Override
    public List<Trial> findExpiredActive(LocalDate asOfDate) {
        LOG.debug("Querying expired active trials as of date={}", asOfDate);

        DynamoDbIndex<TrialDataModel> index = trialTable.index(STATUS_END_DATE_INDEX);

        QueryConditional queryConditional = QueryConditional.sortLessThanOrEqualTo(
            Key.builder()
                .partitionValue(TrialStatus.ACTIVE.name())
                .sortValue(asOfDate.toString())
                .build()
        );

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .build();

        List<Trial> trials = new ArrayList<>();
        try {
            index.query(queryRequest).forEach(page ->
                page.items().forEach(item -> trials.add(item.toDomain())));

            LOG.debug("Found {} expired active trials as of date={}",
                trials.size(), asOfDate);
            return trials;

        } catch (Exception e) {
            LOG.error("Failed to query expired active trials: asOfDate={}, error={}",
                asOfDate, e.getMessage(), e);
            throw new RuntimeException("Failed to query expired active trials", e);
        }
    }
}
