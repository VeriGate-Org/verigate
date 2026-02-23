/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package verigate.partner.domain.events;

import java.time.Instant;
import java.util.UUID;

public class PartnerActivatedEvent extends PartnerEvent {

    public static final String EVENT_TYPE = "partnerActivatedEvent";

    private final String activatedBy;

    public PartnerActivatedEvent() {
        super();
        this.activatedBy = null;
    }

    public PartnerActivatedEvent(UUID partnerId, String activatedBy) {
        super(UUID.randomUUID(), EVENT_TYPE, partnerId, Instant.now(), Instant.now());
        this.activatedBy = activatedBy;
    }

    public String getActivatedBy() { return activatedBy; }
}
