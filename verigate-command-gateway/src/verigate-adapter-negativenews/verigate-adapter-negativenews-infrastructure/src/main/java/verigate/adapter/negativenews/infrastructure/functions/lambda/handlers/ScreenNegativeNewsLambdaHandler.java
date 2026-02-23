/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.negativenews.infrastructure.functions.lambda.handlers;

import infrastructure.commands.handler.sqs.ResilientSqsCommandLambdaHandler;
import java.util.Map;
import verigate.adapter.negativenews.infrastructure.functions.lambda.di.factories.ScreenNegativeNewsDependencyFactory;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Handles the Lambda function for {@link VerifyPartyCommand} using negative news screening.
 */
public class ScreenNegativeNewsLambdaHandler
    extends ResilientSqsCommandLambdaHandler<VerifyPartyCommand, Map<String, String>> {

  /** Default no-arg constructor for AWS Lambda init. */
  public ScreenNegativeNewsLambdaHandler() {
    this(new ScreenNegativeNewsDependencyFactory());
  }

  /** Allow construction with an arbitrary ScreenNegativeNewsDependencyFactory. */
  public ScreenNegativeNewsLambdaHandler(
      ScreenNegativeNewsDependencyFactory dependencyFactory) {
    super(
        (messageBody) ->
            dependencyFactory.getSerializer().deserialize(messageBody, VerifyPartyCommand.class),
        dependencyFactory.getScreenNegativeNewsCommandHandler(),
        dependencyFactory.getScreenNegativeNewsInvalidMessageQueue(),
        dependencyFactory.getScreenNegativeNewsDeadLetterQueue());
  }
}
