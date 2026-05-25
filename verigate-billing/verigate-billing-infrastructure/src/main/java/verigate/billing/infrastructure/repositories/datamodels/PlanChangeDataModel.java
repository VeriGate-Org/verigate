/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.repositories.datamodels;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import verigate.billing.domain.constants.DomainConstants;
import verigate.billing.domain.enums.PlanChangeStatus;
import verigate.billing.domain.enums.PlanChangeType;
import verigate.billing.domain.models.PlanChange;

/**
 * DynamoDB data model for plan changes.
 * Table: {@code verigate-plan-changes}
 * <ul>
 *   <li>PK: {@code partnerId}</li>
 *   <li>SK: {@code requestedDate#planChangeId}</li>
 *   <li>GSI {@code status-index}: PK: {@code status}, SK: {@code effectiveDate}</li>
 * </ul>
 */
@DynamoDbBean
public class PlanChangeDataModel {

    private String partnerId;
    private String sortKey;
    private String planChangeId;
    private String fromPlanId;
    private String toPlanId;
    private String changeType;
    private String status;
    private String proratedCredit;
    private String proratedCharge;
    private String effectiveDate;
    private String requestedDate;
    private String requestedBy;

    public PlanChangeDataModel() {
        // Required by DynamoDB enhanced client
    }

    /**
     * Creates a data model from a domain plan change.
     *
     * @param planChange the domain plan change
     * @return the corresponding data model
     */
    public static PlanChangeDataModel fromDomain(PlanChange planChange) {
        PlanChangeDataModel model = new PlanChangeDataModel();
        model.setPartnerId(planChange.partnerId());
        model.setSortKey(planChange.requestedDate().toString()
            + DomainConstants.SORT_KEY_SEPARATOR + planChange.planChangeId());
        model.setPlanChangeId(planChange.planChangeId());
        model.setFromPlanId(planChange.fromPlanId());
        model.setToPlanId(planChange.toPlanId());
        model.setChangeType(planChange.changeType().name());
        model.setStatus(planChange.status().name());
        model.setProratedCredit(planChange.proratedCredit() != null
            ? planChange.proratedCredit().toPlainString() : null);
        model.setProratedCharge(planChange.proratedCharge() != null
            ? planChange.proratedCharge().toPlainString() : null);
        model.setEffectiveDate(planChange.effectiveDate().toString());
        model.setRequestedDate(planChange.requestedDate().toString());
        model.setRequestedBy(planChange.requestedBy());
        return model;
    }

    /**
     * Converts this data model to a domain plan change.
     *
     * @return the corresponding domain plan change
     */
    public PlanChange toDomain() {
        return new PlanChange(
            planChangeId,
            partnerId,
            fromPlanId,
            toPlanId,
            PlanChangeType.valueOf(changeType),
            PlanChangeStatus.valueOf(status),
            proratedCredit != null ? new BigDecimal(proratedCredit) : null,
            proratedCharge != null ? new BigDecimal(proratedCharge) : null,
            LocalDate.parse(effectiveDate),
            Instant.parse(requestedDate),
            requestedBy
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

    @DynamoDbAttribute("planChangeId")
    public String getPlanChangeId() {
        return planChangeId;
    }

    public void setPlanChangeId(String planChangeId) {
        this.planChangeId = planChangeId;
    }

    @DynamoDbAttribute("fromPlanId")
    public String getFromPlanId() {
        return fromPlanId;
    }

    public void setFromPlanId(String fromPlanId) {
        this.fromPlanId = fromPlanId;
    }

    @DynamoDbAttribute("toPlanId")
    public String getToPlanId() {
        return toPlanId;
    }

    public void setToPlanId(String toPlanId) {
        this.toPlanId = toPlanId;
    }

    @DynamoDbAttribute("changeType")
    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {"status-index"})
    @DynamoDbAttribute("status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @DynamoDbAttribute("proratedCredit")
    public String getProratedCredit() {
        return proratedCredit;
    }

    public void setProratedCredit(String proratedCredit) {
        this.proratedCredit = proratedCredit;
    }

    @DynamoDbAttribute("proratedCharge")
    public String getProratedCharge() {
        return proratedCharge;
    }

    public void setProratedCharge(String proratedCharge) {
        this.proratedCharge = proratedCharge;
    }

    @DynamoDbSecondarySortKey(indexNames = {"status-index"})
    @DynamoDbAttribute("effectiveDate")
    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @DynamoDbAttribute("requestedDate")
    public String getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(String requestedDate) {
        this.requestedDate = requestedDate;
    }

    @DynamoDbAttribute("requestedBy")
    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }
}
