/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.partner.domain.services;

import verigate.partner.domain.models.PartnerAggregateRoot;
import verigate.partner.domain.models.PartnerType;

import java.util.UUID;

/**
 * Domain service interface for partner-related business operations.
 */
public interface PartnerService {
    
    /**
     * Creates a new partner with the given details.
     * 
     * @param partnerName the name of the partner
     * @param contactEmail the contact email for the partner
     * @param partnerType the type of partner
     * @return the created partner aggregate
     */
    PartnerAggregateRoot createPartner(String partnerName, String contactEmail, PartnerType partnerType);
    
    /**
     * Retrieves a partner by their unique identifier.
     * 
     * @param partnerId the unique identifier of the partner
     * @return the partner aggregate
     * @throws IllegalArgumentException if the partner is not found
     */
    PartnerAggregateRoot getPartner(UUID partnerId);
    
    /**
     * Activates a partner account.
     * 
     * @param partnerId the unique identifier of the partner to activate
     * @return the updated partner aggregate
     */
    PartnerAggregateRoot activatePartner(UUID partnerId);
    
    /**
     * Deactivates a partner account.
     * 
     * @param partnerId the unique identifier of the partner to deactivate
     * @return the updated partner aggregate
     */
    PartnerAggregateRoot deactivatePartner(UUID partnerId);
    
    /**
     * Suspends a partner account.
     * 
     * @param partnerId the unique identifier of the partner to suspend
     * @return the updated partner aggregate
     */
    PartnerAggregateRoot suspendPartner(UUID partnerId);
}