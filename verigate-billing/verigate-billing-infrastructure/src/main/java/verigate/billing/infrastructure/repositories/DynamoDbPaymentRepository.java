/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.repositories;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import verigate.billing.application.services.DefaultPaymentService;
import verigate.billing.domain.models.Payment;
import verigate.billing.infrastructure.repositories.datamodels.PaymentDataModel;

/**
 * DynamoDB implementation of the payment repository.
 * Provides read and write access to payments stored in the
 * {@code verigate-payments} table.
 *
 * <p>Table schema:
 * <ul>
 *   <li>PK: {@code partnerId}</li>
 *   <li>SK: {@code paymentId}</li>
 *   <li>GSI {@code invoiceId-index}: PK: {@code invoiceId}</li>
 *   <li>GSI {@code payFastReference-index}: PK: {@code payFastReference}</li>
 * </ul>
 */
public class DynamoDbPaymentRepository implements DefaultPaymentService.PaymentRepository {

    private static final Logger LOG =
        LoggerFactory.getLogger(DynamoDbPaymentRepository.class);

    private static final String INVOICE_ID_INDEX = "invoiceId-index";
    private static final String PAYFAST_REFERENCE_INDEX = "payFastReference-index";

    private final DynamoDbTable<PaymentDataModel> paymentTable;

    /**
     * Constructs a new {@link DynamoDbPaymentRepository}.
     *
     * @param enhancedClient the DynamoDB enhanced client
     * @param tableName      the name of the payments table
     */
    @Inject
    public DynamoDbPaymentRepository(
        DynamoDbEnhancedClient enhancedClient,
        @Named("paymentsTableName") String tableName) {
        this.paymentTable = enhancedClient.table(
            tableName, TableSchema.fromBean(PaymentDataModel.class));
    }

    @Override
    public void save(Payment payment) {
        LOG.debug("Saving payment: paymentId={}, partnerId={}",
            payment.paymentId(), payment.partnerId());

        try {
            PaymentDataModel dataModel = PaymentDataModel.fromDomain(payment);
            paymentTable.putItem(dataModel);

            LOG.debug("Payment saved: paymentId={}", payment.paymentId());

        } catch (Exception e) {
            LOG.error("Failed to save payment: paymentId={}, error={}",
                payment.paymentId(), e.getMessage(), e);
            throw new RuntimeException("Failed to save payment", e);
        }
    }

    @Override
    public Optional<Payment> findById(String paymentId) {
        LOG.debug("Finding payment by id: paymentId={}", paymentId);

        try {
            // paymentId is the sort key; without knowing the partnerId we must scan
            List<Payment> results = new ArrayList<>();
            paymentTable.scan().items().forEach(item -> {
                if (paymentId.equals(item.getPaymentId())) {
                    results.add(item.toDomain());
                }
            });

            Optional<Payment> result = results.stream().findFirst();
            LOG.debug("Find payment by id result: paymentId={}, found={}",
                paymentId, result.isPresent());
            return result;

        } catch (Exception e) {
            LOG.error("Failed to find payment: paymentId={}, error={}",
                paymentId, e.getMessage(), e);
            throw new RuntimeException("Failed to find payment", e);
        }
    }

    @Override
    public List<Payment> findByInvoiceId(String invoiceId) {
        LOG.debug("Querying payments by invoiceId={}", invoiceId);

        DynamoDbIndex<PaymentDataModel> index = paymentTable.index(INVOICE_ID_INDEX);

        QueryConditional queryConditional = QueryConditional.keyEqualTo(
            Key.builder()
                .partitionValue(invoiceId)
                .build()
        );

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .build();

        List<Payment> payments = new ArrayList<>();
        try {
            index.query(queryRequest).forEach(page ->
                page.items().forEach(item -> payments.add(item.toDomain())));

            LOG.debug("Found {} payments for invoiceId={}", payments.size(), invoiceId);
            return payments;

        } catch (Exception e) {
            LOG.error("Failed to query payments by invoiceId: invoiceId={}, error={}",
                invoiceId, e.getMessage(), e);
            throw new RuntimeException("Failed to query payments by invoiceId", e);
        }
    }

    @Override
    public Optional<Payment> findByPayFastReference(String reference) {
        LOG.debug("Querying payment by payFastReference={}", reference);

        DynamoDbIndex<PaymentDataModel> index = paymentTable.index(PAYFAST_REFERENCE_INDEX);

        QueryConditional queryConditional = QueryConditional.keyEqualTo(
            Key.builder()
                .partitionValue(reference)
                .build()
        );

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .build();

        try {
            List<Payment> results = new ArrayList<>();
            index.query(queryRequest).forEach(page ->
                page.items().forEach(item -> results.add(item.toDomain())));

            Optional<Payment> result = results.stream().findFirst();
            LOG.debug("Find payment by reference result: reference={}, found={}",
                reference, result.isPresent());
            return result;

        } catch (Exception e) {
            LOG.error("Failed to query payment by reference: reference={}, error={}",
                reference, e.getMessage(), e);
            throw new RuntimeException("Failed to query payment by reference", e);
        }
    }

    @Override
    public List<Payment> findByPartnerId(String partnerId) {
        LOG.debug("Querying payments by partnerId={}", partnerId);

        QueryConditional queryConditional = QueryConditional.keyEqualTo(
            Key.builder()
                .partitionValue(partnerId)
                .build()
        );

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .build();

        List<Payment> payments = new ArrayList<>();
        try {
            paymentTable.query(queryRequest)
                .items()
                .forEach(item -> payments.add(item.toDomain()));

            LOG.debug("Found {} payments for partnerId={}", payments.size(), partnerId);
            return payments;

        } catch (Exception e) {
            LOG.error("Failed to query payments: partnerId={}, error={}",
                partnerId, e.getMessage(), e);
            throw new RuntimeException("Failed to query payments", e);
        }
    }
}
