/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.repositories;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import verigate.billing.application.services.DefaultLedgerService;
import verigate.billing.domain.constants.DomainConstants;
import verigate.billing.domain.enums.LedgerEntryType;
import verigate.billing.domain.models.LedgerEntry;
import verigate.billing.infrastructure.repositories.datamodels.LedgerEntryDataModel;

/**
 * DynamoDB implementation of the ledger entry repository.
 * Provides read and write access to ledger entries stored in the
 * {@code verigate-ledger-entries} table.
 *
 * <p>Table schema:
 * <ul>
 *   <li>PK: {@code partnerId}</li>
 *   <li>SK: {@code period#ledgerEntryId} (e.g., {@code 2025-01#LED-uuid})</li>
 * </ul>
 */
public class DynamoDbLedgerRepository implements DefaultLedgerService.LedgerRepository {

    private static final Logger LOG =
        LoggerFactory.getLogger(DynamoDbLedgerRepository.class);

    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbTable<LedgerEntryDataModel> ledgerTable;

    /**
     * Constructs a new {@link DynamoDbLedgerRepository}.
     *
     * @param enhancedClient the DynamoDB enhanced client
     * @param tableName      the name of the ledger entries table
     */
    @Inject
    public DynamoDbLedgerRepository(
        DynamoDbEnhancedClient enhancedClient,
        @Named("ledgerEntriesTableName") String tableName) {
        this.enhancedClient = enhancedClient;
        this.ledgerTable = enhancedClient.table(
            tableName, TableSchema.fromBean(LedgerEntryDataModel.class));
    }

    @Override
    public void saveAll(List<LedgerEntry> entries) {
        LOG.debug("Saving {} ledger entries", entries.size());

        try {
            WriteBatch.Builder<LedgerEntryDataModel> batchBuilder =
                WriteBatch.builder(LedgerEntryDataModel.class)
                    .mappedTableResource(ledgerTable);

            for (LedgerEntry entry : entries) {
                LedgerEntryDataModel dataModel = LedgerEntryDataModel.fromDomain(entry);
                batchBuilder.addPutItem(dataModel);
            }

            BatchWriteItemEnhancedRequest batchRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(batchBuilder.build())
                .build();

            enhancedClient.batchWriteItem(batchRequest);

            LOG.debug("Saved {} ledger entries successfully", entries.size());

        } catch (Exception e) {
            LOG.error("Failed to save ledger entries: count={}, error={}",
                entries.size(), e.getMessage(), e);
            throw new RuntimeException("Failed to save ledger entries", e);
        }
    }

    @Override
    public List<LedgerEntry> findByPartnerId(String partnerId) {
        LOG.debug("Querying ledger entries by partnerId={}", partnerId);

        QueryConditional queryConditional = QueryConditional.keyEqualTo(
            Key.builder()
                .partitionValue(partnerId)
                .build()
        );

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .build();

        List<LedgerEntry> entries = new ArrayList<>();
        try {
            ledgerTable.query(queryRequest)
                .items()
                .forEach(item -> entries.add(item.toDomain()));

            LOG.debug("Found {} ledger entries for partnerId={}",
                entries.size(), partnerId);
            return entries;

        } catch (Exception e) {
            LOG.error("Failed to query ledger entries: partnerId={}, error={}",
                partnerId, e.getMessage(), e);
            throw new RuntimeException("Failed to query ledger entries", e);
        }
    }

    @Override
    public List<LedgerEntry> findByPartnerIdAndPeriod(String partnerId, YearMonth period) {
        LOG.debug("Querying ledger entries: partnerId={}, period={}", partnerId, period);

        String sortKeyPrefix = period.toString() + DomainConstants.SORT_KEY_SEPARATOR;

        QueryConditional queryConditional = QueryConditional.sortBeginsWith(
            Key.builder()
                .partitionValue(partnerId)
                .sortValue(sortKeyPrefix)
                .build()
        );

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .build();

        List<LedgerEntry> entries = new ArrayList<>();
        try {
            ledgerTable.query(queryRequest)
                .items()
                .forEach(item -> entries.add(item.toDomain()));

            LOG.debug("Found {} ledger entries for partnerId={}, period={}",
                entries.size(), partnerId, period);
            return entries;

        } catch (Exception e) {
            LOG.error("Failed to query ledger entries: partnerId={}, period={}, error={}",
                partnerId, period, e.getMessage(), e);
            throw new RuntimeException("Failed to query ledger entries", e);
        }
    }

    @Override
    public BigDecimal getAccountBalance(String partnerId) {
        LOG.debug("Computing account balance for partnerId={}", partnerId);

        try {
            List<LedgerEntry> entries = findByPartnerId(partnerId);

            BigDecimal balance = BigDecimal.ZERO;
            for (LedgerEntry entry : entries) {
                if (entry.entryType() == LedgerEntryType.DEBIT) {
                    balance = balance.add(entry.amount());
                } else {
                    balance = balance.subtract(entry.amount());
                }
            }

            LOG.debug("Account balance for partnerId={}: {}", partnerId, balance);
            return balance;

        } catch (Exception e) {
            LOG.error("Failed to compute account balance: partnerId={}, error={}",
                partnerId, e.getMessage(), e);
            throw new RuntimeException("Failed to compute account balance", e);
        }
    }
}
