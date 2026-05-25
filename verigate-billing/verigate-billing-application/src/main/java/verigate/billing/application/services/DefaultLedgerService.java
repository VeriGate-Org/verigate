/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.application.services;

import com.google.inject.Inject;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.billing.domain.constants.DomainConstants;
import verigate.billing.domain.enums.FinancialEventType;
import verigate.billing.domain.enums.LedgerAccountType;
import verigate.billing.domain.enums.LedgerEntryType;
import verigate.billing.domain.models.Invoice;
import verigate.billing.domain.models.LedgerEntry;
import verigate.billing.domain.models.Payment;
import verigate.billing.domain.services.LedgerService;

/**
 * Default implementation of {@link LedgerService}.
 * Implements double-entry accounting for all financial events.
 */
public class DefaultLedgerService implements LedgerService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultLedgerService.class);

    private final LedgerRepository ledgerRepository;

    @Inject
    public DefaultLedgerService(LedgerRepository ledgerRepository) {
        this.ledgerRepository = ledgerRepository;
    }

    @Override
    public void recordInvoiceIssued(Invoice invoice) {
        LOG.info("Recording ledger entries for invoice: {}", invoice.invoiceId());
        Instant now = Instant.now();
        YearMonth period = invoice.billingPeriod();

        // DR Accounts Receivable
        LedgerEntry debit = createEntry(
            invoice.partnerId(), LedgerAccountType.ACCOUNTS_RECEIVABLE, LedgerEntryType.DEBIT,
            invoice.total(), FinancialEventType.INVOICE_ISSUED,
            invoice.invoiceId(), "Invoice", "Invoice issued: " + invoice.invoiceNumber(),
            period, now);

        // CR Revenue (subtotal)
        LedgerEntry creditRevenue = createEntry(
            invoice.partnerId(), LedgerAccountType.REVENUE, LedgerEntryType.CREDIT,
            invoice.subtotal(), FinancialEventType.INVOICE_ISSUED,
            invoice.invoiceId(), "Invoice", "Revenue: " + invoice.invoiceNumber(),
            period, now);

        // CR VAT Liability (VAT amount)
        if (invoice.vatAmount().compareTo(BigDecimal.ZERO) > 0) {
            LedgerEntry creditVat = createEntry(
                invoice.partnerId(), LedgerAccountType.VAT_LIABILITY, LedgerEntryType.CREDIT,
                invoice.vatAmount(), FinancialEventType.INVOICE_ISSUED,
                invoice.invoiceId(), "Invoice", "VAT liability: " + invoice.invoiceNumber(),
                period, now);
            ledgerRepository.saveAll(List.of(debit, creditRevenue, creditVat));
        } else {
            ledgerRepository.saveAll(List.of(debit, creditRevenue));
        }
    }

    @Override
    public void recordPaymentReceived(Payment payment, Invoice invoice) {
        LOG.info("Recording ledger entries for payment: {}", payment.paymentId());
        Instant now = Instant.now();
        YearMonth period = invoice.billingPeriod();

        // DR Cash
        LedgerEntry debitCash = createEntry(
            payment.partnerId(), LedgerAccountType.CASH, LedgerEntryType.DEBIT,
            payment.amount(), FinancialEventType.PAYMENT_RECEIVED,
            payment.paymentId(), "Payment", "Payment received: " + payment.paymentId(),
            period, now);

        // CR Accounts Receivable
        LedgerEntry creditAR = createEntry(
            payment.partnerId(), LedgerAccountType.ACCOUNTS_RECEIVABLE, LedgerEntryType.CREDIT,
            payment.amount(), FinancialEventType.PAYMENT_RECEIVED,
            payment.paymentId(), "Payment", "AR cleared: " + invoice.invoiceNumber(),
            period, now);

        ledgerRepository.saveAll(List.of(debitCash, creditAR));
    }

    @Override
    public void recordCreditApplied(String partnerId, String creditNoteId, String invoiceId,
                                    BigDecimal amount, YearMonth period) {
        LOG.info("Recording ledger entries for credit: {}", creditNoteId);
        Instant now = Instant.now();

        // DR Credit Notes
        LedgerEntry debitCN = createEntry(
            partnerId, LedgerAccountType.CREDIT_NOTES, LedgerEntryType.DEBIT,
            amount, FinancialEventType.CREDIT_APPLIED,
            creditNoteId, "CreditNote", "Credit applied to " + invoiceId,
            period, now);

        // CR Accounts Receivable
        LedgerEntry creditAR = createEntry(
            partnerId, LedgerAccountType.ACCOUNTS_RECEIVABLE, LedgerEntryType.CREDIT,
            amount, FinancialEventType.CREDIT_APPLIED,
            creditNoteId, "CreditNote", "AR reduced by credit " + creditNoteId,
            period, now);

        ledgerRepository.saveAll(List.of(debitCN, creditAR));
    }

    @Override
    public void recordRefundProcessed(String partnerId, String refundId, BigDecimal amount,
                                      YearMonth period) {
        LOG.info("Recording ledger entries for refund: {}", refundId);
        Instant now = Instant.now();

        // DR Revenue
        LedgerEntry debitRevenue = createEntry(
            partnerId, LedgerAccountType.REVENUE, LedgerEntryType.DEBIT,
            amount, FinancialEventType.REFUND_PROCESSED,
            refundId, "Refund", "Revenue reversed for refund " + refundId,
            period, now);

        // CR Cash
        LedgerEntry creditCash = createEntry(
            partnerId, LedgerAccountType.CASH, LedgerEntryType.CREDIT,
            amount, FinancialEventType.REFUND_PROCESSED,
            refundId, "Refund", "Cash refunded: " + refundId,
            period, now);

        ledgerRepository.saveAll(List.of(debitRevenue, creditCash));
    }

    @Override
    public void recordInvoiceVoided(Invoice invoice) {
        LOG.info("Recording ledger entries for voided invoice: {}", invoice.invoiceId());
        Instant now = Instant.now();
        YearMonth period = invoice.billingPeriod();

        // Reverse the original invoice entries
        LedgerEntry creditAR = createEntry(
            invoice.partnerId(), LedgerAccountType.ACCOUNTS_RECEIVABLE, LedgerEntryType.CREDIT,
            invoice.total(), FinancialEventType.INVOICE_VOIDED,
            invoice.invoiceId(), "Invoice", "Invoice voided: " + invoice.invoiceNumber(),
            period, now);

        LedgerEntry debitRevenue = createEntry(
            invoice.partnerId(), LedgerAccountType.REVENUE, LedgerEntryType.DEBIT,
            invoice.subtotal(), FinancialEventType.INVOICE_VOIDED,
            invoice.invoiceId(), "Invoice", "Revenue reversed: " + invoice.invoiceNumber(),
            period, now);

        ledgerRepository.saveAll(List.of(creditAR, debitRevenue));
    }

    @Override
    public BigDecimal getAccountBalance(String partnerId) {
        return ledgerRepository.getAccountBalance(partnerId);
    }

    private LedgerEntry createEntry(
        String partnerId, LedgerAccountType account, LedgerEntryType entryType,
        BigDecimal amount, FinancialEventType eventType, String referenceId,
        String referenceType, String description, YearMonth period, Instant createdAt) {

        return new LedgerEntry(
            DomainConstants.LEDGER_ENTRY_ID_PREFIX + UUID.randomUUID(),
            partnerId, account, entryType, amount, eventType,
            referenceId, referenceType, description, period, createdAt);
    }

    /**
     * Repository interface for ledger entry persistence.
     */
    public interface LedgerRepository {

        void saveAll(List<LedgerEntry> entries);

        List<LedgerEntry> findByPartnerId(String partnerId);

        List<LedgerEntry> findByPartnerIdAndPeriod(String partnerId, YearMonth period);

        BigDecimal getAccountBalance(String partnerId);
    }
}
