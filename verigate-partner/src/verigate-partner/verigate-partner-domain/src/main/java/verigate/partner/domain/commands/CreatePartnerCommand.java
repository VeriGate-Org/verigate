/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.partner.domain.commands;

import verigate.partner.domain.models.PartnerType;
import domain.commands.BaseCommand;

import java.time.Instant;
import java.util.UUID;

/**
 * Command to create a new partner in the system.
 */
public class CreatePartnerCommand extends BaseCommand {
    
    private final String partnerName;
    private final String contactEmail;
    private final PartnerType partnerType;
    
    public CreatePartnerCommand(String partnerName, String contactEmail, PartnerType partnerType) {
        super(UUID.randomUUID(), Instant.now(), "system");
        this.partnerName = partnerName;
        this.contactEmail = contactEmail;
        this.partnerType = partnerType;
    }
    
    public CreatePartnerCommand(UUID commandId, String partnerName, String contactEmail, 
                               PartnerType partnerType, Instant createdDate, String createdBy) {
        super(commandId, createdDate, createdBy);
        this.partnerName = partnerName;
        this.contactEmail = contactEmail;
        this.partnerType = partnerType;
    }
    
    public String getPartnerName() {
        return partnerName;
    }
    
    public String getContactEmail() {
        return contactEmail;
    }
    
    public PartnerType getPartnerType() {
        return partnerType;
    }
}