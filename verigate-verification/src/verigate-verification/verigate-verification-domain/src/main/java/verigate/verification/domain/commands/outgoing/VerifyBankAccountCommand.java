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
 * Command to initiate bank account verification through external adapters.
 */
public class VerifyBankAccountCommand extends BaseCommand {
    
    private final UUID verificationId;
    private final UUID stepId;
    private final String partnerId;
    private final String accountNumber;
    private final String routingNumber;
    private final String accountHolderName;
    private final Map<String, Object> additionalData;
    
    public VerifyBankAccountCommand(UUID commandId, Instant timestamp, String issuedBy,
                                 UUID verificationId, UUID stepId, String partnerId,
                                 String accountNumber, String routingNumber, String accountHolderName,
                                 Map<String, Object> additionalData) {
        super(commandId, timestamp, issuedBy);
        this.verificationId = verificationId;
        this.stepId = stepId;
        this.partnerId = partnerId;
        this.accountNumber = accountNumber;
        this.routingNumber = routingNumber;
        this.accountHolderName = accountHolderName;
        this.additionalData = additionalData;
    }
    
    public UUID getVerificationId() { return verificationId; }
    public UUID getStepId() { return stepId; }
    public String getPartnerId() { return partnerId; }
    public String getAccountNumber() { return accountNumber; }
    public String getRoutingNumber() { return routingNumber; }
    public String getAccountHolderName() { return accountHolderName; }
    public Map<String, Object> getAdditionalData() { return additionalData; }
}