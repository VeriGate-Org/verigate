/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.application.services;

import com.google.inject.Inject;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.billing.domain.constants.DomainConstants;
import verigate.billing.domain.enums.CancellationType;
import verigate.billing.domain.enums.SubscriptionStatus;
import verigate.billing.domain.models.Subscription;
import verigate.billing.domain.services.SubscriptionService;

/**
 * Default implementation of {@link SubscriptionService}.
 */
public class DefaultSubscriptionService implements SubscriptionService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultSubscriptionService.class);

    private final SubscriptionRepository subscriptionRepository;

    @Inject
    public DefaultSubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public Subscription createSubscription(String partnerId, String planId) {
        LOG.info("Creating subscription for partnerId={}, planId={}", partnerId, planId);

        LocalDate periodStart = LocalDate.now();
        LocalDate periodEnd = periodStart.plusMonths(1);
        Instant now = Instant.now();

        Subscription subscription = new Subscription(
            DomainConstants.SUBSCRIPTION_ID_PREFIX + UUID.randomUUID(),
            partnerId,
            planId,
            SubscriptionStatus.ACTIVE,
            periodStart,
            periodEnd,
            null,
            null,
            null,
            now,
            now
        );

        subscriptionRepository.save(subscription);
        LOG.info("Subscription created: subscriptionId={}", subscription.subscriptionId());
        return subscription;
    }

    @Override
    public Optional<Subscription> getActiveSubscription(String partnerId) {
        return subscriptionRepository.findActiveByPartnerId(partnerId);
    }

    @Override
    public Subscription cancelSubscription(String subscriptionId, CancellationType type, String reason) {
        LOG.info("Cancelling subscription: subscriptionId={}, type={}", subscriptionId, type);

        Subscription sub = subscriptionRepository.findById(subscriptionId)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found: " + subscriptionId));

        SubscriptionStatus newStatus = type == CancellationType.IMMEDIATE
            ? SubscriptionStatus.CANCELLED : sub.status();

        Subscription cancelled = new Subscription(
            sub.subscriptionId(),
            sub.partnerId(),
            sub.planId(),
            newStatus,
            sub.currentPeriodStart(),
            sub.currentPeriodEnd(),
            Instant.now(),
            type,
            reason,
            sub.createdAt(),
            Instant.now()
        );

        subscriptionRepository.save(cancelled);
        LOG.info("Subscription cancellation recorded: subscriptionId={}", subscriptionId);
        return cancelled;
    }

    @Override
    public Subscription renewPeriod(String subscriptionId) {
        LOG.info("Renewing subscription period: subscriptionId={}", subscriptionId);

        Subscription sub = subscriptionRepository.findById(subscriptionId)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found: " + subscriptionId));

        if (sub.cancellationType() == CancellationType.END_OF_PERIOD) {
            Subscription expired = new Subscription(
                sub.subscriptionId(), sub.partnerId(), sub.planId(),
                SubscriptionStatus.CANCELLED,
                sub.currentPeriodStart(), sub.currentPeriodEnd(),
                sub.cancelledAt(), sub.cancellationType(), sub.cancelReason(),
                sub.createdAt(), Instant.now()
            );
            subscriptionRepository.save(expired);
            return expired;
        }

        LocalDate newStart = sub.currentPeriodEnd();
        LocalDate newEnd = newStart.plusMonths(1);

        Subscription renewed = new Subscription(
            sub.subscriptionId(), sub.partnerId(), sub.planId(),
            SubscriptionStatus.ACTIVE,
            newStart, newEnd,
            null, null, null,
            sub.createdAt(), Instant.now()
        );

        subscriptionRepository.save(renewed);
        LOG.info("Subscription renewed: subscriptionId={}, newEnd={}", subscriptionId, newEnd);
        return renewed;
    }

    @Override
    public Subscription updateStatus(String subscriptionId, SubscriptionStatus status) {
        Subscription sub = subscriptionRepository.findById(subscriptionId)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found: " + subscriptionId));

        Subscription updated = new Subscription(
            sub.subscriptionId(), sub.partnerId(), sub.planId(),
            status,
            sub.currentPeriodStart(), sub.currentPeriodEnd(),
            sub.cancelledAt(), sub.cancellationType(), sub.cancelReason(),
            sub.createdAt(), Instant.now()
        );

        subscriptionRepository.save(updated);
        return updated;
    }

    /**
     * Repository interface for subscription persistence.
     */
    public interface SubscriptionRepository {

        void save(Subscription subscription);

        Optional<Subscription> findById(String subscriptionId);

        Optional<Subscription> findActiveByPartnerId(String partnerId);
    }
}
