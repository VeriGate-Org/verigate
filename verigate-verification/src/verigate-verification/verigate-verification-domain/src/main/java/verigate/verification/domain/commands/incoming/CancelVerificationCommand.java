/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.verification.domain.commands.incoming;

import domain.commands.BaseCommand;

import java.time.Instant;
import java.util.UUID;

/**
 * Command to cancel an in-progress verification flow.
 */
public class CancelVerificationCommand extends BaseCommand {
    
    private final UUID verificationId;
    private final String reason;
    
    public CancelVerificationCommand(UUID commandId, Instant timestamp, String issuedBy,
                                   UUID verificationId, String reason) {
        super(commandId, timestamp, issuedBy);
        this.verificationId = verificationId;
        this.reason = reason;
    }
    
    public UUID getVerificationId() { return verificationId; }
    public String getReason() { return reason; }
}