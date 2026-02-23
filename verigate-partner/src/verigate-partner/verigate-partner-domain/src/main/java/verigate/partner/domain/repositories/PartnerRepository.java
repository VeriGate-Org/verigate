/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.partner.domain.repositories;

import verigate.partner.domain.models.PartnerAggregateRoot;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Partner aggregate persistence operations.
 */
public interface PartnerRepository {
    
    /**
     * Retrieves a partner by their unique identifier.
     * 
     * @param partnerId the unique identifier of the partner
     * @return an Optional containing the partner if found, empty otherwise
     */
    Optional<PartnerAggregateRoot> get(UUID partnerId);
    
    /**
     * Adds a new partner or updates an existing one.
     * 
     * @param partner the partner aggregate to persist
     * @return the persisted partner aggregate
     */
    PartnerAggregateRoot addOrUpdate(PartnerAggregateRoot partner);
    
    /**
     * Checks if a partner exists with the given identifier.
     * 
     * @param partnerId the unique identifier of the partner
     * @return true if the partner exists, false otherwise
     */
    boolean exists(UUID partnerId);
    
    /**
     * Removes a partner from the repository.
     * 
     * @param partnerId the unique identifier of the partner to remove
     * @return true if the partner was removed, false if it didn't exist
     */
    boolean remove(UUID partnerId);
}