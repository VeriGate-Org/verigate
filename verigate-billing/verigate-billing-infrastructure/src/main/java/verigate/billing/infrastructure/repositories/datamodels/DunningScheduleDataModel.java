/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.repositories.datamodels;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import verigate.billing.domain.constants.DomainConstants;
import verigate.billing.domain.enums.DunningStatus;
import verigate.billing.domain.models.DunningAttempt;
import verigate.billing.domain.models.DunningSchedule;

/**
 * DynamoDB data model for dunning schedules.
 * Table: {@code verigate-dunning-schedules}
 * <ul>
 *   <li>PK: {@code partnerId}</li>
 *   <li>SK: {@code invoiceId#dunningId}</li>
 *   <li>GSI {@code nextRetry-index}: PK: {@code status}, SK: {@code nextRetryDate}</li>
 * </ul>
 */
@DynamoDbBean
public class DunningScheduleDataModel {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private String partnerId;
    private String sortKey;
    private String dunningId;
    private String invoiceId;
    private String status;
    private int retryCount;
    private int maxRetries;
    private String nextRetryDate;
    private String attemptsJson;
    private String createdAt;

    public DunningScheduleDataModel() {
        // Required by DynamoDB enhanced client
    }

    /**
     * Creates a data model from a domain dunning schedule.
     *
     * @param schedule the domain dunning schedule
     * @return the corresponding data model
     */
    public static DunningScheduleDataModel fromDomain(DunningSchedule schedule) {
        DunningScheduleDataModel model = new DunningScheduleDataModel();
        model.setPartnerId(schedule.partnerId());
        model.setSortKey(schedule.invoiceId()
            + DomainConstants.SORT_KEY_SEPARATOR + schedule.dunningId());
        model.setDunningId(schedule.dunningId());
        model.setInvoiceId(schedule.invoiceId());
        model.setStatus(schedule.status().name());
        model.setRetryCount(schedule.retryCount());
        model.setMaxRetries(schedule.maxRetries());
        model.setNextRetryDate(schedule.nextRetryDate() != null
            ? schedule.nextRetryDate().toString() : null);
        model.setCreatedAt(schedule.createdAt().toString());

        try {
            model.setAttemptsJson(OBJECT_MAPPER.writeValueAsString(schedule.attempts()));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize dunning attempts", e);
        }

        return model;
    }

    /**
     * Converts this data model to a domain dunning schedule.
     *
     * @return the corresponding domain dunning schedule
     */
    public DunningSchedule toDomain() {
        List<DunningAttempt> attemptList;
        try {
            attemptList = attemptsJson != null
                ? OBJECT_MAPPER.readValue(attemptsJson, new TypeReference<>() {})
                : List.of();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize dunning attempts", e);
        }

        return new DunningSchedule(
            dunningId,
            partnerId,
            invoiceId,
            DunningStatus.valueOf(status),
            retryCount,
            maxRetries,
            nextRetryDate != null ? LocalDate.parse(nextRetryDate) : null,
            attemptList,
            Instant.parse(createdAt)
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

    @DynamoDbAttribute("dunningId")
    public String getDunningId() {
        return dunningId;
    }

    public void setDunningId(String dunningId) {
        this.dunningId = dunningId;
    }

    @DynamoDbAttribute("invoiceId")
    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {"nextRetry-index"})
    @DynamoDbAttribute("status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @DynamoDbAttribute("retryCount")
    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    @DynamoDbAttribute("maxRetries")
    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @DynamoDbSecondarySortKey(indexNames = {"nextRetry-index"})
    @DynamoDbAttribute("nextRetryDate")
    public String getNextRetryDate() {
        return nextRetryDate;
    }

    public void setNextRetryDate(String nextRetryDate) {
        this.nextRetryDate = nextRetryDate;
    }

    @DynamoDbAttribute("attemptsJson")
    public String getAttemptsJson() {
        return attemptsJson;
    }

    public void setAttemptsJson(String attemptsJson) {
        this.attemptsJson = attemptsJson;
    }

    @DynamoDbAttribute("createdAt")
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
