/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.application.services;

import com.google.inject.Inject;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.billing.domain.constants.DomainConstants;
import verigate.billing.domain.enums.TrialStatus;
import verigate.billing.domain.models.Trial;
import verigate.billing.domain.services.SubscriptionService;
import verigate.billing.domain.services.TrialService;

/**
 * Default implementation of {@link TrialService}.
 */
public class DefaultTrialService implements TrialService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultTrialService.class);

    private final TrialRepository trialRepository;
    private final SubscriptionService subscriptionService;

    @Inject
    public DefaultTrialService(
        TrialRepository trialRepository,
        SubscriptionService subscriptionService) {
        this.trialRepository = trialRepository;
        this.subscriptionService = subscriptionService;
    }

    @Override
    public Trial startTrial(String partnerId, String planId) {
        LOG.info("Starting trial for partnerId={}, planId={}", partnerId, planId);

        Optional<Trial> existing = getActiveTrial(partnerId);
        if (existing.isPresent()) {
            throw new IllegalStateException("Partner already has an active trial");
        }

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(DomainConstants.DEFAULT_TRIAL_DURATION_DAYS);

        Trial trial = new Trial(
            DomainConstants.TRIAL_ID_PREFIX + UUID.randomUUID(),
            partnerId,
            planId,
            startDate,
            endDate,
            TrialStatus.ACTIVE,
            null,
            null
        );

        trialRepository.save(trial);
        LOG.info("Trial started: trialId={}, endDate={}", trial.trialId(), endDate);
        return trial;
    }

    @Override
    public Trial convertTrial(String trialId) {
        LOG.info("Converting trial: trialId={}", trialId);

        Trial trial = trialRepository.findById(trialId)
            .orElseThrow(() -> new IllegalArgumentException("Trial not found: " + trialId));

        if (trial.status() != TrialStatus.ACTIVE) {
            throw new IllegalStateException("Can only convert ACTIVE trials");
        }

        subscriptionService.createSubscription(trial.partnerId(), trial.planId());

        Trial converted = new Trial(
            trial.trialId(),
            trial.partnerId(),
            trial.planId(),
            trial.startDate(),
            trial.endDate(),
            TrialStatus.CONVERTED,
            Instant.now(),
            null
        );

        trialRepository.save(converted);
        LOG.info("Trial converted to subscription: trialId={}", trialId);
        return converted;
    }

    @Override
    public Trial cancelTrial(String trialId) {
        LOG.info("Cancelling trial: trialId={}", trialId);

        Trial trial = trialRepository.findById(trialId)
            .orElseThrow(() -> new IllegalArgumentException("Trial not found: " + trialId));

        Trial cancelled = new Trial(
            trial.trialId(),
            trial.partnerId(),
            trial.planId(),
            trial.startDate(),
            trial.endDate(),
            TrialStatus.CANCELLED,
            null,
            Instant.now()
        );

        trialRepository.save(cancelled);
        return cancelled;
    }

    @Override
    public List<Trial> getExpiredTrials() {
        return trialRepository.findExpiredActive(LocalDate.now());
    }

    @Override
    public Optional<Trial> getActiveTrial(String partnerId) {
        return trialRepository.findActiveByPartnerId(partnerId);
    }

    /**
     * Repository interface for trial persistence.
     */
    public interface TrialRepository {

        void save(Trial trial);

        Optional<Trial> findById(String trialId);

        Optional<Trial> findActiveByPartnerId(String partnerId);

        List<Trial> findExpiredActive(LocalDate asOfDate);
    }
}
