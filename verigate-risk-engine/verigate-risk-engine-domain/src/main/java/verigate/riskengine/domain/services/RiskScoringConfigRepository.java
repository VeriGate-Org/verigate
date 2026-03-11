/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.domain.services;

import java.util.Optional;
import verigate.riskengine.domain.models.RiskScoringConfig;

/**
 * Repository for partner risk scoring configurations.
 */
public interface RiskScoringConfigRepository {

    Optional<RiskScoringConfig> findByPartnerId(String partnerId);

    void save(RiskScoringConfig config);
}
