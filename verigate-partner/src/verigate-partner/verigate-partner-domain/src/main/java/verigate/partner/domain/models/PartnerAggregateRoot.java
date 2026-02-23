/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.partner.domain.models;

import domain.AggregateRoot;
import domain.invariants.SpecificationResult;

import java.time.Instant;
import java.util.UUID;

/**
 * Partner aggregate root representing a partner entity in the system.
 * This is the main aggregate for managing partner-related business logic
 * including lifecycle transitions and configuration management.
 */
public class PartnerAggregateRoot extends AggregateRoot<UUID, PartnerAggregateRoot> {

    private final UUID partnerId;
    private final String partnerName;
    private final String contactEmail;
    private PartnerStatus status;
    private final PartnerType partnerType;
    private final Instant createdAt;
    private Instant updatedAt;
    private PartnerConfiguration configuration;

    public PartnerAggregateRoot(UUID partnerId, String partnerName, String contactEmail,
                               PartnerType partnerType) {
        super(partnerId, true, null, 1, null);
        this.partnerId = partnerId;
        this.partnerName = partnerName;
        this.contactEmail = contactEmail;
        this.partnerType = partnerType;
        this.status = PartnerStatus.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.configuration = PartnerConfiguration.withDefaults(partnerId.toString());
    }

    /**
     * Full constructor for rehydration from persistence.
     */
    public PartnerAggregateRoot(UUID partnerId, String partnerName, String contactEmail,
                               PartnerType partnerType, PartnerStatus status,
                               Instant createdAt, Instant updatedAt,
                               PartnerConfiguration configuration) {
        super(partnerId, true, null, 1, null);
        this.partnerId = partnerId;
        this.partnerName = partnerName;
        this.contactEmail = contactEmail;
        this.partnerType = partnerType;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.configuration = configuration;
    }

    public void activate() {
        if (this.status != PartnerStatus.PENDING && this.status != PartnerStatus.SUSPENDED) {
            throw new IllegalStateException(
                "Partner can only be activated from PENDING or SUSPENDED status, current: " + this.status);
        }
        this.status = PartnerStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        if (this.status != PartnerStatus.ACTIVE) {
            throw new IllegalStateException("Partner must be ACTIVE to deactivate, current: " + this.status);
        }
        this.status = PartnerStatus.DEACTIVATED;
        this.updatedAt = Instant.now();
    }

    public void suspend() {
        if (this.status != PartnerStatus.ACTIVE) {
            throw new IllegalStateException("Partner must be ACTIVE to suspend, current: " + this.status);
        }
        this.status = PartnerStatus.SUSPENDED;
        this.updatedAt = Instant.now();
    }

    public void terminate() {
        if (this.status == PartnerStatus.TERMINATED) {
            throw new IllegalStateException("Partner is already terminated");
        }
        this.status = PartnerStatus.TERMINATED;
        this.updatedAt = Instant.now();
    }

    public void updateConfiguration(PartnerConfiguration newConfiguration) {
        if (newConfiguration == null) {
            throw new IllegalArgumentException("Configuration must not be null");
        }
        this.configuration = newConfiguration;
        this.updatedAt = Instant.now();
    }

    public boolean isActive() {
        return this.status == PartnerStatus.ACTIVE;
    }

    // Getters
    public UUID getPartnerId() { return partnerId; }
    public String getPartnerName() { return partnerName; }
    public String getContactEmail() { return contactEmail; }
    public PartnerStatus getStatus() { return status; }
    public PartnerType getPartnerType() { return partnerType; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public PartnerConfiguration getConfiguration() { return configuration; }

    @Override
    public SpecificationResult checkSpecification() {
        if (partnerName == null || partnerName.isBlank()) {
            return SpecificationResult.fail("Partner name must not be blank");
        }
        if (contactEmail == null || contactEmail.isBlank()) {
            return SpecificationResult.fail("Contact email must not be blank");
        }
        if (partnerType == null) {
            return SpecificationResult.fail("Partner type must not be null");
        }
        return SpecificationResult.success();
    }
}
