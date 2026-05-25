/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.repositories;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.time.YearMonth;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeAction;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.ReturnValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemResponse;
import verigate.billing.application.services.DefaultInvoiceService;

/**
 * DynamoDB implementation of the invoice number generator.
 * Uses atomic counters via {@code UpdateItem} with {@code ADD} to ensure
 * globally unique, sequential invoice numbers per billing period.
 *
 * <p>Table schema:
 * <ul>
 *   <li>PK: {@code sequenceKey} (format: {@code INVOICE#YYYY-MM})</li>
 *   <li>Attribute: {@code currentValue} (numeric counter)</li>
 * </ul>
 *
 * <p>Generated format: {@code INV-YYYYMM-NNNN} (e.g., {@code INV-202501-0001}).
 */
public class DynamoDbInvoiceSequenceRepository
    implements DefaultInvoiceService.InvoiceNumberGenerator {

    private static final Logger LOG =
        LoggerFactory.getLogger(DynamoDbInvoiceSequenceRepository.class);

    private static final String SEQUENCE_KEY_PREFIX = "INVOICE#";
    private static final String PARTITION_KEY_NAME = "sequenceKey";
    private static final String COUNTER_ATTRIBUTE = "currentValue";

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;

    /**
     * Constructs a new {@link DynamoDbInvoiceSequenceRepository}.
     *
     * @param dynamoDbClient the low-level DynamoDB client
     * @param tableName      the name of the invoice sequences table
     */
    @Inject
    public DynamoDbInvoiceSequenceRepository(
        DynamoDbClient dynamoDbClient,
        @Named("invoiceSequencesTableName") String tableName) {
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
    }

    @Override
    public String generateNextNumber(YearMonth period) {
        LOG.debug("Generating next invoice number for period={}", period);

        String sequenceKey = SEQUENCE_KEY_PREFIX + period.toString();

        try {
            UpdateItemRequest updateRequest = UpdateItemRequest.builder()
                .tableName(tableName)
                .key(Map.of(
                    PARTITION_KEY_NAME,
                    AttributeValue.builder().s(sequenceKey).build()
                ))
                .attributeUpdates(Map.of(
                    COUNTER_ATTRIBUTE,
                    AttributeValueUpdate.builder()
                        .value(AttributeValue.builder().n("1").build())
                        .action(AttributeAction.ADD)
                        .build()
                ))
                .returnValues(ReturnValue.UPDATED_NEW)
                .build();

            UpdateItemResponse response = dynamoDbClient.updateItem(updateRequest);

            long sequenceNumber = Long.parseLong(
                response.attributes().get(COUNTER_ATTRIBUTE).n());

            String yearMonth = String.format("%d%02d",
                period.getYear(), period.getMonthValue());
            String invoiceNumber = String.format("INV-%s-%04d", yearMonth, sequenceNumber);

            LOG.debug("Generated invoice number: {} for period={}", invoiceNumber, period);
            return invoiceNumber;

        } catch (Exception e) {
            LOG.error("Failed to generate invoice number: period={}, error={}",
                period, e.getMessage(), e);
            throw new RuntimeException("Failed to generate invoice number", e);
        }
    }
}
