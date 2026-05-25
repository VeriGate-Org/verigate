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
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import verigate.billing.application.services.DefaultCreditNoteService;
import verigate.billing.domain.enums.CreditNoteStatus;
import verigate.billing.domain.models.CreditNote;
import verigate.billing.infrastructure.repositories.datamodels.CreditNoteDataModel;

/**
 * DynamoDB implementation of the credit note repository.
 * Provides read and write access to credit notes stored in the
 * {@code verigate-credit-notes} table.
 *
 * <p>Table schema:
 * <ul>
 *   <li>PK: {@code partnerId}</li>
 *   <li>SK: {@code creditNoteId}</li>
 * </ul>
 */
public class DynamoDbCreditNoteRepository
    implements DefaultCreditNoteService.CreditNoteRepository {

    private static final Logger LOG =
        LoggerFactory.getLogger(DynamoDbCreditNoteRepository.class);

    private final DynamoDbTable<CreditNoteDataModel> creditNoteTable;

    /**
     * Constructs a new {@link DynamoDbCreditNoteRepository}.
     *
     * @param enhancedClient the DynamoDB enhanced client
     * @param tableName      the name of the credit notes table
     */
    @Inject
    public DynamoDbCreditNoteRepository(
        DynamoDbEnhancedClient enhancedClient,
        @Named("creditNotesTableName") String tableName) {
        this.creditNoteTable = enhancedClient.table(
            tableName, TableSchema.fromBean(CreditNoteDataModel.class));
    }

    @Override
    public void save(CreditNote creditNote) {
        LOG.debug("Saving credit note: creditNoteId={}, partnerId={}",
            creditNote.creditNoteId(), creditNote.partnerId());

        try {
            CreditNoteDataModel dataModel = CreditNoteDataModel.fromDomain(creditNote);
            creditNoteTable.putItem(dataModel);

            LOG.debug("Credit note saved: creditNoteId={}", creditNote.creditNoteId());

        } catch (Exception e) {
            LOG.error("Failed to save credit note: creditNoteId={}, error={}",
                creditNote.creditNoteId(), e.getMessage(), e);
            throw new RuntimeException("Failed to save credit note", e);
        }
    }

    @Override
    public Optional<CreditNote> findById(String creditNoteId) {
        LOG.debug("Finding credit note by id: creditNoteId={}", creditNoteId);

        try {
            // creditNoteId is the sort key; without knowing the partnerId we must scan
            List<CreditNote> results = new ArrayList<>();
            creditNoteTable.scan().items().forEach(item -> {
                if (creditNoteId.equals(item.getCreditNoteId())) {
                    results.add(item.toDomain());
                }
            });

            Optional<CreditNote> result = results.stream().findFirst();
            LOG.debug("Find credit note by id result: creditNoteId={}, found={}",
                creditNoteId, result.isPresent());
            return result;

        } catch (Exception e) {
            LOG.error("Failed to find credit note: creditNoteId={}, error={}",
                creditNoteId, e.getMessage(), e);
            throw new RuntimeException("Failed to find credit note", e);
        }
    }

    @Override
    public List<CreditNote> findByPartnerId(String partnerId) {
        LOG.debug("Querying credit notes by partnerId={}", partnerId);

        QueryConditional queryConditional = QueryConditional.keyEqualTo(
            Key.builder()
                .partitionValue(partnerId)
                .build()
        );

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .build();

        List<CreditNote> creditNotes = new ArrayList<>();
        try {
            creditNoteTable.query(queryRequest)
                .items()
                .forEach(item -> creditNotes.add(item.toDomain()));

            LOG.debug("Found {} credit notes for partnerId={}",
                creditNotes.size(), partnerId);
            return creditNotes;

        } catch (Exception e) {
            LOG.error("Failed to query credit notes: partnerId={}, error={}",
                partnerId, e.getMessage(), e);
            throw new RuntimeException("Failed to query credit notes", e);
        }
    }

    @Override
    public List<CreditNote> findAvailableByPartnerId(String partnerId) {
        LOG.debug("Querying available credit notes by partnerId={}", partnerId);

        QueryConditional queryConditional = QueryConditional.keyEqualTo(
            Key.builder()
                .partitionValue(partnerId)
                .build()
        );

        Expression filterExpression = Expression.builder()
            .expression("#s IN (:issued, :partiallyApplied)")
            .putExpressionName("#s", "status")
            .putExpressionValue(":issued",
                AttributeValue.builder().s(CreditNoteStatus.ISSUED.name()).build())
            .putExpressionValue(":partiallyApplied",
                AttributeValue.builder().s(CreditNoteStatus.PARTIALLY_APPLIED.name()).build())
            .build();

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .filterExpression(filterExpression)
            .build();

        List<CreditNote> creditNotes = new ArrayList<>();
        try {
            creditNoteTable.query(queryRequest)
                .items()
                .forEach(item -> creditNotes.add(item.toDomain()));

            LOG.debug("Found {} available credit notes for partnerId={}",
                creditNotes.size(), partnerId);
            return creditNotes;

        } catch (Exception e) {
            LOG.error("Failed to query available credit notes: partnerId={}, error={}",
                partnerId, e.getMessage(), e);
            throw new RuntimeException("Failed to query available credit notes", e);
        }
    }
}
