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
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import verigate.billing.domain.enums.PaymentMethod;
import verigate.billing.domain.enums.PaymentStatus;
import verigate.billing.domain.models.Payment;

/**
 * DynamoDB data model for payments.
 * Table: {@code verigate-payments}
 * <ul>
 *   <li>PK: {@code partnerId}</li>
 *   <li>SK: {@code paymentId}</li>
 *   <li>GSI {@code invoiceId-index}: PK: {@code invoiceId}</li>
 *   <li>GSI {@code payFastReference-index}: PK: {@code payFastReference}</li>
 * </ul>
 */
@DynamoDbBean
public class PaymentDataModel {

    private String partnerId;
    private String paymentId;
    private String invoiceId;
    private String amount;
    private String currency;
    private String status;
    private String paymentMethod;
    private String payFastPaymentId;
    private String payFastReference;
    private String merchantId;
    private String initiatedAt;
    private String completedAt;
    private String failureReason;
    private String itnPayload;

    public PaymentDataModel() {
        // Required by DynamoDB enhanced client
    }

    /**
     * Creates a data model from a domain payment.
     *
     * @param payment the domain payment
     * @return the corresponding data model
     */
    public static PaymentDataModel fromDomain(Payment payment) {
        PaymentDataModel model = new PaymentDataModel();
        model.setPartnerId(payment.partnerId());
        model.setPaymentId(payment.paymentId());
        model.setInvoiceId(payment.invoiceId());
        model.setAmount(payment.amount().toPlainString());
        model.setCurrency(payment.currency());
        model.setStatus(payment.status().name());
        model.setPaymentMethod(payment.paymentMethod() != null
            ? payment.paymentMethod().name() : null);
        model.setPayFastPaymentId(payment.payFastPaymentId());
        model.setPayFastReference(payment.payFastReference());
        model.setMerchantId(payment.merchantId());
        model.setInitiatedAt(payment.initiatedAt().toString());
        model.setCompletedAt(payment.completedAt() != null
            ? payment.completedAt().toString() : null);
        model.setFailureReason(payment.failureReason());
        model.setItnPayload(payment.itnPayload());
        return model;
    }

    /**
     * Converts this data model to a domain payment.
     *
     * @return the corresponding domain payment
     */
    public Payment toDomain() {
        return new Payment(
            paymentId,
            invoiceId,
            partnerId,
            new BigDecimal(amount),
            currency,
            PaymentStatus.valueOf(status),
            paymentMethod != null ? PaymentMethod.valueOf(paymentMethod) : null,
            payFastPaymentId,
            payFastReference,
            merchantId,
            Instant.parse(initiatedAt),
            completedAt != null ? Instant.parse(completedAt) : null,
            failureReason,
            itnPayload
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
    @DynamoDbAttribute("paymentId")
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {"invoiceId-index"})
    @DynamoDbAttribute("invoiceId")
    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    @DynamoDbAttribute("amount")
    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @DynamoDbAttribute("currency")
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @DynamoDbAttribute("status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @DynamoDbAttribute("paymentMethod")
    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @DynamoDbAttribute("payFastPaymentId")
    public String getPayFastPaymentId() {
        return payFastPaymentId;
    }

    public void setPayFastPaymentId(String payFastPaymentId) {
        this.payFastPaymentId = payFastPaymentId;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {"payFastReference-index"})
    @DynamoDbAttribute("payFastReference")
    public String getPayFastReference() {
        return payFastReference;
    }

    public void setPayFastReference(String payFastReference) {
        this.payFastReference = payFastReference;
    }

    @DynamoDbAttribute("merchantId")
    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    @DynamoDbAttribute("initiatedAt")
    public String getInitiatedAt() {
        return initiatedAt;
    }

    public void setInitiatedAt(String initiatedAt) {
        this.initiatedAt = initiatedAt;
    }

    @DynamoDbAttribute("completedAt")
    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    @DynamoDbAttribute("failureReason")
    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    @DynamoDbAttribute("itnPayload")
    public String getItnPayload() {
        return itnPayload;
    }

    public void setItnPayload(String itnPayload) {
        this.itnPayload = itnPayload;
    }
}
