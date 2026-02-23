/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package verigate.partner.domain.models;

/**
 * Full resolved configuration composite for a partner.
 * Combines verification flow, API, and billing configuration.
 */
public class PartnerConfiguration {

    private final String partnerId;
    private final VerificationFlowConfiguration verificationFlowConfiguration;
    private final ApiConfiguration apiConfiguration;
    private final BillingConfiguration billingConfiguration;

    public PartnerConfiguration(String partnerId,
                               VerificationFlowConfiguration verificationFlowConfiguration,
                               ApiConfiguration apiConfiguration,
                               BillingConfiguration billingConfiguration) {
        this.partnerId = partnerId;
        this.verificationFlowConfiguration = verificationFlowConfiguration;
        this.apiConfiguration = apiConfiguration;
        this.billingConfiguration = billingConfiguration;
    }

    public static PartnerConfiguration withDefaults(String partnerId) {
        return new PartnerConfiguration(
            partnerId,
            VerificationFlowConfiguration.defaultConfiguration(),
            null,
            BillingConfiguration.defaultConfiguration()
        );
    }

    public String getPartnerId() { return partnerId; }
    public VerificationFlowConfiguration getVerificationFlowConfiguration() { return verificationFlowConfiguration; }
    public ApiConfiguration getApiConfiguration() { return apiConfiguration; }
    public BillingConfiguration getBillingConfiguration() { return billingConfiguration; }

    public boolean hasApiConfiguration() {
        return apiConfiguration != null;
    }

    public boolean hasBillingConfiguration() {
        return billingConfiguration != null;
    }
}
