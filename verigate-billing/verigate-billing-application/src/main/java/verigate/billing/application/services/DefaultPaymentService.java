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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.billing.domain.constants.DomainConstants;
import verigate.billing.domain.enums.InvoiceStatus;
import verigate.billing.domain.enums.PaymentMethod;
import verigate.billing.domain.enums.PaymentStatus;
import verigate.billing.domain.models.Invoice;
import verigate.billing.domain.models.Payment;
import verigate.billing.domain.services.DunningService;
import verigate.billing.domain.services.InvoiceService;
import verigate.billing.domain.services.LedgerService;
import verigate.billing.domain.services.PaymentService;

/**
 * Default implementation of {@link PaymentService}.
 * Integrates with PayFast for payment collection and ITN webhook processing.
 */
public class DefaultPaymentService implements PaymentService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultPaymentService.class);

    private final PaymentRepository paymentRepository;
    private final PayFastAdapter payFastAdapter;
    private final InvoiceService invoiceService;
    private final LedgerService ledgerService;
    private final DunningService dunningService;

    @Inject
    public DefaultPaymentService(
        PaymentRepository paymentRepository,
        PayFastAdapter payFastAdapter,
        InvoiceService invoiceService,
        LedgerService ledgerService,
        DunningService dunningService) {
        this.paymentRepository = paymentRepository;
        this.payFastAdapter = payFastAdapter;
        this.invoiceService = invoiceService;
        this.ledgerService = ledgerService;
        this.dunningService = dunningService;
    }

    @Override
    public Payment initiatePayment(String invoiceId) {
        LOG.info("Initiating payment for invoiceId={}", invoiceId);

        Invoice invoice = invoiceService.getInvoice(invoiceId)
            .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + invoiceId));

        String paymentId = DomainConstants.PAYMENT_ID_PREFIX + UUID.randomUUID();
        Instant now = Instant.now();

        Payment payment = new Payment(
            paymentId,
            invoiceId,
            invoice.partnerId(),
            invoice.total(),
            invoice.currency(),
            PaymentStatus.PENDING,
            null,
            null,
            paymentId,
            payFastAdapter.getMerchantId(),
            now,
            null,
            null,
            null
        );

        paymentRepository.save(payment);
        LOG.info("Payment initiated: paymentId={}, amount={}", paymentId, invoice.total());
        return payment;
    }

    @Override
    public Payment processItnNotification(Map<String, String> itnData) {
        LOG.info("Processing PayFast ITN notification");

        if (!payFastAdapter.validateSignature(itnData)) {
            LOG.error("Invalid PayFast signature");
            throw new SecurityException("Invalid PayFast ITN signature");
        }

        if (!payFastAdapter.validateSourceIp(itnData.get("source_ip"))) {
            LOG.error("Invalid PayFast source IP: {}", itnData.get("source_ip"));
            throw new SecurityException("Invalid PayFast source IP");
        }

        String payFastReference = itnData.get("m_payment_id");
        String payFastPaymentId = itnData.get("pf_payment_id");
        String paymentStatusStr = itnData.get("payment_status");

        Payment payment = paymentRepository.findByPayFastReference(payFastReference)
            .orElseThrow(() -> new IllegalArgumentException(
                "Payment not found for reference: " + payFastReference));

        PaymentStatus newStatus = mapPayFastStatus(paymentStatusStr);
        PaymentMethod method = parsePaymentMethod(itnData.get("payment_method"));
        Instant completedAt = newStatus == PaymentStatus.COMPLETE ? Instant.now() : null;
        String failureReason = newStatus == PaymentStatus.FAILED
            ? itnData.getOrDefault("reason", "Payment failed") : null;

        Payment updated = new Payment(
            payment.paymentId(),
            payment.invoiceId(),
            payment.partnerId(),
            payment.amount(),
            payment.currency(),
            newStatus,
            method,
            payFastPaymentId,
            payFastReference,
            payment.merchantId(),
            payment.initiatedAt(),
            completedAt,
            failureReason,
            itnData.toString()
        );

        paymentRepository.save(updated);

        if (newStatus == PaymentStatus.COMPLETE) {
            Invoice invoice = invoiceService.updateStatus(payment.invoiceId(), InvoiceStatus.PAID);
            try {
                ledgerService.recordPaymentReceived(updated, invoice);
            } catch (Exception e) {
                LOG.warn("Failed to record ledger entry for payment: {}", payment.paymentId(), e);
            }
            LOG.info("Payment completed: paymentId={}", payment.paymentId());
        } else if (newStatus == PaymentStatus.FAILED) {
            try {
                dunningService.createDunningSchedule(payment.invoiceId(), payment.partnerId());
            } catch (Exception e) {
                LOG.warn("Failed to create dunning schedule for invoice: {}", payment.invoiceId(), e);
            }
            LOG.info("Payment failed: paymentId={}, reason={}", payment.paymentId(), failureReason);
        }

        return updated;
    }

    @Override
    public Optional<Payment> getPayment(String paymentId) {
        return paymentRepository.findById(paymentId);
    }

    @Override
    public List<Payment> getPaymentsForInvoice(String invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId);
    }

    @Override
    public String generatePaymentUrl(String invoiceId) {
        Invoice invoice = invoiceService.getInvoice(invoiceId)
            .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + invoiceId));

        Payment payment = initiatePayment(invoiceId);
        return payFastAdapter.generatePaymentUrl(payment, invoice);
    }

    private PaymentStatus mapPayFastStatus(String status) {
        if (status == null) return PaymentStatus.PENDING;
        return switch (status.toUpperCase()) {
            case "COMPLETE" -> PaymentStatus.COMPLETE;
            case "FAILED" -> PaymentStatus.FAILED;
            case "CANCELLED" -> PaymentStatus.CANCELLED;
            default -> PaymentStatus.PROCESSING;
        };
    }

    private PaymentMethod parsePaymentMethod(String method) {
        if (method == null) return null;
        return switch (method.toLowerCase()) {
            case "cc" -> PaymentMethod.CREDIT_CARD;
            case "eft" -> PaymentMethod.EFT;
            case "dc" -> PaymentMethod.DEBIT_ORDER;
            default -> null;
        };
    }

    /**
     * Repository interface for payment persistence.
     */
    public interface PaymentRepository {

        void save(Payment payment);

        Optional<Payment> findById(String paymentId);

        List<Payment> findByInvoiceId(String invoiceId);

        Optional<Payment> findByPayFastReference(String reference);

        List<Payment> findByPartnerId(String partnerId);
    }

    /**
     * Adapter interface for PayFast payment gateway.
     */
    public interface PayFastAdapter {

        String getMerchantId();

        boolean validateSignature(Map<String, String> itnData);

        boolean validateSourceIp(String sourceIp);

        String generatePaymentUrl(Payment payment, Invoice invoice);
    }
}
