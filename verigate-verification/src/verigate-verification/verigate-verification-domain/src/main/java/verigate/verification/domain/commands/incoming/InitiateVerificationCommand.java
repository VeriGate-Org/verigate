/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.domain.commands.incoming;

import domain.commands.BaseCommand;
import verigate.verification.domain.models.VerificationType;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Command to initiate a new verification flow.
 */
public class InitiateVerificationCommand extends BaseCommand {
    
    private final String partnerId;
    private final String verificationRequestId;
    private final VerificationType verificationType;
    private final Map<String, Object> metadata;
    
    public InitiateVerificationCommand(UUID commandId, Instant timestamp, String issuedBy,
                                     String partnerId, String verificationRequestId, 
                                     VerificationType verificationType, Map<String, Object> metadata) {
        super(commandId, timestamp, issuedBy);
        this.partnerId = partnerId;
        this.verificationRequestId = verificationRequestId;
        this.verificationType = verificationType;
        this.metadata = metadata;
    }
    
    public String getPartnerId() { return partnerId; }
    public String getVerificationRequestId() { return verificationRequestId; }
    public VerificationType getVerificationType() { return verificationType; }
    public Map<String, Object> getMetadata() { return metadata; }
}