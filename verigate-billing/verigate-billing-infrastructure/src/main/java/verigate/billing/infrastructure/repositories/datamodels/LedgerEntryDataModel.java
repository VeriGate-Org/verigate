/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.repositories.datamodels;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import verigate.billing.domain.constants.DomainConstants;
import verigate.billing.domain.enums.FinancialEventType;
import verigate.billing.domain.enums.LedgerAccountType;
import verigate.billing.domain.enums.LedgerEntryType;
import verigate.billing.domain.models.LedgerEntry;

/**
 * DynamoDB data model for ledger entries.
 * Table: {@code verigate-ledger-entries}
 * <ul>
 *   <li>PK: {@code partnerId}</li>
 *   <li>SK: {@code period#createdAt#ledgerEntryId}</li>
 *   <li>GSI {@code account-period-index}: PK: {@code account}, SK: {@code period#createdAt}</li>
 * </ul>
 */
@DynamoDbBean
public class LedgerEntryDataModel {

    private String partnerId;
    private String sortKey;
    private String ledgerEntryId;
    private String account;
    private String entryType;
    private String amount;
    private String financialEventType;
    private String referenceId;
    private String referenceType;
    private String description;
    private String period;
    private String createdAt;
    private String gsiSortKey;

    public LedgerEntryDataModel() {
        // Required by DynamoDB enhanced client
    }

    /**
     * Creates a data model from a domain ledger entry.
     *
     * @param entry the domain ledger entry
     * @return the corresponding data model
     */
    public static LedgerEntryDataModel fromDomain(LedgerEntry entry) {
        LedgerEntryDataModel model = new LedgerEntryDataModel();
        model.setPartnerId(entry.partnerId());
        model.setSortKey(entry.period().toString()
            + DomainConstants.SORT_KEY_SEPARATOR + entry.createdAt().toString()
            + DomainConstants.SORT_KEY_SEPARATOR + entry.ledgerEntryId());
        model.setLedgerEntryId(entry.ledgerEntryId());
        model.setAccount(entry.account().name());
        model.setEntryType(entry.entryType().name());
        model.setAmount(entry.amount().toPlainString());
        model.setFinancialEventType(entry.financialEventType().name());
        model.setReferenceId(entry.referenceId());
        model.setReferenceType(entry.referenceType());
        model.setDescription(entry.description());
        model.setPeriod(entry.period().toString());
        model.setCreatedAt(entry.createdAt().toString());
        model.setGsiSortKey(entry.period().toString()
            + DomainConstants.SORT_KEY_SEPARATOR + entry.createdAt().toString());
        return model;
    }

    /**
     * Converts this data model to a domain ledger entry.
     *
     * @return the corresponding domain ledger entry
     */
    public LedgerEntry toDomain() {
        return new LedgerEntry(
            ledgerEntryId,
            partnerId,
            LedgerAccountType.valueOf(account),
            LedgerEntryType.valueOf(entryType),
            new BigDecimal(amount),
            FinancialEventType.valueOf(financialEventType),
            referenceId,
            referenceType,
            description,
            YearMonth.parse(period),
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

    @DynamoDbAttribute("ledgerEntryId")
    public String getLedgerEntryId() {
        return ledgerEntryId;
    }

    public void setLedgerEntryId(String ledgerEntryId) {
        this.ledgerEntryId = ledgerEntryId;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {"account-period-index"})
    @DynamoDbAttribute("account")
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @DynamoDbAttribute("entryType")
    public String getEntryType() {
        return entryType;
    }

    public void setEntryType(String entryType) {
        this.entryType = entryType;
    }

    @DynamoDbAttribute("amount")
    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @DynamoDbAttribute("financialEventType")
    public String getFinancialEventType() {
        return financialEventType;
    }

    public void setFinancialEventType(String financialEventType) {
        this.financialEventType = financialEventType;
    }

    @DynamoDbAttribute("referenceId")
    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    @DynamoDbAttribute("referenceType")
    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    @DynamoDbAttribute("description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @DynamoDbAttribute("period")
    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    @DynamoDbAttribute("createdAt")
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @DynamoDbSecondarySortKey(indexNames = {"account-period-index"})
    @DynamoDbAttribute("gsiSortKey")
    public String getGsiSortKey() {
        return gsiSortKey;
    }

    public void setGsiSortKey(String gsiSortKey) {
        this.gsiSortKey = gsiSortKey;
    }
}
