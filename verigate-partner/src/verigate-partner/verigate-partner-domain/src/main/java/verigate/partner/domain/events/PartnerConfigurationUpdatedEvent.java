/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package verigate.partner.domain.events;

import java.time.Instant;
import java.util.UUID;

public class PartnerConfigurationUpdatedEvent extends PartnerEvent {

    public static final String EVENT_TYPE = "partnerConfigurationUpdatedEvent";

    private final String configurationType;
    private final String updatedBy;

    public PartnerConfigurationUpdatedEvent() {
        super();
        this.configurationType = null;
        this.updatedBy = null;
    }

    public PartnerConfigurationUpdatedEvent(UUID partnerId, String configurationType,
                                            String updatedBy) {
        super(UUID.randomUUID(), EVENT_TYPE, partnerId, Instant.now(), Instant.now());
        this.configurationType = configurationType;
        this.updatedBy = updatedBy;
    }

    public String getConfigurationType() { return configurationType; }
    public String getUpdatedBy() { return updatedBy; }
}
