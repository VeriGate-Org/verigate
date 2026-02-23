/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.repositories.datamodels;

import java.math.BigDecimal;
import java.time.YearMonth;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import verigate.billing.domain.constants.DomainConstants;
import verigate.billing.domain.models.UsageSummary;

/**
 * DynamoDB data model for usage summaries.
 * Table: {@code verigate-usage-summaries}
 * <ul>
 *   <li>PK: {@code partnerId}</li>
 *   <li>SK: {@code period#verificationType} (e.g., {@code 2025-01#SANCTIONS_SCREENING})</li>
 * </ul>
 */
@DynamoDbBean
public class UsageSummaryDataModel {

    private String partnerId;
    private String sortKey;
    private String verificationType;
    private String period;
    private long successCount;
    private long failureCount;
    private long totalCount;
    private String totalCost;

    public UsageSummaryDataModel() {
        // Required by DynamoDB enhanced client
    }

    /**
     * Creates a data model from a domain usage summary.
     *
     * @param summary the domain usage summary
     * @return the corresponding data model
     */
    public static UsageSummaryDataModel fromDomain(UsageSummary summary) {
        UsageSummaryDataModel model = new UsageSummaryDataModel();
        model.setPartnerId(summary.partnerId());
        model.setSortKey(summary.period().toString()
            + DomainConstants.SORT_KEY_SEPARATOR + summary.verificationType());
        model.setVerificationType(summary.verificationType());
        model.setPeriod(summary.period().toString());
        model.setSuccessCount(summary.successCount());
        model.setFailureCount(summary.failureCount());
        model.setTotalCount(summary.totalCount());
        model.setTotalCost(summary.totalCost().toPlainString());
        return model;
    }

    /**
     * Converts this data model to a domain usage summary.
     *
     * @return the corresponding domain usage summary
     */
    public UsageSummary toDomain() {
        return new UsageSummary(
            partnerId,
            verificationType,
            YearMonth.parse(period),
            successCount,
            failureCount,
            totalCount,
            new BigDecimal(totalCost)
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

    @DynamoDbAttribute("verificationType")
    public String getVerificationType() {
        return verificationType;
    }

    public void setVerificationType(String verificationType) {
        this.verificationType = verificationType;
    }

    @DynamoDbAttribute("period")
    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    @DynamoDbAttribute("successCount")
    public long getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(long successCount) {
        this.successCount = successCount;
    }

    @DynamoDbAttribute("failureCount")
    public long getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(long failureCount) {
        this.failureCount = failureCount;
    }

    @DynamoDbAttribute("totalCount")
    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    @DynamoDbAttribute("totalCost")
    public String getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }
}
