/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.repositories.datamodels;

import java.time.Instant;
import java.time.LocalDate;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import verigate.billing.domain.enums.CancellationType;
import verigate.billing.domain.enums.SubscriptionStatus;
import verigate.billing.domain.models.Subscription;

/**
 * DynamoDB data model for subscriptions.
 * Table: {@code verigate-subscriptions}
 * <ul>
 *   <li>PK: {@code partnerId}</li>
 *   <li>SK: {@code subscriptionId}</li>
 *   <li>GSI {@code status-index}: PK: {@code status}, SK: {@code currentPeriodEnd}</li>
 * </ul>
 */
@DynamoDbBean
public class SubscriptionDataModel {

    private String partnerId;
    private String subscriptionId;
    private String planId;
    private String status;
    private String currentPeriodStart;
    private String currentPeriodEnd;
    private String cancelledAt;
    private String cancellationType;
    private String cancelReason;
    private String createdAt;
    private String updatedAt;

    public SubscriptionDataModel() {
        // Required by DynamoDB enhanced client
    }

    /**
     * Creates a data model from a domain subscription.
     *
     * @param subscription the domain subscription
     * @return the corresponding data model
     */
    public static SubscriptionDataModel fromDomain(Subscription subscription) {
        SubscriptionDataModel model = new SubscriptionDataModel();
        model.setPartnerId(subscription.partnerId());
        model.setSubscriptionId(subscription.subscriptionId());
        model.setPlanId(subscription.planId());
        model.setStatus(subscription.status().name());
        model.setCurrentPeriodStart(subscription.currentPeriodStart().toString());
        model.setCurrentPeriodEnd(subscription.currentPeriodEnd().toString());
        model.setCancelledAt(subscription.cancelledAt() != null
            ? subscription.cancelledAt().toString() : null);
        model.setCancellationType(subscription.cancellationType() != null
            ? subscription.cancellationType().name() : null);
        model.setCancelReason(subscription.cancelReason());
        model.setCreatedAt(subscription.createdAt().toString());
        model.setUpdatedAt(subscription.updatedAt() != null
            ? subscription.updatedAt().toString() : null);
        return model;
    }

    /**
     * Converts this data model to a domain subscription.
     *
     * @return the corresponding domain subscription
     */
    public Subscription toDomain() {
        return new Subscription(
            subscriptionId,
            partnerId,
            planId,
            SubscriptionStatus.valueOf(status),
            LocalDate.parse(currentPeriodStart),
            LocalDate.parse(currentPeriodEnd),
            cancelledAt != null ? Instant.parse(cancelledAt) : null,
            cancellationType != null ? CancellationType.valueOf(cancellationType) : null,
            cancelReason,
            Instant.parse(createdAt),
            updatedAt != null ? Instant.parse(updatedAt) : null
        );
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("partnerId")
    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("subscriptionId")
    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    @DynamoDbAttribute("planId")
    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {"status-index"})
    @DynamoDbAttribute("status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @DynamoDbAttribute("currentPeriodStart")
    public String getCurrentPeriodStart() {
        return currentPeriodStart;
    }

    public void setCurrentPeriodStart(String currentPeriodStart) {
        this.currentPeriodStart = currentPeriodStart;
    }

    @DynamoDbSecondarySortKey(indexNames = {"status-index"})
    @DynamoDbAttribute("currentPeriodEnd")
    public String getCurrentPeriodEnd() {
        return currentPeriodEnd;
    }

    public void setCurrentPeriodEnd(String currentPeriodEnd) {
        this.currentPeriodEnd = currentPeriodEnd;
    }

    @DynamoDbAttribute("cancelledAt")
    public String getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(String cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    @DynamoDbAttribute("cancellationType")
    public String getCancellationType() {
        return cancellationType;
    }

    public void setCancellationType(String cancellationType) {
        this.cancellationType = cancellationType;
    }

    @DynamoDbAttribute("cancelReason")
    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    @DynamoDbAttribute("createdAt")
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @DynamoDbAttribute("updatedAt")
    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
