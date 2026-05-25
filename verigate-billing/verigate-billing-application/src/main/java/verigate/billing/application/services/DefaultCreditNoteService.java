/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.application.services;

import com.google.inject.Inject;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.billing.domain.constants.DomainConstants;
import verigate.billing.domain.enums.CreditNoteStatus;
import verigate.billing.domain.enums.CreditNoteType;
import verigate.billing.domain.models.CreditNote;
import verigate.billing.domain.services.CreditNoteService;

/**
 * Default implementation of {@link CreditNoteService}.
 * Manages credit notes with FIFO application (oldest credits applied first).
 */
public class DefaultCreditNoteService implements CreditNoteService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultCreditNoteService.class);

    private final CreditNoteRepository creditNoteRepository;

    @Inject
    public DefaultCreditNoteService(CreditNoteRepository creditNoteRepository) {
        this.creditNoteRepository = creditNoteRepository;
    }

    @Override
    public CreditNote issueCredit(String partnerId, BigDecimal amount, CreditNoteType type,
                                  String referenceInvoiceId, String reason, String issuedBy) {
        LOG.info("Issuing credit note: partnerId={}, amount={}, type={}", partnerId, amount, type);

        String creditNoteId = DomainConstants.CREDIT_NOTE_ID_PREFIX + UUID.randomUUID();

        CreditNote creditNote = new CreditNote(
            creditNoteId,
            partnerId,
            generateCreditNoteNumber(),
            amount,
            BigDecimal.ZERO,
            amount,
            type,
            CreditNoteStatus.ISSUED,
            referenceInvoiceId,
            null,
            reason,
            issuedBy,
            Instant.now(),
            null
        );

        creditNoteRepository.save(creditNote);
        LOG.info("Credit note issued: creditNoteId={}, amount={}", creditNoteId, amount);
        return creditNote;
    }

    @Override
    public BigDecimal getCreditBalance(String partnerId) {
        List<CreditNote> available = getAvailableCredits(partnerId);
        return available.stream()
            .map(CreditNote::remainingAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public CreditNote applyCredit(String creditNoteId, String invoiceId, BigDecimal amount) {
        LOG.info("Applying credit: creditNoteId={}, invoiceId={}, amount={}",
            creditNoteId, invoiceId, amount);

        CreditNote credit = creditNoteRepository.findById(creditNoteId)
            .orElseThrow(() -> new IllegalArgumentException("Credit note not found: " + creditNoteId));

        if (amount.compareTo(credit.remainingAmount()) > 0) {
            throw new IllegalArgumentException("Amount exceeds remaining credit");
        }

        BigDecimal newApplied = credit.appliedAmount().add(amount);
        BigDecimal newRemaining = credit.amount().subtract(newApplied);
        CreditNoteStatus newStatus = newRemaining.compareTo(BigDecimal.ZERO) == 0
            ? CreditNoteStatus.APPLIED : CreditNoteStatus.PARTIALLY_APPLIED;

        CreditNote updated = new CreditNote(
            credit.creditNoteId(),
            credit.partnerId(),
            credit.creditNoteNumber(),
            credit.amount(),
            newApplied,
            newRemaining,
            credit.type(),
            newStatus,
            credit.referenceInvoiceId(),
            invoiceId,
            credit.reason(),
            credit.issuedBy(),
            credit.issuedAt(),
            Instant.now()
        );

        creditNoteRepository.save(updated);
        LOG.info("Credit applied: creditNoteId={}, applied={}, remaining={}",
            creditNoteId, amount, newRemaining);
        return updated;
    }

    @Override
    public List<CreditNote> getAvailableCredits(String partnerId) {
        return creditNoteRepository.findAvailableByPartnerId(partnerId);
    }

    @Override
    public List<CreditNote> getCreditHistory(String partnerId) {
        return creditNoteRepository.findByPartnerId(partnerId);
    }

    private String generateCreditNoteNumber() {
        return "CRN-" + System.currentTimeMillis();
    }

    /**
     * Repository interface for credit note persistence.
     */
    public interface CreditNoteRepository {

        void save(CreditNote creditNote);

        java.util.Optional<CreditNote> findById(String creditNoteId);

        List<CreditNote> findByPartnerId(String partnerId);

        List<CreditNote> findAvailableByPartnerId(String partnerId);
    }
}
