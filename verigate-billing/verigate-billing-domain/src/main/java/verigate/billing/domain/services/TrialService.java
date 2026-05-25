/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.domain.services;

import java.util.List;
import java.util.Optional;
import verigate.billing.domain.models.Trial;

/**
 * Service for managing partner trial periods.
 */
public interface TrialService {

    Trial startTrial(String partnerId, String planId);

    Trial convertTrial(String trialId);

    Trial cancelTrial(String trialId);

    List<Trial> getExpiredTrials();

    Optional<Trial> getActiveTrial(String partnerId);
}
