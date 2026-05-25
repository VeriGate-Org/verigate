/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.repositories;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import verigate.billing.application.services.DefaultSubscriptionService;
import verigate.billing.domain.enums.SubscriptionStatus;
import verigate.billing.domain.models.Subscription;
import verigate.billing.infrastructure.repositories.datamodels.SubscriptionDataModel;

/**
 * DynamoDB implementation of the subscription repository.
 * Provides read and write access to subscriptions stored in the
 * {@code verigate-subscriptions} table.
 *
 * <p>Table schema:
 * <ul>
 *   <li>PK: {@code partnerId}</li>
 *   <li>SK: {@code subscriptionId}</li>
 * </ul>
 */
public class DynamoDbSubscriptionRepository
    implements DefaultSubscriptionService.SubscriptionRepository {

    private static final Logger LOG =
        LoggerFactory.getLogger(DynamoDbSubscriptionRepository.class);

    private final DynamoDbTable<SubscriptionDataModel> subscriptionTable;

    /**
     * Constructs a new {@link DynamoDbSubscriptionRepository}.
     *
     * @param enhancedClient the DynamoDB enhanced client
     * @param tableName      the name of the subscriptions table
     */
    @Inject
    public DynamoDbSubscriptionRepository(
        DynamoDbEnhancedClient enhancedClient,
        @Named("subscriptionsTableName") String tableName) {
        this.subscriptionTable = enhancedClient.table(
            tableName, TableSchema.fromBean(SubscriptionDataModel.class));
    }

    @Override
    public void save(Subscription subscription) {
        LOG.debug("Saving subscription: subscriptionId={}, partnerId={}",
            subscription.subscriptionId(), subscription.partnerId());

        try {
            SubscriptionDataModel dataModel = SubscriptionDataModel.fromDomain(subscription);
            subscriptionTable.putItem(dataModel);

            LOG.debug("Subscription saved: subscriptionId={}", subscription.subscriptionId());

        } catch (Exception e) {
            LOG.error("Failed to save subscription: subscriptionId={}, error={}",
                subscription.subscriptionId(), e.getMessage(), e);
            throw new RuntimeException("Failed to save subscription", e);
        }
    }

    @Override
    public Optional<Subscription> findById(String subscriptionId) {
        LOG.debug("Finding subscription by id: subscriptionId={}", subscriptionId);

        try {
            // subscriptionId is the sort key; without knowing the partnerId we must scan
            List<Subscription> results = new ArrayList<>();
            subscriptionTable.scan().items().forEach(item -> {
                if (subscriptionId.equals(item.getSubscriptionId())) {
                    results.add(item.toDomain());
                }
            });

            Optional<Subscription> result = results.stream().findFirst();
            LOG.debug("Find subscription by id result: subscriptionId={}, found={}",
                subscriptionId, result.isPresent());
            return result;

        } catch (Exception e) {
            LOG.error("Failed to find subscription: subscriptionId={}, error={}",
                subscriptionId, e.getMessage(), e);
            throw new RuntimeException("Failed to find subscription", e);
        }
    }

    @Override
    public Optional<Subscription> findActiveByPartnerId(String partnerId) {
        LOG.debug("Querying active subscription for partnerId={}", partnerId);

        QueryConditional queryConditional = QueryConditional.keyEqualTo(
            Key.builder()
                .partitionValue(partnerId)
                .build()
        );

        Expression filterExpression = Expression.builder()
            .expression("#s = :status")
            .putExpressionName("#s", "status")
            .putExpressionValue(":status",
                AttributeValue.builder().s(SubscriptionStatus.ACTIVE.name()).build())
            .build();

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .filterExpression(filterExpression)
            .build();

        try {
            List<Subscription> results = new ArrayList<>();
            subscriptionTable.query(queryRequest)
                .items()
                .forEach(item -> results.add(item.toDomain()));

            Optional<Subscription> result = results.stream().findFirst();
            LOG.debug("Find active subscription result: partnerId={}, found={}",
                partnerId, result.isPresent());
            return result;

        } catch (Exception e) {
            LOG.error("Failed to query active subscription: partnerId={}, error={}",
                partnerId, e.getMessage(), e);
            throw new RuntimeException("Failed to query active subscription", e);
        }
    }
}
