/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.repositories.datamodels;

import java.math.BigDecimal;
import java.time.Instant;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import verigate.billing.domain.constants.DomainConstants;
import verigate.billing.domain.enums.CreditNoteStatus;
import verigate.billing.domain.enums.CreditNoteType;
import verigate.billing.domain.models.CreditNote;

/**
 * DynamoDB data model for credit notes.
 * Table: {@code verigate-credit-notes}
 * <ul>
 *   <li>PK: {@code partnerId}</li>
 *   <li>SK: {@code issuedAt#creditNoteId}</li>
 *   <li>GSI {@code status-index}: PK: {@code status}, SK: {@code partnerId}</li>
 * </ul>
 */
@DynamoDbBean
public class CreditNoteDataModel {

    private String partnerId;
    private String sortKey;
    private String creditNoteId;
    private String creditNoteNumber;
    private String amount;
    private String appliedAmount;
    private String remainingAmount;
    private String type;
    private String status;
    private String referenceInvoiceId;
    private String appliedToInvoiceId;
    private String reason;
    private String issuedBy;
    private String issuedAt;
    private String appliedAt;

    public CreditNoteDataModel() {
        // Required by DynamoDB enhanced client
    }

    /**
     * Creates a data model from a domain credit note.
     *
     * @param creditNote the domain credit note
     * @return the corresponding data model
     */
    public static CreditNoteDataModel fromDomain(CreditNote creditNote) {
        CreditNoteDataModel model = new CreditNoteDataModel();
        model.setPartnerId(creditNote.partnerId());
        model.setSortKey(creditNote.issuedAt().toString()
            + DomainConstants.SORT_KEY_SEPARATOR + creditNote.creditNoteId());
        model.setCreditNoteId(creditNote.creditNoteId());
        model.setCreditNoteNumber(creditNote.creditNoteNumber());
        model.setAmount(creditNote.amount().toPlainString());
        model.setAppliedAmount(creditNote.appliedAmount() != null
            ? creditNote.appliedAmount().toPlainString() : null);
        model.setRemainingAmount(creditNote.remainingAmount() != null
            ? creditNote.remainingAmount().toPlainString() : null);
        model.setType(creditNote.type().name());
        model.setStatus(creditNote.status().name());
        model.setReferenceInvoiceId(creditNote.referenceInvoiceId());
        model.setAppliedToInvoiceId(creditNote.appliedToInvoiceId());
        model.setReason(creditNote.reason());
        model.setIssuedBy(creditNote.issuedBy());
        model.setIssuedAt(creditNote.issuedAt().toString());
        model.setAppliedAt(creditNote.appliedAt() != null
            ? creditNote.appliedAt().toString() : null);
        return model;
    }

    /**
     * Converts this data model to a domain credit note.
     *
     * @return the corresponding domain credit note
     */
    public CreditNote toDomain() {
        return new CreditNote(
            creditNoteId,
            partnerId,
            creditNoteNumber,
            new BigDecimal(amount),
            appliedAmount != null ? new BigDecimal(appliedAmount) : null,
            remainingAmount != null ? new BigDecimal(remainingAmount) : null,
            CreditNoteType.valueOf(type),
            CreditNoteStatus.valueOf(status),
            referenceInvoiceId,
            appliedToInvoiceId,
            reason,
            issuedBy,
            Instant.parse(issuedAt),
            appliedAt != null ? Instant.parse(appliedAt) : null
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

    @DynamoDbAttribute("creditNoteId")
    public String getCreditNoteId() {
        return creditNoteId;
    }

    public void setCreditNoteId(String creditNoteId) {
        this.creditNoteId = creditNoteId;
    }

    @DynamoDbAttribute("creditNoteNumber")
    public String getCreditNoteNumber() {
        return creditNoteNumber;
    }

    public void setCreditNoteNumber(String creditNoteNumber) {
        this.creditNoteNumber = creditNoteNumber;
    }

    @DynamoDbAttribute("amount")
    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @DynamoDbAttribute("appliedAmount")
    public String getAppliedAmount() {
        return appliedAmount;
    }

    public void setAppliedAmount(String appliedAmount) {
        this.appliedAmount = appliedAmount;
    }

    @DynamoDbAttribute("remainingAmount")
    public String getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(String remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    @DynamoDbAttribute("type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {"status-index"})
    @DynamoDbAttribute("status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @DynamoDbSecondarySortKey(indexNames = {"status-index"})
    @DynamoDbAttribute("gsiPartnerId")
    public String getGsiPartnerId() {
        return partnerId;
    }

    public void setGsiPartnerId(String gsiPartnerId) {
        // GSI sort key mirrors partnerId; setter present for DynamoDB enhanced client
    }

    @DynamoDbAttribute("referenceInvoiceId")
    public String getReferenceInvoiceId() {
        return referenceInvoiceId;
    }

    public void setReferenceInvoiceId(String referenceInvoiceId) {
        this.referenceInvoiceId = referenceInvoiceId;
    }

    @DynamoDbAttribute("appliedToInvoiceId")
    public String getAppliedToInvoiceId() {
        return appliedToInvoiceId;
    }

    public void setAppliedToInvoiceId(String appliedToInvoiceId) {
        this.appliedToInvoiceId = appliedToInvoiceId;
    }

    @DynamoDbAttribute("reason")
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @DynamoDbAttribute("issuedBy")
    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    @DynamoDbAttribute("issuedAt")
    public String getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(String issuedAt) {
        this.issuedAt = issuedAt;
    }

    @DynamoDbAttribute("appliedAt")
    public String getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(String appliedAt) {
        this.appliedAt = appliedAt;
    }
}
