/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.partner.infrastructure.repositories.datamodels;

import java.time.Instant;

/**
 * DynamoDB data model for Partner entity.
 */
public class PartnerDataModel {

    private String partnerId;
    private String partnerName;
    private String contactEmail;
    private String partnerType;
    private String status;
    private String createdAt;
    private String updatedAt;

    public PartnerDataModel() {}

    public PartnerDataModel(String partnerId, String partnerName, String contactEmail,
                           String partnerType, String status, String createdAt, String updatedAt) {
        this.partnerId = partnerId;
        this.partnerName = partnerName;
        this.contactEmail = contactEmail;
        this.partnerType = partnerType;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getPartnerId() { return partnerId; }
    public void setPartnerId(String partnerId) { this.partnerId = partnerId; }
    public String getPartnerName() { return partnerName; }
    public void setPartnerName(String partnerName) { this.partnerName = partnerName; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public String getPartnerType() { return partnerType; }
    public void setPartnerType(String partnerType) { this.partnerType = partnerType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
