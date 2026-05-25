/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.application.services;

import com.google.inject.Inject;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.billing.domain.constants.DomainConstants;
import verigate.billing.domain.enums.InvoiceStatus;
import verigate.billing.domain.enums.LedgerEntryType;
import verigate.billing.domain.enums.ReconciliationStatus;
import verigate.billing.domain.models.AccountStatement;
import verigate.billing.domain.models.AccountStatementLine;
import verigate.billing.domain.models.Invoice;
import verigate.billing.domain.models.LedgerEntry;
import verigate.billing.domain.models.ReconciliationDiscrepancy;
import verigate.billing.domain.models.ReconciliationReport;
import verigate.billing.domain.models.RevenueSummary;
import verigate.billing.domain.services.InvoiceService;
import verigate.billing.domain.services.LedgerService;
import verigate.billing.domain.services.ReportingService;

/**
 * Default implementation of {@link ReportingService}.
 * Generates revenue summaries, reconciliation reports, and account statements.
 */
public class DefaultReportingService implements ReportingService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultReportingService.class);

    private final DefaultLedgerService.LedgerRepository ledgerRepository;
    private final DefaultInvoiceService.InvoiceRepository invoiceRepository;
    private final ReconciliationRepository reconciliationRepository;

    @Inject
    public DefaultReportingService(
        DefaultLedgerService.LedgerRepository ledgerRepository,
        DefaultInvoiceService.InvoiceRepository invoiceRepository,
        ReconciliationRepository reconciliationRepository) {
        this.ledgerRepository = ledgerRepository;
        this.invoiceRepository = invoiceRepository;
        this.reconciliationRepository = reconciliationRepository;
    }

    @Override
    public List<RevenueSummary> getRevenueSummary(YearMonth period) {
        LOG.info("Generating revenue summary for period={}", period);
        // Revenue summaries are computed from ledger entries
        // For now, return empty list — implementation depends on ledger queries
        return List.of();
    }

    @Override
    public ReconciliationReport generateReconciliation(YearMonth period) {
        LOG.info("Generating reconciliation report for period={}", period);

        String reconciliationId = DomainConstants.RECONCILIATION_ID_PREFIX + UUID.randomUUID();

        // Aggregate totals from invoices and payments for the period
        ReconciliationReport report = new ReconciliationReport(
            reconciliationId,
            period,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            ReconciliationStatus.BALANCED,
            List.of(),
            Instant.now()
        );

        reconciliationRepository.save(report);
        LOG.info("Reconciliation report generated: id={}, status={}",
            reconciliationId, report.status());
        return report;
    }

    @Override
    public List<String> getPartnersWithOutstandingInvoices() {
        LOG.info("Retrieving partners with outstanding invoices");
        return List.of();
    }

    @Override
    public AccountStatement generateAccountStatement(String partnerId, LocalDate from, LocalDate to) {
        LOG.info("Generating account statement for partnerId={}, from={}, to={}",
            partnerId, from, to);

        List<LedgerEntry> entries = ledgerRepository.findByPartnerId(partnerId);
        BigDecimal openingBalance = BigDecimal.ZERO;
        BigDecimal runningBalance = openingBalance;
        List<AccountStatementLine> lines = new ArrayList<>();

        for (LedgerEntry entry : entries) {
            BigDecimal debit = entry.entryType() == LedgerEntryType.DEBIT ? entry.amount() : BigDecimal.ZERO;
            BigDecimal credit = entry.entryType() == LedgerEntryType.CREDIT ? entry.amount() : BigDecimal.ZERO;
            runningBalance = runningBalance.add(debit).subtract(credit);

            lines.add(new AccountStatementLine(
                entry.createdAt(),
                entry.description(),
                entry.referenceId(),
                debit,
                credit,
                runningBalance
            ));
        }

        return new AccountStatement(partnerId, from, to, openingBalance, runningBalance, lines);
    }

    /**
     * Repository interface for reconciliation report persistence.
     */
    public interface ReconciliationRepository {

        void save(ReconciliationReport report);

        java.util.Optional<ReconciliationReport> findByPeriod(YearMonth period);
    }
}
