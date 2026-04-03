/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.opensanctions.infrastructure.functions.lambda.handlers;

import infrastructure.commands.handler.sqs.ResilientSqsCommandLambdaHandler;
import java.util.Map;
import verigate.adapter.opensanctions.infrastructure.functions.lambda.di.factories.ScreenSanctionsDependencyFactory;
import verigate.verification.cg.domain.commands.incoming.VerifyPartyCommand;

/**
 * Handles the Lambda function for {@link VerifyPartyCommand} using OpenSanctions screening.
 */
public class ScreenSanctionsLambdaHandler
    extends ResilientSqsCommandLambdaHandler<VerifyPartyCommand, Map<String, String>> {

  /** Default no-arg constructor for AWS Lambda init. */
  public ScreenSanctionsLambdaHandler() {
    this(new ScreenSanctionsDependencyFactory());
  }

  /** Allow construction with an arbitrary ScreenSanctionsDependencyFactory. */
  public ScreenSanctionsLambdaHandler(
      ScreenSanctionsDependencyFactory dependencyFactory) {
    super(
        (messageBody) ->
            dependencyFactory.getSerializer().deserialize(messageBody, VerifyPartyCommand.class),
        dependencyFactory.getScreenSanctionsCommandHandler(),
        dependencyFactory.getScreenSanctionsInvalidMessageQueue(),
        dependencyFactory.getScreenSanctionsDeadLetterQueue());
  }
}
