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
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import verigate.billing.domain.enums.InvoiceStatus;
import verigate.billing.domain.models.Invoice;
import verigate.billing.domain.models.InvoiceLineItem;

/**
 * DynamoDB data model for invoices.
 * Table: {@code verigate-invoices}
 * <ul>
 *   <li>PK: {@code partnerId}</li>
 *   <li>SK: {@code invoiceId}</li>
 *   <li>GSI {@code invoiceNumber-index}: PK: {@code invoiceNumber}</li>
 * </ul>
 */
@DynamoDbBean
public class InvoiceDataModel {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private String partnerId;
    private String invoiceId;
    private String invoiceNumber;
    private String partnerName;
    private String billingPeriod;
    private String status;
    private String issueDate;
    private String dueDate;
    private String lineItemsJson;
    private String subtotal;
    private String vatRate;
    private String vatAmount;
    private String total;
    private boolean monthlyMinimumApplied;
    private String currency;
    private String pdfS3Key;
    private String paymentId;
    private String createdAt;
    private String updatedAt;
    private String notes;

    public InvoiceDataModel() {
        // Required by DynamoDB enhanced client
    }

    /**
     * Creates a data model from a domain invoice.
     *
     * @param invoice the domain invoice
     * @return the corresponding data model
     */
    public static InvoiceDataModel fromDomain(Invoice invoice) {
        InvoiceDataModel model = new InvoiceDataModel();
        model.setPartnerId(invoice.partnerId());
        model.setInvoiceId(invoice.invoiceId());
        model.setInvoiceNumber(invoice.invoiceNumber());
        model.setPartnerName(invoice.partnerName());
        model.setBillingPeriod(invoice.billingPeriod().toString());
        model.setStatus(invoice.status().name());
        model.setIssueDate(invoice.issueDate() != null ? invoice.issueDate().toString() : null);
        model.setDueDate(invoice.dueDate() != null ? invoice.dueDate().toString() : null);
        model.setSubtotal(invoice.subtotal().toPlainString());
        model.setVatRate(invoice.vatRate().toPlainString());
        model.setVatAmount(invoice.vatAmount().toPlainString());
        model.setTotal(invoice.total().toPlainString());
        model.setMonthlyMinimumApplied(invoice.monthlyMinimumApplied());
        model.setCurrency(invoice.currency());
        model.setPdfS3Key(invoice.pdfS3Key());
        model.setPaymentId(invoice.paymentId());
        model.setCreatedAt(invoice.createdAt().toString());
        model.setUpdatedAt(invoice.updatedAt() != null ? invoice.updatedAt().toString() : null);
        model.setNotes(invoice.notes());

        try {
            model.setLineItemsJson(OBJECT_MAPPER.writeValueAsString(invoice.lineItems()));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize line items", e);
        }

        return model;
    }

    /**
     * Converts this data model to a domain invoice.
     *
     * @return the corresponding domain invoice
     */
    public Invoice toDomain() {
        List<InvoiceLineItem> items;
        try {
            items = lineItemsJson != null
                ? OBJECT_MAPPER.readValue(lineItemsJson, new TypeReference<>() {})
                : List.of();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize line items", e);
        }

        return new Invoice(
            invoiceId,
            invoiceNumber,
            partnerId,
            partnerName,
            YearMonth.parse(billingPeriod),
            InvoiceStatus.valueOf(status),
            issueDate != null ? LocalDate.parse(issueDate) : null,
            dueDate != null ? LocalDate.parse(dueDate) : null,
            items,
            new BigDecimal(subtotal),
            new BigDecimal(vatRate),
            new BigDecimal(vatAmount),
            new BigDecimal(total),
            monthlyMinimumApplied,
            currency,
            pdfS3Key,
            paymentId,
            Instant.parse(createdAt),
            updatedAt != null ? Instant.parse(updatedAt) : null,
            notes
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
    @DynamoDbAttribute("invoiceId")
    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {"invoiceNumber-index"})
    @DynamoDbAttribute("invoiceNumber")
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    @DynamoDbAttribute("partnerName")
    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    @DynamoDbAttribute("billingPeriod")
    public String getBillingPeriod() {
        return billingPeriod;
    }

    public void setBillingPeriod(String billingPeriod) {
        this.billingPeriod = billingPeriod;
    }

    @DynamoDbAttribute("status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @DynamoDbAttribute("issueDate")
    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    @DynamoDbAttribute("dueDate")
    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    @DynamoDbAttribute("lineItemsJson")
    public String getLineItemsJson() {
        return lineItemsJson;
    }

    public void setLineItemsJson(String lineItemsJson) {
        this.lineItemsJson = lineItemsJson;
    }

    @DynamoDbAttribute("subtotal")
    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    @DynamoDbAttribute("vatRate")
    public String getVatRate() {
        return vatRate;
    }

    public void setVatRate(String vatRate) {
        this.vatRate = vatRate;
    }

    @DynamoDbAttribute("vatAmount")
    public String getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(String vatAmount) {
        this.vatAmount = vatAmount;
    }

    @DynamoDbAttribute("total")
    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    @DynamoDbAttribute("monthlyMinimumApplied")
    public boolean isMonthlyMinimumApplied() {
        return monthlyMinimumApplied;
    }

    public void setMonthlyMinimumApplied(boolean monthlyMinimumApplied) {
        this.monthlyMinimumApplied = monthlyMinimumApplied;
    }

    @DynamoDbAttribute("currency")
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @DynamoDbAttribute("pdfS3Key")
    public String getPdfS3Key() {
        return pdfS3Key;
    }

    public void setPdfS3Key(String pdfS3Key) {
        this.pdfS3Key = pdfS3Key;
    }

    @DynamoDbAttribute("paymentId")
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
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

    @DynamoDbAttribute("notes")
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
