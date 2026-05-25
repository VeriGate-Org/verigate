/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.repositories.datamodels;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.util.List;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import verigate.billing.domain.enums.ReconciliationStatus;
import verigate.billing.domain.models.ReconciliationDiscrepancy;
import verigate.billing.domain.models.ReconciliationReport;

/**
 * DynamoDB data model for reconciliation reports.
 * Table: {@code verigate-reconciliation-reports}
 * <ul>
 *   <li>PK: {@code period}</li>
 *   <li>SK: {@code reconciliationId}</li>
 * </ul>
 */
@DynamoDbBean
public class ReconciliationReportDataModel {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private String period;
    private String reconciliationId;
    private String totalInvoiced;
    private String totalPaymentsReceived;
    private String totalCreditsIssued;
    private String totalRefunds;
    private String expectedBalance;
    private String actualBalance;
    private String discrepancy;
    private String status;
    private String discrepanciesJson;
    private String generatedAt;

    public ReconciliationReportDataModel() {
        // Required by DynamoDB enhanced client
    }

    /**
     * Creates a data model from a domain reconciliation report.
     *
     * @param report the domain reconciliation report
     * @return the corresponding data model
     */
    public static ReconciliationReportDataModel fromDomain(ReconciliationReport report) {
        ReconciliationReportDataModel model = new ReconciliationReportDataModel();
        model.setPeriod(report.period().toString());
        model.setReconciliationId(report.reconciliationId());
        model.setTotalInvoiced(report.totalInvoiced() != null
            ? report.totalInvoiced().toPlainString() : null);
        model.setTotalPaymentsReceived(report.totalPaymentsReceived() != null
            ? report.totalPaymentsReceived().toPlainString() : null);
        model.setTotalCreditsIssued(report.totalCreditsIssued() != null
            ? report.totalCreditsIssued().toPlainString() : null);
        model.setTotalRefunds(report.totalRefunds() != null
            ? report.totalRefunds().toPlainString() : null);
        model.setExpectedBalance(report.expectedBalance() != null
            ? report.expectedBalance().toPlainString() : null);
        model.setActualBalance(report.actualBalance() != null
            ? report.actualBalance().toPlainString() : null);
        model.setDiscrepancy(report.discrepancy() != null
            ? report.discrepancy().toPlainString() : null);
        model.setStatus(report.status().name());
        model.setGeneratedAt(report.generatedAt().toString());

        try {
            model.setDiscrepanciesJson(
                OBJECT_MAPPER.writeValueAsString(report.discrepancies()));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize discrepancies", e);
        }

        return model;
    }

    /**
     * Converts this data model to a domain reconciliation report.
     *
     * @return the corresponding domain reconciliation report
     */
    public ReconciliationReport toDomain() {
        List<ReconciliationDiscrepancy> discrepancyList;
        try {
            discrepancyList = discrepanciesJson != null
                ? OBJECT_MAPPER.readValue(discrepanciesJson, new TypeReference<>() {})
                : List.of();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize discrepancies", e);
        }

        return new ReconciliationReport(
            reconciliationId,
            YearMonth.parse(period),
            totalInvoiced != null ? new BigDecimal(totalInvoiced) : null,
            totalPaymentsReceived != null ? new BigDecimal(totalPaymentsReceived) : null,
            totalCreditsIssued != null ? new BigDecimal(totalCreditsIssued) : null,
            totalRefunds != null ? new BigDecimal(totalRefunds) : null,
            expectedBalance != null ? new BigDecimal(expectedBalance) : null,
            actualBalance != null ? new BigDecimal(actualBalance) : null,
            discrepancy != null ? new BigDecimal(discrepancy) : null,
            ReconciliationStatus.valueOf(status),
            discrepancyList,
            Instant.parse(generatedAt)
        );
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("period")
    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("reconciliationId")
    public String getReconciliationId() {
        return reconciliationId;
    }

    public void setReconciliationId(String reconciliationId) {
        this.reconciliationId = reconciliationId;
    }

    @DynamoDbAttribute("totalInvoiced")
    public String getTotalInvoiced() {
        return totalInvoiced;
    }

    public void setTotalInvoiced(String totalInvoiced) {
        this.totalInvoiced = totalInvoiced;
    }

    @DynamoDbAttribute("totalPaymentsReceived")
    public String getTotalPaymentsReceived() {
        return totalPaymentsReceived;
    }

    public void setTotalPaymentsReceived(String totalPaymentsReceived) {
        this.totalPaymentsReceived = totalPaymentsReceived;
    }

    @DynamoDbAttribute("totalCreditsIssued")
    public String getTotalCreditsIssued() {
        return totalCreditsIssued;
    }

    public void setTotalCreditsIssued(String totalCreditsIssued) {
        this.totalCreditsIssued = totalCreditsIssued;
    }

    @DynamoDbAttribute("totalRefunds")
    public String getTotalRefunds() {
        return totalRefunds;
    }

    public void setTotalRefunds(String totalRefunds) {
        this.totalRefunds = totalRefunds;
    }

    @DynamoDbAttribute("expectedBalance")
    public String getExpectedBalance() {
        return expectedBalance;
    }

    public void setExpectedBalance(String expectedBalance) {
        this.expectedBalance = expectedBalance;
    }

    @DynamoDbAttribute("actualBalance")
    public String getActualBalance() {
        return actualBalance;
    }

    public void setActualBalance(String actualBalance) {
        this.actualBalance = actualBalance;
    }

    @DynamoDbAttribute("discrepancy")
    public String getDiscrepancy() {
        return discrepancy;
    }

    public void setDiscrepancy(String discrepancy) {
        this.discrepancy = discrepancy;
    }

    @DynamoDbAttribute("status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @DynamoDbAttribute("discrepanciesJson")
    public String getDiscrepanciesJson() {
        return discrepanciesJson;
    }

    public void setDiscrepanciesJson(String discrepanciesJson) {
        this.discrepanciesJson = discrepanciesJson;
    }

    @DynamoDbAttribute("generatedAt")
    public String getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }
}
