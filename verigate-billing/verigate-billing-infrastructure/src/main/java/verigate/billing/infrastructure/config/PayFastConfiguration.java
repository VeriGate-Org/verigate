/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.billing.infrastructure.config;

import com.google.inject.Inject;

/**
 * Configuration for PayFast payment gateway integration.
 */
public class PayFastConfiguration {

    private final String merchantId;
    private final String merchantKey;
    private final String passphrase;
    private final String baseUrl;

    @Inject
    public PayFastConfiguration() {
        this.merchantId = getEnvOrDefault(EnvironmentConstants.PAYFAST_MERCHANT_ID, "");
        this.merchantKey = getEnvOrDefault(EnvironmentConstants.PAYFAST_MERCHANT_KEY, "");
        this.passphrase = getEnvOrDefault(EnvironmentConstants.PAYFAST_PASSPHRASE, "");
        this.baseUrl = getEnvOrDefault(EnvironmentConstants.PAYFAST_BASE_URL,
            EnvironmentConstants.DEFAULT_PAYFAST_BASE_URL);
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getMerchantKey() {
        return merchantKey;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getMaskedKey() {
        if (merchantKey == null || merchantKey.length() < 4) {
            return "****";
        }
        return merchantKey.substring(0, 4) + "****";
    }

    private static String getEnvOrDefault(String envVar, String defaultValue) {
        String value = System.getenv(envVar);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }
}
