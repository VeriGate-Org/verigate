/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.partner.domain.models;

/**
 * Enumeration representing different types of partners.
 */
public enum PartnerType {
    FINANCIAL_INSTITUTION("Financial institutions and banks"),
    INSURANCE_COMPANY("Insurance companies and providers"),
    FINTECH("Financial technology companies"),
    REGULATORY_BODY("Regulatory and compliance organizations"),
    TECHNOLOGY_PARTNER("Technology integration partners"),
    CONSULTING_FIRM("Professional services and consulting firms");
    
    private final String description;
    
    PartnerType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}