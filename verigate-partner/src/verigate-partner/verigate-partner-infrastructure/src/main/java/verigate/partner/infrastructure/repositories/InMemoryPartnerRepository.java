/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.partner.infrastructure.repositories;

import verigate.partner.domain.models.PartnerAggregateRoot;
import verigate.partner.domain.repositories.PartnerRepository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of PartnerRepository for development and testing purposes.
 */
public class InMemoryPartnerRepository implements PartnerRepository {
    
    private final Map<UUID, PartnerAggregateRoot> partners = new ConcurrentHashMap<>();
    
    @Override
    public Optional<PartnerAggregateRoot> get(UUID partnerId) {
        return Optional.ofNullable(partners.get(partnerId));
    }
    
    @Override
    public PartnerAggregateRoot addOrUpdate(PartnerAggregateRoot partner) {
        partners.put(partner.getPartnerId(), partner);
        return partner;
    }
    
    @Override
    public boolean exists(UUID partnerId) {
        return partners.containsKey(partnerId);
    }
    
    @Override
    public boolean remove(UUID partnerId) {
        return partners.remove(partnerId) != null;
    }
}