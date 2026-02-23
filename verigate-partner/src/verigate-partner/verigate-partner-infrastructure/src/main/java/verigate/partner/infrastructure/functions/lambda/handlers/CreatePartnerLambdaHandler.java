/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.partner.infrastructure.functions.lambda.handlers;

import infrastructure.commands.handler.sqs.ResilientSqsCommandLambdaHandler;
import verigate.partner.domain.commands.CreatePartnerCommand;
import verigate.partner.domain.models.PartnerAggregateRoot;
import verigate.partner.infrastructure.functions.lambda.di.factories.CreatePartnerDependencyFactory;

public class CreatePartnerLambdaHandler
    extends ResilientSqsCommandLambdaHandler<CreatePartnerCommand, PartnerAggregateRoot> {

    public CreatePartnerLambdaHandler() {
        this(new CreatePartnerDependencyFactory());
    }

    public CreatePartnerLambdaHandler(CreatePartnerDependencyFactory dependencyFactory) {
        super(
            (messageBody) -> dependencyFactory.getSerializer()
                .deserialize(messageBody, CreatePartnerCommand.class),
            dependencyFactory.getCreatePartnerCommandHandler(),
            dependencyFactory.getCreatePartnerInvalidMessageQueue(),
            dependencyFactory.getCreatePartnerDeadLetterQueue());
    }
}
