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
import verigate.billing.domain.enums.TrialStatus;
import verigate.billing.domain.models.Trial;

/**
 * DynamoDB data model for trials.
 * Table: {@code verigate-trials}
 * <ul>
 *   <li>PK: {@code partnerId}</li>
 *   <li>SK: {@code trialId}</li>
 *   <li>GSI {@code status-endDate-index}: PK: {@code status}, SK: {@code endDate}</li>
 * </ul>
 */
@DynamoDbBean
public class TrialDataModel {

    private String partnerId;
    private String trialId;
    private String planId;
    private String startDate;
    private String endDate;
    private String status;
    private String convertedAt;
    private String cancelledAt;

    public TrialDataModel() {
        // Required by DynamoDB enhanced client
    }

    /**
     * Creates a data model from a domain trial.
     *
     * @param trial the domain trial
     * @return the corresponding data model
     */
    public static TrialDataModel fromDomain(Trial trial) {
        TrialDataModel model = new TrialDataModel();
        model.setPartnerId(trial.partnerId());
        model.setTrialId(trial.trialId());
        model.setPlanId(trial.planId());
        model.setStartDate(trial.startDate().toString());
        model.setEndDate(trial.endDate().toString());
        model.setStatus(trial.status().name());
        model.setConvertedAt(trial.convertedAt() != null
            ? trial.convertedAt().toString() : null);
        model.setCancelledAt(trial.cancelledAt() != null
            ? trial.cancelledAt().toString() : null);
        return model;
    }

    /**
     * Converts this data model to a domain trial.
     *
     * @return the corresponding domain trial
     */
    public Trial toDomain() {
        return new Trial(
            trialId,
            partnerId,
            planId,
            LocalDate.parse(startDate),
            LocalDate.parse(endDate),
            TrialStatus.valueOf(status),
            convertedAt != null ? Instant.parse(convertedAt) : null,
            cancelledAt != null ? Instant.parse(cancelledAt) : null
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
    @DynamoDbAttribute("trialId")
    public String getTrialId() {
        return trialId;
    }

    public void setTrialId(String trialId) {
        this.trialId = trialId;
    }

    @DynamoDbAttribute("planId")
    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    @DynamoDbAttribute("startDate")
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {"status-endDate-index"})
    @DynamoDbAttribute("status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @DynamoDbSecondarySortKey(indexNames = {"status-endDate-index"})
    @DynamoDbAttribute("endDate")
    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @DynamoDbAttribute("convertedAt")
    public String getConvertedAt() {
        return convertedAt;
    }

    public void setConvertedAt(String convertedAt) {
        this.convertedAt = convertedAt;
    }

    @DynamoDbAttribute("cancelledAt")
    public String getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(String cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
}
