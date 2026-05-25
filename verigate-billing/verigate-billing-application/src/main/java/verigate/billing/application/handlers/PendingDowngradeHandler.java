/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.application.handlers;

import com.google.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.billing.domain.enums.PlanChangeStatus;
import verigate.billing.domain.models.PlanChange;
import verigate.billing.application.services.DefaultPlanChangeService;

/**
 * Daily handler that applies pending plan downgrades where effectiveDate <= today.
 */
public class PendingDowngradeHandler {

    private static final Logger LOG = LoggerFactory.getLogger(PendingDowngradeHandler.class);

    private final DefaultPlanChangeService.PlanChangeRepository planChangeRepository;

    @Inject
    public PendingDowngradeHandler(DefaultPlanChangeService.PlanChangeRepository planChangeRepository) {
        this.planChangeRepository = planChangeRepository;
    }

    public void handle() {
        LOG.info("Starting pending downgrade check");

        try {
            List<PlanChange> pendingChanges = planChangeRepository
                .findPendingByEffectiveDate(LocalDate.now());

            if (pendingChanges.isEmpty()) {
                LOG.info("No pending downgrades to apply");
                return;
            }

            LOG.info("Applying {} pending downgrades", pendingChanges.size());

            int applied = 0;
            int failed = 0;

            for (PlanChange change : pendingChanges) {
                try {
                    PlanChange effective = new PlanChange(
                        change.planChangeId(),
                        change.partnerId(),
                        change.fromPlanId(),
                        change.toPlanId(),
                        change.changeType(),
                        PlanChangeStatus.EFFECTIVE,
                        change.proratedCredit(),
                        change.proratedCharge(),
                        change.effectiveDate(),
                        change.requestedDate(),
                        change.requestedBy()
                    );
                    planChangeRepository.save(effective);
                    applied++;
                    LOG.info("Downgrade applied for partner: {}", change.partnerId());
                } catch (Exception e) {
                    failed++;
                    LOG.error("Failed to apply downgrade: {}", change.planChangeId(), e);
                }
            }

            LOG.info("Pending downgrade check completed. Applied: {}, failed: {}", applied, failed);

        } catch (Exception e) {
            LOG.error("Pending downgrade check failed", e);
            throw new RuntimeException("Pending downgrade check failed", e);
        }
    }
}
