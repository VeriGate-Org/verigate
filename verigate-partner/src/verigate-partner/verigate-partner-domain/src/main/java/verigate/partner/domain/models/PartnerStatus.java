/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.partner.domain.models;

/**
 * Enumeration representing the various states a partner can be in.
 */
public enum PartnerStatus {
    PENDING("Partner registration is pending approval"),
    ACTIVE("Partner is active and operational"),
    SUSPENDED("Partner is temporarily suspended"),
    DEACTIVATED("Partner has been deactivated"),
    TERMINATED("Partner relationship has been terminated");
    
    private final String description;
    
    PartnerStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}