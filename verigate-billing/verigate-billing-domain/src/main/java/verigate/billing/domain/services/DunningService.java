/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.services;

import java.util.List;
import java.util.Optional;
import verigate.billing.domain.models.DunningSchedule;

/**
 * Service for managing payment retry (dunning) schedules.
 */
public interface DunningService {

    DunningSchedule createDunningSchedule(String invoiceId, String partnerId);

    DunningSchedule processRetry(String dunningId);

    List<DunningSchedule> getDueRetries();

    DunningSchedule cancelDunning(String dunningId);

    Optional<DunningSchedule> getDunningForInvoice(String invoiceId);
}
