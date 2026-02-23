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
 * Command to initiate identity verification through external adapters.
 */
public class VerifyIdentityCommand extends BaseCommand {
    
    private final UUID verificationId;
    private final UUID stepId;
    private final String partnerId;
    private final String firstName;
    private final String lastName;
    private final String dateOfBirth;
    private final String idNumber;
    private final String countryOfResidence;
    private final Map<String, Object> additionalData;
    
    public VerifyIdentityCommand(UUID commandId, Instant timestamp, String issuedBy,
                               UUID verificationId, UUID stepId, String partnerId,
                               String firstName, String lastName, String dateOfBirth,
                               String idNumber, String countryOfResidence,
                               Map<String, Object> additionalData) {
        super(commandId, timestamp, issuedBy);
        this.verificationId = verificationId;
        this.stepId = stepId;
        this.partnerId = partnerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.idNumber = idNumber;
        this.countryOfResidence = countryOfResidence;
        this.additionalData = additionalData;
    }
    
    public UUID getVerificationId() { return verificationId; }
    public UUID getStepId() { return stepId; }
    public String getPartnerId() { return partnerId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getDateOfBirth() { return dateOfBirth; }
    public String getIdNumber() { return idNumber; }
    public String getCountryOfResidence() { return countryOfResidence; }
    public Map<String, Object> getAdditionalData() { return additionalData; }
}