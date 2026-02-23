/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package verigate.partner.domain.commands;

import domain.commands.BaseCommand;
import java.time.Instant;
import java.util.UUID;

public class RegenerateApiKeyCommand extends BaseCommand {

    private final UUID partnerId;
    private final String requestedBy;

    public RegenerateApiKeyCommand(UUID partnerId, String requestedBy) {
        super(UUID.randomUUID(), Instant.now(), requestedBy);
        this.partnerId = partnerId;
        this.requestedBy = requestedBy;
    }

    public RegenerateApiKeyCommand(UUID commandId, UUID partnerId, String requestedBy,
                                   Instant createdAt) {
        super(commandId, createdAt, requestedBy);
        this.partnerId = partnerId;
        this.requestedBy = requestedBy;
    }

    public UUID getPartnerId() { return partnerId; }
    public String getRequestedBy() { return requestedBy; }
}
