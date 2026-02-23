/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package verigate.partner.domain.events;

import java.time.Instant;
import java.util.UUID;

public class ApiKeyRegeneratedEvent extends PartnerEvent {

    public static final String EVENT_TYPE = "apiKeyRegeneratedEvent";

    private final String requestedBy;
    private final String apiKeyPrefix;

    public ApiKeyRegeneratedEvent() {
        super();
        this.requestedBy = null;
        this.apiKeyPrefix = null;
    }

    public ApiKeyRegeneratedEvent(UUID partnerId, String requestedBy, String apiKeyPrefix) {
        super(UUID.randomUUID(), EVENT_TYPE, partnerId, Instant.now(), Instant.now());
        this.requestedBy = requestedBy;
        this.apiKeyPrefix = apiKeyPrefix;
    }

    public String getRequestedBy() { return requestedBy; }
    public String getApiKeyPrefix() { return apiKeyPrefix; }
}
