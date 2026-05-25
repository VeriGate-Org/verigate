/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.services;

import java.util.List;
import verigate.billing.domain.models.PlanChange;
import verigate.billing.domain.models.ProrationCalculation;

/**
 * Service for managing billing plan changes with proration.
 */
public interface PlanChangeService {

    ProrationCalculation calculateProration(String partnerId, String newPlanId);

    PlanChange requestUpgrade(String partnerId, String newPlanId, String requestedBy);

    PlanChange requestDowngrade(String partnerId, String newPlanId, String requestedBy);

    PlanChange cancelPendingChange(String planChangeId);

    List<PlanChange> getPlanChangesForPartner(String partnerId);
}
