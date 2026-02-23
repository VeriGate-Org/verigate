/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package verigate.partner.domain.commands;

import domain.commands.BaseCommand;
import java.time.Instant;
import java.util.UUID;

public class ActivatePartnerCommand extends BaseCommand {

    private final UUID partnerId;
    private final String activatedBy;

    public ActivatePartnerCommand(UUID partnerId, String activatedBy) {
        super(UUID.randomUUID(), Instant.now(), activatedBy);
        this.partnerId = partnerId;
        this.activatedBy = activatedBy;
    }

    public ActivatePartnerCommand(UUID commandId, UUID partnerId, String activatedBy,
                                  Instant createdAt) {
        super(commandId, createdAt, activatedBy);
        this.partnerId = partnerId;
        this.activatedBy = activatedBy;
    }

    public UUID getPartnerId() { return partnerId; }
    public String getActivatedBy() { return activatedBy; }
}
