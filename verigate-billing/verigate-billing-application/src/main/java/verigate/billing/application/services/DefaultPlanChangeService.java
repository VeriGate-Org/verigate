/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.application.services;

import com.google.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import verigate.billing.domain.constants.DomainConstants;
import verigate.billing.domain.enums.CreditNoteType;
import verigate.billing.domain.enums.PlanChangeStatus;
import verigate.billing.domain.enums.PlanChangeType;
import verigate.billing.domain.models.BillingPlan;
import verigate.billing.domain.models.PlanChange;
import verigate.billing.domain.models.ProrationCalculation;
import verigate.billing.domain.services.BillingService;
import verigate.billing.domain.services.CreditNoteService;
import verigate.billing.domain.services.PlanChangeService;

/**
 * Default implementation of {@link PlanChangeService}.
 * Handles plan upgrades (immediate) and downgrades (end of period) with proration.
 */
public class DefaultPlanChangeService implements PlanChangeService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultPlanChangeService.class);

    private final PlanChangeRepository planChangeRepository;
    private final BillingService billingService;
    private final CreditNoteService creditNoteService;

    @Inject
    public DefaultPlanChangeService(
        PlanChangeRepository planChangeRepository,
        BillingService billingService,
        CreditNoteService creditNoteService) {
        this.planChangeRepository = planChangeRepository;
        this.billingService = billingService;
        this.creditNoteService = creditNoteService;
    }

    @Override
    public ProrationCalculation calculateProration(String partnerId, String newPlanId) {
        LOG.info("Calculating proration for partnerId={}, newPlanId={}", partnerId, newPlanId);

        BillingPlan currentPlan = billingService.getBillingPlan(partnerId)
            .orElseThrow(() -> new IllegalStateException("No active plan for partner: " + partnerId));

        YearMonth currentMonth = YearMonth.now();
        int daysInMonth = currentMonth.lengthOfMonth();
        int dayOfMonth = LocalDate.now().getDayOfMonth();
        int remainingDays = daysInMonth - dayOfMonth;

        BigDecimal oldDaily = currentPlan.monthlyMinimum()
            .divide(BigDecimal.valueOf(daysInMonth), 4, RoundingMode.HALF_UP);
        BigDecimal newDaily = DomainConstants.DEFAULT_MONTHLY_MINIMUM
            .divide(BigDecimal.valueOf(daysInMonth), 4, RoundingMode.HALF_UP);

        BigDecimal credit = oldDaily.multiply(BigDecimal.valueOf(remainingDays))
            .setScale(2, RoundingMode.HALF_UP);
        BigDecimal charge = newDaily.multiply(BigDecimal.valueOf(remainingDays))
            .setScale(2, RoundingMode.HALF_UP);
        BigDecimal netAdjustment = charge.subtract(credit);

        return new ProrationCalculation(
            daysInMonth, remainingDays, oldDaily, newDaily, credit, charge, netAdjustment);
    }

    @Override
    public PlanChange requestUpgrade(String partnerId, String newPlanId, String requestedBy) {
        LOG.info("Processing upgrade for partnerId={}, newPlanId={}", partnerId, newPlanId);

        BillingPlan currentPlan = billingService.getBillingPlan(partnerId)
            .orElseThrow(() -> new IllegalStateException("No active plan for partner: " + partnerId));

        ProrationCalculation proration = calculateProration(partnerId, newPlanId);

        if (proration.creditAmount().compareTo(BigDecimal.ZERO) > 0) {
            creditNoteService.issueCredit(partnerId, proration.creditAmount(),
                CreditNoteType.PRORATION, null, "Proration credit for plan upgrade", "system");
        }

        PlanChange planChange = new PlanChange(
            DomainConstants.PLAN_CHANGE_ID_PREFIX + UUID.randomUUID(),
            partnerId,
            currentPlan.planId(),
            newPlanId,
            PlanChangeType.UPGRADE,
            PlanChangeStatus.EFFECTIVE,
            proration.creditAmount(),
            proration.chargeAmount(),
            LocalDate.now(),
            Instant.now(),
            requestedBy
        );

        planChangeRepository.save(planChange);
        LOG.info("Upgrade effective: planChangeId={}", planChange.planChangeId());
        return planChange;
    }

    @Override
    public PlanChange requestDowngrade(String partnerId, String newPlanId, String requestedBy) {
        LOG.info("Processing downgrade for partnerId={}, newPlanId={}", partnerId, newPlanId);

        BillingPlan currentPlan = billingService.getBillingPlan(partnerId)
            .orElseThrow(() -> new IllegalStateException("No active plan for partner: " + partnerId));

        YearMonth currentMonth = YearMonth.now();
        LocalDate effectiveDate = currentMonth.atEndOfMonth();

        PlanChange planChange = new PlanChange(
            DomainConstants.PLAN_CHANGE_ID_PREFIX + UUID.randomUUID(),
            partnerId,
            currentPlan.planId(),
            newPlanId,
            PlanChangeType.DOWNGRADE,
            PlanChangeStatus.PENDING,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            effectiveDate,
            Instant.now(),
            requestedBy
        );

        planChangeRepository.save(planChange);
        LOG.info("Downgrade scheduled: planChangeId={}, effectiveDate={}",
            planChange.planChangeId(), effectiveDate);
        return planChange;
    }

    @Override
    public PlanChange cancelPendingChange(String planChangeId) {
        LOG.info("Cancelling plan change: planChangeId={}", planChangeId);

        PlanChange existing = planChangeRepository.findById(planChangeId)
            .orElseThrow(() -> new IllegalArgumentException("Plan change not found: " + planChangeId));

        if (existing.status() != PlanChangeStatus.PENDING) {
            throw new IllegalStateException("Can only cancel PENDING plan changes");
        }

        PlanChange cancelled = new PlanChange(
            existing.planChangeId(),
            existing.partnerId(),
            existing.fromPlanId(),
            existing.toPlanId(),
            existing.changeType(),
            PlanChangeStatus.CANCELLED,
            existing.proratedCredit(),
            existing.proratedCharge(),
            existing.effectiveDate(),
            existing.requestedDate(),
            existing.requestedBy()
        );

        planChangeRepository.save(cancelled);
        return cancelled;
    }

    @Override
    public List<PlanChange> getPlanChangesForPartner(String partnerId) {
        return planChangeRepository.findByPartnerId(partnerId);
    }

    /**
     * Repository interface for plan change persistence.
     */
    public interface PlanChangeRepository {

        void save(PlanChange planChange);

        java.util.Optional<PlanChange> findById(String planChangeId);

        List<PlanChange> findByPartnerId(String partnerId);

        List<PlanChange> findPendingByEffectiveDate(LocalDate effectiveDate);
    }
}
