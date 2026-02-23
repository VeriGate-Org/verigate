/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package verigate.partner.domain.repositories;

import verigate.partner.domain.models.PartnerConfiguration;

import java.util.Optional;

/**
 * Repository interface for Partner configuration persistence.
 */
public interface PartnerConfigurationRepository {

    Optional<PartnerConfiguration> findByPartnerId(String partnerId);

    PartnerConfiguration save(PartnerConfiguration configuration);

    boolean existsByPartnerId(String partnerId);

    boolean deleteByPartnerId(String partnerId);
}
