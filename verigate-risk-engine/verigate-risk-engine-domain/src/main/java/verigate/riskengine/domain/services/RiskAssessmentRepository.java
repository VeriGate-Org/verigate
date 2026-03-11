/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.riskengine.domain.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import verigate.riskengine.domain.models.RiskAssessment;

/**
 * Repository for computed risk assessments.
 */
public interface RiskAssessmentRepository {

    void save(RiskAssessment assessment);

    Optional<RiskAssessment> findByVerificationId(UUID verificationId);

    List<RiskAssessment> findByPartnerId(String partnerId, int limit);
}
