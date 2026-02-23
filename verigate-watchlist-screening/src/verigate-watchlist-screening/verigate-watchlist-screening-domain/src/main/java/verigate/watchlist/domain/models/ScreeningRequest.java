/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.models;

import java.util.Map;
import java.util.UUID;

/**
 * Represents a request for watchlist screening.
 */
public record ScreeningRequest(
    String firstName,
    String lastName,
    String dateOfBirth,
    String countryOfResidence,
    EntityType entityType,
    Map<String, String> additionalFields
) {

    public static ScreeningRequest person(String firstName, String lastName, 
                                        String dateOfBirth, String countryOfResidence) {
        return new ScreeningRequest(firstName, lastName, dateOfBirth, 
                                  countryOfResidence, EntityType.PERSON, Map.of());
    }
    
    public static ScreeningRequest company(String companyName, String countryOfIncorporation, 
                                         Map<String, String> additionalFields) {
        return new ScreeningRequest(null, companyName, null, 
                                  countryOfIncorporation, EntityType.COMPANY, additionalFields);
    }
    
    /**
     * Gets the primary name for screening (person or company name).
     */
    public String getPrimaryName() {
        return entityType == EntityType.PERSON 
            ? (firstName + " " + lastName).trim()
            : lastName; // For companies, lastName contains the company name
    }
}