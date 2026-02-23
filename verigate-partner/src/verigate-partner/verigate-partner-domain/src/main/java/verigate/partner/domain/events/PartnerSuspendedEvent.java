/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package verigate.partner.domain.events;

import java.time.Instant;
import java.util.UUID;

public class PartnerSuspendedEvent extends PartnerEvent {

    public static final String EVENT_TYPE = "partnerSuspendedEvent";

    private final String reason;
    private final String suspendedBy;

    public PartnerSuspendedEvent() {
        super();
        this.reason = null;
        this.suspendedBy = null;
    }

    public PartnerSuspendedEvent(UUID partnerId, String reason, String suspendedBy) {
        super(UUID.randomUUID(), EVENT_TYPE, partnerId, Instant.now(), Instant.now());
        this.reason = reason;
        this.suspendedBy = suspendedBy;
    }

    public String getReason() { return reason; }
    public String getSuspendedBy() { return suspendedBy; }
}
