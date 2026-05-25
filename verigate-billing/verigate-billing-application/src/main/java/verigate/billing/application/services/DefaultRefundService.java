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
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.billing.domain.constants.DomainConstants;
import verigate.billing.domain.enums.CreditNoteType;
import verigate.billing.domain.enums.RefundStatus;
import verigate.billing.domain.models.Payment;
import verigate.billing.domain.models.Refund;
import verigate.billing.domain.services.CreditNoteService;
import verigate.billing.domain.services.LedgerService;
import verigate.billing.domain.services.PaymentService;
import verigate.billing.domain.services.RefundService;

/**
 * Default implementation of {@link RefundService}.
 */
public class DefaultRefundService implements RefundService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultRefundService.class);

    private final RefundRepository refundRepository;
    private final PaymentService paymentService;
    private final CreditNoteService creditNoteService;
    private final LedgerService ledgerService;

    @Inject
    public DefaultRefundService(
        RefundRepository refundRepository,
        PaymentService paymentService,
        CreditNoteService creditNoteService,
        LedgerService ledgerService) {
        this.refundRepository = refundRepository;
        this.paymentService = paymentService;
        this.creditNoteService = creditNoteService;
        this.ledgerService = ledgerService;
    }

    @Override
    public Refund requestFullRefund(String paymentId, String reason) {
        Payment payment = paymentService.getPayment(paymentId)
            .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));
        return createRefund(payment, payment.amount(), reason);
    }

    @Override
    public Refund requestPartialRefund(String paymentId, BigDecimal amount, String reason) {
        Payment payment = paymentService.getPayment(paymentId)
            .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));

        if (amount.compareTo(payment.amount()) > 0) {
            throw new IllegalArgumentException("Refund amount exceeds payment amount");
        }
        return createRefund(payment, amount, reason);
    }

    @Override
    public Refund processRefund(String refundId) {
        LOG.info("Processing refund: refundId={}", refundId);

        Refund refund = refundRepository.findById(refundId)
            .orElseThrow(() -> new IllegalArgumentException("Refund not found: " + refundId));

        try {
            creditNoteService.issueCredit(
                refund.partnerId(), refund.amount(), CreditNoteType.REFUND,
                refund.invoiceId(), refund.reason(), "system");

            Refund completed = new Refund(
                refund.refundId(), refund.partnerId(), refund.paymentId(),
                refund.invoiceId(), refund.amount(), RefundStatus.COMPLETED,
                refund.providerReference(), refund.creditNoteId(),
                refund.reason(), refund.requestedAt(), Instant.now()
            );

            refundRepository.save(completed);

            try {
                ledgerService.recordRefundProcessed(
                    refund.partnerId(), refund.refundId(), refund.amount(), YearMonth.now());
            } catch (Exception e) {
                LOG.warn("Failed to record ledger entry for refund: {}", refundId, e);
            }

            LOG.info("Refund completed: refundId={}", refundId);
            return completed;

        } catch (Exception e) {
            LOG.error("Refund processing failed: refundId={}", refundId, e);

            Refund failed = new Refund(
                refund.refundId(), refund.partnerId(), refund.paymentId(),
                refund.invoiceId(), refund.amount(), RefundStatus.FAILED,
                refund.providerReference(), refund.creditNoteId(),
                refund.reason(), refund.requestedAt(), null
            );
            refundRepository.save(failed);
            return failed;
        }
    }

    @Override
    public Optional<Refund> getRefund(String refundId) {
        return refundRepository.findById(refundId);
    }

    private Refund createRefund(Payment payment, BigDecimal amount, String reason) {
        Refund refund = new Refund(
            DomainConstants.REFUND_ID_PREFIX + UUID.randomUUID(),
            payment.partnerId(),
            payment.paymentId(),
            payment.invoiceId(),
            amount,
            RefundStatus.PENDING,
            null,
            null,
            reason,
            Instant.now(),
            null
        );

        refundRepository.save(refund);
        LOG.info("Refund requested: refundId={}, amount={}", refund.refundId(), amount);
        return refund;
    }

    /**
     * Repository interface for refund persistence.
     */
    public interface RefundRepository {

        void save(Refund refund);

        Optional<Refund> findById(String refundId);
    }
}
