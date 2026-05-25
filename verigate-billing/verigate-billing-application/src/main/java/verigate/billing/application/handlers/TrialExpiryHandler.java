/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.application.handlers;

import com.google.inject.Inject;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.billing.domain.models.Trial;
import verigate.billing.domain.services.TrialService;

/**
 * Daily handler that processes expired trials.
 * Converts trials with payment methods, expires those without.
 */
public class TrialExpiryHandler {

    private static final Logger LOG = LoggerFactory.getLogger(TrialExpiryHandler.class);

    private final TrialService trialService;

    @Inject
    public TrialExpiryHandler(TrialService trialService) {
        this.trialService = trialService;
    }

    public void handle() {
        LOG.info("Starting trial expiry check");

        try {
            List<Trial> expiredTrials = trialService.getExpiredTrials();

            if (expiredTrials.isEmpty()) {
                LOG.info("No expired trials found");
                return;
            }

            LOG.info("Processing {} expired trials", expiredTrials.size());

            int converted = 0;
            int cancelled = 0;

            for (Trial trial : expiredTrials) {
                try {
                    trialService.cancelTrial(trial.trialId());
                    cancelled++;
                } catch (Exception e) {
                    LOG.error("Failed to process expired trial: {}", trial.trialId(), e);
                }
            }

            LOG.info("Trial expiry check completed. Converted: {}, cancelled: {}",
                converted, cancelled);

        } catch (Exception e) {
            LOG.error("Trial expiry check failed", e);
            throw new RuntimeException("Trial expiry check failed", e);
        }
    }
}
