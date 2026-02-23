/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package verigate.partner.domain.models;

import java.time.Instant;
import java.util.Set;

/**
 * API access configuration for a partner.
 */
public class ApiConfiguration {

    private final String apiKeyHash;
    private final String apiKeyPrefix;
    private final int rateLimitPerMinute;
    private final int rateLimitPerDay;
    private final Set<String> allowedIpAddresses;
    private final String webhookUrl;
    private final boolean webhookEnabled;
    private final Instant apiKeyCreatedAt;
    private final Instant apiKeyExpiresAt;

    public ApiConfiguration(String apiKeyHash, String apiKeyPrefix, int rateLimitPerMinute,
                           int rateLimitPerDay, Set<String> allowedIpAddresses,
                           String webhookUrl, boolean webhookEnabled,
                           Instant apiKeyCreatedAt, Instant apiKeyExpiresAt) {
        this.apiKeyHash = apiKeyHash;
        this.apiKeyPrefix = apiKeyPrefix;
        this.rateLimitPerMinute = rateLimitPerMinute;
        this.rateLimitPerDay = rateLimitPerDay;
        this.allowedIpAddresses = allowedIpAddresses != null ? Set.copyOf(allowedIpAddresses) : Set.of();
        this.webhookUrl = webhookUrl;
        this.webhookEnabled = webhookEnabled;
        this.apiKeyCreatedAt = apiKeyCreatedAt;
        this.apiKeyExpiresAt = apiKeyExpiresAt;
    }

    public static ApiConfiguration withDefaults(String apiKeyHash, String apiKeyPrefix) {
        return new ApiConfiguration(apiKeyHash, apiKeyPrefix, 60, 10000,
            Set.of(), null, false, Instant.now(), null);
    }

    public String getApiKeyHash() { return apiKeyHash; }
    public String getApiKeyPrefix() { return apiKeyPrefix; }
    public int getRateLimitPerMinute() { return rateLimitPerMinute; }
    public int getRateLimitPerDay() { return rateLimitPerDay; }
    public Set<String> getAllowedIpAddresses() { return allowedIpAddresses; }
    public String getWebhookUrl() { return webhookUrl; }
    public boolean isWebhookEnabled() { return webhookEnabled; }
    public Instant getApiKeyCreatedAt() { return apiKeyCreatedAt; }
    public Instant getApiKeyExpiresAt() { return apiKeyExpiresAt; }

    public boolean isApiKeyExpired() {
        return apiKeyExpiresAt != null && Instant.now().isAfter(apiKeyExpiresAt);
    }
}
