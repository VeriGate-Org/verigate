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
import verigate.billing.application.services.DefaultInvoiceService;
import verigate.billing.domain.models.Invoice;
import verigate.billing.infrastructure.repositories.datamodels.InvoiceDataModel;

/**
 * DynamoDB implementation of the invoice repository.
 * Provides read and write access to invoices stored in the
 * {@code verigate-invoices} table.
 *
 * <p>Table schema:
 * <ul>
 *   <li>PK: {@code partnerId}</li>
 *   <li>SK: {@code invoiceId}</li>
 *   <li>GSI {@code invoiceNumber-index}: PK: {@code invoiceNumber}</li>
 * </ul>
 */
public class DynamoDbInvoiceRepository implements DefaultInvoiceService.InvoiceRepository {

    private static final Logger LOG =
        LoggerFactory.getLogger(DynamoDbInvoiceRepository.class);

    private static final String INVOICE_NUMBER_INDEX = "invoiceNumber-index";

    private final DynamoDbTable<InvoiceDataModel> invoiceTable;

    /**
     * Constructs a new {@link DynamoDbInvoiceRepository}.
     *
     * @param enhancedClient the DynamoDB enhanced client
     * @param tableName      the name of the invoices table
     */
    @Inject
    public DynamoDbInvoiceRepository(
        DynamoDbEnhancedClient enhancedClient,
        @Named("invoicesTableName") String tableName) {
        this.invoiceTable = enhancedClient.table(
            tableName, TableSchema.fromBean(InvoiceDataModel.class));
    }

    @Override
    public void save(Invoice invoice) {
        LOG.debug("Saving invoice: invoiceId={}, partnerId={}",
            invoice.invoiceId(), invoice.partnerId());

        try {
            InvoiceDataModel dataModel = InvoiceDataModel.fromDomain(invoice);
            invoiceTable.putItem(dataModel);

            LOG.debug("Invoice saved: invoiceId={}", invoice.invoiceId());

        } catch (Exception e) {
            LOG.error("Failed to save invoice: invoiceId={}, error={}",
                invoice.invoiceId(), e.getMessage(), e);
            throw new RuntimeException("Failed to save invoice", e);
        }
    }

    @Override
    public Optional<Invoice> findById(String invoiceId) {
        LOG.debug("Finding invoice by id: invoiceId={}", invoiceId);

        try {
            // invoiceId is the sort key; without knowing the partnerId we must scan
            List<Invoice> results = new ArrayList<>();
            invoiceTable.scan().items().forEach(item -> {
                if (invoiceId.equals(item.getInvoiceId())) {
                    results.add(item.toDomain());
                }
            });

            Optional<Invoice> result = results.stream().findFirst();
            LOG.debug("Find invoice by id result: invoiceId={}, found={}",
                invoiceId, result.isPresent());
            return result;

        } catch (Exception e) {
            LOG.error("Failed to find invoice: invoiceId={}, error={}",
                invoiceId, e.getMessage(), e);
            throw new RuntimeException("Failed to find invoice", e);
        }
    }

    @Override
    public List<Invoice> findByPartnerId(String partnerId) {
        LOG.debug("Querying invoices by partnerId={}", partnerId);

        QueryConditional queryConditional = QueryConditional.keyEqualTo(
            Key.builder()
                .partitionValue(partnerId)
                .build()
        );

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .build();

        List<Invoice> invoices = new ArrayList<>();
        try {
            invoiceTable.query(queryRequest)
                .items()
                .forEach(item -> invoices.add(item.toDomain()));

            LOG.debug("Found {} invoices for partnerId={}", invoices.size(), partnerId);
            return invoices;

        } catch (Exception e) {
            LOG.error("Failed to query invoices: partnerId={}, error={}",
                partnerId, e.getMessage(), e);
            throw new RuntimeException("Failed to query invoices", e);
        }
    }

    @Override
    public Optional<Invoice> findByInvoiceNumber(String invoiceNumber) {
        LOG.debug("Querying invoice by invoiceNumber={}", invoiceNumber);

        DynamoDbIndex<InvoiceDataModel> index = invoiceTable.index(INVOICE_NUMBER_INDEX);

        QueryConditional queryConditional = QueryConditional.keyEqualTo(
            Key.builder()
                .partitionValue(invoiceNumber)
                .build()
        );

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .build();

        try {
            List<Invoice> results = new ArrayList<>();
            index.query(queryRequest).forEach(page ->
                page.items().forEach(item -> results.add(item.toDomain())));

            Optional<Invoice> result = results.stream().findFirst();
            LOG.debug("Find invoice by number result: invoiceNumber={}, found={}",
                invoiceNumber, result.isPresent());
            return result;

        } catch (Exception e) {
            LOG.error("Failed to query invoice by number: invoiceNumber={}, error={}",
                invoiceNumber, e.getMessage(), e);
            throw new RuntimeException("Failed to query invoice by number", e);
        }
    }
}
