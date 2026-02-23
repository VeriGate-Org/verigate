/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.domain.commands.outgoing;

import domain.commands.BaseCommand;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Command to initiate watchlist screening through the watchlist screening microservice.
 */
public class CreateWatchlistScreeningCommand extends BaseCommand {
    
    private final UUID verificationId;
    private final UUID stepId;
    private final String partnerId;
    private final String firstName;
    private final String lastName;
    private final String dateOfBirth;
    private final String countryOfResidence;
    private final String entityType;
    private final Map<String, Object> additionalFields;
    
    public CreateWatchlistScreeningCommand(UUID commandId, Instant timestamp, String issuedBy,
                                         UUID verificationId, UUID stepId, String partnerId,
                                         String firstName, String lastName, String dateOfBirth,
                                         String countryOfResidence, String entityType,
                                         Map<String, Object> additionalFields) {
        super(commandId, timestamp, issuedBy);
        this.verificationId = verificationId;
        this.stepId = stepId;
        this.partnerId = partnerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.countryOfResidence = countryOfResidence;
        this.entityType = entityType;
        this.additionalFields = additionalFields;
    }
    
    public UUID getVerificationId() { return verificationId; }
    public UUID getStepId() { return stepId; }
    public String getPartnerId() { return partnerId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getDateOfBirth() { return dateOfBirth; }
    public String getCountryOfResidence() { return countryOfResidence; }
    public String getEntityType() { return entityType; }
    public Map<String, Object> getAdditionalFields() { return additionalFields; }
}