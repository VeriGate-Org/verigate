/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.partner.infrastructure.functions.lambda.handlers;

import infrastructure.commands.handler.sqs.ResilientSqsCommandLambdaHandler;
import verigate.partner.domain.commands.UpdatePartnerConfigurationCommand;
import verigate.partner.domain.models.PartnerConfiguration;
import verigate.partner.infrastructure.functions.lambda.di.factories.UpdatePartnerConfigDependencyFactory;

public class UpdatePartnerConfigLambdaHandler
    extends ResilientSqsCommandLambdaHandler<UpdatePartnerConfigurationCommand, PartnerConfiguration> {

    public UpdatePartnerConfigLambdaHandler() {
        this(new UpdatePartnerConfigDependencyFactory());
    }

    public UpdatePartnerConfigLambdaHandler(UpdatePartnerConfigDependencyFactory dependencyFactory) {
        super(
            (messageBody) -> dependencyFactory.getSerializer()
                .deserialize(messageBody, UpdatePartnerConfigurationCommand.class),
            dependencyFactory.getUpdatePartnerConfigCommandHandler(),
            dependencyFactory.getUpdatePartnerConfigInvalidMessageQueue(),
            dependencyFactory.getUpdatePartnerConfigDeadLetterQueue());
    }
}
