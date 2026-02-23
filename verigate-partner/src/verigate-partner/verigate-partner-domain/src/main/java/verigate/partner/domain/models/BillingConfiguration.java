/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package verigate.partner.domain.models;

import java.util.Map;

/**
 * Billing configuration for a partner including pricing per verification type.
 */
public class BillingConfiguration {

    private final String billingPlan;
    private final Map<String, Double> pricePerVerificationType;
    private final String currency;
    private final String billingEmail;
    private final boolean invoicingEnabled;

    public BillingConfiguration(String billingPlan, Map<String, Double> pricePerVerificationType,
                               String currency, String billingEmail, boolean invoicingEnabled) {
        this.billingPlan = billingPlan;
        this.pricePerVerificationType = pricePerVerificationType != null ? Map.copyOf(pricePerVerificationType) : Map.of();
        this.currency = currency;
        this.billingEmail = billingEmail;
        this.invoicingEnabled = invoicingEnabled;
    }

    public static BillingConfiguration defaultConfiguration() {
        return new BillingConfiguration("STANDARD", Map.of(), "ZAR", null, false);
    }

    public String getBillingPlan() { return billingPlan; }
    public Map<String, Double> getPricePerVerificationType() { return pricePerVerificationType; }
    public String getCurrency() { return currency; }
    public String getBillingEmail() { return billingEmail; }
    public boolean isInvoicingEnabled() { return invoicingEnabled; }
}
