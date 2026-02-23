/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package verigate.partner.domain.events;

import domain.events.BaseEvent;
import java.time.Instant;
import java.util.UUID;

public abstract class PartnerEvent extends BaseEvent<Object> {

    private final UUID partnerId;

    protected PartnerEvent() {
        super();
        this.partnerId = null;
    }

    protected PartnerEvent(UUID eventId, String eventType, UUID partnerId,
                          Instant noticedDate, Instant effectedDate) {
        super(eventId, eventType, noticedDate, effectedDate, 1);
        this.partnerId = partnerId;
    }

    public UUID getPartnerId() { return partnerId; }
}
