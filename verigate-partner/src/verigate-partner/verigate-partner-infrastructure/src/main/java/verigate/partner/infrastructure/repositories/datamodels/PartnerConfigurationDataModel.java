/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.partner.infrastructure.repositories.datamodels;

/**
 * DynamoDB data model for Partner configuration.
 */
public class PartnerConfigurationDataModel {

    private String partnerId;
    private String configurationType;
    private String configurationJson;
    private String updatedAt;

    public PartnerConfigurationDataModel() {}

    public PartnerConfigurationDataModel(String partnerId, String configurationType,
                                        String configurationJson, String updatedAt) {
        this.partnerId = partnerId;
        this.configurationType = configurationType;
        this.configurationJson = configurationJson;
        this.updatedAt = updatedAt;
    }

    public String getPartnerId() { return partnerId; }
    public void setPartnerId(String partnerId) { this.partnerId = partnerId; }
    public String getConfigurationType() { return configurationType; }
    public void setConfigurationType(String configurationType) { this.configurationType = configurationType; }
    public String getConfigurationJson() { return configurationJson; }
    public void setConfigurationJson(String configurationJson) { this.configurationJson = configurationJson; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
