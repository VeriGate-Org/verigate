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
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import verigate.billing.application.services.DefaultBillingService;
import verigate.billing.domain.models.BillingPlan;
import verigate.billing.infrastructure.repositories.datamodels.BillingPlanDataModel;

/**
 * DynamoDB implementation of the billing plan repository.
 * Provides access to partner billing plans stored in the
 * {@code verigate-billing-plans} table.
 */
public class DynamoDbBillingPlanRepository implements DefaultBillingService.BillingPlanRepository {

    private static final Logger LOG = LoggerFactory.getLogger(DynamoDbBillingPlanRepository.class);

    private final DynamoDbTable<BillingPlanDataModel> billingPlanTable;

    /**
     * Constructs a new {@link DynamoDbBillingPlanRepository}.
     *
     * @param enhancedClient the DynamoDB enhanced client
     * @param tableName      the name of the billing plans table
     */
    @Inject
    public DynamoDbBillingPlanRepository(
        DynamoDbEnhancedClient enhancedClient,
        @Named("billingPlansTableName") String tableName) {
        this.billingPlanTable = enhancedClient.table(
            tableName, TableSchema.fromBean(BillingPlanDataModel.class));
    }

    @Override
    public Optional<BillingPlan> findActiveByPartnerId(String partnerId) {
        LOG.debug("Looking up billing plan for partnerId={}", partnerId);

        try {
            Key key = Key.builder().partitionValue(partnerId).build();
            BillingPlanDataModel dataModel = billingPlanTable.getItem(key);

            if (dataModel == null) {
                LOG.debug("No billing plan found for partnerId={}", partnerId);
                return Optional.empty();
            }

            BillingPlan plan = dataModel.toDomain();

            if (!plan.active()) {
                LOG.debug("Billing plan found but inactive for partnerId={}", partnerId);
                return Optional.empty();
            }

            LOG.debug("Active billing plan found for partnerId={}: planId={}",
                partnerId, plan.planId());
            return Optional.of(plan);

        } catch (Exception e) {
            LOG.error("Failed to retrieve billing plan for partnerId={}: {}",
                partnerId, e.getMessage(), e);
            throw new RuntimeException(
                "Failed to retrieve billing plan for partner: " + partnerId, e);
        }
    }

    @Override
    public List<BillingPlan> findAllActive() {
        LOG.debug("Scanning for all active billing plans");

        List<BillingPlan> activePlans = new ArrayList<>();
        try {
            billingPlanTable.scan()
                .items()
                .forEach(item -> {
                    BillingPlan plan = item.toDomain();
                    if (plan.active()) {
                        activePlans.add(plan);
                    }
                });

            LOG.debug("Found {} active billing plans", activePlans.size());
            return activePlans;

        } catch (Exception e) {
            LOG.error("Failed to scan billing plans: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to scan billing plans", e);
        }
    }
}
