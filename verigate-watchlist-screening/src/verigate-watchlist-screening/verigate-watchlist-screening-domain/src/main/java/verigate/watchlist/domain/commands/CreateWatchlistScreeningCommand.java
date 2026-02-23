/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.commands;

import verigate.watchlist.domain.models.ScreeningRequest;
import java.time.Instant;
import java.util.UUID;
import domain.commands.BaseCommand;

/**
 * Command to create a new watchlist screening.
 */
public class CreateWatchlistScreeningCommand extends BaseCommand {
    
    private final UUID commandId;
    private final String partnerId;
    private final ScreeningRequest screeningRequest;
    private final UUID correlationId;
    private final Instant timestamp;
    
    /**
     * Creates a new watchlist screening command.
     * 
     * @param partnerId The partner ID requesting the screening
     * @param screeningRequest The screening request with entity details
     * @param correlationId Optional correlation ID for tracking related operations
     */
    public CreateWatchlistScreeningCommand(String partnerId, ScreeningRequest screeningRequest, UUID correlationId) {
        this.commandId = UUID.randomUUID();
        this.partnerId = partnerId;
        this.screeningRequest = screeningRequest;
        this.correlationId = correlationId;
        this.timestamp = Instant.now();
    }
    
    /**
     * Creates a new watchlist screening command with a generated correlation ID.
     * 
     * @param partnerId The partner ID requesting the screening
     * @param screeningRequest The screening request with entity details
     */
    public CreateWatchlistScreeningCommand(String partnerId, ScreeningRequest screeningRequest) {
        this(partnerId, screeningRequest, UUID.randomUUID());
    }
    
    // Getters
    public UUID getCommandId() { return commandId; }
    public String getPartnerId() { return partnerId; }
    public ScreeningRequest getScreeningRequest() { return screeningRequest; }
    public UUID getCorrelationId() { return correlationId; }
    public Instant getTimestamp() { return timestamp; }
    
    @Override
    public String toString() {
        return "CreateWatchlistScreeningCommand{" +
               "commandId=" + commandId +
               ", partnerId='" + partnerId + '\'' +
               ", timestamp=" + timestamp +
               ", screeningRequest=" + screeningRequest +
               ", correlationId=" + correlationId +
               '}';
    }
}
