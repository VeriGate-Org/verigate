/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.repositories.datamodels;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import verigate.billing.domain.constants.DomainConstants;
import verigate.billing.domain.models.UsageRecord;

/**
 * DynamoDB data model for usage records.
 * Table: {@code verigate-usage-records}
 * <ul>
 *   <li>PK: {@code partnerId}</li>
 *   <li>SK: {@code eventTimestamp#usageId}</li>
 *   <li>GSI {@code verificationType-index}: PK: {@code verificationType}, SK: {@code eventTimestamp}</li>
 * </ul>
 */
@DynamoDbBean
public class UsageRecordDataModel {

    private String partnerId;
    private String sortKey;
    private String usageId;
    private String verificationType;
    private String verificationId;
    private String outcome;
    private String eventTimestamp;

    public UsageRecordDataModel() {
        // Required by DynamoDB enhanced client
    }

    /**
     * Creates a data model from a domain usage record.
     *
     * @param record the domain usage record
     * @return the corresponding data model
     */
    public static UsageRecordDataModel fromDomain(UsageRecord record) {
        UsageRecordDataModel model = new UsageRecordDataModel();
        model.setPartnerId(record.partnerId());
        model.setSortKey(record.eventTimestamp().toString()
            + DomainConstants.SORT_KEY_SEPARATOR + record.usageId());
        model.setUsageId(record.usageId());
        model.setVerificationType(record.verificationType());
        model.setVerificationId(record.verificationId());
        model.setOutcome(record.outcome());
        model.setEventTimestamp(record.eventTimestamp().toString());
        return model;
    }

    /**
     * Converts this data model to a domain usage record.
     *
     * @return the corresponding domain usage record
     */
    public UsageRecord toDomain() {
        return new UsageRecord(
            usageId,
            partnerId,
            verificationType,
            verificationId,
            outcome,
            java.time.LocalDateTime.parse(eventTimestamp)
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
    @DynamoDbAttribute("sortKey")
    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }

    @DynamoDbAttribute("usageId")
    public String getUsageId() {
        return usageId;
    }

    public void setUsageId(String usageId) {
        this.usageId = usageId;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {"verificationType-index"})
    @DynamoDbAttribute("verificationType")
    public String getVerificationType() {
        return verificationType;
    }

    public void setVerificationType(String verificationType) {
        this.verificationType = verificationType;
    }

    @DynamoDbAttribute("verificationId")
    public String getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(String verificationId) {
        this.verificationId = verificationId;
    }

    @DynamoDbAttribute("outcome")
    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    @DynamoDbSecondarySortKey(indexNames = {"verificationType-index"})
    @DynamoDbAttribute("eventTimestamp")
    public String getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(String eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }
}
