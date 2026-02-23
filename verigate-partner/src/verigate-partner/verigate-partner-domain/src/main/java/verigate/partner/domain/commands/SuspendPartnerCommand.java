/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package verigate.partner.domain.commands;

import domain.commands.BaseCommand;
import java.time.Instant;
import java.util.UUID;

public class SuspendPartnerCommand extends BaseCommand {

    private final UUID partnerId;
    private final String reason;
    private final String suspendedBy;

    public SuspendPartnerCommand(UUID partnerId, String reason, String suspendedBy) {
        super(UUID.randomUUID(), Instant.now(), suspendedBy);
        this.partnerId = partnerId;
        this.reason = reason;
        this.suspendedBy = suspendedBy;
    }

    public SuspendPartnerCommand(UUID commandId, UUID partnerId, String reason,
                                 String suspendedBy, Instant createdAt) {
        super(commandId, createdAt, suspendedBy);
        this.partnerId = partnerId;
        this.reason = reason;
        this.suspendedBy = suspendedBy;
    }

    public UUID getPartnerId() { return partnerId; }
    public String getReason() { return reason; }
    public String getSuspendedBy() { return suspendedBy; }
}
