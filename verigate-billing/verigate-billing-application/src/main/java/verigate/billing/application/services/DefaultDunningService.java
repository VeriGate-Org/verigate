/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.application.services;

import com.google.inject.Inject;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.billing.domain.constants.DomainConstants;
import verigate.billing.domain.enums.DunningStatus;
import verigate.billing.domain.enums.SubscriptionStatus;
import verigate.billing.domain.models.DunningAttempt;
import verigate.billing.domain.models.DunningSchedule;
import verigate.billing.domain.services.DunningService;
import verigate.billing.domain.services.PaymentService;
import verigate.billing.domain.services.SubscriptionService;

/**
 * Default implementation of {@link DunningService}.
 * Retry schedule: Day 1, 3, 7, 14.
 * Account status progression: retry 1 → PAST_DUE, retry 3 → SUSPENDED, retry 4 exhausted → CANCELLED.
 */
public class DefaultDunningService implements DunningService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDunningService.class);

    private final DunningRepository dunningRepository;
    private final PaymentService paymentService;
    private final SubscriptionService subscriptionService;

    @Inject
    public DefaultDunningService(
        DunningRepository dunningRepository,
        PaymentService paymentService,
        SubscriptionService subscriptionService) {
        this.dunningRepository = dunningRepository;
        this.paymentService = paymentService;
        this.subscriptionService = subscriptionService;
    }

    @Override
    public DunningSchedule createDunningSchedule(String invoiceId, String partnerId) {
        LOG.info("Creating dunning schedule for invoiceId={}, partnerId={}", invoiceId, partnerId);

        Optional<DunningSchedule> existing = getDunningForInvoice(invoiceId);
        if (existing.isPresent() && existing.get().status() == DunningStatus.ACTIVE) {
            LOG.info("Active dunning schedule already exists for invoiceId={}", invoiceId);
            return existing.get();
        }

        LocalDate nextRetry = LocalDate.now().plusDays(DomainConstants.DUNNING_RETRY_DAYS[0]);

        DunningSchedule schedule = new DunningSchedule(
            DomainConstants.DUNNING_ID_PREFIX + UUID.randomUUID(),
            partnerId,
            invoiceId,
            DunningStatus.ACTIVE,
            0,
            DomainConstants.DUNNING_MAX_RETRIES,
            nextRetry,
            List.of(),
            Instant.now()
        );

        dunningRepository.save(schedule);
        LOG.info("Dunning schedule created: dunningId={}, nextRetry={}",
            schedule.dunningId(), nextRetry);
        return schedule;
    }

    @Override
    public DunningSchedule processRetry(String dunningId) {
        LOG.info("Processing dunning retry: dunningId={}", dunningId);

        DunningSchedule schedule = dunningRepository.findById(dunningId)
            .orElseThrow(() -> new IllegalArgumentException("Dunning schedule not found: " + dunningId));

        int attemptNumber = schedule.retryCount() + 1;
        boolean successful = false;
        String failureReason = null;
        String providerRef = null;

        try {
            var payment = paymentService.initiatePayment(schedule.invoiceId());
            providerRef = payment.paymentId();
            successful = true;
        } catch (Exception e) {
            failureReason = e.getMessage();
            LOG.warn("Dunning retry failed: dunningId={}, attempt={}", dunningId, attemptNumber, e);
        }

        DunningAttempt attempt = new DunningAttempt(
            attemptNumber, Instant.now(), successful, failureReason, providerRef);

        List<DunningAttempt> attempts = new ArrayList<>(schedule.attempts());
        attempts.add(attempt);

        DunningStatus newStatus;
        LocalDate nextRetry;

        if (successful) {
            newStatus = DunningStatus.COMPLETED;
            nextRetry = null;
        } else if (attemptNumber >= schedule.maxRetries()) {
            newStatus = DunningStatus.EXHAUSTED;
            nextRetry = null;
            updateAccountStatus(schedule.partnerId(), SubscriptionStatus.CANCELLED);
        } else {
            newStatus = DunningStatus.ACTIVE;
            nextRetry = LocalDate.now().plusDays(
                DomainConstants.DUNNING_RETRY_DAYS[Math.min(attemptNumber,
                    DomainConstants.DUNNING_RETRY_DAYS.length - 1)]);

            if (attemptNumber == 1) {
                updateAccountStatus(schedule.partnerId(), SubscriptionStatus.PAST_DUE);
            } else if (attemptNumber >= 3) {
                updateAccountStatus(schedule.partnerId(), SubscriptionStatus.SUSPENDED);
            }
        }

        DunningSchedule updated = new DunningSchedule(
            schedule.dunningId(),
            schedule.partnerId(),
            schedule.invoiceId(),
            newStatus,
            attemptNumber,
            schedule.maxRetries(),
            nextRetry,
            attempts,
            schedule.createdAt()
        );

        dunningRepository.save(updated);
        LOG.info("Dunning retry processed: dunningId={}, attempt={}, successful={}, newStatus={}",
            dunningId, attemptNumber, successful, newStatus);
        return updated;
    }

    @Override
    public List<DunningSchedule> getDueRetries() {
        return dunningRepository.findDueRetries(LocalDate.now());
    }

    @Override
    public DunningSchedule cancelDunning(String dunningId) {
        DunningSchedule schedule = dunningRepository.findById(dunningId)
            .orElseThrow(() -> new IllegalArgumentException("Dunning schedule not found: " + dunningId));

        DunningSchedule cancelled = new DunningSchedule(
            schedule.dunningId(), schedule.partnerId(), schedule.invoiceId(),
            DunningStatus.CANCELLED,
            schedule.retryCount(), schedule.maxRetries(), null,
            schedule.attempts(), schedule.createdAt()
        );

        dunningRepository.save(cancelled);
        return cancelled;
    }

    @Override
    public Optional<DunningSchedule> getDunningForInvoice(String invoiceId) {
        return dunningRepository.findByInvoiceId(invoiceId);
    }

    private void updateAccountStatus(String partnerId, SubscriptionStatus status) {
        try {
            subscriptionService.getActiveSubscription(partnerId)
                .ifPresent(sub -> subscriptionService.updateStatus(sub.subscriptionId(), status));
        } catch (Exception e) {
            LOG.warn("Failed to update subscription status for partner: {}", partnerId, e);
        }
    }

    /**
     * Repository interface for dunning schedule persistence.
     */
    public interface DunningRepository {

        void save(DunningSchedule schedule);

        Optional<DunningSchedule> findById(String dunningId);

        Optional<DunningSchedule> findByInvoiceId(String invoiceId);

        List<DunningSchedule> findDueRetries(LocalDate asOfDate);
    }
}
