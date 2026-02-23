/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */
package verigate.partner.domain.commands;

import domain.commands.BaseCommand;
import verigate.partner.domain.models.VerificationFlowConfiguration;
import verigate.partner.domain.models.ApiConfiguration;
import verigate.partner.domain.models.BillingConfiguration;

import java.time.Instant;
import java.util.UUID;

public class UpdatePartnerConfigurationCommand extends BaseCommand {

    private final UUID partnerId;
    private final VerificationFlowConfiguration verificationFlowConfiguration;
    private final ApiConfiguration apiConfiguration;
    private final BillingConfiguration billingConfiguration;

    public UpdatePartnerConfigurationCommand(UUID partnerId,
                                             VerificationFlowConfiguration verificationFlowConfiguration,
                                             ApiConfiguration apiConfiguration,
                                             BillingConfiguration billingConfiguration) {
        super(UUID.randomUUID(), Instant.now(), "system");
        this.partnerId = partnerId;
        this.verificationFlowConfiguration = verificationFlowConfiguration;
        this.apiConfiguration = apiConfiguration;
        this.billingConfiguration = billingConfiguration;
    }

    public UpdatePartnerConfigurationCommand(UUID commandId, UUID partnerId,
                                             VerificationFlowConfiguration verificationFlowConfiguration,
                                             ApiConfiguration apiConfiguration,
                                             BillingConfiguration billingConfiguration,
                                             Instant createdAt, String createdBy) {
        super(commandId, createdAt, createdBy);
        this.partnerId = partnerId;
        this.verificationFlowConfiguration = verificationFlowConfiguration;
        this.apiConfiguration = apiConfiguration;
        this.billingConfiguration = billingConfiguration;
    }

    public UUID getPartnerId() { return partnerId; }
    public VerificationFlowConfiguration getVerificationFlowConfiguration() { return verificationFlowConfiguration; }
    public ApiConfiguration getApiConfiguration() { return apiConfiguration; }
    public BillingConfiguration getBillingConfiguration() { return billingConfiguration; }
}
